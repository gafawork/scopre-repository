package gafawork.scopre.repository.util;

public class ErroVO {
    private volatile String projetName;
    private volatile String projectId;
    private volatile String projetctBranchName;
    private volatile String path;
    private volatile String msgErros;

    public String getProjetName() {
        return projetName;
    }

    public void setProjetName(String projetName) {
        this.projetName = projetName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjetctBranchName() {
        return projetctBranchName;
    }

    public void setProjetctBranchName(String projetctBranchName) {
        this.projetctBranchName = projetctBranchName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMsgErros() {
        return msgErros;
    }

    public void setMsgErros(String msgErros) {
        this.msgErros = msgErros;
    }
}
