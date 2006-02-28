package org.mycore.frontend.workflow;

public class MCRWFObject {
	
		private String sUserID;
		private String sDocID;
		private String sAuthorID;
		private String sUrn;
		private String sStatus;
		private String sWFObjectType;
		boolean bInitialized=false;

		// getter und setter für das Object 
		public String getSAuthorID() {
			return sAuthorID;
		}
		public String getSDocID() {
			return sDocID;
		}
		public String getSStatus() {
			return sStatus;
		}
		public String getSUrn() {
			return sUrn;
		}
		public String getSUserID() {
			return sUserID;
		}
		public String getSWFObjectType() {
			return sWFObjectType;
		}
		public void setSAuthorID(String authorID) {
			sAuthorID = authorID;
		}
		public void setSDocID(String docID) {
			sDocID = docID;
		}
		public void setSStatus(String status) {
			sStatus = status;
		}
		public void setSUrn(String urn) {
			sUrn = urn;
		}
		public void setSUserID(String userID) {
			sUserID = userID;
		} 
		public void setSWFObjectType(String objectType) {
			sWFObjectType = objectType;
		}

		protected MCRWFObject(String sWFType) {
			sAuthorID="";
			sDocID="";
			sStatus="";
			sUrn="";
			sUserID="";
			sWFObjectType=sWFType;			
		}
		
		protected boolean hasAuthor(String userID) {
			if ( !bInitialized ) 
				getWFObject(userID);		
		    return ( sAuthorID.length()>0);			
		}

		protected boolean hasUrn(String userID) {
			if ( !bInitialized ) 
				getWFObject(userID);		
		    return ( sUrn.length()>0);			
		}
		protected boolean hasDocID(String userID) {
			if ( !bInitialized ) 
				getWFObject(userID);		
			return ( sDocID.length()>0 );
		}

		protected boolean setWFObject() {
			// im jpbm das Workflowtag setzen
			// update .... set sAuthorID=AuthorID;	sDocID=DocID;	sStatus=Status;	sUrn=Urn;sUserID=UserID;
			return true;			
		}
			
		protected MCRWFObject getWFObject(String userID) {
			// aus jbpm alles holen und Object initialisieren
			// select * from wf... wher userID=sowieso and objType=sWfType 
			sAuthorID="";
			sDocID="";
			sStatus="";
			sUrn="";
			sUserID=userID;			
			bInitialized=true;			
			return this;
		}
	}