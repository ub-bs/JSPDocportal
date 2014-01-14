package org.mycore.tools.dissonline.formimport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.MCRConfiguration;
import org.xml.sax.InputSource;

public class DissOnlineFormImport {
    private static String XSLT_FILE="/xsl/ubr_form2mcr4disshab.xsl";
    
    public static String[] retrieveMetadataVersions(String folderName) {
        ArrayList<String> result = new ArrayList<String>();
        MCRConfiguration config = MCRConfiguration.instance();
        
        FTPClient ftp = new FTPClient();
        int reply;
        try {
            
            ftp.connect(config.getString("dissonline.upload.ftp.url"));
            ftp.login(config.getString("dissonline.upload.ftp.user"), config.getString("dissonline.upload.ftp.password"));
            reply = ftp.getReplyCode();
            if (FTPReply.isPositiveCompletion(reply)) {
                ftp.changeWorkingDirectory(config.getString("dissonline.upload.ftp.basedir"));
                ftp.changeWorkingDirectory(folderName);
                for (FTPFile f: ftp.listDirectories()){
                    if(f.getName().startsWith("Metadaten")){
                    result.add(f.getName());
                    }
                }
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(DissOnlineFormImport.class).error(ex);
        } catch (IOException ex) {
            Logger.getLogger(DissOnlineFormImport.class).error(ex);
        }
        finally{
            try{
                ftp.disconnect();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
   
        return result.toArray(new String[]{});
    }

    //can return null
    public static Document retrieveMetadataContent(String folderName, String metadataVersion) {
        Document doc = null;
            MCRConfiguration config = MCRConfiguration.instance();
            
            FTPClient ftp = new FTPClient();
            InputStream is = null;
            int reply;
            try {
                
                ftp.connect(config.getString("dissonline.upload.ftp.url"));
                ftp.login(config.getString("dissonline.upload.ftp.user"), config.getString("dissonline.upload.ftp.password"));
                reply = ftp.getReplyCode();
                if (FTPReply.isPositiveCompletion(reply)) {
                    ftp.changeWorkingDirectory(config.getString("dissonline.upload.ftp.basedir"));
                    ftp.changeWorkingDirectory(folderName);
                    ftp.changeWorkingDirectory(metadataVersion);
                    String filename=null;
                    for(FTPFile file: ftp.listFiles()){
                        if(file.getName().endsWith(".xml")){
                            filename = file.getName();
                        }
                    }
                    if(filename!=null){
                        is = ftp.retrieveFileStream(filename);
                        doc = new SAXBuilder().build(is, "UTF-8");
                    }
                }
                
            } catch (SocketException ex) {
                Logger.getLogger(DissOnlineFormImport.class).error(ex);
            } catch (IOException ex) {
                Logger.getLogger(DissOnlineFormImport.class).error(ex);
            } catch (JDOMException ex) {
                Logger.getLogger(DissOnlineFormImport.class).error(ex);
            }
            finally{
                try{
                    if(is!=null){
                        is.close();
                    }
                    ftp.disconnect();
                    
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
       
            return doc;
      }
    
    public static void loadFormDataIntoMCRObject(String content, File mcrFile){
        try{
            StreamSource xsltSource = new StreamSource(DissOnlineFormImport.class.getResourceAsStream(XSLT_FILE)); 
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
        
            
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(new InputSource(new StringReader(content)));
            

            transformer.setParameter("formData", doc.getChildNodes());
            transformer.setParameter("currentYear", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            
            File mcrOutFile = mcrFile;
            //debugging: 
            //File mcrOutFile = new File(mcrFile.getPath().replace(".xml", ".out.xml"));
            
            StreamSource xmlSource = new StreamSource(mcrFile);
            DOMResult domResult = new DOMResult();
            transformer.transform(xmlSource, domResult);
            
            //output pretty print
            DOMSource domSource = new DOMSource(domResult.getNode());
            StreamResult streamResult = new StreamResult(mcrOutFile);
            Transformer transformerOut = TransformerFactory.newInstance().newTransformer();
            transformerOut.setOutputProperty(OutputKeys.INDENT, "yes");
            transformerOut.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformerOut.transform(domSource,  streamResult);
                             
            
        } catch (Exception e) {
            Logger.getLogger(DissOnlineFormImport.class).error("Error processing formdata", e);
        }
    
    }
}
