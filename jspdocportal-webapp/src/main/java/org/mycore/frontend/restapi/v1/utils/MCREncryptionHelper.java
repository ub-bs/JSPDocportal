package org.mycore.frontend.restapi.v1.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.SortedMap;

import org.apache.commons.codec.binary.Base64;


/**
 * Key-generation with OpenSSL
 * ---------------------------
 *  > openssl genrsa -out clientPrivateKey.pem 1024 -des3
 *  > openssl genrsa -out serverPrivateKey.pem 1024 -des3
 *  
 *  > openssl rsa -in clientPrivateKey.pem -out clientPublicKey.key -pubout -outform der
 *  > openssl rsa -in serverPrivateKey.pem -out serverPublicKey.key -pubout -outform der
 *  
 *  > openssl pkcs8 -in clientPrivateKey.pem -out clientPrivateKey.p8 -outform der -nocrypt -topk8
 *  > openssl pkcs8 -in serverPrivateKey.pem -out serverPrivateKey.p8 -outform der -nocrypt -topk8
 *  
 *   
 * @author mcradmin
 *
 */
public class MCREncryptionHelper {

    //http://blog.axxg.de/java-verschluesselung-beispiel-quickstart/

    public static long getFileSize(File f) {
        return f.length();
    }

    public static boolean verifySHA1Checksum(String file, String testChecksum) throws NoSuchAlgorithmException,
            IOException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");

        byte[] data = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            int read = 0;
            while ((read = fis.read(data)) != -1) {
                sha1.update(data, 0, read);
            }
        }
        byte[] hashBytes = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        String fileHash = sb.toString();

        return fileHash.equals(testChecksum);
    }

    public static String createSHA1Checksum(File file) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException nsae) {
            //do nothing
        }

        byte[] data = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            int read = 0;
            while ((read = fis.read(data)) != -1) {
                sha1.update(data, 0, read);
            }
        } catch (IOException e) {
            //do nothing
        }
        byte[] hashBytes = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static final void createRSAKEYFiles(File dir, String filenamePrefix) {
        // Datei
        try {
            // Verzeichnis anlegen
            dir.mkdirs();

            // zufaelligen Key erzeugen
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(1024);
            KeyPair keyPair = keygen.genKeyPair();

            // schluessel lesen
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Public Key sichern
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            FileOutputStream fos = new FileOutputStream(new File(dir, filenamePrefix + ".public.key"));
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();

            // Private Key sichern
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            fos = new FileOutputStream(new File(dir, filenamePrefix + ".private.key"));
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PublicKey readPublicKey(File f) throws Exception {

        // Public key lesen
        FileInputStream fis = new FileInputStream(f);
        byte[] encodedPublicKey = new byte[(int) f.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // generiere Key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return publicKey;
    }

    public static PrivateKey readPrivateKey(File f) throws Exception {
        FileInputStream fis = new FileInputStream(f);
        byte[] encodedPrivateKey = new byte[(int) f.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // generiere Key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return privateKey;
    }

    public static KeyPair genRSAKeys() {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(1024);
            return keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException nsae) {
            //should not happen
            return null;
        }
    }

    public static String generateMessagsFromProperties(SortedMap<String, String> data) {
        StringWriter sw = new StringWriter();
        sw.append("{");
        for (String key : data.keySet()) {
            sw.append("\"").append(key).append("\"").append(":").append("\"").append(data.get(key)).append("\"")
                    .append(",");
        }
        String result = sw.toString();
        if (result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }
        result = result + "}";

        return result;
    }

    public static String generateSignatureFromProperties(SortedMap<String, String> data, File privateKeyFile) {
        String message = generateMessagsFromProperties(data);

        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA1withRSA");
        } catch (NoSuchAlgorithmException nsae) {
            //should not happen;
        }

        try {
            PrivateKey privateKey = readPrivateKey(privateKeyFile);
            signature.initSign(privateKey);
            signature.update(message.getBytes());

            byte[] sigBytes = signature.sign();

            return Base64.encodeBase64String(sigBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verifyPropertiesWithSignature(SortedMap<String, String> data, String base64Signature,
            File publicKeyFile) {
        try {
            PublicKey publicKey = readPublicKey(publicKeyFile);
            String message = generateMessagsFromProperties(data);

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());

            return signature.verify(Base64.decodeBase64(base64Signature));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            KeyPair keys = genRSAKeys();
            System.out.println("RSA-Keys");
            System.out.println("========");
            System.out.println("private: " + keys.getPrivate());
            System.out.println("public:  " + keys.getPublic());

            File dirOut = new File("C:\\workspaces\\rosdok\\projects\\encryption\\sample\\keys");
            createRSAKEYFiles(dirOut, "server");
            createRSAKEYFiles(dirOut, "client");
        }
    }
}
