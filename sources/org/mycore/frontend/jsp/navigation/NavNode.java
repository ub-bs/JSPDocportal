package org.mycore.frontend.jsp.navigation;

import org.mycore.frontend.jsp.navigation.NavEntry;

import java.util.*;

public class NavNode {
    private List children = new LinkedList();
    private SortedMap child2node = new TreeMap();
    private NavEntry value;
    private String type;
    private boolean flag;
    private NavNode nextFlagged;

    public boolean equals(Object obj) {
        NavNode config = (NavNode) obj;
        if(this.value == null) {
            if(config.value != null) return false;
        } else {
            if(!this.value.equals(config.value)) return false;
            if(!this.type.equals(config.type)) return false;
        }
        if(!this.children.equals(config.children)) return false;
        return true;
    }

    /**
     * returns the child node of this node with the given key. if the key is specifying a '.' separated path
     * in the preferences tree, the tree is searched recursively for the given childnode
     * if the key is not found, a keynot found exception which is actually extending illegalstate exception is thrown
     * @param key
     * @return
     */
    public NavNode getChild(String key) {
        int index = key.indexOf('.');
        NavNode child = null;
        String rest = null;
        if(index != -1) {
            rest = key.substring(index + 1);
            key = key.substring(0,index);
        }
        child = (NavNode) child2node.get(key);
        if(child == null) throw new PathNotFoundException(key);
        if(rest != null) {
            try {
                return child.getChild(rest);
            } catch (PathNotFoundException e) {
                throw new PathNotFoundException(key);
            }
        } else {
            return child;
        }
    }

    public NavEntry get(String key) {
        NavNode node =  this.getChild(key);
        if(node==null)
            return null;
        else
            return node.value;
    }

    /**
     * returns all children of the given node
     * @return
     */
    public Collection getChildren(boolean visibilityCheck) {
        Iterator it = children.iterator();
        List l = new LinkedList();
        while(it.hasNext()) {
            String name = (String)it.next();
            NavNode node = (NavNode)child2node.get(name);
            NavEntry entry = node.getValue();
            if(!visibilityCheck || !entry.isHidden())
                l.add(node);
        }
        return l;
    }

    public Collection getChildren()
    {
        return getChildren(false);
    }
    public Collection getVisibleChildren()
    {
        return getChildren(true);
    }

    /**
     * returns a collection containing all child keys stored in this configurations node
     * @return
     */
    public Collection getChildNames() {
        return new ArrayList(children);
    }

    /**
     * returns all child knots in a map
     * @return
     */
    public Map getChildMap() {
        return new HashMap(child2node);
    }

    public NavEntry getValue() {
        return value;
    }

    public Iterator iterator() {
        Iterator i = getVisibleChildren().iterator();
        return i;
    }

    public String toString() {
        if(value != null) {
            return value.toString();
        } else {
            return super.toString();
        }
    }

    private Object retrieveValue(String key, String type) {
        if(key == null || key.length() == 0) {
            if(value == null) throw new PathNotFoundException(key);
            return value;
        }
        int index = key.indexOf('.');
        String rest = null;
        String newKey = null;
        if(index != -1) {
            rest = key.substring(index + 1);
            newKey = key.substring(0,index);
        } else {
            newKey = key;
        }
        NavNode child = (NavNode) child2node.get(newKey);
        if(child == null) throw new PathNotFoundException(key);
        try {
            return child.retrieveValue(rest,type);
        } catch (PathNotFoundException e) {
            throw new PathNotFoundException(key);
        }
    }

    private void setValue(NavEntry value) {
        this.value = value;
    }

    public NavNode addNode(String key, NavEntry value) {
        int index = key.indexOf('.');
        String rest = null;
        if(index != -1) {
            rest = key.substring(index + 1);
            key = key.substring(0,index);
        }
        NavNode child = (NavNode) child2node.get(key);
        if(child == null) {
            child = new NavNode();
            child2node.put(key,child);
            children.add(key);
        }
        if(rest == null) {
            child.setValue(value); // leaf
            return this;
        } else {
            return child.addNode(rest,value); // another node
        }
    }

    public void clearFlags() {
        this.flag = false;
        this.nextFlagged = null;
        Iterator itr = child2node.values().iterator();
        while (itr.hasNext()) {
            NavNode n = (NavNode)itr.next();
            n.clearFlags();
        }
    }
    public boolean isOpened() {
        return flag;
    }
    public void setFlag(String key) {
        this.flag = true;
        int i = key.indexOf('.');
        String rest = null, prefix = key;
        if(i != -1) {
            rest = key.substring(i + 1);
            prefix = key.substring(0,i);
        }
        System.out.println("key="+key+" rest="+rest+" prefix="+prefix);
        NavNode child = (NavNode) child2node.get(prefix);
        if(child==null)
            return;
        else
            this.nextFlagged = child;
        if(rest == null) {
            child.flag = true;
        } else {
            child.setFlag(rest);
        }
    }

    public void flag(String key) {
        clearFlags();
        setFlag(key);
    }

    public NavNode getNextFlagged() {
        return nextFlagged;
    }
    public boolean hasNextFlagged() {
        return nextFlagged!=null;
    }

    private void mergeTree(NavNode subtree) {
        if(subtree.value != null && this.value == null) {
            this.value = subtree.value;
            this.type = subtree.type;
        }
        Iterator itr = subtree.child2node.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            NavNode newChild = (NavNode) subtree.child2node.get(key);
            NavNode child = (NavNode) this.child2node.get(key);
            if(child == null) {
                child = new NavNode();
                this.child2node.put(key,child);
                this.children.add(key);
            }
            child.mergeTree(newChild);
        }
    }
}
