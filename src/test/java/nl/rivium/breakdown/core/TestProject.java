package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.jms.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Tests project stuff.
 */
public class TestProject {

    /**
     * Helper function to create a dummy project to test on.
     *
     * @return The project with suites, cases, steps etc.
     */
    private Project createProject() {
        Project p = new Project("Project 1");
        p.setAuthor("Me myself and I");

        JMSConnection connection1 = new JMSConnection("Localhost jms connection", p);
        connection1.setDescription("Bogus description");
        connection1.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        connection1.setConnectionUrl("tcp://localhost:7222");
        connection1.setUsername("admin");
        connection1.setPassword(null);
        connection1.setQueueConnectionFactory("QueueConnectionFactory");
        connection1.setTopicConnectionFactory("TopicConnectionFactory");

        JMSConnection connection2 = new JMSConnection("JBoss Connection", p);
        connection2.setDescription("Description of the JBoss connection");

        TestSuite suite = new TestSuite("Suite 1", p);
        TestSuite suite2 = new TestSuite("Suite 2", p);

        TestCase testCase = new TestCase("Testcase 1", suite);

        // First test step:
        JMSRequestReply jrr = new JMSRequestReply("sample.queue sender", testCase);
        JMSSenderInput input = new JMSSenderInput();
        input.getProperties().put("Some Property", "Yarp!");
        input.getProperties().put("One", "1");
        input.setPayload("Payload!");

        jrr.setInput(input);
        jrr.setRequestDestination(new JMSDestination(DestinationType.QUEUE, "sample.queue"));
        jrr.setReplyDestination(new JMSDestination(DestinationType.TOPIC, "sample.topic"));

        p.setFilename("/home/whatevs/example/filename.xml");

        return p;
    }

    @Test
    public void findConnection() {
        Project p = createProject();
        JMSConnection cnull = p.findJMSConnectionByName("not found");
        Assert.assertNull(cnull);

        JMSConnection first = p.findJMSConnectionByName("Localhost jms connection");
        Assert.assertNotNull(first);
        Assert.assertEquals("tcp://localhost:7222", first.getConnectionUrl());

        JMSConnection second = p.findJMSConnectionByName("JBoss Connection");
        Assert.assertNotNull(second);
        Assert.assertEquals("Description of the JBoss connection", second.getDescription());
    }

    @Test
    public void serialize() throws JAXBException {
        Project p = createProject();

        p.write(System.out);
    }

    @Test
    public void unmarshalling() {
        InputStream stream = TestProject.class.getResourceAsStream("/project.xml");
        Assert.assertNotNull(stream);

        try {
            Project p = Project.read(stream);
            Assert.assertEquals("Me myself and I", p.getAuthor());

            List<TestSuite> suites = p.getTestSuites();
            Assert.assertEquals(2, suites.size());

            List<TestCase> testCases = suites.get(0).getTestCases();
            Assert.assertEquals(1, testCases.size());

            List<TestStep> testSteps = testCases.get(0).getTestSteps();
            Assert.assertEquals(1, testSteps.size());

            TestStep one = testSteps.get(0);

            Assert.assertTrue(one instanceof JMSRequestReply);

            JMSRequestReply jmsRequestReply = (JMSRequestReply) one;
            Assert.assertEquals("sample.queue sender", jmsRequestReply.getName());
            Assert.assertEquals("Payload!", jmsRequestReply.getInput().getPayload());
            Assert.assertEquals(2, jmsRequestReply.getInput().getProperties().size());
            Assert.assertEquals("1", jmsRequestReply.getInput().getProperties().get("One"));

            Assert.assertEquals(2, p.getJmsConnections().size());

        } catch (JAXBException | BreakdownException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Should end in an exception due to faulty XML.
     *
     * @throws JAXBException      The JAXBException
     * @throws BreakdownException The BreakdownException
     */
    @Test(expected = JAXBException.class)
    public void unmarshalFaultyXML() throws JAXBException, BreakdownException {
        Project.read(TestProject.class.getResourceAsStream("/project-err.xml"));
    }

    /**
     * Implementation to search for a class implementing an interface in all JARs on the classpath. Not fast, but works.
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testSearchClasspath() throws IOException, URISyntaxException {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            File f = new File(url.toURI());
            if (f.isFile() && f.getName().endsWith(".jar")) {
                JarFile jf = new JarFile(f);
                Enumeration<JarEntry> e = jf.entries();
                while (e.hasMoreElements()) {
                    JarEntry je = e.nextElement();
                    if (je.getName().endsWith(".class")) {
                        String re = je.getName().substring(0, je.getName().indexOf(".class"));
                        re = re.replace("/", ".");
                        if (re.startsWith("java.") ||
                                re.startsWith("com.sun") ||
                                re.startsWith("javax.") ||
                                re.startsWith("sun.") ||
                                re.startsWith("javafx.")) {
                            continue;
                        }

                        try {
                            Class c = Class.forName(re, false, cl);
                            Class[] ifs = c.getInterfaces();
                            if (ifs.length == 0) {
                                continue;
                            }

                            for (Class izz : ifs) {
                                if (izz.getName().equals(Runnable.class.getName())) {
                                    System.out.println("GOT A RUNNABLE!: " + c);
                                }
                            }
                        } catch (ClassNotFoundException | NoClassDefFoundError e1) {
                            // ignore
                        }
                    }
                }
            }
        }
    }
}
