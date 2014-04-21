package org.mycore.frontend.jsp.pica2mods;

/**
 * Bean Style Class
 * @author mcradmin
 *
 */
public class MCRGVKMODSImporter {
    private String mcrid;
    private String oppn;
    private String modsdata;
    public String getMcrid() {
        return mcrid;
    }
    public void setMcrid(String mcrid) {
        this.mcrid = mcrid;
    }
    public String getOppn() {
        return oppn;
    }
    public void setOppn(String oppn) {
        this.oppn = oppn;
    }
    
    
    public String getModsdata() {
        importMODSData();
        return modsdata;
    }
    
    public void setModsdata(String s) {
        modsdata = s;
        storeMODSData();        
    }
    
    private void importMODSData(){
        
    }
    
    private void storeMODSData(){
        
    }
    
    
   
}
