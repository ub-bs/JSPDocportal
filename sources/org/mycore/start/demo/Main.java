package org.mycore.start.demo;

import java.io.File;
import org.mortbay.jetty.Server;

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

	final static String NL = System.getProperty("line.separator");
	private String jettyHome;

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
		String curDir = System.getProperty("user.dir");
		File jetty = new File(curDir + File.separator + "mycore-working" + File.separator + "lib" + File.separator + "jetty");
		if(jetty.exists()){
			jettyHome = jetty.getAbsolutePath().replaceAll("\\\\","/");
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
        		
        	}else if (args[0].equals("start")){
        		main.startJetty();
        		Thread.sleep(2000);
        		main.launchBrowser("http://127.0.0.1:8085/mycoresample/");
        	}else if (args[0].equals("help")){
        		System.err.println("Usage (LINUX): %JDK_HOME%/bin/java -jar demo.jar [--help] [start]");
            	System.err.println("Usage (WINDOWS): %JDK_HOME%\\bin\\java -jar demo.jar [--help] [start]");
            	System.exit(1);        		
        	}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void startJetty(){
		Server server = new Server();
		try {
			System.setProperty("jetty.home",jettyHome);
			server.configure(jettyHome + "/webapp.xml");
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void launchBrowser(String url) throws Exception {

		BrowserLauncher launcher = new BrowserLauncher(null);
		BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, url, null);
		Thread launcherThread = new Thread(runner);
		launcherThread.start();
    }
}