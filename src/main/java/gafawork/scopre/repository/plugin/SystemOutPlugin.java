package gafawork.scopre.repository.plugin;

public class SystemOutPlugin extends ExecutePlugin {

    @Override
    public void run() {
        String msg = "<Execute Plugin>\n Project: " + getProjectName() + "\n"
                + " Branch: " + getBranchName()
                + " \n Path: " + getPath()
                + "\n Url: " + getUrl()
                + "\n Rule: " + getRule();

        logger.info(msg);
    }
}
