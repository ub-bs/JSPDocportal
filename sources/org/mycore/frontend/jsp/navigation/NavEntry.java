package org.mycore.frontend.jsp.navigation;

public class NavEntry {
    String name;
    String path;
    String description;
    String page;
    String type;
    NavTree parent;
    boolean hidden;
    boolean extern;

    public NavEntry(NavTree parent, String path, String name, String description, String page, String type, boolean hidden, boolean extern)
    {
        this.parent = parent;
        this.path = path;
        this.name = name;
        this.description = description;
        this.page = page;
        this.type = type;
        this.hidden = hidden;
        this.extern = extern;
    }

    public boolean isReference() {
        return "reference".equals(type);
    }
    public boolean isHidden() {
        return hidden;
    }
    
    public boolean isExtern() {
    	return extern;
    }

    public String getLink() {
        if(!extern) {
            return "nav?path="+path;
        } else {
            return page;
        }
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPage() {
        return page;
    }
}
