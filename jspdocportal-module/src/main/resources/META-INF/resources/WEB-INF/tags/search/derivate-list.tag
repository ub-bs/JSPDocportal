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
	    out.write("<dl class=\"ir-derivate-list\">");
	    try (MCRHibernateTransactionWrapper hib = new MCRHibernateTransactionWrapper()) {
            MCRDirectory root = MCRDirectory.getRootDirectory(derid);
            if (root != null) {
                MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
    
				String mainDoc = mcrDerivate.getDerivate().getInternals().getMainDoc();
				String url = MCRFrontendUtil.getBaseURL()+"file/"+mcrDerivate.getOwnerID().toString()+"/"+mcrDerivate.getId().toString()+"/";
				String label=mcrDerivate.getLabel();
				String displayLabel = MCRTranslation.translate("OMD.derivatedisplay." + label);
                out.write("\n<dt>"+displayLabel+"</dt>");
				
                boolean accessAllowed = MCRAccessManager.checkPermission(derid, "read");
                for (int j = 0; j < myfiles.length; j++) {
                    MCRFilesystemNode theFile = (MCRFilesystemNode) myfiles[j];
                    
                    
                    out.write("<dd>");

                    if (accessAllowed) {
                        String fURL = url + theFile.getName();
                        out.write("<a class=\"btn btn-link btn-sm\" href=\"" + fURL + "\" target=\"_blank\">");
                        String imgURL = MCRFrontendUtil.getBaseURL() + "images/derivate_unknown.gif";
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".pdf")) {
                            imgURL = MCRFrontendUtil.getBaseURL() + "images/derivate_pdf.gif";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpg")
                            || theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpeg")) {
                            imgURL = MCRFrontendUtil.getBaseURL() + "images/derivate_portrait.gif";
                        }
                        if (theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".doc")
                            || theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".txt")) {
                            imgURL = MCRFrontendUtil.getBaseURL() + "images/derivate_doc.gif";
                        }
                        out.write("<img src=\"" + imgURL + "\" />&nbsp;");
                        out.write(theFile.getName());
                       
                        if (showSize) {
                            out.write(" <span style=\"color:black\">(" + theFile.getSizeFormatted().replace(" ","&#160;") + "</span>)");
                        }
                        out.write("</a>");
                    } else {
                        out.write(theFile.getName().replace(".mets.xml", " .mets.xml"));
                        if (showSize) {
                            out.write(" (" + theFile.getSizeFormatted().replace(" ","&#160;") + ")<br />");
                        }
                        out.write("&#160;---&#160;"
                            + MCRTranslation.translate("OMD.fileaccess.denied"));
                    }
                    out.write("</dd>");
                }
			}
        }
	    out.write("\n</dl>");
	}
%>

