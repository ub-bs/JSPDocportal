package org.mycore.frontend.restapi.v1.utils;

public class MCRRestAPISortObject {
    enum SortOrder{
        ASC, DESC
    }
    private String field;
    private SortOrder order;
    
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public SortOrder getOrder() {
        return order;
    }
    public void setOrder(SortOrder order) {
        this.order = order;
    }
    
    
}
