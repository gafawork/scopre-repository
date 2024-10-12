/**
 *
 */
package gafawork.easyfind.main;

import gafawork.easyfind.parallel.ConsumerGitlab;
import gafawork.easyfind.parallel.ProductorGitlab;

import gafawork.easyfind.plugin.ExecutePlugin;
import gafawork.easyfind.util.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Easyfind {

    private static Logger logger = LogManager.getLogger();

    static int totalThreads;

    static ExecutorService executorPrincipal;

    static ExecutorService executorPlugin;

    static BlockingQueue<SearchVO> sharedQueue;

    static AtomicReference<String> sharedStatus;


    public static void main(String[] args)  {
        System.out.println("Inventio Repository for Gitlab by Gafawork");
        System.out.println("-------------------------------");
        System.out.println("");

        setup(args);
        addProductor();
        addConsumer();
        registerShutdownHook();
    }


    public static void setup(String[] args) {
        Parameters.validateParameters(args);

        if(Parameters.isDebug()) {
            configureLoggers();
        }

        UtilGitlab.connect();

        Parameters.getProjectNames();

        totalThreads = Integer.parseInt(Parameters.getParallel());

        Monitor.setTotalParallel(totalThreads);

        executorPrincipal = Executors.newFixedThreadPool(totalThreads + 1);
        sharedQueue = new LinkedBlockingQueue<>();
        sharedStatus = new AtomicReference<>(Constantes.PRODUCING);

        executorPlugin = Executors.newFixedThreadPool(totalThreads + 1);

    }

    public static void addProductor() {
        ProductorGitlab produtorGitlab = ProductorGitlab.getInstanceConfig(sharedQueue, sharedStatus);
        produtorGitlab.setSharedStatus(sharedStatus);
        produtorGitlab.setSharedQueue(sharedQueue);
        executorPrincipal.execute(produtorGitlab);

        Monitor.addProductor(produtorGitlab);
    }

    public static void addConsumer() {
        for (int i = 1; i <= totalThreads; i++) {
            ConsumerGitlab consumerGitlab = new ConsumerGitlab(sharedQueue, sharedStatus);
            executorPrincipal.execute(consumerGitlab);
            Monitor.addConsumer(consumerGitlab);
        }
    }

    public static void addPlugin(String name, String path, String branch, String url, String rule) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String classNamePlugin = Parameters.getClassPlugin();

        if (classNamePlugin != null && !classNamePlugin.equals("")) {
            var clazz = Class.forName(classNamePlugin);
            ExecutePlugin plugin = (ExecutePlugin) clazz.getDeclaredConstructor().newInstance();

            plugin.setProjectName(name);
            plugin.setBranchName(branch);
            plugin.setPath(path);
            plugin.setUrl(url);
            plugin.setRule(rule);

            plugin.run();

            Monitor.addPluign(plugin);

            executorPlugin.execute(plugin);
        }
    }



    @SuppressWarnings("java:S2142")
    public static void shutdown() throws  IOException {
        logger.info("shutdown");
        Monitor.abort();
        executorPrincipal.shutdown();
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
