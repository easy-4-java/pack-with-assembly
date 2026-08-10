package main;

import service.FileLogger;

/**
 * Application entry point for the Java Service Wrapper (JSW) deployment.
 * Sets the working directory to the classpath root and invokes the file logger.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class MainClass {

    /**
     * Main method that bootstraps the JSW-wrapped application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        String workDir = FileLogger.class.getResource("/").getPath();
        System.setProperty("WORKDIR", workDir);

        FileLogger logger = new FileLogger();
        logger.logInfo2file();
    }

}
