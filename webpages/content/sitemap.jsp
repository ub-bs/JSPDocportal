<%@ page import="java.util.Iterator,
                 org.mycore.frontend.jsp.navigation.NavTree,
                 org.mycore.frontend.jsp.navigation.NavNode,
                 org.mycore.frontend.jsp.navigation.NavEntry"%>
<%{%>

<%
    NavTree tree = (NavTree)request.getAttribute("nav");
%>

<table width="100%" cellspacing="0" cellpadding="0">
<tr width="100%"><th width="50%" style="text-align: right">Hauptmen&uuml; links</th><th width="50%">&nbsp;</th></tr>
<tr><td colspan="2">&nbsp;</td></tr>

<%  {   
	Iterator i = tree.getChild("left").iterator();
	boolean left = true;
	boolean first = true;
	while(i.hasNext()) {
	      if(left) {
		  %><tr width="100%"><td width="50%" valign="top"><%
	      } else {
		  %><td width="50%" valign="top"><%
	      }
	      NavNode n = (NavNode)i.next();
	      NavEntry e = n.getValue();
	      %>
		  <a class="MainMenuPoint" target="_self" href="<%=e.getLink()%>"><%=e.getDescription()%></a><br/>
	      <%
	      Iterator i2 = n.iterator();
              while(i2.hasNext()) {
                  NavNode n2 = (NavNode)i2.next();
	          NavEntry e2 = n2.getValue();
		  boolean last = !i2.hasNext();
		  if(last) {
		      %><img src="images/line-with-element_end.gif"/><%
		  } else {
		      %><img src="images/line-with-element.gif"/><%
		  }
                  %>
		  <a target="_self" href="<%=e2.getLink()%>"><%=e2.getDescription()%></a><br/>
	          <%
	      }

	      if(left) {
		  %></td><%
	      } else {
		  %></td></tr><%
		  if(i.hasNext()) {
		      /* insert spacer line */
		      %><tr><td colspan="2">&nbsp;</td></tr><%
		  }
	      }
	      left = !left;
	      first = false;
	}
	if(!left) {
	    %></td><td>&nbsp;</td></tr><%
	}
    }
%>

</table>

<%}%>
