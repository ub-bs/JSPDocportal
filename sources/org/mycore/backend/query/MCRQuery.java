/*
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */

package org.mycore.backend.query;

import java.sql.Types.*;

public class MCRQuery
{
    private String mcrid;
    private String keywords_lang;
    private String contributorID;
    private String title;
    private String description_type;
    private String publisher;
    private String objectType;
    private String origin;
    private java.sql.Date date;
    private String id;
    private java.sql.Date modified;
    private String date_type;
    private String authorID;
    private Integer zip;
    private String contributor;
    private String source_lang;
    private String publisherID;
    private String allMeta;
    private String contributor_type;
    private String coverage_lang;
    private String type;
    private String keywords;
    private String link;
    private String modified_type;
    private String language;
    private String ddc;
    private String creatorID;
    private String creator;
    private String coverage;
    private String surname;
    private String description_lang;
    private String institution;
    private String firstname;
    private String description;
    private String identifier;
    private String subject;
    private String source;
    private String author;
    private String parent;
    private String format;

    /**
    * @hibernate.property
    * column="MCRID"
    * not-null="true"
    * update="true"
    */
    public String getMcrid() {
        return mcrid;
    }
    public void setMcrid(String mcrid) {
        this.mcrid = mcrid;
    }

    /**
    * @hibernate.property
    * column="KEYWORDS_LANG"
    * not-null="true"
    * update="true"
    */
    public String getKeywords_lang() {
        return keywords_lang;
    }
    public void setKeywords_lang(String keywords_lang) {
        this.keywords_lang = keywords_lang;
    }

    /**
    * @hibernate.property
    * column="CONTRIBUTORID"
    * not-null="true"
    * update="true"
    */
    public String getContributorID() {
        return contributorID;
    }
    public void setContributorID(String contributorID) {
        this.contributorID = contributorID;
    }

    /**
    * @hibernate.property
    * column="TITLE"
    * not-null="true"
    * update="true"
    */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    /**
    * @hibernate.property
    * column="DESCRIPTION_TYPE"
    * not-null="true"
    * update="true"
    */
    public String getDescription_type() {
        return description_type;
    }
    public void setDescription_type(String description_type) {
        this.description_type = description_type;
    }

    /**
    * @hibernate.property
    * column="PUBLISHER"
    * not-null="true"
    * update="true"
    */
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
    * @hibernate.property
    * column="OBJECTTYPE"
    * not-null="true"
    * update="true"
    */
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
    * @hibernate.property
    * column="ORIGIN"
    * not-null="true"
    * update="true"
    */
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
    * @hibernate.property
    * column="DATE"
    * not-null="true"
    * update="true"
    */
    public java.sql.Date getDate() {
        return date;
    }
    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    /**
    * @hibernate.property
    * column="ID"
    * not-null="true"
    * update="true"
    */
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
    * @hibernate.property
    * column="MODIFIED"
    * not-null="true"
    * update="true"
    */
    public java.sql.Date getModified() {
        return modified;
    }
    public void setModified(java.sql.Date modified) {
        this.modified = modified;
    }

    /**
    * @hibernate.property
    * column="DATE_TYPE"
    * not-null="true"
    * update="true"
    */
    public String getDate_type() {
        return date_type;
    }
    public void setDate_type(String date_type) {
        this.date_type = date_type;
    }

    /**
    * @hibernate.property
    * column="AUTHORID"
    * not-null="true"
    * update="true"
    */
    public String getAuthorID() {
        return authorID;
    }
    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    /**
    * @hibernate.property
    * column="ZIP"
    * not-null="true"
    * update="true"
    */
    public Integer getZip() {
        return zip;
    }
    public void setZip(Integer zip) {
        this.zip = zip;
    }

    /**
    * @hibernate.property
    * column="CONTRIBUTOR"
    * not-null="true"
    * update="true"
    */
    public String getContributor() {
        return contributor;
    }
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    /**
    * @hibernate.property
    * column="SOURCE_LANG"
    * not-null="true"
    * update="true"
    */
    public String getSource_lang() {
        return source_lang;
    }
    public void setSource_lang(String source_lang) {
        this.source_lang = source_lang;
    }

    /**
    * @hibernate.property
    * column="PUBLISHERID"
    * not-null="true"
    * update="true"
    */
    public String getPublisherID() {
        return publisherID;
    }
    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    /**
    * @hibernate.property
    * column="ALLMETA"
    * not-null="true"
    * update="true"
    */
    public String getAllMeta() {
        return allMeta;
    }
    public void setAllMeta(String allMeta) {
        this.allMeta = allMeta;
    }

    /**
    * @hibernate.property
    * column="CONTRIBUTOR_TYPE"
    * not-null="true"
    * update="true"
    */
    public String getContributor_type() {
        return contributor_type;
    }
    public void setContributor_type(String contributor_type) {
        this.contributor_type = contributor_type;
    }

    /**
    * @hibernate.property
    * column="COVERAGE_LANG"
    * not-null="true"
    * update="true"
    */
    public String getCoverage_lang() {
        return coverage_lang;
    }
    public void setCoverage_lang(String coverage_lang) {
        this.coverage_lang = coverage_lang;
    }

    /**
    * @hibernate.property
    * column="TYPE"
    * not-null="true"
    * update="true"
    */
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    /**
    * @hibernate.property
    * column="KEYWORDS"
    * not-null="true"
    * update="true"
    */
    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
    * @hibernate.property
    * column="LINK"
    * not-null="true"
    * update="true"
    */
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    /**
    * @hibernate.property
    * column="MODIFIED_TYPE"
    * not-null="true"
    * update="true"
    */
    public String getModified_type() {
        return modified_type;
    }
    public void setModified_type(String modified_type) {
        this.modified_type = modified_type;
    }

    /**
    * @hibernate.property
    * column="LANGUAGE"
    * not-null="true"
    * update="true"
    */
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
    * @hibernate.property
    * column="DDC"
    * not-null="true"
    * update="true"
    */
    public String getDdc() {
        return ddc;
    }
    public void setDdc(String ddc) {
        this.ddc = ddc;
    }

    /**
    * @hibernate.property
    * column="CREATORID"
    * not-null="true"
    * update="true"
    */
    public String getCreatorID() {
        return creatorID;
    }
    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    /**
    * @hibernate.property
    * column="CREATOR"
    * not-null="true"
    * update="true"
    */
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
    * @hibernate.property
    * column="COVERAGE"
    * not-null="true"
    * update="true"
    */
    public String getCoverage() {
        return coverage;
    }
    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    /**
    * @hibernate.property
    * column="SURNAME"
    * not-null="true"
    * update="true"
    */
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
    * @hibernate.property
    * column="DESCRIPTION_LANG"
    * not-null="true"
    * update="true"
    */
    public String getDescription_lang() {
        return description_lang;
    }
    public void setDescription_lang(String description_lang) {
        this.description_lang = description_lang;
    }

    /**
    * @hibernate.property
    * column="INSTITUTION"
    * not-null="true"
    * update="true"
    */
    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
    * @hibernate.property
    * column="FIRSTNAME"
    * not-null="true"
    * update="true"
    */
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
    * @hibernate.property
    * column="DESCRIPTION"
    * not-null="true"
    * update="true"
    */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
    * @hibernate.property
    * column="IDENTIFIER"
    * not-null="true"
    * update="true"
    */
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
    * @hibernate.property
    * column="SUBJECT"
    * not-null="true"
    * update="true"
    */
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
    * @hibernate.property
    * column="SOURCE"
    * not-null="true"
    * update="true"
    */
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    /**
    * @hibernate.property
    * column="AUTHOR"
    * not-null="true"
    * update="true"
    */
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
    * @hibernate.property
    * column="PARENT"
    * not-null="true"
    * update="true"
    */
    public String getParent() {
        return parent;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
    * @hibernate.property
    * column="FORMAT"
    * not-null="true"
    * update="true"
    */
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
}
