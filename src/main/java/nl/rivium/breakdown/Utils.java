package nl.rivium.breakdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Miscellaneous utilities.
 */
public final class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static final Set<String> IGNORED = new HashSet<>();

    static {
        String[] ign = new String[]{"java.", "javax.", "javafx.", "sun.", "org.eclipse.", "ch.qos.logback.", "org.slf4j."};
        Collections.addAll(IGNORED, ign);
    }

    /**
     * This method will iterate through every JAR file on the system classloader's classpath, find every class in it,
     * attempts to Class.forName() it (without initializing), and checks if it implements the javax.jms.ConnectionFactory
     * interface.
     * <p/>
     * Probably some weird and ugly hack, but whatever. This function will skip analyzing classes which start with:
     * <ol>
     * <li>java.</li>
     * <li>javax.</li>
     * <li>javafx.</li>
     * <li>sun.</li>
     * <li>org.eclipse.</li>
     * <li>ch.qos.logback</li>
     * <li>org.slf4j</li>
     * </ol>
     *
     * @return A Class[] array with classes which implement the <code>javax.jms.ConnectionFactory</code> interface. If no
     * classes are found, an empty array will be returned.
     */
    public static Class[] findConnectionFactories()  {
        // The classes which have been found.
        Set<Class> classSet = new HashSet<>();

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            File f = null;
            try {
                f = new File(url.toURI());
            } catch (URISyntaxException e) {
                LOG.warn("Unable to create File object from URL '{}'", url);
                continue;
            }

            if (f.isFile() && f.getName().endsWith(".jar")) {
                JarFile jf = null;
                try {
                    jf = new JarFile(f);
                } catch (IOException e) {
                    LOG.warn("Unable to open JAR file '{}'", f);
                    LOG.warn("Exception is: ", e);
                    continue;
                }

                Enumeration<JarEntry> e = jf.entries();
                while (e.hasMoreElements()) {
                    JarEntry je = e.nextElement();
                    if (je.getName().endsWith(".class")) {
                        String re = je.getName().substring(0, je.getName().indexOf(".class"));
                        re = re.replace("/", ".");

                        if (isClassIgnored(re)) {
                            continue;
                        }

                        try {
                            Class c = Class.forName(re, false, cl);
                            Class[] ifs = c.getInterfaces();
                            if (ifs.length == 0) {
                                continue;
                            }

                            for (Class izz : ifs) {
                                if (izz.getName().equals(ConnectionFactory.class.getName())) {
                                    classSet.add(c);
                                }
                            }
                        } catch (ClassNotFoundException | NoClassDefFoundError e1) {
                            // ignore
                        }
                    }
                }
            }
        }

        return classSet.toArray(new Class[classSet.size()]);
    }

    private static boolean isClassIgnored(String clazz) {
        for (String ignorable : IGNORED) {
            if (clazz.startsWith(ignorable)) {
                return true;
            }
        }

        return false;
    }
}
