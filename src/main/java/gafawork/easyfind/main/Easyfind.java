/**
 *
 */
package gafawork.easyfind.main;


import gafawork.easyfind.parallel.ConsumerGitlab;
import gafawork.easyfind.parallel.ProductorGitlab;

import gafawork.easyfind.util.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Easyfind {

    private static Logger logger = LogManager.getLogger();

    static int totalThreads;

    static ExecutorService executor;

    static BlockingQueue<SearchVO> sharedQueue;

    static AtomicReference<String> sharedStatus;


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Inventio Repository for Gitlab by Gafawork");
        System.out.println("-------------------------------");
        System.out.println("");

        System.out.println("SETUP");
        setup(args);

        System.out.println("ADD PRODUCTOR");
        addProductor();

        System.out.println("ADD CONSUMER");
        addConsumer();

        System.out.println("REGISTER SHUTDOWNHOOK");
        registerShutdownHook();

        System.out.println("SETUP");

        // TODO VERIFICAR SE É NECESSÁRIO
        executor.awaitTermination(1, TimeUnit.MINUTES);

    }


    public static void setup(String[] args) {
        Parameters.validateParameters(args);

        if(Parameters.isDebug()) {
            configureLoggers();
        }

        UtilGitlab.connect();

        totalThreads = Integer.parseInt(Parameters.getParallel());

        Monitor.setTotalParallel(totalThreads);

        executor = Executors.newFixedThreadPool(totalThreads + 1);
        sharedQueue = new LinkedBlockingQueue<>();
        sharedStatus = new AtomicReference<>(Constantes.PRODUCING);
    }

    public static void addProductor() {
        ProductorGitlab produtorGitlab = ProductorGitlab.getInstanceConfig(sharedQueue, sharedStatus);
        produtorGitlab.setSharedStatus(sharedStatus);
        produtorGitlab.setSharedQueue(sharedQueue);
        executor.execute(produtorGitlab);

        Monitor.addProductor(produtorGitlab);
    }

    public static void addConsumer() {
        for (int i = 1; i <= totalThreads; i++) {
            ConsumerGitlab consumerGitlab = new ConsumerGitlab(sharedQueue, sharedStatus);
            executor.execute(consumerGitlab);
            Monitor.addConsumer(consumerGitlab);
        }
    }

    @SuppressWarnings("java:S2142")
    public static void shutdown() throws  IOException {
        logger.info("shutdown");
        Monitor.abort();
        executor.shutdown();
        Monitor.report();
    }

    @SuppressWarnings("java:S2142")
    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("shutdownHook in action");
                try {
                    Easyfind.shutdown();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    private static void configureLoggers() {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);

        logger.debug("DEBUG mode");
    }
}
