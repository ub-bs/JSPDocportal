package org.mycore.common;

import java.io.File;
import java.io.FilenameFilter;


public class MCRDerivateFileFilter implements FilenameFilter{

	public boolean accept(File dir, String name) {
		if(name.indexOf("_derivate_") > -1){
			return true;
		}
		return false;
	}

}
