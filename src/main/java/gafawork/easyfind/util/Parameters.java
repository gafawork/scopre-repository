package gafawork.easyfind.util;

import org.apache.commons.cli.*;

import java.util.Arrays;

@SuppressWarnings("java:S6548")
public class Parameters {

    private static boolean debug = false;
    private static String[]  projectNames = null;
    private static String token = null;
    private static String parallel = null;

    private static String classPlugin = null;

    private static String[] texts = null;
    private static String[] filters = null;
    private static String[] searchBranches = null;

    private static String fileFilter = null;

    private static String hostUrl = null;
    private static Options options = new Options();

    private static Parameters instance;

    private static Object mutex = new Object();

    private Parameters() {

    }


    private static Parameters getInstance() {
        Parameters result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Parameters();
            }
        }
        return result;
    }

    @SuppressWarnings("java:S1192")
    public static void validateParameters(String[] args) {

        getInstance();

        options.addOption(Option.builder("c")
                .longOpt("classPlugin")
                .hasArg(true)
                .desc("classPlugin")
                .required(false)
                .build());


        options.addOption(Option.builder("d")
                .longOpt("debug")
                .hasArg(false)
                .desc("debug")
                .required(false)
                .build());

        options.addOption(Option.builder("n")
                .longOpt("projectName")
                .hasArg(true)
                .desc("project name")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .required(false)
                .build());

        options.addOption(Option.builder("t")
                .longOpt("token")
                .hasArg(true)
                .desc("token ([REQUIRED])")
                .required(true)
                .build());

        options.addOption(Option.builder("h")
                .longOpt("hostUrl")
                .hasArg(true)
                .desc("hostUrl ([REQUIRED])")
                .required(true)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("parallel")
                .hasArg(true)
                .desc("parallel ([REQUIRED])")
                .required(true)
                .build());

        options.addOption(Option.builder("f")
                .longOpt("filter")
                .hasArg(true)
                .desc("filter")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .build());

        options.addOption(Option.builder("s")
                .longOpt("search")
                .hasArg(true)
                .desc("debug")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .required(false)
                .build());

        options.addOption(Option.builder("b")
                .longOpt("searchBranches")
                .hasArg(true)
                .desc("branches")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());

        options.addOption(Option.builder("i")
                .longOpt("inputFile")
                .hasArg(true)
                .desc("input file filter")
                .required(false)
                .build());

        parseParameter(options, args);
    }

    static void parseParameterDebug(CommandLine cmd) {
        if (cmd.hasOption("d")) {
            debug = true;
            System.out.println("-d option = true");
        }
    }
    @SuppressWarnings("java:S2681")
    private static void parseParameterNameProject(CommandLine cmd) {
        if (cmd.hasOption("n")) {
            projectNames = cmd.getOptionValues("n");
            if (debug)
                System.out.println("Number of project(s):" + (projectNames.length));
                System.out.println("Search project(s):" + String.join(",", Arrays.asList(projectNames)));

            if (!cmd.hasOption("n")) {
                System.out.println("-n project name is not defined");
            }
        }
    }

    @SuppressWarnings("java:S2681")
    private static void parseParameterClassPlugin(CommandLine cmd) {
        if (cmd.hasOption("c")) {
            classPlugin = cmd.getOptionValue("c");
            if (debug)
                System.out.println("-c option = " + classPlugin);

            if (!cmd.hasOption("c")) {
                System.out.println("-c class plugin is not defined");
            }
        }
    }

    private static void parseParameterParallel(CommandLine cmd) {
        if (cmd.hasOption("p")) {
            parallel = cmd.getOptionValue("p");
            if (debug)
                System.out.println("-p option = " + parallel);

            if (!cmd.hasOption("p")) {
                System.out.println("-p parallel option is not defined");
            }
        }
    }

    private static void parseParameterFileFilter(CommandLine cmd) {
        if (cmd.hasOption("i")) {
            fileFilter = cmd.getOptionValue("i");
            if (debug)
                System.out.println("-i option = " + fileFilter);

            if (!cmd.hasOption("i")) {
                System.out.println("-i input file filter option is not defined");
            }
        }
    }

    private static void parseParameterToken(CommandLine cmd) {
        if (cmd.hasOption("t")) {
            token = cmd.getOptionValue("t");
            if (debug)
                System.out.println("-t option = " + token);

            if (!cmd.hasOption("t")) {
                System.out.println("-t token option is not defined");
            }
        }
    }

    private static void parseParameterHostUrl(CommandLine cmd) {
        if (cmd.hasOption("h")) {
            hostUrl = cmd.getOptionValue("h");
            if (debug)
                System.out.println("-h option = " + hostUrl);

            if (!cmd.hasOption("h")) {
                System.out.println("-h hostUrl option is not defined");
            }
        }
    }
    @SuppressWarnings("java:S2681")
    private static void parseParameterFilter(CommandLine cmd) {
        if (cmd.hasOption("f")) {
            filters = cmd.getOptionValues("f");
            if (debug)
                System.out.println("Number of filter(s):" + (filters.length));
                System.out.println("Search filter(s):" + String.join(",", Arrays.asList(filters)));

            if (!cmd.hasOption("f")) {
                System.out.println("-t filter option is not defined");
            }
        }
    }

    private static void parseParameterBranch(CommandLine cmd) {
        if (cmd.hasOption("b")) {
            searchBranches = cmd.getOptionValues("b");
            if (debug) {
                System.out.println("Number of branche(s):" + (searchBranches.length));
                System.out.println("Search branche(s):" + String.join(",", Arrays.asList(searchBranches)));
            }
        }
    }

    private static void parseParameterSearch(CommandLine cmd) {
        if (cmd.hasOption("s")) {
            texts = cmd.getOptionValues("s");
            if (debug) {
                System.out.println("Number of search(s):" + (texts.length));
                System.out.println("text(s):" + String.join(",", Arrays.asList(texts)));
            }
        } else {

            String msg = """
                    please specify one of the command line options: 
                    -n <arg>
                    OR 
                    -t <arg>
                    OR 
                    -p <arg>
                    OR
                    -f <arg>
                    OR
                    -i <arg>
                    OR
                    -s <arg>""";

            System.out.println(msg);
        }
    }

    private static void parseParameter(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            parseParameterDebug(cmd);
            parseParameterNameProject(cmd);
            parseParameterParallel(cmd);
            parseParameterToken(cmd);
            parseParameterHostUrl(cmd);
            parseParameterFilter(cmd);
            parseParameterBranch(cmd);
            parseParameterSearch(cmd);
            parseParameterFileFilter(cmd);
            parseParameterClassPlugin(cmd);


        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println("Please, follow the isntructions below");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Search gitlab", options);
            System.exit(1);
        }

    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Parameters.debug = debug;
    }

    public static String[] getProjectNames() {
        return projectNames;
    }

    public static void setProjectNames(String[] projectNames) {
        Parameters.projectNames = projectNames;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Parameters.token = token;
    }

    public static String getParallel() {
        return parallel;
    }

    public static void setParallel(String parallel) {
        Parameters.parallel = parallel;
    }

    public static String[] getTexts() {
        return texts;
    }

    public static void setTexts(String[] texts) {
        Parameters.texts = texts;
    }

    public static String[] getFilters() {
        return filters;
    }

    public static void setFilter(String[] filters) {
        Parameters.filters = filters;
    }

    public static String[] getSearchBranches() {
        return searchBranches;
    }

    public static void setSearchBranches(String[] searchBranches) {
        Parameters.searchBranches = searchBranches;
    }

    public static Options getOptions() {
        return options;
    }

    public static void setOptions(Options options) {
        Parameters.options = options;
    }

    public static String getHostUrl() {
        return hostUrl;
    }

    public static void setHostUrl(String hostUrl) {
        Parameters.hostUrl = hostUrl;
    }

    public static String getFilefilter() {
        return fileFilter;
    }

    public static void setFilefilter(String filefilter) {
        Parameters.fileFilter = filefilter;
    }

    public static String getClassPlugin() {
        return classPlugin;
    }

    public static void setClassPlugin(String classPlugin) {
        Parameters.classPlugin = classPlugin;
    }
}