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
            executeAbort();
        }
    }

    protected static void executeAbort() throws InterruptedException {
            logger.error("Thread producer - Abort");
            Monitor.abort();

            // TODO VERIFICAR
            exit(1);
    }


}


