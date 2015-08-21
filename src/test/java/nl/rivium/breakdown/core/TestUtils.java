package nl.rivium.breakdown.core;


import nl.rivium.breakdown.Utils;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestUtils {

    @Test
    public void test() throws IOException, URISyntaxException {
        Class[] classes = Utils.findConnectionFactories();
        for (Class c : classes) {
            System.out.println(c);
        }
    }

}
