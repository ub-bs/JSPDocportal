package org.mycore.frontend.editor;

import org.apache.tools.ant.taskdefs.Concat;
import org.apache.xpath.NodeSet;
import org.mycore.common.config.MCRConfiguration;
import org.w3c.dom.NodeList;

public class MCREditorFunctions {
	public static String propertiesToOption(String propertyPrefix){
		NodeSet result = new NodeSet();
		String keys = MCRConfiguration.instance().getString(propertyPrefix, "");
		if(!keys.equals("")){
			for(String k : keys.split(",")){
				k = k.trim();
			
			}
		}
		return "<option value=\"1\">Eins</option><option value=\"2\">Zwei</option>";
	}
}
