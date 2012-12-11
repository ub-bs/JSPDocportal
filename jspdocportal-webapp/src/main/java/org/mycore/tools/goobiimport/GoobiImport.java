package org.mycore.tools.goobiimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import net.sf.antcontrib.net.httpclient.GetCookieTask;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class GoobiImport {
    private static String SRU_URL = "http://sru.gbv.de/opac-de-28?version=1.1&operation=searchRetrieve&maximumRecords=1";
    

    public static Element retrieveMODS(String goobiFolderName) {
        //<mods xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        //      xmlns="http://www.loc.gov/mods/v3" version="3.4" 
        //      xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd">
        Namespace nsMODS = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
        Namespace nsXSI = Namespace.getNamespace("xsi", "http://www.w3.org/2000/10/XMLSchema-instance");
        Namespace nsZS = Namespace.getNamespace("zs", "http://www.loc.gov/zing/srw/");
        
        String ppn = goobiFolderName.split("_")[1];    
        Element eMODS = null;
        try{
    
            URL urlSRU = new URL(SRU_URL + "&recordSchema=mods&query=pica.ppn%3D"+ ppn);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlSRU.openStream(), "UTF-8"));

            SAXBuilder sb = new SAXBuilder(false);
            Document docJdom = sb.build(br, "UTF-8");
            br.close();            
            
            @SuppressWarnings("unchecked")
            Element e = ((List<Element>)docJdom.getRootElement().getChild("records", nsZS).getChild("record", nsZS).getChild("recordData", nsZS).getChildren()).get(0);
            eMODS = (Element)e.detach();
            XPath xpMods = XPath.newInstance("//*[namespace-uri()='http://www.loc.gov/mods/v3']");
            List l = xpMods.selectNodes(eMODS);
            for(Object o: l){
                if(o instanceof Element){
                    ((Element)o).setNamespace(nsMODS);
                }
            }            
        }       
        
        catch(IOException e){
            Logger.getLogger(GoobiImport.class).error("Error retrieving MODS from GVK", e);
        }
        catch(JDOMException e){
            Logger.getLogger(GoobiImport.class).error("Error parsing XML from MODS", e);
        }
        
       /*
            Element eMODS = new Element("mods", nsMODS);
            eMODS.setAttribute("version", "3.4");
            eMODS.setAttribute("schemalocation", "http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd", nsXSI);
         */
        if(eMODS != null){
            //<identifier type="gvk-ppn">721494285</identifier>>
            Element eIdentifier = new Element("identifier", nsMODS);
            eIdentifier.setAttribute("type", "gvk-ppn");
            eIdentifier.setText(ppn);
            eMODS.addContent(eIdentifier);
        
            Element ePica = retrievePicaXML(ppn);
        
            Element eExtension = new Element("extention", nsMODS);
            eExtension.setAttribute("displayLabel", "PicaXML for Validation");
            eExtension.addContent(ePica);
            eMODS.addContent(eExtension);
        }  
        

        return eMODS;
    }

    @SuppressWarnings("unchecked")
    public static Element retrievePicaXML(String ppn) {
        Element e = null;
        try {
            URL urlSRU = new URL(SRU_URL + "&recordSchema=picaxml&query=pica.ppn%3D"+ ppn);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlSRU.openStream(), "UTF-8"));

            SAXBuilder sb = new SAXBuilder(false);
            Document docJdom = sb.build(br, "UTF-8");
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
            e = ((List<Element>)docJdom.getRootElement().getChild("records", nsZS).getChild("record", nsZS).getChild("recordData", nsZS).getChildren()).get(0);
            e = (Element)e.detach();
            System.out.println(e.getName());
        } catch (MalformedURLException mfex) {
            //ignore
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return e;

    }

    public static void main(String[] args) {
        String ppn = "585909008";

        Element eMODs = retrieveMODS(ppn);
        if (eMODs != null) {
            Document doc = new Document();
            doc.addContent(eMODs);

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = outputter.outputString(doc);
            System.out.println(xmlString);
        }

    }

}
