package gafawork.easyfind.parallel;

import gafawork.easyfind.util.Monitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.System.exit;

public abstract class AbortUtil {
    protected static Logger logger = LogManager.getLogger();

    private static volatile boolean abort = false;

    public static boolean isAbort() {
        return abort;
    }

    public static void callAbort() {
        abort = true;
    }

    protected AbortUtil() {

    }

    protected static void verifyAbort() {
        if (abort) {
            logger.error("Thread producer - Abort");
            executeAbort();
        }
    }

    protected static void executeAbort() {
            logger.error("Thread producer - Abort");
            Monitor.abort();

            exit(1);
    }


}


