/**
 *
 */

package gafawork.easyfind.util;

import gafawork.easyfind.parallel.ConsumerGitlab;
import gafawork.easyfind.parallel.ProductorGitlab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("java:S6548")
public class Monitor {
    private static Logger logger = LogManager.getLogger();

    private static Monitor instance;

    private static Object mutex = new Object();

    private static List<ErroVO> listErros = new ArrayList<>();

    private static ConcurrentMap<Long, SearchDetail> searchDetails = new ConcurrentHashMap <>();

    private static List<ProductorGitlab> listProductor = new ArrayList<>();

    private static List<ConsumerGitlab> listConsumer = new ArrayList<>();

    private static int totalProject = 0;

    private static int totalParallel = 0;

    private Monitor() {

    }

    public static Monitor getInstance() {
        Monitor result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Monitor();
            }
        }
        return result;
    }
@SuppressWarnings("java:S1192")
    public static void report() throws IOException {
        int totalSearch = 0;

        Iterator<Long> iteratorSearchDetail = searchDetails.keySet().iterator();

        WriteFile.getInstance().writeTxt("Easyfind by Tirso Andrade");
        WriteFile.getInstance().writeTxt("=============================================");
        if(Parameters.getFilters() != null)
            WriteFile.getInstance().writeTxt("Filter: " + Parameters.getFilters());

        WriteFile.getInstance().writeTxt("Search: " + Arrays.toString(Parameters.getTexts()));

        while (iteratorSearchDetail.hasNext()) {
            Long searchDetailId = iteratorSearchDetail.next();

            SearchDetail searchDetail = searchDetails.get(searchDetailId);

            if(searchDetail.getReferences() > 0) {
                totalSearch++;
                WriteFile.getInstance().writeTxt("=============================================");
                WriteFile.getInstance().writeTxt("Id Project:" + searchDetail.getId());
                WriteFile.getInstance().writeTxt("Name Project:" + searchDetail.getNome());
                WriteFile.getInstance().writeTxt("Name Branch:" + searchDetail.getBranch());
                WriteFile.getInstance().writeTxt("Path:" + searchDetail.getPath());
                WriteFile.getInstance().writeTxt("url:" + searchDetail.getUrl());
                WriteFile.getInstance().writeTxt("references:" + searchDetail.getReferences());

                Iterator<Long> iteratorLines = searchDetail.getLines().keySet().iterator();

                while (iteratorLines.hasNext()) {
                    Long searchDetailLinesId = iteratorLines.next();
                    String searchDetailLine = searchDetail.getLines().get(searchDetailLinesId);

                    WriteFile.getInstance().writeTxt("     " + searchDetailLine);
                }

            }
        }
        WriteFile.getInstance().writeTxt("=============================================");
        WriteFile.getInstance().writeTxt("Projects Total:" + searchDetails.size());
        WriteFile.getInstance().writeTxt("Projects Found:" + totalSearch);
        WriteFile.getInstance().writeTxt("=============================================");
    }

    public static void addProductor(ProductorGitlab productorGitlab) {
        listProductor.add(productorGitlab);
    }

    public static void addConsumer(ConsumerGitlab consumerGitlab) {
        listConsumer.add(consumerGitlab);
    }

    public static int getTotalProject() {
        return totalProject;
    }

    public static void setTotalProject(int totalProject) {
        Monitor.totalProject = totalProject;
    }

    public static int getTotalParallel() {
        return totalParallel;
    }

    public static void setTotalParallel(int totalParallel) {
        Monitor.totalParallel = totalParallel;
    }

    public static List<ProductorGitlab> getListaProductor() {
        return listProductor;
    }

    public static List<ConsumerGitlab> getListaConsumer() {
        return listConsumer;
    }

    public static void abort() {
        logger.info("realizando Monitor abort produtor") ;// produtores

        ProductorGitlab.callAbort();

        // consumidoras
        Iterator<ConsumerGitlab> iteratorConsumer = listConsumer.iterator();
        while (iteratorConsumer.hasNext()) {
            logger.info("realizando Monitor abort consumidores"); // produtores
            ConsumerGitlab consumerGitlab = iteratorConsumer.next();
            consumerGitlab.callAbort();
        }
    }

    public static List<ErroVO> getListaErros() {
        return Monitor.listErros;
    }

    public static ConcurrentMap<Long, SearchDetail> getSearchDetails() {
        return Monitor.searchDetails;
    }

    public static void addErro(ErroVO erroVO) {
        Monitor.getListaErros().add(erroVO);
    }

    public static void addSearchDetail(SearchDetail searchDetail) {
        Monitor.getSearchDetails().put(searchDetail.getId(), searchDetail);
    }

}
