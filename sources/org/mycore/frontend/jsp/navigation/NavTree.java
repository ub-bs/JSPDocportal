package org.mycore.frontend.jsp.navigation;

import org.mycore.frontend.jsp.navigation.NavEntry;
import org.mycore.frontend.jsp.navigation.NavNode;

public class NavTree extends NavNode
{
    public NavNode addNode(String path, String description) {
        return addNode(path, description, null, "node", false, false);
    }
    public NavNode addReference(String path, String description, String link) {
        return addNode(path, description, link, "reference", false, false);
    }
    public NavNode addExternalNode(String path, String description, String page) {
        return addNode(path, description, page, "node", false, true);
    }    
    public NavNode addHiddenNode(String path, String description, String page) {
        return addNode(path, description, page, "node", true, false);
    }
    public NavNode addNode(String path, String description, String page) {
        return addNode(path, description, page, "node", false, false);
    }
    private NavNode addNode(String path, String description, String page, String type, boolean hidden, boolean extern) {
        int i = path.lastIndexOf('.');
        String name = path.substring(i+1);
        NavEntry e = new NavEntry(this, path, name, description, page, type, hidden, extern);
	return super.addNode(path, e);
    }
};
