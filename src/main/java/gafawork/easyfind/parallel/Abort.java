package gafawork.easyfind.parallel;

import gafawork.easyfind.util.Monitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.System.exit;

abstract class Abort {
    protected static Logger logger = LogManager.getLogger();

    private static volatile boolean abort = false;

    public static boolean isAbort() {
        return abort;
    }

    public static void setAbort() {
        abort = true;
    }

    protected static void verifyAbort() throws InterruptedException {
        if (abort) {
            logger.error("Thread producer - Abort");
            //Thread.sleep(1000);

            // TODO TEMOVER
            //Thread.currentThread().interrupt();
            executeAbort();

        }
    }

    protected static void executeAbort() throws InterruptedException {
            logger.error("Thread producer - Abort");
            //Thread.sleep(1000);

            // TODO TEMOVER
            //Thread.currentThread().interrupt();
            Monitor.abort();
            exit(1);
    }


}


