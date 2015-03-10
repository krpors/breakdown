package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.GenericEntity;
import nl.rivium.breakdown.core.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Properties;

@XmlAccessorType(XmlAccessType.FIELD)
public class JMSConnection extends GenericEntity<Project, GenericEntity> implements Serializable {

    private static final long serialVersionUID = 8887645362048544923L;

    /**
     * Static loggah.
     */
    private static Logger LOG = LoggerFactory.getLogger(JMSConnection.class);

    private String contextFactory = "";
    private String connectionUrl = "";
    private String username = "";

    @XmlElement(nillable = true)
    private String password;
    private String queueConnectionFactory = "";
    private String topicConnectionFactory = "";

    public JMSConnection() {
    }

    public JMSConnection(String name, Project parent) {
        super(name);
        setParent(parent);
        parent.getJmsConnections().add(this);
    }

    public String getContextFactory() {
        return contextFactory;
    }

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopicConnectionFactory() {
        return topicConnectionFactory;
    }

    public void setTopicConnectionFactory(String topicConnectionFactory) {
        this.topicConnectionFactory = topicConnectionFactory;
    }

    public String getQueueConnectionFactory() {
        return queueConnectionFactory;
    }

    public void setQueueConnectionFactory(String queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
    }

    /**
     * Creates an InitialContext based on the current fields.
     *
     * @return The InitialContext created.
     * @throws NamingException When the context factory cannot be found or the like.
     */
    private InitialContext createContext() throws NamingException {
        Properties env = new Properties();
        env.setProperty(Context.PROVIDER_URL, getConnectionUrl());
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, getContextFactory());
        if (getUsername() != null) {
            env.setProperty(Context.SECURITY_PRINCIPAL, getUsername());
        }
        if (getPassword() != null) {
            env.setProperty(Context.SECURITY_CREDENTIALS, getPassword());
        }

        return new InitialContext(env);
    }

    /**
     * Creates a queue connection.
     *
     * @return The queue connection.
     * @throws NamingException
     * @throws JMSException
     */
    public Connection createQueueConnection() throws NamingException, JMSException {
        InitialContext ctx = createContext();
        Object o = ctx.lookup(getQueueConnectionFactory());
        if (o != null && o instanceof QueueConnectionFactory) {
            QueueConnectionFactory qcf = (QueueConnectionFactory) o;
            return qcf.createQueueConnection(getUsername(), getPassword());
        }
        String expl = String.format("Object lookup returned '%s', but an " +
                "instance of java.jms.QueueConnectionFactory was expected", o.getClass().getName());
        LOG.warn(expl);
        throw new NamingException(expl);
    }

    /**
     * Creates a topic connection.
     *
     * @return The topic connection.
     * @throws NamingException
     * @throws JMSException
     */
    public Connection createTopicConnection() throws NamingException, JMSException {
        InitialContext ctx = createContext();
        Object o = ctx.lookup(getTopicConnectionFactory());
        if (o != null && o instanceof TopicConnectionFactory) {
            TopicConnectionFactory tcf = (TopicConnectionFactory) o;
            return tcf.createTopicConnection(getUsername(), getPassword());
        }

        String expl = String.format("Object lookup returned '%s', but an " +
                "instance of java.jms.TopicConnectionFactory was expected", o.getClass().getName());
        LOG.warn(expl);
        throw new NamingException(expl);
    }

    /**
     * Removes ourselves from our parent Project.
     */
    @Override
    public void removeFromParent() {
        if (getParent() != null) {
            getParent().getJmsConnections().remove(this);
        }
    }

    /**
     * Has no children.
     *
     * @return
     */
    @Override
    public GenericEntity[] getChildren() {
        return new GenericEntity[0];
    }
}
