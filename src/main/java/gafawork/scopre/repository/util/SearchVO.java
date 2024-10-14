package gafawork.scopre.repository.util;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;

public class SearchVO {

    private  GitLabApi gitlabapi;

    private  Project project;

    private  Branch branch;

    private  String[] texts;

    private  String[] filter;

    private  SearchDetail searchDetail;

    public GitLabApi getGitlabapi() {
        return gitlabapi;
    }

    public void setGitlabapi(GitLabApi gitlabapi) {
        this.gitlabapi = gitlabapi;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String[] getTexts() {
        return texts;
    }

    public void setTexts(String[] texts) {
        this.texts = texts;
    }

    public String[] getFilters() {
        return filter;
    }

    public void setFilters(String[] filter) {
        this.filter = filter;
    }

    public SearchDetail getSearchDetail() {
        return searchDetail;
    }

    public void setSearchDetail(SearchDetail searchDetail) {
        this.searchDetail = searchDetail;
    }
}
