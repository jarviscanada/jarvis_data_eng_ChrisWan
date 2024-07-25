package ca.jrvs.apps.grep;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JavaGrepImp implements JavaGrep {

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception e) {
            System.out.print(e);
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
            System.out.print("A problem has occurred when reading from a file");
        }

        return lines;
    }

    @Override
    public boolean containsPattern(String line) {
        String regex = getRegex();
        // from this regex pass it into the Regex library and then match it with the line
        // if it matches return true else false
        return false;
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getOutFile()), StandardCharsets.UTF_8));
            for (String line : lines ) {
                writer.write(line);
            }

        } catch (IOException ex) {
            throw new IOException("Problem has occurred when writing to this file");
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                System.out.print("A problem has occurred with closing the file" + e);
            }
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