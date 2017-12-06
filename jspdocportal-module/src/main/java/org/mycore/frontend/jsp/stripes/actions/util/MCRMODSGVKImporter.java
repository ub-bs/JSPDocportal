package org.mycore.frontend.jsp.stripes.actions.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.MCRConstants;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObjectID;

public class MCRMODSGVKImporter {
    private static Logger LOGGER = LogManager.getLogger(MCRMODSGVKImporter.class);

    private static XPathExpression<Element> XP_URN = XPathFactory.instance().compile("//mods:identifier[@type='urn']",
        Filters.element(), null, MCRConstants.MODS_NAMESPACE);

    private static XPathExpression<Element> XP_MODS_ROOT = XPathFactory.instance().compile("//*[./mods:mods]",
        Filters.element(), null, MCRConstants.MODS_NAMESPACE);

    public static void updateWorkflowFile(MCRObjectID mcrObjID) {
       Path mcrFile = MCRActivitiUtils.getWorkflowObjectFile(mcrObjID);
    	try {
        	Document docJdom = MCRActivitiUtils.getWorkflowObjectXML(mcrObjID);
        	Element eModsContainer = XP_MODS_ROOT.evaluateFirst(docJdom);
            Element eURN = XP_URN.evaluateFirst(docJdom);
            if (eModsContainer != null && eURN != null) {
                Element ePica = retrievePicaXMLByURN(eURN.getTextTrim());
                if (ePica != null) {
                    Element eMODS = transformPica2MODS(ePica);
                    if (eMODS != null) {
                        eModsContainer.removeContent();
                        eModsContainer.addContent(eMODS.detach());
                        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                        try(BufferedWriter bw = Files.newBufferedWriter(mcrFile)){
                        	outputter.output(docJdom, bw);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }

    }

    private static Element transformPica2MODS(Element ePica) {
        JDOMResult modsResult = new JDOMResult();
        try {
            Source xmlFile = new JDOMSource(ePica);
            Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(MCRMODSGVKImporter.class
                    .getResourceAsStream(MCRConfiguration.instance().getString("MCR.Editor.Pica2MODS.xslt"))));
            transformer.transform(xmlFile, modsResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modsResult.getDocument().getRootElement();
    }

    private static Element retrievePicaXMLByURN(String urn) {
        Element e = null;
        try {
            String sruBaseURL = MCRConfiguration.instance().getString("MCR.Editor.Pica2MODS.sru-url");
            URL urlSRU = new URL(sruBaseURL + "&recordSchema=picaxml&query=pica.urn%3D" + urn);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlSRU.openStream(), "UTF-8"));

            Document docJdom = new SAXBuilder().build(br, "UTF-8");
            br.close();

            /*<zs:searchRetrieveResponse xmlns:zs="http://www.loc.gov/zing/srw/">
                <zs:version>1.1</zs:version>
                <zs:numberOfRecords>1</zs:numberOfRecords>
                <zs:records>
                    <zs:record>
                        <zs:recordSchema>picaxml</zs:recordSchema>
                        <zs:recordPacking>xml</zs:recordPacking>
                        <zs:recordData>
                            <record xmlns="info:srw/schema/5/picaXML-v1.0">
            */
            Namespace nsZS = Namespace.getNamespace("zs", "http://www.loc.gov/zing/srw/");
            e = ((List<Element>) docJdom.getRootElement().getChild("records", nsZS).getChild("record", nsZS)
                .getChild("recordData", nsZS).getChildren()).get(0);
            e = (Element) e.detach();
        } catch (MalformedURLException mfex) {
            //ignore
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return e;
    }
}
