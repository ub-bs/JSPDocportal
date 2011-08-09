package org.mycore.frontend.workflowengine.jbpm;

import java.io.Serializable;

/**
 * This Class is a wrapper for a string
 * It is used to store a long String as workflow variable.
 * JBPM only allowes value length of 255 for String (see definition of MySQL table).
 * By storing an object as variable we can avoid this issue.
 * 
 * @author Robert Stephan
 *
 */
public class MCRWorkflowLargeStringObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private String data;
	public MCRWorkflowLargeStringObject(String s){
		data = s;
	}
	public String getData(){
		return data;
	}
	
	public void setData(String s){
		data =s;
	}
	
	public String toString(){
		return data;
	}	
}
