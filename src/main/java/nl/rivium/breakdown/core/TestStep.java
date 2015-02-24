package nl.rivium.breakdown.core;


public abstract class TestStep<I extends Input, O extends Output> extends GenericEntity<TestCase, GenericEntity> {
    private I input;
    private O output;

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

    public O getOutput() {
        return output;
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
     * Sets the output of this test step. You should not be able to specify that yourself, since the output
     * should come because of a step that has been run. That's why it's defined protected, so only subclasses can invoke
     * this properly.
     *
     * @param output The output of this test step after it was run.
     */
    protected void setOutput(O output) {
        this.output = output;
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
     * @param previous  The previous test step. Useful to check the output of the previous step.
     * @throws AssertionException Whenever an assertion has failed in this step.
     * @throws BreakdownException Whenever something goes wrong in executing the test step, like connection errors etc.
     */
    public abstract void execute(TestStep previous) throws AssertionException, BreakdownException;
}
