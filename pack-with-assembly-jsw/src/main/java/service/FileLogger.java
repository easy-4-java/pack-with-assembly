package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that writes informational log messages to a file via SLF4J.
 * Produces 100 numbered log entries on each invocation.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(FileLogger.class);

    /**
     * Writes 100 informational log lines to the configured log output.
     */
    public void logInfo2file() {
        for (int i = 0; i < 100; i++) {
            logger.info("我的测试：my test" + i);
        }
    }
}
