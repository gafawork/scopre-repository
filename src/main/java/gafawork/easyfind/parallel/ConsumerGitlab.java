package gafawork.easyfind.parallel;

import gafawork.easyfind.util.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.TreeItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsumerGitlab implements Runnable {

    private static Logger logger = LogManager.getLogger();

    private  BlockingQueue<SearchVO> sharedQueue;

    private  AtomicReference<String> sharedStatus;

    private volatile boolean abort = false;


    public ConsumerGitlab(BlockingQueue<SearchVO> sharedQueue, AtomicReference<String> sharedStatus) {
        this.sharedQueue = sharedQueue;
        this.sharedStatus = sharedStatus;
    }

    public boolean isAbort() {
        return abort;
    }

    public void abort() {
        abort = true;
    }

    public boolean search(SearchDetail searchDetail, Pattern pattern, String path, int posLine, String line) {
        boolean retorno = false;

        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            retorno = true;

            searchDetail.encontrado();

            String outArquivo = "Path:" + path;
            String outLinha = "Line:" + posLine + " - " + line;

            String msgLog = String.format("Consumer Thread: %s  - find:  %s", Thread.currentThread().threadId(), outLinha);
            logger.info(msgLog);

            searchDetail.addLine(outArquivo);
            searchDetail.addLine(outLinha);
        }

        return retorno;
    }

    @Override
    public void run() {
        try {
            while (!sharedStatus.get().equals(Constantes.FINISH) || !sharedQueue.isEmpty()) {
                SearchVO searchVO = sharedQueue.poll();
                if (searchVO != null) {
                    String msgLog = String.format("Consumer Thread:  %s - Branch: %s", Thread.currentThread().threadId(), searchVO.getBranch().getName());
                    logger.info(msgLog);

                    searchBranch(searchVO);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void searchBranch(SearchVO searchVO) {
        try {
            String msgLog = String.format("SearchBranch - tree - Thread : %s", Thread.currentThread().threadId());
            logger.info(msgLog);

            SearchDetail searchDetail = searchVO.getSearchDetail();

            List<TreeItem> tree = null;
            tree = searchVO.getGitlabapi().getRepositoryApi().getTree(searchVO.getProject(), "", searchVO.getBranch().getName(), true);

            for (Iterator<TreeItem> iter = tree.iterator(); iter.hasNext(); ) {
                verifyAbort();

                TreeItem treeItem = iter.next();
                if (searchVO.getFilter() != null) {
                    if (treeItem.getType() != TreeItem.Type.TREE && treeItem.getName().matches(searchVO.getFilter())) {
                        searchAux(searchVO, treeItem, searchVO.getProject(), searchVO.getBranch(), searchVO.getTexts());
                    }
                } else {
                    if (treeItem.getType() != TreeItem.Type.TREE)
                        searchAux(searchVO, treeItem, searchVO.getProject(), searchVO.getBranch(), searchVO.getTexts());
                }
            }

            writeCSV(searchDetail);

        } catch (InterruptedException ex) {
            logger.error(ex.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error(e.getMessage());

            String msgLog = String.format("Thread Interrupted:  %s - Project: %s  - Branch: %s", Thread.currentThread().threadId(), searchVO.getProject().getName() ,  searchVO.getBranch().getName());
            logger.info(msgLog);
        }

        String msgLog = String.format("SearchBranch - tree - Thread Finish: %s", Thread.currentThread().threadId());
        logger.info(msgLog);
    }

    private void verifyAbort() throws InterruptedException {
        if (abort) {
            logger.error("Thread Consumer - abort");
            Thread.sleep(1000);
            Thread.currentThread().interrupt();
        }
    }

    private void writeCSV(SearchDetail searchDetail) throws IOException {
        WriteFile writeCSV = WriteFile.getInstance();

        if (!searchDetail.getLines().isEmpty()) {
            synchronized (writeCSV) {
                writeCSV.writeCSV(searchDetail);
            }
        }
    }

    private void searchAux(SearchVO searchVO, TreeItem treeItem, Project project, Branch branch, String[] texts) throws IOException, GitLabApiException {
        logger.info(treeItem.getName());
        InputStream is = null;

        is = searchVO.getGitlabapi().getRepositoryFileApi().getRawFile(project.getId(), branch.getName(), treeItem.getPath());

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        int lineID = 0;

        String line = null;

        while ((line = br.readLine()) != null) {
            lineID++;

            for (int i = 0; i < texts.length; i++) {
                search(searchVO.getSearchDetail(), getPattern(texts[i]), treeItem.getPath(), lineID, line);
            }
        }

        br.close();
        is.close();
    }

    public Pattern getPattern(String regex) {
        return Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
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
        this.sharedStatus= sharedStatus;
    }

    public void addSharedQueue(SearchVO searchVO) throws InterruptedException {
        sharedQueue.put(searchVO);
        Thread.sleep(1000);
    }

}
