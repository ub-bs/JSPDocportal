package org.mycore.frontend.jsp.taglibs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRSession;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.i18n.MCRTranslation;

/**
 * Tag, that displays the specified file in the proper language
 * by adding a "language-suffix" to the filename
 * If the current user has the permission a button to edit this file will be displayed.
 * If the button was clicked the OpenSource CKeditor would be opened to edit the file.
 * 
 * @author Robert Stephan
 */
public class MCRIncludeWebContentTag extends SimpleTagSupport {
	public static final String OPENEDITOR_PARAMETER = "openeditor";
	public static final String CK_FORM_INPUT_CANCEL_NAME = "cancel";
	public static final String VARNAME_CKEDITOR_LOADED = "ckeditor_loaded";

	private static MCRConfiguration CONFIG = MCRConfiguration.instance();
	
	private String file;
	private File f_read, f_save;

	/**
	 * used to set the filname as tag attribute
	 * @param file - the filename
	 */
	public void setFile(String file) {
		this.file = file;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		out.flush();
		boolean isEditallowed = MCRAccessManager.checkPermission("administrate-webcontent");
		boolean isOpenEditor = "true".equalsIgnoreCase(pageContext
				.getRequest().getParameter(OPENEDITOR_PARAMETER));

		adaptFiles();
		
		String result = pageContext.getRequest().getParameter(f_read.getName()); 
		String wasCanceled = pageContext.getRequest().getParameter(CK_FORM_INPUT_CANCEL_NAME);
		
		if (result != null) {
			 //the editor was closed (submitted or canceled)
			isOpenEditor = false;
			if (wasCanceled == null) {
				//editor was submitted -> save file
				BufferedWriter bwResult = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f_save), "UTF-8"));
				bwResult.write(result);
				bwResult.close();
				f_read = f_save;
			}
		}

		//display the editor / the editbutton or nothing (depending from accessrights and parameter)
		if (isEditallowed) {
			if (isOpenEditor) {
				showCKEditor(f_read);
				return; // do not show the text block
			} else {
				showEditButton(); 
			}
		}
				
		//editing not allowed simply display the file
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f_read), "UTF-8"));
		String temp=null;
		while ((temp = br.readLine()) != null) {
			out.write(temp);
			out.newLine();
		}
		br.close();
	}

	/**
	 * opens the CKEditor
	 * by including the editor as described in:
	 * http://docs.cksource.com/CKEditor_3.x/Developers_Guide/Integration
	 * 
	 * @param file2display - the file that should be displayed
	 * @throws JspException
	 * @throws IOException
	 */
	//TODO: Integrate new CK Editor 
	private void showCKEditor(File file2display)
			throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		/* <script type="text/javascript" src="/ckeditor/ckeditor.js"></script>
		<form method="post">
			<p><textarea name="editor1">
			    &lt;p&gt;Initial value.&lt;/p&gt;</textarea>
				<script type="text/javascript">
					CKEDITOR.replace( 'editor1', 
					{customConfig : '/custom/ckeditor_config.js'});
				</script>
			</p>
		</form>	*/
		Boolean isLoaded = (Boolean)pageContext.findAttribute(VARNAME_CKEDITOR_LOADED);
		String baseURL = (String) pageContext.findAttribute("WebApplicationBaseURL");  
		if(isLoaded==null || !isLoaded.booleanValue()){
			out.write("<script type=\"text/javascript\" src=\""+baseURL+"ckeditor/ckeditor.js\" ></script>");
			pageContext.setAttribute(VARNAME_CKEDITOR_LOADED, Boolean.TRUE, PageContext.PAGE_SCOPE);
		}
		out.write("<form method=\"post\"><p>");
		out.write("<textarea name=\""+f_read.getName()+"\">");
		out.newLine();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file2display), "UTF-8"));
		String temp=null;
		while ((temp = br.readLine()) != null) {
			out.write(temp);
			out.newLine();
		}
		br.close();
		out.newLine();		
		out.write("</textarea>");
		out.write("<script type=\"text/javascript\">");
		out.write("CKEDITOR.replace( '"+f_read.getName()+"',");
		out.write("{customConfig : '"+baseURL+"admin/ckeditor_config.js', ");
     	out.write("toolbar : 'Full'");
		out.write("});</script>");
				
		String lblSave = MCRTranslation.translate("Editor.Common.button.Save");
		String lblCancel = MCRTranslation.translate("Editor.Common.button.Cancel");
		out.write("</p><p><input type=\"submit\" value=\""+lblSave+"\">&nbsp;&nbsp;&nbsp;"
				+ "<input type=\"submit\" name=\"" + CK_FORM_INPUT_CANCEL_NAME
				+ "\" value=\""+lblCancel+"\">"
				+ "</p></form>");
		out.flush();
		
	}

	/**
	 * display the button, which opens the FCK-Editor
	 * @throws IOException
	 */
	private void showEditButton() throws IOException {
		PageContext pageContext = (PageContext) getJspContext();
		String myURL = MCRFrontendUtil.getBaseURL();
		String label = MCRTranslation.translate("Webpage.editwebcontent");
		JspWriter out = pageContext.getOut();
		out.write("<form method=\"post\" action=\"\">");
		out.write("<input	name=\"" + OPENEDITOR_PARAMETER
				+ "\" value=\"true\" type=\"hidden\"> ");
		out.write("	<input	title=\""+label+"\" src=\"" + myURL
				+ "images/edit_webcontent.gif\" type=\"image\""
				+ "class=\"imagebutton\">");
		out.write("</form>");
	}
	
	
	/**
	 * tries to adapt the filename by adding the language as suffix
	 * If such a file exists, it will be taken otherwise the "default" file will be displayed.
	 * If the file (after beeing edited) is saved - the name + language suffix is used. 
	 * 
	 * @throws IOException
	 */
	private void adaptFiles()throws IOException{
	    MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)((PageContext) getJspContext()).getRequest());
		String lang = mcrSession.getCurrentLanguage();
		String foldername = CONFIG.getString("MCR.WebContent.Folder");
		String storeFolderName=CONFIG.getString("MCR.WebContent.SaveFolder");
		PageContext pageContext = (PageContext) getJspContext();

		String[] s = file.split("\\."); //split the filename into path and extension
		f_read = new File(new File(storeFolderName), s[0] + "_" + lang + "." + s[1]);
		
		f_save = f_read;
		
		if(!f_read.exists()){
			String path = foldername + "/" + s[0] + "_" + lang + "." + s[1];
			File dir = new File(pageContext.getServletContext().getRealPath("content"));
			f_read = new File(dir, path);
			if (!f_read.exists()) {
				path = foldername + "/" + file;
				f_read = new File(dir, path);
			}
		}
		if (!f_read.canRead()) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f_read), "UTF-8"));
			bw.write("New["+f_read.getName()+"] ...");
			bw.close();
		}
	}
}