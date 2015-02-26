package nl.rivium.breakdown.core;


import nl.rivium.breakdown.core.assertion.PayloadAssertion;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.core.jms.JMSSenderInput;
import org.apache.commons.lang.SerializationUtils;
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

    private static final long serialVersionUID = -2600956139969094067L;
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
        super("Project 1");
    }

    public Project(String name) {
        super(name);
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
        forceNotifyObservers();
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

    /**
     * Iterates through the list of JMS connections and finds the first one that matches the name 'name'.
     *
     * @param name The name of the JMS connection.
     * @return The JMSconnection that's found,
     */
    public JMSConnection findJMSConnectionByName(String name) {
        for (JMSConnection c : getJmsConnections()) {
            if (c.getName() != null && c.getName().equals(name)) {
                return c;
            }
        }

        // Not found. Should actually not happen when using the UI configuration tool.
        return null;
    }

    private static JAXBContext createContext() throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(
                Project.class, JMSSenderInput.class, JMSRequestReply.class,
                PayloadAssertion.class);
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

    /**
     * Reads the specified file, attempts to parse it and unmarshal it to a Project instance.
     *
     * @param file The file to read.
     * @return The Project instance.
     * @throws JAXBException      Parsing failed.
     * @throws BreakdownException Something else.
     */
    public static Project read(String file) throws JAXBException, BreakdownException {
        JAXBContext ctx = createContext();
        Unmarshaller um = ctx.createUnmarshaller();
        Object o = um.unmarshal(new File(file));
        if (o instanceof Project) {
            Project p = (Project) o;
            p.setFilename(file);
            return p;
        }

        throw new BreakdownException("Can not read project properly");
    }


    /**
     * Marshals the project to the given outputstream.
     *
     * @param os The output stream to write the project to.
     * @throws JAXBException When exceptions arise while attempting to marshal to file.
     */
    public void write(OutputStream os) throws JAXBException {
        JAXBContext ctx = createContext();
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        m.marshal(this, os);
    }

    /**
     * Writes the project to the filename.
     */
    public void write() throws JAXBException {
        JAXBContext ctx = createContext();
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        m.marshal(this, new File(getFilename()));

    }

    /**
     * Runs every TestSuite, TestCase and their TestSteps in this project.
     */
    public void execute() throws BreakdownException, AssertionException {
        LOG.info("Executing project '{}'", getName());
        for (TestSuite suite : testSuites) {
            try {
                suite.execute();
            } catch (AssertionException ex) {
                // TODO what?
            }
        }
    }
}
