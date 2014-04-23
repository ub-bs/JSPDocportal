package org.mycore.tools.goobiimport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

import org.mycore.common.config.MCRConfiguration;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class GoobiSFTPDownloader {
    /**
     * lädt die prodzierten Dateien vom Goobi-Server nach MyCoRe
     *  
     * @param goobiFolderID
     *            - die Goobi-Folder ID
     * @param downladDir
     *            - das DownloadVerzeichnis
     */
    public static void sftpDownload(String goobiFolderID, File downloadDir) {
        Session session = null;
        Channel channel = null;
        try {
            JSch jsch = new JSch();

            int port = 22;
            try {
                port = Integer.parseInt(MCRConfiguration.instance().getString("goobi.sftp.port"));
            } catch (Exception e) {
                // use default;
            }
            session = jsch.getSession(MCRConfiguration.instance().getString("goobi.sftp.user"), MCRConfiguration.instance().getString("goobi.sftp.host"), port);
            session.setPassword(MCRConfiguration.instance().getString("goobi.sftp.password"));
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
              
            
            channelSftp.cd(MCRConfiguration.instance().getString("goobi.basedir"));
            channelSftp.cd("metadata");
            String goobiProcessName = goobiFolderID.split("__")[0];
            String goobiProcessID = goobiFolderID.split("__")[1];
            goobiProcessID = goobiProcessID.replace("[", "").replace("]", "").trim();
            channelSftp.cd(goobiProcessID);
            
            try {
                File fMeta = new File(downloadDir, "meta.xml");
                channelSftp.get("meta.xml", fMeta.getPath());
                               
                channelSftp.cd("images");
                channelSftp.cd("orig_"+goobiProcessName+"_tif");
                @SuppressWarnings("unchecked")
                Vector<LsEntry> v = channelSftp.ls("*");
                File dirImages = new File(downloadDir, "images");
                dirImages.mkdir();
                if (v.size() > 0) {
                    for (LsEntry lse : v) {
                        if (lse.getFilename().equals(".") || lse.getFilename().equals("..")) {
                            continue;
                        }
                        channelSftp.get(lse.getFilename(), new File(dirImages, lse.getFilename()).getPath());
                    }                   
                }                
            } catch (SftpException e) {
                if (!e.getMessage().contains("No such file")) {
                    System.err.println("SFTPUpload Error:");
                    e.printStackTrace();
                }
               
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Disconnecting the channel
        if (channel != null) {
            channel.disconnect();
        }
        // Disconnecting the session
        if (session != null) {
            session.disconnect();
        }
    }
}
