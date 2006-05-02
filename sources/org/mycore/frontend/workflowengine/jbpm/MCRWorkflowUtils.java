/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.util.ArrayList;


/**
 * This class holds useful methods for the workflow.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 * 
 */
public class MCRWorkflowUtils {

	/**
	 * The method return a ArrayList of file names from objects of a special type in the workflow
	 * 
	 * @param  type the MCRObjectID type attribute
	 * @return an ArrayList of file names
	 */
	public final static ArrayList getAllObjectFileNames(String type) {
		String dirname = MCRWorkflowEngineManagerFactory.getDefaultImpl().getWorkflowDirectory(type);
		ArrayList workfiles = new ArrayList();
		if (!dirname.equals(".")) {
			File dir = new File(dirname);
			String[] dirl = null;
			if (dir.isDirectory()) {
				dirl = dir.list();
			}
			if (dirl != null) {
				for (int i = 0; i < dirl.length; i++) {
					if ((dirl[i].indexOf(type) != -1)
							&& (dirl[i].endsWith(".xml"))) {
						workfiles.add(dirl[i]);
					}
				}
			}
			java.util.Collections.sort(workfiles);
		}
		return workfiles;
	}	
	
	/**
	 * The method return a ArrayList of file names from derivates 
	 * 				from objects of a special type in the workflow
	 * 
	 * @param type
	 *            the MCRObjectID type attribute
	 * @return an ArrayList of file names
	 */
	public final static ArrayList getAllDerivateFileNames(String type) {
		String dirname = MCRWorkflowEngineManagerFactory.getDefaultImpl().getWorkflowDirectory(type);
		ArrayList workfiles = new ArrayList();
		if (!dirname.equals(".")) {
			File dir = new File(dirname);
			String[] dirl = null;
			if (dir.isDirectory()) {
				dirl = dir.list();
			}
			if (dirl != null) {
				for (int i = 0; i < dirl.length; i++) {
					if ((dirl[i].indexOf("_derivate_") != -1)
							&& (dirl[i].endsWith(".xml"))) {
						workfiles.add(dirl[i]);
					}
				}
			}
			java.util.Collections.sort(workfiles);
		}
		return workfiles;
	}	
}
