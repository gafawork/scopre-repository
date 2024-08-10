package gafawork.easyfind.util;

import org.gitlab4j.api.GitLabApi;

public class UtilGitlab {
    private static GitLabApi gitlabapi = null;

    private UtilGitlab() {
    }

    public static void connect() {
        gitlabapi = new GitLabApi(Parameters.getHostUrl(), Parameters.getToken());
        gitlabapi.setIgnoreCertificateErrors(true);
        gitlabapi.setRequestTimeout(300000, 300000);

    }


    public static void logout() {
        UtilGitlab.gitlabapi.close();
    }

    public static GitLabApi getGitlabApi() {
        return gitlabapi;
    }
}
