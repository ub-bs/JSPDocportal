package org.mycore.frontend.jsp.stripes.actions.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.MCRConstants;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObjectID;

public class MCRMODSGVKImporter {
    private static Logger LOGGER = LogManager.getLogger(MCRMODSGVKImporter.class);

    private static MCRMODSCatalogService modsCatService = MCRConfiguration.instance()
        .getInstanceOf("MCR.Workflow.MODSCatalogueService.class");

    private static XPathExpression<Element> XP_URN = XPathFactory.instance().compile(
        "//mods:mods/mods:identifier[@type='urn']",
        Filters.element(), null, MCRConstants.MODS_NAMESPACE);

    private static XPathExpression<Element> XP_PPN = XPathFactory.instance().compile(
        "//mods:mods/mods:identifier[@type='PPN']",
        Filters.element(), null, MCRConstants.MODS_NAMESPACE);

    private static XPathExpression<Element> XP_RECORD_ID = XPathFactory.instance().compile(
        "//mods:mods/mods:recordInfo/mods:recordIdentifier",
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
                Element eMODS = retrieveMODSByURN(eURN.getTextTrim());
                if (eMODS != null) {
                    updateWorkflowMetadataFile(mcrFile, docJdom, eModsContainer, eMODS);
                    return;
                }
            }

            Element eRecordInfo = XP_RECORD_ID.evaluateFirst(docJdom);
            if (eModsContainer != null && eRecordInfo != null) {
                Element eMODS = retrieveMODSByPURL(eRecordInfo.getTextTrim());
                if (eMODS != null) {
                    updateWorkflowMetadataFile(mcrFile, docJdom, eModsContainer, eMODS);
                    return;
                }
            }

            Element ePPN = XP_PPN.evaluateFirst(docJdom);
            if (eModsContainer != null && ePPN != null) {
                Element eMODS = retrieveMODSByPPN(ePPN.getTextTrim());
                if (eMODS != null) {
                    updateWorkflowMetadataFile(mcrFile, docJdom, eModsContainer, eMODS);
                    return;
                }
            }

            if (eModsContainer != null) {
                Element eMODS = retrieveMODSByMyCoReID(mcrObjID.toString());
                if (eMODS != null) {
                    updateWorkflowMetadataFile(mcrFile, docJdom, eModsContainer, eMODS);
                    return;
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static void updateWorkflowMetadataFile(Path mcrFile, Document docJdom, Element eModsContainer, Element eMODS)
        throws IOException {
        eModsContainer.removeContent();
        eModsContainer.addContent(eMODS.detach());
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try (BufferedWriter bw = Files.newBufferedWriter(mcrFile)) {
            outputter.output(docJdom, bw);
        }
    }

    private static Element retrieveMODSByURN(String urn) {
        String query = "pica.urn=" + urn;
        return modsCatService.retrieveMODSFromCatalogue(query);
    }

    private static Element retrieveMODSByPURL(String recordIdentifer) {
        String query = "pica.url=purl*" + recordIdentifer.replace("/", "");
        return modsCatService.retrieveMODSFromCatalogue(query);
    }

    private static Element retrieveMODSByMyCoReID(String mcrID) {
        String query = "pica.url=rosdok*resolveid" + mcrID.replace("_", "");
        return modsCatService.retrieveMODSFromCatalogue(query);
    }

    private static Element retrieveMODSByPPN(String ppn) {
        String query = "pica.ppn=" + ppn;
        return modsCatService.retrieveMODSFromCatalogue(query);
    }
}
