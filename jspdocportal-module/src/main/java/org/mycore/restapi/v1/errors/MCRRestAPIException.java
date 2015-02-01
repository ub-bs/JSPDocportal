package org.mycore.restapi.v1.errors;


public class MCRRestAPIException extends Exception{

    private static final long serialVersionUID = 1L;
    
    private MCRRestAPIError error;
    
    public MCRRestAPIException(MCRRestAPIError error){
        this.error = error;
    }

    public MCRRestAPIError getError() {
        return error;
    }

}
