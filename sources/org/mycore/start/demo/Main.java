package org.mycore.start.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

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

	private String classPath;
	private String javaBase;
	final static String NL = System.getProperty("line.separator");
	private String base;

	public Main() {
		super();
		base = System.getProperty("demo.home");
		if (base == null) {
			base = "";
		} else {
			base = base + "/";
		}
		classPath = base + "ant/lib/ant-launcher.jar" + File.pathSeparator + base + "mycore/lib/BrowserLauncher2.jar";
		javaBase = System.getProperty("java.home");
		if (javaBase != null) {
			javaBase = javaBase + File.separator + "bin" + File.separator;
		}else {
			javaBase = "";
		}
	}

	/**
	 * @param args
	 */
    public static void main(String[] args)
    {

        try
        {
        	Main myMain = new Main();
            myMain.start(args);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void start(String[] args)
    {
    	try{
    	String[] defaultArgs = {"start"};
    	if(args.length == 0) args = defaultArgs;
        ArrayList al = new ArrayList();
        for(int i=0;i<args.length;i++)
        {
            if(args[i]==null)
                continue;
            if(args[i].startsWith("-") || args.length < 1)
            {
            	System.err.println("Usage: java -jar demo.jar [--help] [start | stop]");
            	System.err.println("       or, if you can't go in the directory for any reasons");
            	System.getProperty("       java -Ddemo.home=C:/Programme/MyCoReSample -jar C:/Programme/MyCoReSample/demo.jar [--help] [start | stop]");
                System.exit(1);
            }
            else
                al.add(args[i]);
        }
        args=(String[])al.toArray(new String[al.size()]);
        //System.out.println("args-0=" + args[0]);
        if(args[0].equals("stop")) {
        	stopHsqldb();
        	stopJetty();
        	System.out.println("MyCoRe-Demo has stopped");
        } else if(args[0].equals("browserStart")) {
        	startBrowser();
        } else {
        	if(stopHsqldb()) {
        		startHsqldb();
        	}
        	if(stopJetty()) {
        		startJetty();
        	}
        	System.out.println("Database and Webserver are starting, go to http://127.0.0.1:8085/mycoresample/nav?path=left in your Browser!");
        	startBrowser();
        }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public void launchBrowser(String url) throws Exception {

		BrowserLauncher launcher = new BrowserLauncher(null);
		BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, url, null);
		Thread launcherThread = new Thread(runner);
		launcherThread.start();
    }

    public void startBrowser() throws Exception{
    	String startURL = "http://127.0.0.1:8085/mycoresample/nav?path=left";
    	// test, if jetty was started correctly
        boolean jettyOK = false;
        int count = 0;
        while(!jettyOK && count < 10) {
        	try{
            URL url = new URL(startURL);
        	   InputStream uin = url.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(uin));
            String entry;
            while ((entry = in.readLine()) != null) {
            	if (entry.indexOf("Navigation") > -1){
            		jettyOK = true;
            		break;
            	}
            }
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
            count++;
            if(!jettyOK) {
            	if(stopJetty()) {
            		startJetty();
            	}
            }
        }
        if(count > 8) {
        	System.out.println("unknown error in starting the jetty in org.mycore.start.demo.Main");
        } else {
        	launchBrowser(startURL);
        }
    }

    public void startHsqldb() throws Exception {
    	String exec1 = javaBase + "java";
    	String exec2 = "-cp";
    	String exec3 = classPath;
    	String exec4 = "org.apache.tools.ant.launch.Launcher" ;
    	String exec5 = "-buildfile";
    	String exec6 = base + "jspdocportal/build.xml";
    	String exec7 = "hsqldbstart";
    	String[] cmd = { exec1, exec2, exec3, exec4, exec5, exec6, exec7 };
    	BackgroundProcess p = new BackgroundProcess(cmd, new FileOutputStream(new File(base + "log/hsqldbstart.log")));
    	p.start();
    	return;
    }
    public boolean stopHsqldb() throws Exception {
    	String exec1 = javaBase + "java";
    	String exec2 = "-cp";
    	String exec3 = classPath;
    	String exec4 = "org.apache.tools.ant.launch.Launcher" ;
    	String exec5 = "-buildfile";
    	String exec6 = base + "jspdocportal/build.xml";
    	String exec7 = "hsqldbstop";
    	String[] cmd = { exec1, exec2, exec3, exec4, exec5, exec6, exec7 };

//		for (int i = 0; i < cmd.length; i++) {
//			if (i == 0) System.out.println("exec-command-arguments");
//			System.out.println("[" + i + "]=" + cmd[i]);
//		}

    	Process p = Runtime.getRuntime().exec(cmd);
    	BufferedReader in = new BufferedReader(
		  	      new InputStreamReader(p.getInputStream()) );
		for ( String s; (s = in.readLine()) != null; ){
			//System.out.println(s);
			if(s.indexOf("BUILD") > -1) {
				return true;
		  	}
 		}
    	return false;
    }
    public void startJetty() throws Exception {
    	File lib1 = new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xercesImpl_2_7_1.jar");
    	if (lib1.exists()) {
    		if (!lib1.renameTo(new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xercesImpl_2_7_1.jar.notallowed"))) {
    			System.out.println("could not rename xercesImpl_2_7_1.jar");
    			System.exit(2);
    		}
    	}
    	File lib2 = new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xml-apis.jar");
    	if (lib2.exists()) {
    		if (!lib2.renameTo(new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xml-apis.jar.notallowed"))) {
    			System.out.println("could not rename xml-apis.jar");
    			System.exit(2);
    		}
    	}
    	String exec1 = javaBase + "java";
    	String exec2 = "-cp";
    	String exec3 = classPath;
    	String exec4 = "org.apache.tools.ant.launch.Launcher" ;
    	String exec5 = "-buildfile";
    	String exec6 = base + "jspdocportal/build.xml";
    	String exec7 = "jettystart";
    	String[] cmd = { exec1, exec2, exec3, exec4, exec5, exec6, exec7 };

    	BackgroundProcess p = new BackgroundProcess(cmd, new FileOutputStream(new File(base + "log/jettystart.log")));
    	p.start();
    	int count = 0;
    	while(!p.isJettyStarted() && count < 20) {
    		Thread.sleep(1000);
    		count ++;
    	}
    	return;
    }
    public boolean stopJetty() throws Exception {
    	File lib1 = new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xercesImpl_2_7_1.jar.notallowed");
    	if (lib1.exists()) {
    		if (!lib1.renameTo(new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xercesImpl_2_7_1.jar"))) {
    			System.out.println("could not rename xercesImpl_2_7_1.jar.notallowed");
    			System.exit(2);
    		}
    	}
    	File lib2 = new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xml-apis.jar.notallowed");
    	if (lib2.exists()) {
    		if (!lib2.renameTo(new File(base + "jspdocportal/webapps/mycoresample/WEB-INF/lib/xml-apis.jar"))) {
    			System.out.println("could not rename xml-apis.jar.notallowed");
    			System.exit(2);
    		}
    	}
    	String exec1 = javaBase + "java";
    	String exec2 = "-cp";
    	String exec3 = classPath;
    	String exec4 = "org.apache.tools.ant.launch.Launcher" ;
    	String exec5 = "-buildfile";
    	String exec6 = base + "jspdocportal/build.xml";
    	String exec7 = "jettystop";
    	String[] cmd = { exec1, exec2, exec3, exec4, exec5, exec6, exec7 };
    	Process p = Runtime.getRuntime().exec(cmd);
    	BufferedReader in = new BufferedReader(
		  	      new InputStreamReader(p.getInputStream()) );
		for ( String s; (s = in.readLine()) != null; ){
			//System.out.println(s);
			if(s.indexOf("BUILD") > -1) {
				return true;
		  	}
 		}
    	return false;
    }

    private class BackgroundProcess extends Thread {

    	private String[] cmd;
    	private FileOutputStream fos;
    	
    	private boolean jettyStarted;

    	public BackgroundProcess(String[] cmd, FileOutputStream fos) {
    		this.cmd = cmd;
    		this.fos = fos;
    		jettyStarted = false;
    	}
    	public void run() {
            try {
            	Process p = Runtime.getRuntime().exec( cmd );
            	BufferedReader in = new BufferedReader(
    			  	      new InputStreamReader(p.getInputStream()) );
    			  	for ( String s; (s = in.readLine()) != null; ){
    			  		  if(s.indexOf("Started org.mortbay.jetty.servlet.WebApplicationHandler") > -1) {
    			  			  jettyStarted = true;
    			  		  }
    			  	      fos.write((s + NL).getBytes());
    			  	}
           	} catch (IOException e) {
           		e.printStackTrace();
            }
           	return;
    	}
    	
    	public boolean isJettyStarted() {
    		return jettyStarted;
    	}
    }
}