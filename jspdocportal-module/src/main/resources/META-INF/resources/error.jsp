<%@ page import="java.io.StringWriter,
                 java.io.PrintWriter"%><html>

<% Throwable e = (Throwable)request.getAttribute("javax.servlet.error.exception");
   String stacktrace = null;
   if(e!=null) {
       StringWriter wr = new StringWriter();
       e.printStackTrace(new PrintWriter(wr));
       stacktrace = wr.toString();
   }
   Throwable e2 = (Throwable) request.getAttribute("javax.servlet.jsp.jspException");
   if(e2!=null) {
       StringWriter wr = new StringWriter();
       e2.printStackTrace(new PrintWriter(wr));
       stacktrace = wr.toString();
   }   
   response.setContentType("text/html; charset=utf-8");

   String message = (String)request.getAttribute("javax.servlet.error.message");
   String url = (String)request.getAttribute("javax.servlet.error.request_uri");
   String servlet = (String)request.getAttribute("javax.servlet.error.servlet_name");
   if((message == null || message.equals("null")) && e!=null) {
       message = e.getMessage();
   }
//   Class exception_type = (Class)request.getAttribute("javax.servlet.error.exception_type");
//   Integer status_code = (Integer)request.getAttribute("javax.servlet.error.status_code");
%>

<html>
<body bgcolor="#ffffff">

<h2>
<pre>
Error:
<%=message%>
</pre>
</h2>

<pre>
<font color="#ffffff">
<%=stacktrace%>
</font>
</pre>

</body>

</html>
