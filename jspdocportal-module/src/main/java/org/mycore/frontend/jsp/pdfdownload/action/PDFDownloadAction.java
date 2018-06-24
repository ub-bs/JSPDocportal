/*
 * $RCSfile$
 * $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan 2010) $
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
 */
package org.mycore.frontend.jsp.pdfdownload.action;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mycore.common.HashedDirectoryStructure;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.jsp.pdfdownload.PDFGenerator;
import org.mycore.frontend.jsp.pdfdownload.PDFGeneratorService;
import org.mycore.solr.MCRSolrClientFactory;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/pdfdownload/recordIdentifier")
public class PDFDownloadAction implements ActionBean {
    public static final String SESSION_ATTRIBUTE_PROGRESS_PREFIX = "pdfdownload_progress_";

    private List<String> errorMessages = new ArrayList<String>();

    private Path depotDir;

    private String recordIdentifier;

    private String filename;

    private String requestURL;

    private String filesize = "O MB";

    private boolean ready;

    public PDFDownloadAction() {
        depotDir = Paths.get(MCRConfiguration.instance().getString("MCR.depotdir"));
    }

    private static final Logger LOGGER = LogManager.getLogger(PDFDownloadAction.class);

    private ActionBeanContext context;

    public ActionBeanContext getContext() {
        return context;
    }

    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    @DefaultHandler
    public Resolution defaultResolution() {
        requestURL = getContext().getRequest().getRequestURL().toString();
        String path = getContext().getRequest().getPathInfo().replace("/pdfdownload/recordIdentifier", "").replace("..",
                "");
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.length() == 0) {
            return new ForwardResolution("/index.jsp");
        }
        path = path.replace("%25", "%").replace("%2F", "/");
        if (path.endsWith(".pdf")) {
            // download file if it exists or show progress html
            recordIdentifier = path.substring(0, path.lastIndexOf("/"));
        } else {
            recordIdentifier = path;
        }

        SolrClient solrClient = MCRSolrClientFactory.getMainSolrClient();
        SolrQuery query = new SolrQuery();
        query.setQuery("recordIdentifier:" + recordIdentifier);

        try {
            QueryResponse response = solrClient.query(query);
            SolrDocumentList solrResults = response.getResults();

            if (solrResults.getNumFound() > 0) {
                filename = recordIdentifier.replace("/", "_") + ".pdf";

                final Path resultPDF = calculateCacheDir().resolve(recordIdentifier).resolve(filename);
                ready = Files.exists(resultPDF);
                if (ready) {
                    filesize = String.format(Locale.GERMANY, "%1.1f%n MB",
                            (double) Files.size(resultPDF) / 1024 / 1024);
                }

                if (path.endsWith(".pdf") && ready && getProgress() < 0) {
                    // download pdf
                    Path fCount = resultPDF.getParent().resolve(resultPDF.getFileName() + ".count");
                    Files.write(fCount, ".".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);

                    return new StreamingResolution("application/pdf") {
                        @Override
                        protected void stream(HttpServletResponse response) throws Exception {
                            Files.copy(resultPDF, response.getOutputStream());
                        }
                    }.setFilename(filename);
                }

                String mcrid = String.valueOf(solrResults.get(0).getFirstValue("returnId"));

                if (!ready && getProgress() < 0) {
                    getContext().getServletContext().setAttribute(SESSION_ATTRIBUTE_PROGRESS_PREFIX + recordIdentifier,
                            0);
                    PDFGeneratorService.execute(new PDFGenerator(resultPDF,
                            HashedDirectoryStructure.createOutputDirectory(depotDir, recordIdentifier),
                            recordIdentifier, mcrid, getContext().getServletContext()));
                }

                if (getProgress() > 100) {
                    getContext().getServletContext()
                            .removeAttribute(SESSION_ATTRIBUTE_PROGRESS_PREFIX + recordIdentifier);
                }

            } else {
                errorMessages.add("The RecordIdentifier \"<strong>" + recordIdentifier + "\"</strong> is unkown.");
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e);
        }

        return new ForwardResolution("/WEB-INF/views/pdfdownload.jsp");
    }

    public String getRecordIdentifier() {
        return recordIdentifier.replace("/", "%252F");
    }

    public boolean isReady() {
        return ready;
    }

    public String getFilename() {
        return filename;
    }

    public int getProgress() {
        Integer num = (Integer) getContext().getServletContext()
                .getAttribute(SESSION_ATTRIBUTE_PROGRESS_PREFIX + recordIdentifier);
        if (num == null) {
            return -1;
        } else {
            return num;
        }
    }

    private Path calculateCacheDir() {
        Path cacheDir = Paths.get(MCRConfiguration.instance().getString("MCR.PDFDownload.CacheDir"));
        return cacheDir;
    }

    public String getFilesize() {
        return filesize;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getRequestURL() {
        return requestURL;
    }
}
