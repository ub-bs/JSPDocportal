<%@tag import="org.mycore.common.MCRUtils"%>
<%@tag import="org.mycore.datamodel.niofs.MCRFileAttributes"%>
<%@tag import="java.nio.file.Files" %>
<%@tag import="org.mycore.datamodel.niofs.MCRPath" %>
<%@tag import="java.nio.file.Path" %>
<%@tag import="org.mycore.common.MCRPersistenceException"%>
<%@tag import="org.mycore.services.i18n.MCRTranslation"%>
<%@tag import="org.mycore.frontend.MCRFrontendUtil"%>
<%@tag import="java.util.Locale"%>
<%@tag import="org.mycore.datamodel.ifs.MCRFile"%>
<%@tag import="org.mycore.datamodel.ifs.MCRFilesystemNode"%>
<%@tag import="org.mycore.datamodel.ifs.MCRDirectory"%>
<%@tag import="org.mycore.frontend.jsp.MCRHibernateTransactionWrapper"%>
<%@tag import="org.mycore.access.MCRAccessManager"%>
<%@tag pageEncoding="UTF-8"%> 
<%@tag import="org.mycore.datamodel.metadata.MCRDerivate"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObject"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="derid" required="true" type="java.lang.String" %>
<%@ attribute name="showSize" required="false" type="java.lang.Boolean" %>

<% 
	MCRDerivate mcrDerivate = null;
	try{
		mcrDerivate = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(derid));
	}
	catch(MCRPersistenceException e){
	    //do nothing
	}
	if(mcrDerivate==null){
		out.write("<b>No derivate found for id: "+derid+".</b>");
	}
	else{
	    try (MCRHibernateTransactionWrapper hib = new MCRHibernateTransactionWrapper()) {
            Path root = MCRPath.getPath(derid.toString(), "/");
            if (root != null) {
				String mainDoc = mcrDerivate.getDerivate().getInternals().getMainDoc();
				String url = MCRFrontendUtil.getBaseURL()+"file/"+mcrDerivate.getOwnerID().toString()+"/"+mcrDerivate.getId().toString()+"/";
				String label=mcrDerivate.getLabel();
				String displayLabel = MCRTranslation.translate("OMD.derivatedisplay." + mcrDerivate.getOwnerID().getBase()+"."+label);
                if(!MCRTranslation.exists("OMD.derivatedisplay." + mcrDerivate.getOwnerID().getBase()+"."+label)){
				  displayLabel = MCRTranslation.translate("OMD.derivatedisplay." + label);
                }
				
                boolean accessAllowed = MCRAccessManager.checkPermission(derid, "read");
                out.write("    <tr class=\"ir-derivate-list-row\">");
                out.write("      <th>"+ displayLabel +"</th>");
                out.write("      <td>");
                out.write("        <ul class=\"ir-derivate-list-files\">");

                try (DirectoryStream<Path> ds = Files.newDirectoryStream(root)) {
                    for (Path theFile: ds) {
                      out.write("      <li>");
                      if (accessAllowed) {
                          String fileName =  theFile.getFileName().toString();
                          String fURL = url + fileName;
                          String fontAwesomeName = "fa fa-file-o";
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".pdf")) {
                        	fontAwesomeName = "fa fa-file-pdf-o";
                        }
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".jpg")
                            || theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpeg")) {
                        	fontAwesomeName = "fa fa-file-image-o";
                        }
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".doc")
                            || theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".txt")) {
                        	fontAwesomeName = "fa fa-file-text-o";
                        }
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".xml")) {
                        	fontAwesomeName = "fa fa-file-code-o";
                        }
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".mp3")) {
                        	fontAwesomeName = "fa fa-file-audio-o";
                        }
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".zip")) {
                        	fontAwesomeName = "fa fa-file-archive-o";
                        }
                        if (fileName.toLowerCase(Locale.GERMAN).endsWith(".mp4")
                        	|| theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".mpeg")
                        	|| theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".mpg")) {
                        	fontAwesomeName = "fa fa-file-video-o";
                        }
                        if (Files.isDirectory(theFile)) {
                        	fontAwesomeName = "fa fa-files-o";
                        }
                        out.write("<i class=\"" + fontAwesomeName + "\"></i>&nbsp;&nbsp;");
                        out.write("    <a href=\"" + fURL + "\" target=\"_blank\">");
                        out.write(fileName);
                        out.write("    </a>");
                        if (showSize) { 
                          String md5 = "";
                          if(Files.isRegularFile(theFile)) {
                              @SuppressWarnings("rawtypes")
                              MCRFileAttributes attrs = Files.readAttributes(child, MCRFileAttributes.class);
                               md5 = "; MD5: " + attrs.md5sum(); 
                              out.write("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<small>(" +  MCRUtils.getSizeFormatted(attrs.size()).replace(" ","&#160;") + md5 + ")</small>");
                          }
                        }
                        out.write("  </li>");
                    } else {
                    	out.write("  <li>");
                        out.write(theFile.getName().replace(".mets.xml", " .mets.xml"));
                        if (showSize) {
                            out.write(" (" + MCRUtils.getSizeFormatted(Files.size(theFile)).replace(" ","&#160;") + ")<br />");
                        }
                        out.write("&#160;---&#160;"
                            + MCRTranslation.translate("OMD.fileaccess.denied"));
                        out.write("  </li>");
                    }
                }
                out.write("      </ul>");
                out.write("    </td>");
                out.write("  </tr>");
			}
        }
	}
%>

