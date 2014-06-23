package org.mycore.activiti;

import java.io.File;
import java.io.FileOutputStream;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * provides some static utility methods
 * 
 * @author Robert Stephan
 * 
 */
public class MCRActivitiUtils {
	/**
	 * saves a given MCR object into the workflow directory
	 * @param MCRObject
	 */
	public static void saveToWorkflowDirectory(MCRObject mcrObj) {
		File dir = getWorkflowDirectory(mcrObj.getId());
		File fOut = new File(dir, mcrObj.getId().toString() + ".xml");
		try (FileOutputStream fos = new FileOutputStream(fOut)) {
			XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
			xmlOut.output(mcrObj.createXML(), fos);
		} catch (Exception ex) {
			throw new MCRException("Cant save MCR Object "
					+ mcrObj.getId().toString() + " into workfow directory "
					+ dir.getName());
		}
	}

	public static File getWorkflowDirectory(MCRObjectID mcrObjID) {
		String s = MCRConfiguration.instance().getString("MCR.Workflow.WorkflowDirectory");
		File f = new File(s);
		f = new File(f, mcrObjID.getTypeId());
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

}
