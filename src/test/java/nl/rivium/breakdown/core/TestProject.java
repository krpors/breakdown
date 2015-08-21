package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.jms.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

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

}
