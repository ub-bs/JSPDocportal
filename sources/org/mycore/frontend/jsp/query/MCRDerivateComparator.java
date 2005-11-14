package org.mycore.frontend.jsp.query;

import java.util.Comparator;
import org.jdom.Element;

public class MCRDerivateComparator implements Comparator
{
  public int compare( Object o1, Object o2 )
  {
	Element child1 = (Element) o1;
	Element child2 = (Element) o2;
	
	int firstOrder = child1.getAttributeValue("type").compareTo(child2.getAttributeValue("type"));
	if (firstOrder != 0) return firstOrder;
	
	int secondOrder = child1.getChildText("contentType").compareTo(child2.getChildText("contentType"));
	if (secondOrder != 0) return secondOrder;
	
	int thirdOrder = child1.getChildText("name").compareTo(child2.getChildText("name"));
	return thirdOrder;
  }
}
