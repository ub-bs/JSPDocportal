package org.mycore.frontend.restapi.v1.errors;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.stream.JsonWriter;

public class MCRRestAPIError {
    Response.Status status = Response.Status.BAD_REQUEST;
    String message = "";
    String details = null;
    List<MCRRestAPIFieldError> errors = new Vector<MCRRestAPIFieldError>();

    public MCRRestAPIError(Response.Status status, String message, String details){
        this.status = status;
        this.message = message;
        this.details = details;
    }
    
    public static MCRRestAPIError create(Response.Status status, String message, String details){
        return new MCRRestAPIError(status,  message,  details);
    }
    
    public void addFieldError(MCRRestAPIFieldError error){
        errors.add(error);
    }
    
    public String toJSONString() {
        StringWriter sw = new StringWriter();
        try {
            JsonWriter writer = new JsonWriter(sw);
            writer.setIndent("    ");
            writer.beginObject();
            writer.name("stats").value(status.getStatusCode());
            writer.name("message").value(message);
            if(details!=null){
                writer.name("details").value(details);
            }
            if (errors.size() > 0) {
                writer.name("errors");
                writer.beginArray();
                for (MCRRestAPIFieldError err : errors) {
                    writer.beginObject();
                    writer.name("field").value(err.getField());
                    writer.name("message").value(err.getMessage());
                    writer.endObject();
                }
                writer.endArray();
            }

            writer.endObject();
            writer.close();
        } catch (IOException e) {
            //should not happen;
        }

        return sw.toString();
    }
    
    public Response createHttpResponse(){
        return Response.status(status).type(MediaType.APPLICATION_JSON_TYPE).entity(toJSONString()).build();
    }
}
