package main;

import service.FileLogger;

/**
 * Windows Service Wrapper entry point for the Tomcat deployment.
 * Sets the working directory to the classpath root and invokes the file logger.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WrapperMainClassForWindows {

    /**
     * Main method invoked by the Windows Service Wrapper.
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
