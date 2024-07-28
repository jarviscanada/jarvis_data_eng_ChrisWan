package ca.jrvs.apps.grep;

import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import static org.junit.jupiter.api.Assertions.*;


public class JavaGrepImpTest {

    @BeforeClass
    public static void setUpTest() throws IOException, InterruptedException {
        try {
            // Write a bash script to generate a directory and files to use for testing
            // run this before the start of running the testing class
            String scriptPath = System.getProperty("user.dir") + "/src/test/java/ca/jrvs/apps/grep/script.sh";
            File scriptFile = new File(scriptPath);
            File scriptDirectory = scriptFile.getParentFile(); // Directory containing the script

            ProcessBuilder pb = new ProcessBuilder();
            pb.command("bash", scriptFile.getAbsolutePath()); // Full path to the script
            pb.directory(scriptDirectory); // Working directory

            Process p = pb.start();

            int exitValue = p.waitFor();
            System.out.println("Process has finished with exit code " + exitValue);

        } catch (IOException e) {
            throw new IOException("An error had occurred when setting up the test");
        } catch (InterruptedException e) {
            throw new InterruptedIOException("An error has occurred with the process");
        }
    }

    // test listFiles method
    @Test
    public void testListFiles() {
        // need to fix the ordering of how the files will be listed -> look into other ways
        JavaGrepImp javaGrep = new JavaGrepImp();
        String rootDir = System.getProperty("user.dir") + "/src/test/java/ca/jrvs/apps/grep/data";
        List<File> listOfFiles = javaGrep.listFiles(rootDir);
        List<String> result = Arrays.asList("hello_world.txt", "testing.txt", "data.txt", "spaces.txt");

        int counter = 0;
        for (File file : listOfFiles) {
            String[] parts = file.toString().split("/");
            assertEquals(parts[parts.length - 1], result.get(counter));
            counter++;
        }
    }

    // test readLines
    @Test
    public void testReadLines() {
        JavaGrepImp javaGrep = new JavaGrepImp();
        // files to read
        String helloWorld = "hello_world.txt";
        String data = "data.txt";
        List<String> helloWorldLines = javaGrep.readFile(helloWorld);
        List<String> dataLines = javaGrep.readFile(data);

        String rootDir = System.getProperty("user.dir") + "/src/test/java/ca/jrvs/apps/grep/data/";
        rootDir += "/" + helloWorld;
        List<String> linesfFromHelloWorld = javaGrep.readLines(new File(rootDir));

        for (int i =0; i < helloWorldLines.size(); i++) {
            assertEquals(helloWorldLines.get(i), linesfFromHelloWorld.get(i));
        }

        rootDir = System.getProperty("user.dir") + "/src/test/java/ca/jrvs/apps/grep/data/";
        rootDir += "/" + data;
        List<String> linesFromData = javaGrep.readLines(new File(rootDir));
        for (int i =0; i < dataLines.size(); i++) {
            assertEquals(dataLines.get(i), linesFromData.get(i));
        }

    }

    @Test
    public void testContainsPattern() {
        JavaGrepImp javaGrep = new JavaGrepImp();
        String regex = "apples";
        String actualString = "Hello World";
        javaGrep.setRegex(regex);

        // does not contain a pattern
        assertFalse(javaGrep.containsPattern(actualString));

        // contain the pattern
        actualString = "testing";
        regex = "test";
        javaGrep.setRegex(regex);
        assertTrue(javaGrep.containsPattern(actualString));
    }

    @Test
    public void testWriteToFile() throws IOException {

        JavaGrepImp javaGrep = new JavaGrepImp();
        String rootDir = System.getProperty("user.dir") + "/src/test/java/ca/jrvs/apps/grep/result/result.txt";
        List<String> sampleText = Arrays.asList("This is an example text that needs to be written to a file", "Another text");
        javaGrep.setOutFile(rootDir);

        javaGrep.writeToFile(sampleText);

        String createdFile = System.getProperty("user.dir") + "/src/test/java/ca/jrvs/apps/grep/data/result.txt";
        List<String> linesFromResult = javaGrep.readFile(createdFile);

        for (int i = 0; i < linesFromResult.size(); i++) {
            assertEquals(linesFromResult.get(i), sampleText.get(i));
        }

    }

    @Test
    public void testSetRootPath() {
        /**
         * This test is used to test the setter and getter for rootPath
         */
        JavaGrepImp javaGrep = new JavaGrepImp();
        String rootPath = "./test/path";

        // value should be null initially
        assertNull(javaGrep.getRootPath());

        // after setting the rootPath it should be assigned to that specific file path
        javaGrep.setRootPath(rootPath);
        assertEquals(javaGrep.getRootPath(), rootPath);

    }

    @Test
    public void testSetRegex() {
        /**
         * This test is used to test the setter and getter for regex
         */
        JavaGrepImp javaGrep = new JavaGrepImp();
        String regex = "test";

        // value should be null initially
        assertNull(javaGrep.getRegex());

        // after setting the regex string it should be assigned to that specific regex
        javaGrep.setRegex(regex);
        assertEquals(javaGrep.getRegex(), regex);
    }

    @Test
    public void testSetOutFile() {
        /**
         * This test is used to test the setter and getter for outFile
         */
        JavaGrepImp javaGrep = new JavaGrepImp();
        String outFile = "./result";

        // value should be null initially
        assertNull(javaGrep.getOutFile());

        // after setting the outFile string it should be assigned to that specific outFile path
        javaGrep.setOutFile(outFile);
        assertEquals(javaGrep.getOutFile(), outFile);
    }
}