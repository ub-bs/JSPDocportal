package org.mycore.frontend.jsp;

public class MCRJSPUtils {
    public static String normalizeUmlauts(String input){
        String result = input.replace("ä",  "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss");
        result = result.replace("Ä",  "AE").replace("Ö",  "OE").replace("Ü",  "UE");
        return result;
    }
}
