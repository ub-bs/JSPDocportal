package org.mycore.frontend.jsp.navigation;

public class PathNotFoundException extends IllegalStateException
{
    public PathNotFoundException(String msg) {
	super(msg);
    }
};
