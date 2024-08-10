package gafawork.easyfind.util;


import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("java:S6548")
public class WriteFile {

    private static WriteFile instance;

    private static Object mutex = new Object();

    private String filenameCSV ;

    private String filenameReport ;


    private WriteFile() {

    }

    public static WriteFile getInstance() {
        WriteFile result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new WriteFile();
                    instance.setFilenameCSV(new SimpleDateFormat("'report-'yyyy-MM-dd hh-mm-ss'.csv'").format(new Date()));
                    instance.setFilenameReport(new SimpleDateFormat("'report-'yyyy-MM-dd hh-mm-ss'.txt'").format(new Date()));
                }
            }
        }
        return result;
    }

    public void writeCSV(SearchDetail searchDetail) throws IOException {
        try (FileWriter fileWriter = new FileWriter(".\\" + getFilenameCSV(), true)) {
            fileWriter.write(searchDetail.getNome() + "," + searchDetail.getUrl() + "," + searchDetail.getBranch() + ";\n");
        }
    }

    public void writeTxt(String line) throws IOException {
        try (FileWriter fileWriter = new FileWriter(".\\" + getFilenameReport(), true)) {
            fileWriter.write(line + "\n");
        }
    }

    public String getFilenameCSV() {
        return filenameCSV;
    }

    public void setFilenameCSV(String filenameCSV) {
        this.filenameCSV = filenameCSV;
    }

    public String getFilenameReport() {
        return filenameReport;
    }

    public void setFilenameReport(String filenameReport) {
        this.filenameReport = filenameReport;
    }
}
