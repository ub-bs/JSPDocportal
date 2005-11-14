/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
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
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.backend.hibernate;

import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import org.hibernate.*;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import org.mycore.common.*;
import org.mycore.datamodel.classifications.*;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.services.oai.MCROAIQueryService;
import org.mycore.services.oai.MCROAIResumptionTokenStore;
import org.mycore.backend.hibernate.tables.*;


/**
 * This class implements the MCRHIBResumptionTokenStore
 **/
public class MCRHIBResumptionTokenStore implements MCROAIResumptionTokenStore
{
    // logger
    static Logger logger=Logger.getLogger(MCRHIBResumptionTokenStore.class);

    private static final String STR_OAI_RESUMPTIONTOKEN_TIMEOUT = "MCR.oai.resumptiontoken.timeout";
    private static final String STR_OAI_MAXRETURNS = "MCR.oai.maxreturns"; //maximum   
    private static final String STR_OAI_REPOSITORY_IDENTIFIER = "MCR.oai.repositoryidentifier"; // Identifier    

    static MCRConfiguration config;

    /**
     * The constructor for the class MCRSQLClassificationStore. It reads
     * the classification configuration and checks the table names.
     **/
    public MCRHIBResumptionTokenStore()
    {
        config = MCRConfiguration.instance();
        
    }

    public final void dropTables()
    {
    }

    public Session getSession()
    {
	return MCRHIBConnection.instance().getSession();
    }

    public void deleteOutdatedTokens() {
        int timeout_h = 0;
        try {
            timeout_h = config.getInt(STR_OAI_RESUMPTIONTOKEN_TIMEOUT);
        } catch (MCRConfigurationException mcrx) {
            logger
                    .error("Die Property '"
                            + STR_OAI_RESUMPTIONTOKEN_TIMEOUT
                            + "' ist nicht konfiguriert. Resumption Tokens werden nicht unterstuetzt.");
            return;
        } catch (NumberFormatException nfx) {
            timeout_h = 72;
        }
        long outdateTime = new Date().getTime() - (timeout_h * 60 * 60 * 1000);

        Session session = getSession();
        Transaction tx = session.beginTransaction();

        List delList = session.createCriteria(MCRRESUMPTIONTOKEN.class)
        	.add (Restrictions.le("created",new Date(outdateTime))).list();

        for (Iterator it = delList.iterator(); it.hasNext();) {
            MCRRESUMPTIONTOKEN token = (MCRRESUMPTIONTOKEN) it.next();
            session.delete(token);
        }

    	tx.commit();
    	session.close();
    }

    public final List getResumptionTokenHits(String resumptionTokenID, int requestedSize, int maxResults) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();

        int totalSize = ( (Long) session.createQuery("select completeSize from " +
        		"MCRRESUMPTIONTOKEN " +
        		"where  resumptionTokenID like '" + resumptionTokenID + "'").uniqueResult() ).intValue();
        int hitNrFrom = totalSize - requestedSize;
        boolean isNextTokenPartReady = false;
        int persistedHitsSize = ( (Integer) session.createQuery("select count(*) from " +
        		"MCRRESUMPTIONTOKEN_HIT " +
        		"where  resumptionToken like '" + resumptionTokenID + "'").uniqueResult() ).intValue();
        if( (persistedHitsSize - hitNrFrom > maxResults) ||(totalSize - persistedHitsSize == 0) ) isNextTokenPartReady = true;
        int count = 0;
        while (!isNextTokenPartReady) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            persistedHitsSize = ( (Integer) session.createQuery("select count(*) from " +
            		"MCRRESUMPTIONTOKEN_HIT " +
            		"where  resumptionToken like '" + resumptionTokenID + "'").uniqueResult() ).intValue();            
            if( (persistedHitsSize - hitNrFrom > maxResults) ||(totalSize - persistedHitsSize == 0) ) isNextTokenPartReady = true;
            count++;
            if (count > 100) throw new MCRException("while-loop is not ending," +
                    "hibernate is too slow, or the maxResults-Size to high," + 
                    "persistedHitsSize=" + persistedHitsSize + 
                    " / hitNrFrom=" + hitNrFrom + 
                    " / maxResults=" + maxResults + 
                    " / totalSize=" + totalSize) ;
        }
        
//        List helpList = session.createQuery(
//                "select MCRRESUMPTIONTOKEN_HIT from " +
//        		"MCRRESUMPTIONTOKEN_HIT " +
//        		"where  resumptionTokenID like '" + resumptionTokenID + "' " +
//        		"and hitNr > " + hitNrFrom + " ").setMaxResults(maxResults).list();

        List helpList = session.createCriteria(MCRRESUMPTIONTOKEN_HIT.class)
        	.add( Expression.like("resumptionToken.resumptionTokenID",resumptionTokenID))
        	.addOrder(Order.asc("hitNr"))
        	.setFirstResult(hitNrFrom)
        	.setMaxResults(maxResults).list() 	;

    	List resultList = new ArrayList();

        for (Iterator it = helpList.iterator(); it.hasNext();) {
            MCRRESUMPTIONTOKEN_HIT hit = (MCRRESUMPTIONTOKEN_HIT) it.next();
            String[] identifier = new String[6];
            identifier[0] = hit.getOaiID();
            identifier[1] = hit.getDatestamp();
            identifier[2] = hit.getSpec();
            identifier[3] = hit.getMcrobjID();
            identifier[4] = hit.getSpecName();
            identifier[5] = hit.getSpecDescription();
            resultList.add(identifier);
        }


    	tx.commit();
    	session.close();

    	return resultList;
    }

    /**
     * The method create a new MCRResumptionToken in the datastore.
     *
     * @param classification an instance of a MCRClassificationItem
     **/
    public final void createResumptionToken(String id, String prefix, 
            String instance, List resultList)
    {
        int maxreturns = config.getInt(STR_OAI_MAXRETURNS, 10);       
        MCRRESUMPTIONTOKEN tok = new MCRRESUMPTIONTOKEN();
        try{
		    Session session = getSession();
			Transaction tx = session.beginTransaction();
		
			List resumptionTokList = new ArrayList();
		
			tok.setResumptionTokenID(id);
			tok.setPrefix(prefix);
			tok.setCreated(new Date());
			tok.setCompleted(false);
			tok.setCompleteSize(resultList.size());
			tok.setInstance(instance);
			tok.setResultList(resumptionTokList);
			session.saveOrUpdate(tok);
			tx.commit();
			session.close();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        String repositoryID = config.getString(STR_OAI_REPOSITORY_IDENTIFIER + "."
                + instance);
        Thread t = new ResumptionTokenResultListThread(tok,resultList, 
                maxreturns, prefix, repositoryID, instance);
        return;
    }

    private void delete(Session session, String query)
    {
        List l = session.createQuery(query).list();
        for(int t=0;t<l.size();t++) {
	    session.delete(l.get(t));
        }
    }

    /* (non-Javadoc)
     * @see org.mycore.services.oai.MCROAIResumptionTokenStore#getPrefix(java.lang.String)
     */
    public String getPrefix(String token) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();

        String prefix =  (String) session.createQuery("select prefix from " +
        		"MCRRESUMPTIONTOKEN " +
        		"where resumptionTokenID like '" + token + "'" ).uniqueResult() ;
    	tx.commit();
    	session.close();
        return prefix;
    }
    
    private class ResumptionTokenResultListThread extends Thread {
        
        private MCRRESUMPTIONTOKEN tok;
        private List resultList;
        private int maxreturns;
        private String prefix;
        private String repositoryID;
        private String instance;
        
        public ResumptionTokenResultListThread(MCRRESUMPTIONTOKEN tok, 
                List resultList, int maxreturns, String prefix, 
                String repositoryID, String instance) {
            this.tok = tok;
            this.resultList = resultList;
            this.maxreturns = maxreturns;
            this.prefix = prefix;
            this.repositoryID = repositoryID;
            this.instance = instance;

            start();
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
        	try {
                Session session = getSession();
                Transaction tx = session.beginTransaction();

                List resumptionTokList = new ArrayList();
                int count = 0;
                
                for (Iterator it = resultList.iterator(); it.hasNext();) {
                    count++ ;
                    String oaiID = "";
                    String datestamp = "";
                    String spec = "";
                    String mcrobjID = "";
                    String specDescription = "";
                    String specName = "";
                    
                    if (!prefix.equals("set")) {
                        String objectId = (String) it.next();
                        MCRObject object = new MCRObject();
                        object.receiveFromDatastore(objectId);
                       String[] array = MCROAIQueryService.getHeader(object, objectId, repositoryID,
                               instance);                        
                        oaiID = array[0];
                        datestamp = array[1];
                        spec = array[2] ;
                        mcrobjID = array[3];
                    } else {
                        String[] array = (String[]) it.next();
                        spec = array[0];
                        if ((array[1] != null) && (array[1].length() > 0)) {
                            specName = array[1];
                        }                        
                        if ((array[2] != null) && (array[2].length() > 0)) {
                            specDescription = array[2];
                        }
                    }
                    
                    MCRRESUMPTIONTOKEN_HIT hit = 
                        new MCRRESUMPTIONTOKEN_HIT(count,
            	            mcrobjID,oaiID,spec,specName,
            	            specDescription,datestamp,tok);;

                	session.saveOrUpdate(hit);
                	resumptionTokList.add(hit);
                	
                	// jdbc-batch-size = 25 recommended
                	if((count % 25 == 0)) {
                	    session.flush();
                	    session.clear();
                	    
                	    if( count % ((maxreturns/25 +1)*25) == 0)
                	        sleep(250);
                	}
               	
                }
                
                
                tok.setCompleted(true);
                tok.setResultList(resumptionTokList);
                
                session.saveOrUpdate(tok);

                tx.commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }    
}
