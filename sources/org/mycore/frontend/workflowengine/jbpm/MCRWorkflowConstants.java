package org.mycore.frontend.workflowengine.jbpm;

public class MCRWorkflowConstants {
	public static final Integer KEY_IDENTIFER_TYPE_URN = new Integer(1);
	public static final int INIT_PROCESS_CREATE = 1;
	public static final int INIT_PROCESS_UPDATE = 2;
	public static final int INIT_PROCESS_DELETE = 3;

	public static final int PERMISSION_MODE_DEFAULT = 1;
	public static final int PERMISSION_MODE_PUBLISH = 2;
	public static final int PERMISSION_MODE_EDITING = 3;
	public static final int PERMISSION_MODE_CREATORRREAD = 4;
	
	public static final String ACCESSRULE_PREFIX = "APPRULE";
	public static final String ACCESSRULE_NUMBERFORMAT="0000000000";
	
	public static final String WFM_VAR_SIGNED_AFFIRMATION_AVAILABLE = "signedAffirmationAvailable";
	public static final String WFM_VAR_INITIATOR = "initiator";
	public static final String WFM_VAR_AUTHOR_IDS = "authorIds";
	public static final String WFM_VAR_INITIATORSALUTATION = "initiatorSalutation";
	public static final String WFM_VAR_INITIATOREMAIL = "initiatorEmail";
	public static final String WFM_VAR_RESERVATED_URN = "reservatedURN";
	public static final String WFM_VAR_METADATA_OBJECT_IDS = "createdDocID";
	public static final String WFM_VAR_ATTACHED_DERIVATES = "attachedDerivates";
	public static final String WFM_VAR_DELETED_DERIVATES = "deletedDerivates";
	public static final String WFM_VAR_DELETED_FILES_IN_DERIVATES = "deletedFiles";
	public static final String WFM_VAR_BOOL_TEMPORARY_IN_DATABASE = "temporaryInDatabase";
	public static final String WFM_VAR_END_OF_SUSPENSION="endOfSuspension";
	
    public static final String WFM_VAR_ERROR = "varnameERROR";
    public static final String WFM_VAR_HINT = "hint";  
    
	public static final String WFM_VAR_CONTAINS_PDF = "containsPDF";
	public static final String WFM_VAR_CONTAINS_ZIP = "containsZIP";
	public static final String WFM_VAR_WFOBJECT_TITLE= "wfo-title";
	public static final String WFM_VAR_METADATA_PUBLICATIONTYPE ="publicationType";
	public static final String WFM_VAR_CREATED ="ProcessCreatedTimestamp";
	public static final String WFM_VAR_FILECNT ="fileCnt";
	
	public static final String WFM_VAR_READRULE_TYPE="readrule-type";
	public static final String WFM_VAR_READRULE_XMLSTRING="readrule-xmlstring";
	
    //used in "bean-style" in dissertationData.jsp
	public String getAuthorIdWorkflowVariable(){ 	 
        return WFM_VAR_AUTHOR_IDS; 	 
	} 	 

    public String getReservatedUrnWorkflowVariable(){ 	 
        return WFM_VAR_RESERVATED_URN; 	 
    }
    public String getObjectID(){ 	 
        return WFM_VAR_METADATA_OBJECT_IDS; 	 
    }

}
