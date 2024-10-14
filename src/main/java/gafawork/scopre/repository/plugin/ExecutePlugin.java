package gafawork.scopre.repository.plugin;

import gafawork.scopre.repository.parallel.AbortUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ExecutePlugin extends AbortUtil  implements Runnable {
    protected static Logger logger = LogManager.getLogger();

    private String projectName;

    private String branchName;

    private String path;

    private String rule;

    private String line;

    private String url;

    public static void  abort() {
        callAbort();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }



     public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
