package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.GenericEntity;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.ProjectTree;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to exit the application.
 */
public class ActionDeleteEntity extends Action {

    private static Logger LOG = LoggerFactory.getLogger(ActionDeleteEntity.class);

    private ProjectTree tree;

    public ActionDeleteEntity(ProjectTree tree) {
        super("&Delete", ImageCache.getDescriptor(ImageCache.Icon.Delete));
        this.tree = tree;
//        setAccelerator(SWT.DEL);
    }

    @Override
    public void run() {
        Object o = tree.getFirstSelection();
        if (!(o instanceof GenericEntity)) {
            LOG.error("Object is expected to be of type {}, but was {}", GenericEntity.class, o.getClass());
            return;
        }

        GenericEntity ge = (GenericEntity) o;
        String message = String.format("Are you sure you want to delete '%s'? " +
                "This will remove all children as well (if any).", ge.getName());
        if (!MessageDialog.openQuestion(tree.getBreakdownUI().getShell(), "Remove " + ge.getName(), message)) {
            return;
        }

        if (o instanceof JMSConnection) {
            JMSConnection jmsConnection = (JMSConnection) o;
            jmsConnection.removeFromParent();
        } else if (o instanceof TestSuite) {
            TestSuite testSuite = (TestSuite) o;
            testSuite.removeFromParent();
        } else if (o instanceof TestCase) {
            TestCase tc = (TestCase) o;
            tc.removeFromParent();
        }

        tree.getBreakdownUI().getTabFolder().disposeTabEntity((GenericEntity) o);
        tree.refresh();
    }
}
