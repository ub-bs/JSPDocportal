/*
 * $RCSfile$
 * $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan 2010) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */

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
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Transaction;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * part of the MCRDocdetails Tag Library
 * 
 * provides the outer tag, which retrieves the document from database or
 * workflow
 * 
 * If a request parameter debug=true is found, the xml syntax of the 
 * MCR object will be displayed on the website
 * 
 * @author Robert Stephan
 * 
 * @version $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan
 *          2010) $
 */
public class MCRDocDetailsTag extends SimpleTagSupport {
	private static final long serialVersionUID = 1L;
	private static DocumentBuilder builder;

	private String mcrID;
	private boolean fromWorkflow = false;
	private String lang="de";
	private String var;
	private String stylePrimaryName = "docdetails";
	
	private Document doc;
	private int previousOutput;
	private ResourceBundle messages;


	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			builder = null;
		}
	}

	/**
	 * sets the MCR Object ID (mandatory)
	 * 
	 * @param mcrID
	 *            the MCR object ID
	 * 
	 */
	public void setMcrID(String mcrID) {
		this.mcrID = mcrID;
	}

	/**
	 * if set to true, the MCR object is retrieved from workflow directory and
	 * not from database (mandatory)
	 * 
	 * @param mcrID
	 *            the MCR object ID
	 * 
	 */
	public void setFromWorkflow(boolean fromWorkflow) {
		this.fromWorkflow = fromWorkflow;
	}

	/**
	 * sets the ISO 639 2-letter Language Codes (Obsolete) (defaults to "de")
	 * 
	 * used when retrieving labels and messages from ressource bundle
	 * 
	 * @param lang -
	 *            the language as 2-letter ISO 639 Code
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * sets the name of the variable in which to store the XML Document as
	 * org.w3c.dom.Node for further use in processing the JSP 
	 * (stored in request scope)
	 * 
	 * @param var -
	 *            the name of the variable
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * sets the prefix for CSS-styles names to be used
	 * (defaults to "docdetails")
	 * 
	 * @param stylePrimaryName -
	 *            the CSS style name prefix
	 */
	public void setStylePrimaryName(String stylePrimaryName) {
		this.stylePrimaryName = stylePrimaryName;
	}

	/**
	 * executes the tag
	 */
	public void doTag() throws JspException, IOException {
		boolean isRoot = findAncestorWithClass(this, MCRDocDetailsTag.class) == null;
		Transaction tx = null;
		if (isRoot) {
			tx = MCRHIBConnection.instance().getSession().beginTransaction();
		}
		byte[] data = new byte[0];
		try {
			messages = PropertyResourceBundle.getBundle("messages", new Locale(lang), MCRConfiguration.class.getClassLoader());

			if (fromWorkflow) {
				String[] mcridParts = mcrID.split("_");
				String savedir = MCRWorkflowDirectoryManager.getWorkflowDirectory(mcridParts[1]);
				String filename = savedir + "/" + mcrID + ".xml";
				File file = new File(filename);
				if (file.isFile()) {
					data = getBytesFromFile(file);
				}
			} else {
				data = MCRObject.receiveXMLFromDatastore(mcrID);
			}
			doc = builder.parse(new ByteArrayInputStream(data));
			if (var != null) {
				getJspContext().setAttribute(var, doc, PageContext.REQUEST_SCOPE);
			}

		} catch (IOException e) {
			throw new JspException(e);
		} catch (SAXException e) {
			throw new JspException(e);
		}
		JspWriter out = getJspContext().getOut();
		PageContext pageContext = (PageContext) getJspContext();

		// DEBUG mode: output xml data as text
		if (pageContext.getRequest().getParameter("debug") != null && pageContext.getRequest().getParameter("debug").equals("true")) {
			SAXBuilder sb = new SAXBuilder();
			try {
				org.jdom.Document jdom = sb.build(new ByteArrayInputStream(data));
				StringBuffer debugSB = new StringBuffer("<textarea cols=\"120\" rows=\"30\">").append("MCRObject:\r\n").append(JSPUtils.getPrettyString(jdom))
						.append("</textarea>");
				out.println(debugSB.toString());
			} catch (JDOMException e) {
				// do nothing
			}
		}

		out.write("<table class=\"" + getStylePrimaryName() + "-table\" width=\"95%\" cellpadding=\"0\" cellspacing=\"0\">\n");
		out.write("<tbody>\n");

		getJspBody().invoke(out);

		out.write("</tbody>\n");
		out.write("</table>\n");
	
		if (isRoot) {
			tx.commit();
		}
	}

	/**
	 * returns the context node.
	 * for this tag it is always the document node
	 * @return the document as org.w3c.dom.Node
	 * 
	 * @throws JspTagException
	 */
	public org.w3c.dom.Node getContext() throws JspTagException {
		// expose the current node as the context
		return doc;
	}

	/**
	 * returns the current language used in resource bundles
	 * @return the language as 2-letter ISO 639 Code
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * @return the prefix for CSS-style names
	 */
	public String getStylePrimaryName() {
		return stylePrimaryName;
	}

	/**
	 * sets the number of previous outputs
	 * needed to calculate whether a separator line shall be displayed 
	 * (avoids 2 separator lines next to each other due to missing content between them)
	 * 
	 * @param previousOutput the number of actually displayed rows
	 */
	protected void setPreviousOutput(int previousOutput) {
		this.previousOutput = previousOutput;
	}

	/**
	 * returns the number of previous outputs
	 * needed to calculate whether a separator line shall be displayed 
	 * (avoids 2 separator lines next to each other due to missing content between them)
	 * 
	 * @return the number of actually displayed rows
	 */
	protected int getPreviousOutput() {
		return previousOutput;
	}

	/**
	 * @return the resource bundle for messages, labels, etc.
	 */
	protected ResourceBundle getMessages() {
		return messages;
	}

	/**
	 * used to read a MCR object from workflow directory
	 * 
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
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}
}