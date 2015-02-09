package nl.rivium.breakdown.core;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMSConnection {
    private String contextFactory;
    private String connectionUrl;
    private String username;
    private String password;

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

    public Connection createQueueConnection() throws NamingException, JMSException {
        Properties env = new Properties();
        env.setProperty(Context.PROVIDER_URL, getConnectionUrl());
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, getContextFactory());
        env.setProperty(Context.SECURITY_PRINCIPAL, getUsername());
        env.setProperty(Context.SECURITY_CREDENTIALS, getPassword());

        InitialContext ctx = new InitialContext(env);
        QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
        return qcf.createQueueConnection(getUsername(), getPassword());
    }

    public Connection createTopicConnection() throws NamingException, JMSException {
        Properties env = new Properties();
        env.setProperty(Context.PROVIDER_URL, getConnectionUrl());
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, getContextFactory());
        env.setProperty(Context.SECURITY_PRINCIPAL, getUsername());
        env.setProperty(Context.SECURITY_CREDENTIALS, getPassword());

        InitialContext ctx = new InitialContext(env);
        TopicConnectionFactory tcf = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
        return tcf.createTopicConnection(getUsername(), getPassword());
    }
}
