package org.mycore.frontend.restapi.v1.utils;

import java.util.Comparator;

import org.mycore.datamodel.common.MCRObjectIDDate;

public class MCRRestAPISortFieldComparator implements Comparator<MCRObjectIDDate> {
	private String _sortField = null;
	private String _sortOrder = null;
	public MCRRestAPISortFieldComparator(String sortField, String sortOrder){
		_sortField = sortField.toLowerCase();
		_sortOrder = sortOrder.toLowerCase();
	}
	@Override
    public int compare(MCRObjectIDDate o1, MCRObjectIDDate o2) {
		if("id".equals(_sortField)){
			if("asc".equals(_sortOrder)){
				return o1.getId().compareTo(o2.getId());
			}
			if("desc".equals(_sortOrder)){
				return o2.getId().compareTo(o1.getId());
			}
		}
		if("lastmodified".equals(_sortField)){
			if("asc".equals(_sortOrder)){
				return o1.getLastModified().compareTo(o2.getLastModified());
			}
			if("desc".equals(_sortOrder)){
				return o2.getLastModified().compareTo(o1.getLastModified());
			}
		}
		
        return 0;
    }
}