package ca.jrvs.apps.grep;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        // Use default logger config
        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception e) {
            javaGrepImp.logger.error("Error: Unable to process", e);
        }
    }

    @Override
    public void process() throws IOException {
        try {
            List<String> matchedLines = new ArrayList<String>();
            for (File file : listFiles(getRootPath())) {
                for (String line : readLines(file)) {
                    if (containsPattern(line)) {
                        matchedLines.add(line);
                    }
                }
            }

            writeToFile(matchedLines);
        } catch (IOException ex) {
            logger.debug("This error has occurred when trying to grep", ex);
            throw new IOException("An error had occurred when trying to grep");
        }

    }

    @Override
    public List<File> listFiles(String rootDir) {
        List<File> files = new ArrayList<>();

        File folder = new File(rootDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    @Override
    public List<String> readLines(File inputFile) {
        // Explain FileReader, BufferReader, and character encoding
        // might need to handle reading bytes and strings from a file (might need to create a method for each)
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            logger.debug("A problem has occurred when reading from a file", e);
        }

        return lines;
    }

    @Override
    public boolean containsPattern(String line) {
        try {
            Pattern pattern = Pattern.compile(getRegex());
            Matcher matcher = pattern.matcher(line);
            return matcher.find();
        } catch (Exception e) {
            logger.debug("A problem has occurred when reading from a file", e);
        }

        return false;
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {

        try {

            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(getOutFile())
            );

            for (String line : lines ) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();

        } catch (IOException ex) {
            logger.debug("Problem has occurred when writing to this file", ex);
            throw new IOException("Problem has occurred when writing to this file");
        }
    }

    @Override
    public String getRootPath() {
        return this.rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex() {
        return this.regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getOutFile() {
        return this.outFile;
    }

    @Override
    public void setOutFile(String outfile) {
        this.outFile = outfile;
    }
}