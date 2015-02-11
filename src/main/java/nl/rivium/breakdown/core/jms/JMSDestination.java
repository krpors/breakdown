package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.DestinationType;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

public class JMSDestination {

    private DestinationType type;
    private String name;

    public JMSDestination() {
    }

    public JMSDestination(DestinationType type, String name) {
        setType(type);
        setName(name);
    }

    public DestinationType getType() {
        return type;
    }

    public void setType(DestinationType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Destination createDestination(Session session) throws JMSException {
        Destination dest = null;
        switch (getType()) {
            case QUEUE:
                dest = session.createQueue(getName());
                break;
            case TOPIC:
                dest = session.createTopic(getName());
                break;
            case TEMPORARY_QUEUE:
                dest = session.createTemporaryQueue();
                break;
            case TEMPORARY_TOPIC:
                dest = session.createTemporaryTopic();
                break;
            default:
                throw new IllegalArgumentException("CANNOT HAPPEN FIX THIS");
        }

        return dest;
    }
}
