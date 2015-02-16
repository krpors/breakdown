package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.GenericEntity;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Properties;

@XmlAccessorType(XmlAccessType.FIELD)
public class JMSConnection extends GenericEntity {
    private String contextFactory = "";
    private String connectionUrl = "";
    private String username = "";

    @XmlElement(nillable = true)
    private String password;
    private String queueConnectionFactory = "";
    private String topicConnectionFactory = "";

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
        QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(getQueueConnectionFactory());
        return qcf.createQueueConnection(getUsername(), getPassword());
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
        TopicConnectionFactory tcf = (TopicConnectionFactory) ctx.lookup(getTopicConnectionFactory());
        return tcf.createTopicConnection(getUsername(), getPassword());
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
