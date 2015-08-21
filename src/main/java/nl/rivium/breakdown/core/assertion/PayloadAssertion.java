package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.TestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * Simple string assertion on the payload of a test step.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PayloadAssertion implements Serializable {

    private static final long serialVersionUID = 2138067539470488776L;
    
    /**
     * Logger for this class.
     */
    private static Logger LOG = LoggerFactory.getLogger(PayloadAssertion.class);

    private String name;

    /**
     * Assertion contents.
     */
    private String assertion = "";

    /**
     * Negate the assertion (for example, must NOT contain the string etc.)
     */
    private boolean negate = false;

    /**
     * Whether the assertion string is a regular expression.
     */
    private boolean isRegex = false;

    public PayloadAssertion() {
        this("");
    }

    public PayloadAssertion(String name) {
        this(name, "");
    }

    public PayloadAssertion(String name, String assertion) {
        this.name = name;
        setAssertion(assertion);
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean isRegex) {
        this.isRegex = isRegex;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void execute(TestStep testStep, String payload) throws AssertionException {
        if (payload == null) {
            return; // TODO: what if null?
        }
        LOG.debug("Executing payload assertion on payload {}", payload);
        if (isRegex()) {
            if (!payload.matches(getAssertion())) {
                throw new AssertionException(null, null, "regex mismatch");
            }
        } else {
            if (!payload.contains(getAssertion())) {
                throw new AssertionException(testStep, getAssertion(), payload, "does not contain");
            }
        }
    }
}
