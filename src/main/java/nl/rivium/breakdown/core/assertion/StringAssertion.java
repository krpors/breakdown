package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;
import nl.rivium.breakdown.core.jms.JMSReceiverOutput;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Simple string assertion.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StringAssertion extends Assertion {

    private String assertion = "";
    private boolean isRegex = false;

    public StringAssertion() {
    }

    public StringAssertion(String assertion) {
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

    @Override
    public void executeAssertion(TestCase testCase, TestStep previous) throws AssertionException {
        if (previous == null) {
            return;
        }
        if (previous.getOutput() == null) {
            return;
        }

        if (previous.getOutput() instanceof JMSReceiverOutput) {
            JMSReceiverOutput output = (JMSReceiverOutput) previous.getOutput();
            if (output.getPayload() != null) {
                if (!output.getPayload().contains(assertion)) {
                    throw new AssertionException(testCase, previous, assertion, output.getPayload());
                }
            }
        }
    }
}
