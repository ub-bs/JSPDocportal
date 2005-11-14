package org.mycore.backend.hibernate.tables;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class MCRRESUMPTIONTOKEN_HIT implements Serializable {

    /** identifier field */
    private long hitID;

    /** persistent field */
    private long hitNr;

    /** nullable persistent field */
    private String mcrobjID;

    /** nullable persistent field */
    private String oaiID;

    /** nullable persistent field */
    private String spec;

    /** nullable persistent field */
    private String specName;

    /** nullable persistent field */
    private String specDescription;

    /** nullable persistent field */
    private String datestamp;

    /** persistent field */
    private org.mycore.backend.hibernate.tables.MCRRESUMPTIONTOKEN resumptionToken;

    /** full constructor */
    public MCRRESUMPTIONTOKEN_HIT(long hitNr, String mcrobjID, String oaiID, String spec, String specName, String specDescription, String datestamp, org.mycore.backend.hibernate.tables.MCRRESUMPTIONTOKEN resumptionToken) {
        this.hitNr = hitNr;
        this.mcrobjID = mcrobjID;
        this.oaiID = oaiID;
        this.spec = spec;
        this.specName = specName;
        this.specDescription = specDescription;
        this.datestamp = datestamp;
        this.resumptionToken = resumptionToken;
    }

    /** default constructor */
    public MCRRESUMPTIONTOKEN_HIT() {
    }

    /** minimal constructor */
    public MCRRESUMPTIONTOKEN_HIT(long hitNr, org.mycore.backend.hibernate.tables.MCRRESUMPTIONTOKEN resumptionToken) {
        this.hitNr = hitNr;
        this.resumptionToken = resumptionToken;
    }

    public long getHitID() {
        return this.hitID;
    }

    public void setHitID(long hitID) {
        this.hitID = hitID;
    }

    public long getHitNr() {
        return this.hitNr;
    }

    public void setHitNr(long hitNr) {
        this.hitNr = hitNr;
    }

    public String getMcrobjID() {
        return this.mcrobjID;
    }

    public void setMcrobjID(String mcrobjID) {
        this.mcrobjID = mcrobjID;
    }

    public String getOaiID() {
        return this.oaiID;
    }

    public void setOaiID(String oaiID) {
        this.oaiID = oaiID;
    }

    public String getSpec() {
        return this.spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getSpecName() {
        return this.specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecDescription() {
        return this.specDescription;
    }

    public void setSpecDescription(String specDescription) {
        this.specDescription = specDescription;
    }

    public String getDatestamp() {
        return this.datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public org.mycore.backend.hibernate.tables.MCRRESUMPTIONTOKEN getResumptionToken() {
        return this.resumptionToken;
    }

    public void setResumptionToken(org.mycore.backend.hibernate.tables.MCRRESUMPTIONTOKEN resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("hitID", getHitID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof MCRRESUMPTIONTOKEN_HIT) ) return false;
        MCRRESUMPTIONTOKEN_HIT castOther = (MCRRESUMPTIONTOKEN_HIT) other;
        return new EqualsBuilder()
            .append(this.getHitID(), castOther.getHitID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getHitID())
            .toHashCode();
    }

}
