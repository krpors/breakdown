package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.AssertionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Simple string assertion on the payload of a test step.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PayloadAssertion {

    /**
     * Logger for this class.
     */
    private static Logger LOG = LoggerFactory.getLogger(PayloadAssertion.class);

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
    }

    public PayloadAssertion(String assertion) {
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

    public void execute(String payload) throws AssertionException {
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
                throw new AssertionException(getAssertion(), payload, "does not  contain");
            }
        }
    }
}
