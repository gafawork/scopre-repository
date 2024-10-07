/**
 *
 */
package gafawork.easyfind.main;

import gafawork.easyfind.exception.ProductorGitlabInstanceException;
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

    static BlockingQueue<SearchVO> queue;

    static AtomicReference<String> status;

    public static void main(String[] args) throws IOException, InterruptedException, ProductorGitlabInstanceException {


        System.out.println("Easyfind for Gitlab by Gafawork");
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
        // TODO VERIFICAR
        //shutdown();

        // VERIFICAR SE É NECESSÁRIO
        //executor.awaitTermination(1, TimeUnit.MINUTES);
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
        queue = new LinkedBlockingQueue<>();
        status = new AtomicReference<>(Constantes.PRODUCING);
    }

    public static void addProductor() {
        ProductorGitlab produtorGitlab = ProductorGitlab.getInstanceConfig(queue, status);
        produtorGitlab.setSharedStatus(status);
        produtorGitlab.setSharedQueue(queue);
        executor.execute(produtorGitlab);

        Monitor.addProductor(produtorGitlab);
    }

    public static void addConsumer() {
        for (int i = 1; i <= totalThreads; i++) {
            ConsumerGitlab consumerGitlab = new ConsumerGitlab(queue, status);
            executor.execute(consumerGitlab);
            Monitor.addConsumer(consumerGitlab);
        }
    }

    @SuppressWarnings("java:S2142")
    public static void shutdown() throws InterruptedException, IOException {
        logger.info("shutdown");

        Monitor.abort();

        executor.shutdown();

       // try {
       //     if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
       //         executor.shutdownNow();
       //     }
       // } catch (InterruptedException e) {
       //     executor.shutdownNow();
       // }

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
                } catch (InterruptedException | IOException e) {
                    logger.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private static void configureLoggers() {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);

        logger.debug("DEBUG mode");
    }
}
