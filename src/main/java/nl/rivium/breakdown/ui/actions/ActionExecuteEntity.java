package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.BreakdownException;
import nl.rivium.breakdown.core.GenericEntity;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.ProjectTree;
import nl.rivium.breakdown.ui.TestRunnerTab;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action execute an entity (Project, TestSuite, TestCase or TestStep).
 */
public class ActionExecuteEntity extends Action {

    private static Logger LOG = LoggerFactory.getLogger(ActionExecuteEntity.class);

    private ProjectTree tree;

    public ActionExecuteEntity(ProjectTree tree) {
        super("&Execute", ImageCache.getDescriptor(ImageCache.Icon.Play));
        this.tree = tree;
    }

    @Override
    public void run() {
        Object o = tree.getFirstSelection();
        if (!(o instanceof GenericEntity)) {
            LOG.error("Object is expected to be of type {}, but was {}", GenericEntity.class, o.getClass());
            return;
        }

        GenericEntity ge = (GenericEntity) o;
        LOG.debug("Executing {}", ge.getName());

        TestRunnerTab trt = tree.getBreakdownUI().getTestRunnerTab();
        trt.setProject(tree.getProject());

        // TODO: using get().get().dosomething() is tiring. Maybe an event dispatcher with listeners provides to be easier?

        try {
            if (ge instanceof TestCase) {
                TestCase testCase = (TestCase) ge;
                testCase.execute();
            }
        } catch (BreakdownException | AssertionException e) {
            LOG.error("Exception", e);
        }
    }
}
