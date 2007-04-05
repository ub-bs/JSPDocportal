package org.mycore.start.demo;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.BrowserLauncherRunner;


/**
 * Mainclass in the demo.jar file
 * for starting the demo-application
 * with jetty and hsqldb
 *
 * call it with: java -jar demo.jar [start | stop]
 * @author Helmbrecht Heiko
 *
 */
public class Main {

	final static String deactivateSuffix = ".notallowedinjetty" ;
	final static String[] xmlLibs = {"xercesImpl_2_7_1.jar" , "xml-apis.jar" };
	final static String NL = System.getProperty("line.separator");
	private String jettyHome;
	private String jettyHomeAbsolutePath;
	private String baseDir;

	public Main() {
		super();
		String JAVA_HOME = System.getenv("JAVA_HOME");
		if(existsToolsJar(JAVA_HOME)){
			System.setProperty("java.home", JAVA_HOME);
		}
		if(!existsToolsJar(System.getProperty("java.home"))){
			System.err.println("JAVA_HOME is not set, or does not point to your JDK");
			System.exit(-1);
		}
		baseDir = System.getProperty("user.dir");
		File jetty = new File(baseDir + File.separator + "working" + File.separator + "lib" + File.separator + "jetty");
		if(jetty.exists()){
			jettyHomeAbsolutePath = jetty.getAbsolutePath();
			jettyHome = jettyHomeAbsolutePath.replaceAll("\\\\","/");
			System.out.println("jetty-home=" + jettyHome);
		}else{
			System.err.println("Demo.jar is not in the right directory");
			System.exit(-1);
		}
	}

	public static boolean existsToolsJar(String base){
		File file = new File(base + File.separator + "lib" + File.separator + "tools.jar");
		if (file.exists())
			return true;
		else
			return false;
	}
	/**
	 * @param args
	 */
    public static void main(String[] args)
    {
        try
        {
        	Main main = new Main();
        	if(args.length <= 0) {
        		System.err.println("Usage (LINUX): %JDK_HOME%/bin/java -jar demo.jar start");
            	System.err.println("Usage (WINDOWS): %JDK_HOME%\\bin\\java -jar demo.jar start");
            	System.exit(1);        		
        	}else if (args[0].equals("start")){
        		main.renameXmlLibs("start");
        		main.startJetty();
        		Thread.sleep(5000);
        		main.launchBrowser("http://127.0.0.1:8085/mycoresample/");
        	}else if (args[0].equals("stop")){
        		main.stopJetty();
        		Thread.sleep(5000);
        		main.renameXmlLibs("stop");
        	}else {
        		System.err.println("Usage (LINUX): %JDK_HOME%/bin/java -jar demo.jar start");
            	System.err.println("Usage (WINDOWS): %JDK_HOME%\\bin\\java -jar demo.jar start");
            	System.exit(1);
        	}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void renameXmlLibs(String mode){
    	String basedirLIBS = new StringBuffer(baseDir).append(File.separator)
    		.append("working").append(File.separator)
    		.append("webapps").append(File.separator)
    		.append("mycoresample").append(File.separator)
    		.append("WEB-INF").append(File.separator)
    		.append("lib").append(File.separator).toString();
    	if(mode.equals("start")){
    		for (int i = 0; i < xmlLibs.length; i++) {
				File file = new File(basedirLIBS + xmlLibs[i]);
				if(file.exists()){
					file.renameTo(new File(basedirLIBS + xmlLibs[i] + deactivateSuffix));
				}
			}
    	}else if(mode.equals("stop")){
    		for (int i = 0; i < xmlLibs.length; i++) {
				File file = new File(basedirLIBS + xmlLibs[i] + deactivateSuffix);
				if(file.exists()){
					file.renameTo(new File(basedirLIBS + xmlLibs[i]));
				}
			}    		
    	}
    }
    
    public void startJetty(){
    	Thread jettyThread = new Thread( new JettyRunnable(jettyHome, jettyHomeAbsolutePath, "start") ); 
		jettyThread.start();    	
    }
    
    public void stopJetty(){
    	Thread jettyThread = new Thread( new JettyRunnable(jettyHome, jettyHomeAbsolutePath, "stop") ); 
		jettyThread.start();    	
    }    
    
//    public void startJetty(){
//		Server server = new Server();
//		try {
//			System.setProperty("jetty.home",jettyHome);
//			server.configure(jettyHome + "/webapp.xml");
//			server.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }
    
    public void launchBrowser(String url) throws Exception {

		BrowserLauncher launcher = new BrowserLauncher(null);
		BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, url, null);
		Thread launcherThread = new Thread(runner);
		launcherThread.start();
    }
    
    public static class JettyRunnable implements Runnable{
    	private String jettyHome;
    	private String jettyHomeAbsolutePath;
    	private String mode;
    	public JettyRunnable(String jettyHome, String jettyHomeAbsolutePath, String mode){
    		this.jettyHome = jettyHome;
    		this.jettyHomeAbsolutePath = jettyHomeAbsolutePath;
    		// mode: start|stop
    		this.mode = mode;
    	}
    	public void run() {
	    	try{
	    		// without this complicated procedere jsp-file cannot be compiled and tools.jar is not found
	    		System.setProperty("jetty.home", jettyHome);
	    		URL[] urls = {new File(jettyHomeAbsolutePath + File.separator + mode + ".jar").toURL()};
	    		URLClassLoader ucl = new URLClassLoader(urls);
	    		Class cl = ucl.loadClass("org.mortbay." + mode + ".Main");
	    		Class[] classes = new Class[1];
	    		classes[0] = String[].class;
	    		Method main = cl.getDeclaredMethod("main", classes);
	    		Object[] methodParams = new Object[1];
    			String[] arguments = { jettyHomeAbsolutePath + File.separator + "webapp.xml"};
    			methodParams[0] = (Object) arguments;
	    		main.invoke(null, methodParams);  
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}			
		}
    	
    }
}