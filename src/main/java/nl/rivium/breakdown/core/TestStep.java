package nl.rivium.breakdown.core;

import java.io.Serializable;

public abstract class TestStep<I extends Input> extends GenericEntity<TestCase, GenericEntity> implements Serializable {

    private static final long serialVersionUID = 193690638533879299L;
    /**
     * Generic form of Input.
     */
    private I input;

    public TestStep() {
    }

    public TestStep(String name) {
        super(name);
    }

    public I getInput() {
        return input;
    }

    public void setInput(I input) {
        this.input = input;
    }

    /**
     * Forced override to force a return type of TestCase.
     *
     * @return The TestCase parent.
     */
    @Override
    public TestCase getParent() {
        return super.getParent();
    }

    /**
     * A test step does not have children elements, so always return null.
     *
     * @return null, because a test step cannot have and will not have any children elements.
     */
    @Override
    public GenericEntity[] getChildren() {
        return new GenericEntity[0];
    }

    /**
     * Executed the test step.
     *
     * @throws AssertionException Whenever an assertion has failed in this step.
     * @throws BreakdownException Whenever something goes wrong in executing the test step, like connection errors etc.
     */
    public abstract void execute() throws AssertionException, BreakdownException;
}
