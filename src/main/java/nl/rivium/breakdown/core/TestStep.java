package nl.rivium.breakdown.core;


public abstract class TestStep<I extends Input, O extends Output> extends GenericEntity {
    private I input;
    private O output;

    public TestStep(String name, String description) {
        super(name, description);
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

    public void setOutput(O output) {
        this.output = output;
    }

    public abstract void execute() throws Exception;
}
