package org.mycore.frontend.restapi.v1.errors;


public class MCRRestAPIFieldError {
    private String field;
    private String message;
    
    public MCRRestAPIFieldError(String field, String message){
        this.message = message;
        this.field = field;
    }
    
    public static MCRRestAPIFieldError create(String field, String message){
        return new MCRRestAPIFieldError(field, message);
    }
    
    
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
