package org.mycore.frontend.jsp.stripes.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.tools.gvkmods.GVKMODSImport;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

@UrlBinding("/importMODSFromGVK.action")
public class ImportMODSFromGVKAction implements ActionBean {
	ForwardResolution fwdResolution = new ForwardResolution("/content/workspace/import/import-mods-from-gvk.jsp");
	private ActionBeanContext context;

	private String returnPath = "";
	private String mcrID = "";
	private String gvkPPN = "";
	private String modsXML = "";

	public ActionBeanContext getContext() {
		return context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}

	public ImportMODSFromGVKAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		if (getContext().getRequest().getParameter("mcrid") != null) {
			mcrID = getContext().getRequest().getParameter("mcrid");
			if (gvkPPN.equals("")) {
				findGVKPPN();
			}
		}
		if (getContext().getRequest().getParameter("returnPath") != null) {
			returnPath = getContext().getRequest().getParameter("returnPath");
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
		return fwdResolution;
	}

	public Resolution doRetrieve() {
		if (gvkPPN != null) {
			Element eMODS = GVKMODSImport.retrieveMODS(gvkPPN);
			if (eMODS != null) {
				XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
				modsXML = outputter.outputString(eMODS);
			}
		}
		return fwdResolution;
	}

	public Resolution doSave() {
		if (!mcrID.equals("")) {
			File savedir = MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrID));
			File file = new File(savedir, mcrID + ".xml");
			try {
				SAXBuilder sb = new SAXBuilder();
				Document docJdom = sb.build(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				Element eMeta = docJdom.getRootElement().getChild("metadata");
				if (eMeta != null) {
					Element eDefMods = eMeta.getChild("def.modsContainer");
					if (eDefMods == null) {
						eDefMods = new Element("def.modsContainer");
						eMeta.addContent(0, eDefMods);
						eDefMods.setAttribute("class", "MCRMetaXML");
					}
					eDefMods.removeContent();
					Element eMods = new Element("modsContainer");
					eDefMods.addContent(eMods);
					Element eModsData = sb.build(new StringReader(modsXML)).getRootElement();
					eMods.addContent(eModsData.detach());
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
				xout.output(docJdom, bw);
			} catch (JDOMException jdome) {
				// do nothing
			} catch (IOException e) {
				// do nothing
			}
		}

		return new ForwardResolution(returnPath);
	}

	public Resolution doCancel() {
		return new ForwardResolution(returnPath);
	}

	public String getMcrID() {
		return mcrID;
	}

	public void setMcrID(String mcrid) {
		this.mcrID = mcrid;
	}

	public String getGvkPPN() {
		return gvkPPN;
	}

	public void setGvkPPN(String gvkppn) {
		this.gvkPPN = gvkppn;
	}

	public String getModsXML() {
		return modsXML;
	}

	public void setModsXML(String modsXML) {
		this.modsXML = modsXML;
	}

	public String getReturnPath() {
		return returnPath;
	}

	public void setReturnPath(String returnPath) {
		this.returnPath = returnPath;
	}

	private void findGVKPPN() {
		if (!mcrID.equals("")) {
			File savedir = MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrID));
			File file = new File(savedir, mcrID + ".xml");
			try {
				SAXBuilder sb = new SAXBuilder();
				Document docJdom = sb.build(new InputStreamReader(new FileInputStream(file), "UTF-8"));

				// <identifier type="gvk-ppn">721494285</identifier>>
				Namespace nsMODS = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");

				Element e = XPathFactory.instance().compile("/mods:identifier[@type='gvk-ppn']", Filters.element(), null, nsMODS).evaluateFirst(docJdom);
				if (e != null) {
					setGvkPPN(e.getTextNormalize());
				}
			} catch (JDOMException jdome) {
				// do nothing
			} catch (IOException e) {
				// do nothing
			}
		}
	}
}
