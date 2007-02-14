<%@ page import="org.mycore.user.MCRUserMgr,org.mycore.user.MCRUser,
	java.util.ArrayList, java.util.Comparator,java.util.Collections"%>
<%@ page import="org.mycore.frontend.servlets.MCRServlet" %>

<%
	ArrayList userids = MCRUserMgr.instance().getAllUserIDs();
	String WebApplicationBaseURL = MCRServlet.getBaseURL();

	ArrayList users = new ArrayList(userids.size());

	
	for (int i=0; i<userids.size(); i++){
		String[] ob = {"",""};
		MCRUser user = MCRUserMgr.instance().retrieveUser((String)userids.get(i));
		String x = user.getUserContact().getLastName().length() >1 ? user.getUserContact().getLastName():(String)userids.get(i);
		String name =  x 	+ ", " + user.getUserContact().getFirstName()  ;
		ob[0]= name;
		ob[1]=(String)userids.get(i) ;
		users.add(ob);

	}
	
	Collections.sort(users, new Comparator() {
		
        public int compare(Object o1, Object o2) {
            String name1 = ((String[]) o1)[0];
            String name2 = ((String[]) o2)[0];
            int comp = name1.compareTo(name2);            
            return comp;
        }
    });
	
%>

<h4>Vorhandene Benutzer</h4>

<form method=post action="<%= WebApplicationBaseURL %>servlets/MCRUserValidateServlet" id="overview">

<p>
<%
	for (int i=65; i<90; i++){
		String b = new Character((char)i).toString();
		out.println("<a href='#"+b+"'>" + b + "</a> | ");
	}
	out.print("<a href='#z'>Z</a>");

%>
</p>
<table class="access" cellspacing="1" cellpadding="0" >
	<tr>
		<td >&nbsp;	</td>
		<td>
			<input type="image" title="Neuen Benutzer anlegen" name="new" src="<%= WebApplicationBaseURL %>admin/images/install.png">
		</td>
	</tr>
	<%
		String header = "";
		for (int i=0; i<users.size(); i++){
			String name =((String[])users.get(i))[0];
			String id   =((String[])users.get(i))[1];
		
			if (! header.equals(name.substring(0,1).toUpperCase())){

				out.println("<tr><th colspan='2'><A name='" + name.substring(0,1).toUpperCase() + "'>" 
						+ name.substring(0,1).toUpperCase() + "</a></th></tr>");
				header = name.substring(0,1).toUpperCase();
			}
			out.println("<tr>");
			out.println("<td class='rule'>" + name + " ( " + id + ") </td>");
			out.println("<td class='rule'><input type='image' title='Benutzer bearbeiten' name='e"
						+ id +"' src='" + WebApplicationBaseURL + "admin/images/edit.png'> <input type='image' title='Benutzer löschen' name='d"+ id +"' src='" 
						+ WebApplicationBaseURL + "admin/images/delete.png' onClick='return questionDel()'></td>");
			out.println("</tr>");
		}
	%>
</table>
<input type="hidden" name="operation" value="detail">

</form>
<br>&nbsp;<br>