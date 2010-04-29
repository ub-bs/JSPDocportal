package org.mycore.frontend.jsp.taglibs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.i18n.MCRTranslation;

import com.fredck.FCKeditor.tags.FCKeditorTag;

/**
 * @author Robert Stephan
 * a new tag that displayes the specified file in the proper language
 * by adding a "language-suffix" to the filename
 * If the current user has the permission a button to edit this file will be displayed.
 * If the button was clicked the OpenSource FCKeditor would be displayed to edit the file.
 */
public class MCRIncludeWebContentTag extends SimpleTagSupport {
	public static final String FCK_RESULT_PARAMETER = "fckresult";
	public static final String OPENEDITOR_PARAMETER = "openeditor";
	public static final String FCK_FORM_INPUT_CANCEL_NAME = "cancel";

	private static MCRConfiguration CONFIG = MCRConfiguration.instance();
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();

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

		boolean isEditallowed = AI.checkPermission("administrate-webcontent");
		boolean isOpenEditor = "true".equalsIgnoreCase(pageContext
				.getRequest().getParameter(OPENEDITOR_PARAMETER));

		adaptFiles();
		
		String result = pageContext.getRequest().getParameter(FCK_RESULT_PARAMETER);
		String wasCanceled = pageContext.getRequest().getParameter(FCK_FORM_INPUT_CANCEL_NAME);
		
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
				showFCKEditor(f_read);
			} else {
				showEditButton(); 

				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f_read), "UTF-8"));
				String temp=null;
				while ((temp = br.readLine()) != null) {
					out.write(temp);
					out.newLine();
				}
				br.close();
			}
		} else {
			//editing not allowed simply display the file
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f_read), "UTF-8"));
			String temp=null;
			while ((temp = br.readLine()) != null) {
				out.write(temp);
				out.newLine();
			}
			br.close();
		}
	}

	/**
	 * opens the FCKEditor
	 * by calling the FCKEDitorTag with Java means
	 * 
	 * @param file2display - the file that should be displayed
	 * @throws JspException
	 * @throws IOException
	 */
	private void showFCKEditor(File file2display)
			throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		// create form header
		out.write("<form action=\"\" method=\"post\">");
		out.flush();

		// include FCK Editor (by invoking the FCKeditorTag)

		FCKeditorTag fck = new FCKeditorTag();
		fck.setWidth(CONFIG.getString("MCR.fckeditor.width", "95%"));
		fck.setHeight(CONFIG.getString("MCR.fckeditor.height", "450"));
		fck.setToolbarSet(CONFIG.getString("MCR.fckeditor.toolbarset", "mcr"));
		fck.setId(FCK_RESULT_PARAMETER);

		// add the content of the file to display to the body of the
		// FCKeditorTag
		BodyContent bc = pageContext.pushBody(); // creates a new pagecontext
													// and save the old one in a
													// stack
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file2display), "UTF-8"));
		String temp=null;
		while ((temp = br.readLine()) != null) {
			bc.write(temp);
			bc.newLine();
		}
		br.close();
		fck.setBodyContent(bc);
		fck.setPageContext(pageContext);

		// execute the FCKeditorTag
		fck.doStartTag();
		fck.doInitBody();
		fck.doAfterBody();
		fck.doEndTag();
		out.flush();
		pageContext.popBody();

		//close the html form
		String lblSave = MCRTranslation.translate("Editor.Common.button.Save");
		String lblCancel = MCRTranslation.translate("Editor.Common.button.Cancel");
		out.write("<br>	<input type=\"submit\" value=\""+lblSave+"\">&nbsp;&nbsp;&nbsp;"
				+ "<input type=\"submit\" name=\"" + FCK_FORM_INPUT_CANCEL_NAME
				+ "\" value=\""+lblCancel+"\">"
				+ "</form>");
		out.flush();
	}

	/**
	 * display the button, which opens the FCK-Editor
	 * @throws IOException
	 */
	private void showEditButton() throws IOException {
		PageContext pageContext = (PageContext) getJspContext();
		String myURL = MCRServlet.getBaseURL();
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
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
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