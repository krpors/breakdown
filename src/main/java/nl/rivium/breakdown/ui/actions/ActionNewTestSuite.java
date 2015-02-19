package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.ProjectTree;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to add a new test suite.
 */
public class ActionNewTestSuite extends Action {

    private static Logger LOG = LoggerFactory.getLogger(ActionNewTestSuite.class);

    private ProjectTree tree;

    public ActionNewTestSuite(ProjectTree tree) {
        super("&Create test suite", ImageCache.getDescriptor(ImageCache.UIImage.TestSuite));
        this.tree = tree;
    }

    @Override
    public void run() {
        Object o = tree.getFirstSelection();
        if (o != null) {
            if (o instanceof Project) {
                Project p = (Project) o;
                TestSuite ts = new TestSuite("Test suite", p);
                tree.expandToLevel(2);
                tree.getBreakdownUI().getTabFolder().openTabItem(ts);
            }
        }
        tree.refresh();
    }
}
