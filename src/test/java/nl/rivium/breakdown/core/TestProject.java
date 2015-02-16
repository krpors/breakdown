package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.assertion.StringAssertion;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSDestination;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.core.jms.JMSSenderInput;
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
        JMSConnection connection1 = new JMSConnection();
        connection1.setName("Localhost jms connection");
        connection1.setDescription("Bogus description");
        connection1.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        connection1.setConnectionUrl("tcp://localhost:7222");
        connection1.setUsername("admin");
        connection1.setPassword(null);
        connection1.setQueueConnectionFactory("QueueConnectionFactory");
        connection1.setTopicConnectionFactory("TopicConnectionFactory");

        JMSConnection connection2 = new JMSConnection();
        connection2.setName("JBoss Connection");
        connection2.setDescription("Description of the JBoss connection");

        TestCase testCase = new TestCase("Testcase 1", "Sample testcase");

        // First test step:
        JMSRequestReply jrr = new JMSRequestReply("sample.queue sender", "Sends to the sample.queue. Reply on sample.topic");
        JMSSenderInput input = new JMSSenderInput();
        input.getProperties().put("Some Property", "Yarp!");
        input.getProperties().put("One", "1");
        input.setPayload("Payload!");

        jrr.setInput(input);
        jrr.setRequestDestination(new JMSDestination(DestinationType.QUEUE, "sample.queue"));
        jrr.setReplyDestination(new JMSDestination(DestinationType.TOPIC, "sample.topic"));

        // Second test step:
        StringAssertion sa = new StringAssertion("Some response");

        AssertionCollection ac = new AssertionCollection("Bunch of assertions", "String checks");
        ac.getAssertionList().add(sa);

        testCase.getTestSteps().add(jrr);
        testCase.getTestSteps().add(ac);

        TestSuite suite = new TestSuite("Suite 1", "Desc");
        suite.getTestCases().add(testCase);

        TestSuite suite2 = new TestSuite("Suite 2", "Descirpiotasjd");

        Project p = new Project("Project 1", "Desc");
        p.getTestSuites().add(suite);
        p.getTestSuites().add(suite2);
        p.setAuthor("Me myself and I");

        p.setFilename("/home/whatevs/example/filename.xml");

        // set parents, for debuggin'
        jrr.setParent(testCase);
        ac.setParent(testCase);
        testCase.setParent(suite);
        suite.setParent(p);
        suite2.setParent(p);

        p.getJmsConnections().add(connection1);
        p.getJmsConnections().add(connection2);

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
            Assert.assertEquals(2, testSteps.size());

            TestStep one = testSteps.get(0);
            TestStep two = testSteps.get(1);

            Assert.assertTrue(one instanceof JMSRequestReply);
            Assert.assertTrue(two instanceof AssertionCollection);

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
}
