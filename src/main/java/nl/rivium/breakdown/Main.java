package nl.rivium.breakdown;


import nl.rivium.breakdown.core.JMSConnection;
import nl.rivium.breakdown.core.JMSSender;
import nl.rivium.breakdown.core.JMSSenderInput;
import nl.rivium.breakdown.core.TestCase;

public class Main {
    public static void main(String[] args) throws Exception {
        JMSConnection c = new JMSConnection();
        c.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        c.setConnectionUrl("tcp://localhost:7222");
        c.setUsername("admin");
        c.setPassword("");

        TestCase testCase = new TestCase("Hai", "Description");
        testCase.setJmsConnection(c);

        JMSSender sender = new JMSSender("Sender sender", "Sends something", testCase);
        sender.setDestination("sample.queue");

        JMSSenderInput input = new JMSSenderInput();
        input.setPayload("hello there!");
        input.getProperties().put("Example property", "Value value!");
        sender.setInput(input);

        testCase.getTestSteps().add(sender);

        testCase.execute();
    }
}
