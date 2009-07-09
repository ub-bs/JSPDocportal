package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Transaction;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MCRDocDetailsTag extends SimpleTagSupport {
	private static final long serialVersionUID = 1L;
	private static DocumentBuilder builder;
	private String mcrID;
	private boolean fromWorkflow;
	private String lang;
	private Document doc;
	private int previousOutput;
	private ResourceBundle messages;
	private String stylePrimaryName="docdetails";
	
	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			builder = null;
		}
	}

	public void setMcrID(String mcrID) {
		this.mcrID = mcrID;
	}

	public void setFromWorkflow(boolean fromWorkflow) {
		this.fromWorkflow = fromWorkflow;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void doTag() throws JspException, IOException {
		Transaction tx  = MCRHIBConnection.instance().getSession().beginTransaction();
		if (lang == null || lang.equals("")) {
			lang = "de";
		}
		try{
			messages = PropertyResourceBundle.getBundle("messages",	new Locale(lang), MCRConfiguration.class.getClassLoader());
			byte[] data = new byte[0];
			if (fromWorkflow) {
				String[] mcridParts = mcrID.split("_");
				String savedir = MCRWorkflowDirectoryManager.getWorkflowDirectory(mcridParts[1]);
				String filename = savedir + "/" + mcrID + ".xml";
				//DEVELOPMENT MODE
				filename="C:\\workspaces\\cpr\\projects\\docdetails\\WebContent\\data\\"+mcrID+".xml";
				File file = new File(filename);
				
				if (file.isFile()) {
					data=getBytesFromFile(file);
				}
			} else {
				data = MCRObject.receiveXMLFromDatastore(mcrID);
			}
			doc = builder.parse(new ByteArrayInputStream(data));
			
		} catch (IOException e) {
			throw new JspException(e);
		} catch (SAXException e) {
			throw new JspException(e);
		}
		JspWriter out = getJspContext().getOut();
		out.write("<table class=\""+getStylePrimaryName()+"-table\" width=\"95%\" cellpadding=\"0\" cellspacing=\"0\">\n");
		out.write("<tbody>\n");
		
		getJspBody().invoke(out);
		
		out.write("</tbody>\n");
		out.write("</table>\n");
		tx.commit();
	}

	public Document getXMLDocument() {
		return doc;
	}

	public String getLang() {
		return lang;
	}

	protected void setPreviousOutput(int previousOutput) {
		this.previousOutput = previousOutput;
	}

	protected int getPreviousOutput() {
		return previousOutput;
	}

	protected ResourceBundle getMessages() {
		return messages;
	}


	/**
	 * implementation found in "The Java Developers Almanac 1.4"
	 * http://www.exampledepot.com/egs/java.io/File2ByteArray.html
	 * 
	 * @param file
	 *            the file
	 * @return the file content as byte array
	 * @throws IOException
	 */
	private static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			return new byte[0];
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		is.close();
		return bytes;
	}

	public String getStylePrimaryName() {
		return stylePrimaryName;
	}

	public void setStylePrimaryName(String stylePrimaryName) {
		this.stylePrimaryName = stylePrimaryName;
	}
}
