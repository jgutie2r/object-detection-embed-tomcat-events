package serverless;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;


import serverless.function.ObjectDetectCloudEventListener;
import serverless.logging.Logback;


import java.io.File;
import java.io.FileNotFoundException;

import jakarta.servlet.events.CloudEventListener;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.filters.FilterUtils;
import org.apache.catalina.startup.Tomcat;
import org.opencv.core.Core;

public class Function {
    private static Logger log = LoggerFactory.getLogger(Function.class);
    public static void main(String[] args) throws LifecycleException, FileNotFoundException {
        // Build a Tomcat instance
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            log.info("Loaded {}", Core.NATIVE_LIBRARY_NAME);
        } catch (final UnsatisfiedLinkError e) {
            log.error("loadLibrary ", e.getMessage());
        }

        int port = 8080;

        // Where this endpoint is served
        String baseURL = "object-detect-fn-events";

        // Path to health
        String healthPath = "/health";

        String logLevel = "INFO";

        Logback.setLevel(logLevel);
        
        // Tomcat configuration
        Tomcat tomcat = new Tomcat();
        Connector connector1 = tomcat.getConnector();
        connector1.setPort(port);

        tomcat.setBaseDir(".");

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        CloudEventListener listener = new ObjectDetectCloudEventListener();

        String listenerName = "EventListener";

        // Register EventListener in Tomcat
        Wrapper w = tomcat.addServlet(contextPath, listenerName, listener);
 
        // Add URL mapping to servlet
        w.addMapping(baseURL);

        // Add health filter
        FilterUtils.addHealthFilter(context, w, healthPath);
 
        // Start Tomcat
        tomcat.start();
        tomcat.getServer().await();
    }
}