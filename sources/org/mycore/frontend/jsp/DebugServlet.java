package org.mycore.frontend.jsp;

import org.mycore.common.xml.MCRXMLContainer;
import org.mycore.services.query.MCRQueryCache;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class DebugServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ServletContext context = this.getServletContext();
        PrintWriter out = response.getWriter();
        out.write("<html><body><h1>");
        out.write("Unknown page (not yet implemented)");
        out.write("</h1>");

        out.write("<h2>Servlet Path:</h2>");
        out.write(request.getServletPath());

        Enumeration e = request.getParameterNames();
        out.write("<h2>Parameters:</h2>");
        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = request.getParameter(key);
            out.write(key + "=" + value + "<br>");
        }
        out.write("<h2>Attributes:</h2>");
        e = request.getAttributeNames();
        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            Object value = request.getAttribute(key);
            String valuestr = "";
            if(value instanceof java.lang.String)
                valuestr = (String)value;
            else
                valuestr = value.toString();
            out.write("<i>"+value.getClass().getName() + "</i> " + key + "=" + valuestr + "<br>");
        }

        out.write("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
