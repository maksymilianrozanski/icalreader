package io.github.maksymilianrozanski.icalreader;

import android.content.Context;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

/**
 * based on @author rebeccafranks
 *
 * @since 15/10/24.
 */

public class AndroidTestHelper {

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
    public String getStringFromFile(Context context, String filePath) throws Exception {
        final InputStream stream = context.getResources().getAssets().open(filePath);
        String ret = convertStreamToString(stream);
        stream.close();
        return ret;
    }

    @Test
    public void gettingTextResourceTest() throws Exception {
        Context context = getInstrumentation().getContext();
        String expectedValue = "This is text from testresource.txt\n" +
                "This is second line.Polish characters: óÓ ąĄ śŚ łŁ żŻ źŹ ćĆ ńŃ from testresource.txt";
        String obtainedValue = getStringFromFile(context, "testresource.txt");
        assertEquals(expectedValue, obtainedValue);
    }
}
