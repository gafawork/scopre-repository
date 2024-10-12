package gafawork.easyfind.parallel;



import gafawork.easyfind.exception.ProductorGitlabInstanceException;
import gafawork.easyfind.main.Easyfind;
import gafawork.easyfind.util.Constantes;


import gafawork.easyfind.util.SearchVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gitlab4j.api.GitLabApiException;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.exit;

public class ProductorGitlab extends SearchGitlab implements Runnable {

    private static Logger logger = LogManager.getLogger();

    private static Object mutex = new Object();

    private static ProductorGitlab instance;

    private BlockingQueue<SearchVO> sharedQueue;

    private AtomicReference<String> sharedStatus;

    public ProductorGitlab(BlockingQueue<SearchVO> sharedQueue, AtomicReference<String> sharedStatus) {
        this.sharedQueue = sharedQueue;
        this.sharedStatus = sharedStatus;
    }

    public static void  abort() {
        callAbort();
    }

    public static ProductorGitlab getInstanceConfig(BlockingQueue<SearchVO> sharedQueue, AtomicReference<String> sharedStatus) {
        ProductorGitlab result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new ProductorGitlab(sharedQueue, sharedStatus);
            }
        }
        return result;
    }

    public static ProductorGitlab getInstance() throws ProductorGitlabInstanceException {
        ProductorGitlab result = instance;
        if (result == null) {
            throw new ProductorGitlabInstanceException("getInstanceConfig() not executed");
        }
        return result;
    }

    public void addSharedQueue(SearchVO searchVO) throws InterruptedException {
        sharedQueue.put(searchVO);
        Thread.sleep(1000);
    }

    public BlockingQueue<SearchVO> getSharedQueue() {
        return sharedQueue;
    }

    public void setSharedQueue(BlockingQueue<SearchVO> sharedQueue) {
        this.sharedQueue = sharedQueue;
    }

    public AtomicReference<String> getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(AtomicReference<String> sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    @Override
    public void run() {
        try {
            searchPrincipal();
            sharedStatus.set(Constantes.FINISH);
            logger.info("SEARCH COMPLEATE");

            while (!sharedStatus.get().equals(Constantes.FINISH) || !sharedQueue.isEmpty()) {
                Thread.sleep(1000);
            }

            Easyfind.shutdown();

        } catch (GitLabApiException | InterruptedException | IOException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
