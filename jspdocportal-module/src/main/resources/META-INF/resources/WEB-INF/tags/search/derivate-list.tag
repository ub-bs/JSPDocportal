<%@tag import="org.mycore.common.MCRPersistenceException"%>
<%@tag import="org.mycore.services.i18n.MCRTranslation"%>
<%@tag import="org.mycore.frontend.MCRFrontendUtil"%>
<%@tag import="java.util.Locale"%>
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
            MCRDirectory root = MCRDirectory.getRootDirectory(derid);
            if (root != null) {
                MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
    
				String mainDoc = mcrDerivate.getDerivate().getInternals().getMainDoc();
				String url = MCRFrontendUtil.getBaseURL()+"file/"+mcrDerivate.getOwnerID().toString()+"/"+mcrDerivate.getId().toString()+"/";
				String label=mcrDerivate.getLabel();
				String displayLabel = MCRTranslation.translate("OMD.derivatedisplay." + label);
				
                boolean accessAllowed = MCRAccessManager.checkPermission(derid, "read");
                out.write("    <tr>");
                out.write("      <th>"+ displayLabel +"</th>");
                out.write("      <td>");
                out.write("        <ul class=\"ir-table-docdetails-filelist\">");
                for (int j = 0; j < myfiles.length; j++) {
                    MCRFilesystemNode theFile = (MCRFilesystemNode) myfiles[j];
                    out.write("      <li>");
                    if (accessAllowed) {
                        String fURL = url + theFile.getName();
                        String fontAwesomeName = "fa fa-file-o";
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".pdf")) {
                        	fontAwesomeName = "fa fa-pdf-o";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpg")
                            || theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpeg")) {
                        	fontAwesomeName = "fa fa-file-image-o";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".doc")
                            || theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".txt")) {
                        	fontAwesomeName = "fa fa-file-text-o";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".xml")) {
                        	fontAwesomeName = "fa fa-file-code-o";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".mp3")) {
                        	fontAwesomeName = "fa fa-file-audio-o";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".zip")) {
                        	fontAwesomeName = "fa fa-file-archive-o";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".mp4")
                        	|| theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".mpeg")
                        	|| theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".mpg")) {
                        	fontAwesomeName = "fa fa-file-video-o";
                        }
                        if (theFile instanceof MCRDirectory) {
                        	fontAwesomeName = "fa fa-files-o";
                        }
                        out.write("<i class=\"" + fontAwesomeName + "\"></i>&nbsp;&nbsp;");
                        out.write("    <a href=\"" + fURL + "\" target=\"_blank\">");
                        out.write(theFile.getName());
                        out.write("    </a>");
                        if (showSize) {
                            out.write(" &nbsp;&nbsp;<span>(" + theFile.getSizeFormatted().replace(" ","&#160;") + "</span>)");
                        }
                        out.write("  </li>");
                    } else {
                    	out.write("  <li>");
                        out.write(theFile.getName().replace(".mets.xml", " .mets.xml"));
                        if (showSize) {
                            out.write(" (" + theFile.getSizeFormatted().replace(" ","&#160;") + ")<br />");
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

