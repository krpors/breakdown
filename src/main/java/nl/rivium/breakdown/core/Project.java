package nl.rivium.breakdown.core;


import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.assertion.StringAssertion;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.core.jms.JMSSenderInput;
import nl.rivium.breakdown.core.jms.JMSSenderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "breakdownProject", namespace = "urn:breakdown:project:1.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class Project extends GenericEntity {

    /**
     * The logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(Project.class);

    private List<TestSuite> testSuites = new ArrayList<>();

    private String author;

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
            Project project = (Project) o;
            return project;
        }

        throw new BreakdownException("Can not read project properly");
    }

    public static Project read(String file) throws JAXBException, BreakdownException {
        JAXBContext ctx = createContext();
        Unmarshaller um = ctx.createUnmarshaller();
        Object o = um.unmarshal(new File(file));
        if (o instanceof Project) {
            Project project = (Project) o;
            return project;
        }

        throw new BreakdownException("Can not read project properly");
    }

    public void write() throws JAXBException {
        JAXBContext ctx = createContext();
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        m.marshal(this, System.out);
    }

    /**
     * Runs every TestSuite, TestCase and their TestSteps in this project.
     */
    public void execute() throws BreakdownException, AssertionException {
        LOG.info("Executing project '{}'", getName());
        for (TestSuite suite : testSuites) {
            suite.execute(this);
        }
    }
}
