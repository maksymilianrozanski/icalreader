package io.github.maksymilianrozanski.icalreader;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * based on @author rebeccafranks
 * @since 15/10/24.
 */

public class TestHelper {

    @Ignore
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        String returnString = sb.toString();
        return returnString.substring(0, returnString.length() - 1);
    }

    @Ignore
    public String getStringFromFile(String fileName) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        InputStream stream = new FileInputStream(file);
        String returnValue = convertStreamToString(stream);
        stream.close();
        return returnValue;
    }

    @Test
    public void gettingTextResourceTest() throws Exception {
        String expectedValue = "This is text from testresource.txt\n" +
                "This is second line from testresource.txt";
        String obtainedValue = getStringFromFile("testresource.txt");
        assertEquals(expectedValue, obtainedValue);
    }
}
