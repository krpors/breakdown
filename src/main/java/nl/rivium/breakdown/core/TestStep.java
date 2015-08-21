package nl.rivium.breakdown.core;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class TestStep<I extends Input> extends GenericEntity<TestCase, GenericEntity> implements Serializable {

    private static final long serialVersionUID = 193690638533879299L;
    /**
     * Generic form of Input.
     */
    private I input;


    @XmlTransient
    private List<ResultListener> listenerList = new ArrayList<>();

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

    public boolean addResultListener(ResultListener resultListener) {
        return listenerList.add(resultListener);
    }

    public void clearResultListeners() {
        listenerList.clear();
    }

    public void removeResultListener(ResultListener r) {
        listenerList.remove(r);
    }

    public int getResultListenerSize() {
        return listenerList.size();
    }

    protected void fireListeners(Result result) {
        for (ResultListener l : listenerList) {
            l.resultAcquired(result);
        }
    }

    /**
     * Executed the test step.
     */
    public abstract void execute() throws BreakdownException;
}
