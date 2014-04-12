package org.mycore.frontend.restapi.v1.utils;

import java.util.Comparator;

import org.mycore.datamodel.common.MCRObjectIDDate;
import org.mycore.frontend.restapi.v1.utils.MCRRestAPISortObject.SortOrder;

public class MCRRestAPISortObjectComparator implements Comparator<MCRObjectIDDate> {
	private MCRRestAPISortObject _sortObj = null;
	public MCRRestAPISortObjectComparator(MCRRestAPISortObject sortObj){
		_sortObj = sortObj;		
	}
	@Override
    public int compare(MCRObjectIDDate o1, MCRObjectIDDate o2) {
		if("id".equals(_sortObj.getField().toLowerCase())){
			if(_sortObj.getOrder() == SortOrder.ASC){
				return o1.getId().compareTo(o2.getId());
			}
			if(_sortObj.getOrder() == SortOrder.DESC){
				return o2.getId().compareTo(o1.getId());
			}
		}
		if("lastmodified".equals(_sortObj.getField().toLowerCase())){
			if(_sortObj.getOrder() == SortOrder.ASC){
				return o1.getLastModified().compareTo(o2.getLastModified());
			}
			if(_sortObj.getOrder() == SortOrder.DESC){
				return o2.getLastModified().compareTo(o1.getLastModified());
			}
		}
		
        return 0;
    }
}