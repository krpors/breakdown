package nl.rivium.breakdown.core;


import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.assertion.StringAssertion;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.core.jms.JMSSenderInput;
import nl.rivium.breakdown.core.jms.JMSSenderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "breakdownProject", namespace = "urn:breakdown:project:1.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class Project extends GenericEntity {

    /**
     * The logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(Project.class);

    @XmlElement(name = "testSuite")
    @XmlElementWrapper(name = "testSuites")
    private List<TestSuite> testSuites = new ArrayList<>();

    /**
     * A Project can have multiple JMS connections defined. The sub entities can
     * refer to it by name reference.
     */
    @XmlElement(name = "jmsConnection")
    @XmlElementWrapper(name = "jmsConnections")
    private List<JMSConnection> jmsConnections = new ArrayList<>();

    private String author;

    /**
     * The filename of an unmarshalled Project file.
     */
    @XmlTransient
    private String filename = "";

    public Project() {
        super("", "");
    }

    public Project(String name, String description) {
        super(name, description);
    }

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuite> testSuites) {
        this.testSuites = testSuites;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the filename associated with an unmarshalled project.
     * TODO: nullage
     *
     * @return The filename. Can be null?
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename of this project. Set when a Project is unmarshaled.
     *
     * @param filename The filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public GenericEntity[] getChildren() {
        List<GenericEntity> children = new ArrayList<>();
        children.addAll(jmsConnections);
        children.addAll(testSuites);
        return children.toArray(new GenericEntity[children.size()]);
    }

    /**
     * Returns the JMS connections.
     *
     * @return The JMS connections defined for the project.
     */
    public List<JMSConnection> getJmsConnections() {
        return jmsConnections;
    }

    public void setJmsConnections(List<JMSConnection> jmsConnections) {
        this.jmsConnections = jmsConnections;
    }

    private static JAXBContext createContext() throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(
                Project.class, JMSSenderInput.class, JMSSenderOutput.class, JMSRequestReply.class,
                AssertionCollection.class, StringAssertion.class);
        return ctx;
    }

    public static Project read(InputStream is) throws JAXBException, BreakdownException {
        JAXBContext ctx = createContext();
        Unmarshaller um = ctx.createUnmarshaller();
        Object o = um.unmarshal(is);
        if (o instanceof Project) {
            return (Project) o;
        }

        throw new BreakdownException("Can not read project properly");
    }

    public static Project read(String file) throws JAXBException, BreakdownException {
        JAXBContext ctx = createContext();
        Unmarshaller um = ctx.createUnmarshaller();
        Object o = um.unmarshal(new File(file));
        if (o instanceof Project) {
            return (Project) o;
        }


        throw new BreakdownException("Can not read project properly");
    }

    public void write(OutputStream os) throws JAXBException {
        JAXBContext ctx = createContext();
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        m.marshal(this, os);
    }

    /**
     * Runs every TestSuite, TestCase and their TestSteps in this project.
     */
    public void execute() throws BreakdownException, AssertionException {
        LOG.info("Executing project '{}'", getName());
        for (TestSuite suite : testSuites) {
            try {
                suite.execute(this);
            } catch (AssertionException ex) {
                List<ExecutionListener> l = getExecutionListeners();
                l.get(0).assertionFailed(this, null, ex.getTestCase(), ex.getTestStep());
            }
        }
    }
}
