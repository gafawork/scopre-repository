/**
 *
 */

package gafawork.easyfind.parallel;

import gafawork.easyfind.exception.ProductorGitlabInstanceException;
import gafawork.easyfind.util.*;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("java:S1118")
public class SearchGitlab extends  Abort {

    private static void addError(String projectName, String projectId, String projectBranchName, String path, String msgErro) {
        ErroVO erroVO = new ErroVO();
        erroVO.setProjetName(projectName);
        erroVO.setMsgErros(msgErro);
        erroVO.setPath(path);
        erroVO.setProjectId(projectId);
        erroVO.setProjetctBranchName(projectBranchName);

        Monitor.addErro(erroVO);
    }

    @SuppressWarnings("java:S1192")
    public static void searchBranches(SearchDetail searchDetail, Project project, List<Branch> branches) throws InterruptedException {
        try {
            for (Iterator<Branch> iter = branches.iterator(); iter.hasNext(); ) {
                verifyAbort();

                Branch branch = iter.next();

                SearchDetail searchDetailClone = searchDetail.cloneBean();

                searchDetailClone.setBranch(branch.getName());
                SearchVO searchVO = setSearchVO(searchDetailClone, project, branch);

                if (Parameters.getSearchBranches() != null) {
                    filterBranches(searchDetailClone, project, branch);
                } else {
                    Monitor.addSearchDetail(searchDetailClone);

                    String msgLog = String.format("Progress: %s"  , Monitor.getSearchDetails().size());
                    logger.info(msgLog);
                    ProductorGitlab.getInstance().addSharedQueue(searchVO);
                }
            }
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage());
        } catch (Exception e) {
            addError(searchDetail.getNome(), searchDetail.getId().toString(), searchDetail.getBranch(), searchDetail.getPath(), e.getMessage());

            executeAbort();
            String msgLog = String.format("Progress: %s - Branch: %s -  Progress: %s"  , searchDetail.getNome(), searchDetail.getBranch(), Monitor.getSearchDetails().size());
            logger.info(msgLog);
        }
    }

    private static void filterBranches(SearchDetail searchDetail, Project project, Branch branch) throws ProductorGitlabInstanceException, InterruptedException {
        for (int i = 0; i < Parameters.getSearchBranches().length; i++) {
            verifyAbort();

            if (searchDetail.getBranch().equals(Parameters.getSearchBranches()[i])) {
           Monitor.addSearchDetail(searchDetail);

                String msgLog = String.format("Progress: %s"  , Monitor.getSearchDetails().size());
                logger.info(msgLog);

                SearchVO searchVO = setSearchVO(searchDetail, project, branch);
                ProductorGitlab.getInstance().addSharedQueue(searchVO);
                break;
            }
        }
    }

    private static SearchVO setSearchVO(SearchDetail searchDetail, Project projectId, Branch branchId) {
        SearchVO searchVO = new SearchVO();
        searchVO.setSearchDetail(searchDetail);
        searchVO.setGitlabapi(UtilGitlab.getGitlabApi());
        searchVO.setBranch(branchId);
        searchVO.setProject(projectId);
        searchVO.setTexts(Parameters.getTexts());
        searchVO.setFilters(Parameters.getFilters());
        return searchVO;
    }

    public static void searchPrincipal() throws GitLabApiException, InterruptedException {
        if (Parameters.getProjectNames() == null)
            searchPrincipalPaged();
        else
            searchPrincipalWithoutPaged();
    }

    private static void searchPrincipalPaged() throws GitLabApiException, InterruptedException {

        List<Project> projects = null;

        Pager<Project> projectPager = UtilGitlab.getGitlabApi().getProjectApi().getProjects(10);
        while (projectPager.hasNext()) {

            projects = projectPager.next();

            for (Iterator<Project> iter = projects.iterator(); iter.hasNext(); ) {

                try {
                    Project project = iter.next();

                    String msgLog = String.format("Project: %s - ProjectId: %s - Path: %s - Thread: %s"  , project.getName(), project.getId() , project.getPath(),  Thread.currentThread().threadId());
                    logger.info(msgLog);

                    SearchDetail searchDetail = new SearchDetail(project.getId(), project.getName(), project.getPath(), "", project.getHttpUrlToRepo());

                    List<Branch> branches = UtilGitlab.getGitlabApi().getRepositoryApi().getBranches(project);

                    msgLog = String.format("total branches: %s"  , branches.size());
                    logger.info(msgLog);

                    logger.info("=======================================");

                    searchBranches(searchDetail, project, branches);

                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private static void searchPrincipalWithoutPaged() throws GitLabApiException, InterruptedException {
        Thread.sleep(1000);
        logger.info("estou no produtor");

        List<Project> projects = null;

        projects = new ArrayList<Project>();
        for (int i = 0; i < Parameters.getProjectNames().length; i++) {
            ArrayList<Project> listTemp = (ArrayList<Project>) UtilGitlab.getGitlabApi().getProjectApi().getProjects( Parameters.getProjectNames()[i] );
            projects.addAll(listTemp);
        }

        String msgLog = String.format("Total Project: %s " , projects.size());
        logger.info(msgLog);
        logger.info("===============================================");

        for (Iterator<Project> iter = projects.iterator(); iter.hasNext(); ) {

            try {

                Project project = iter.next();

                msgLog = String.format("Project: %s - Project Id: %s - Path: %s"  , project.getName(),  project.getId() , project.getPath() );
                logger.info(msgLog);

                SearchDetail searchDetail = new SearchDetail(project.getId(), project.getName(), project.getPath(), "", project.getHttpUrlToRepo());

                List<Branch> branches = UtilGitlab.getGitlabApi().getRepositoryApi().getBranches(project);
                msgLog = String.format("Total branches: %s " , branches.size());
                logger.info(msgLog);
                logger.info("===============================================");

                searchBranches(searchDetail, project, branches);

            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }
}
