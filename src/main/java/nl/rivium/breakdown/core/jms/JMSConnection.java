package nl.rivium.breakdown.core.jms;

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
public class JMSConnection {
    private String contextFactory;
    private String connectionUrl;
    private String username;

    @XmlElement(nillable = true)
    private String password;
    private String queueConnectionFactory;
    private String topicConnectionFactory;

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

    public Connection createQueueConnection() throws NamingException, JMSException {
        Properties env = new Properties();
        env.setProperty(Context.PROVIDER_URL, getConnectionUrl());
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, getContextFactory());
        env.setProperty(Context.SECURITY_PRINCIPAL, getUsername());
        env.setProperty(Context.SECURITY_CREDENTIALS, getPassword());

        InitialContext ctx = new InitialContext(env);
        QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(getQueueConnectionFactory());
        return qcf.createQueueConnection(getUsername(), getPassword());
    }

    public Connection createTopicConnection() throws NamingException, JMSException {
        Properties env = new Properties();
        env.setProperty(Context.PROVIDER_URL, getConnectionUrl());
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, getContextFactory());
        env.setProperty(Context.SECURITY_PRINCIPAL, getUsername());
        env.setProperty(Context.SECURITY_CREDENTIALS, getPassword());

        InitialContext ctx = new InitialContext(env);
        TopicConnectionFactory tcf = (TopicConnectionFactory) ctx.lookup(getTopicConnectionFactory());
        return tcf.createTopicConnection(getUsername(), getPassword());
    }
}
