package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.ProjectTree;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to add a new test case.
 */
public class ActionNewTestCase extends Action {

    private static Logger LOG = LoggerFactory.getLogger(ActionNewTestCase.class);

    private ProjectTree tree;

    public ActionNewTestCase(ProjectTree tree) {
        super("&Create test case", ImageCache.getDescriptor(ImageCache.UIImage.TestCase));
        this.tree = tree;
    }

    @Override
    public void run() {
        Object o = tree.getFirstSelection();
        if (o != null) {
            if (o instanceof TestSuite) {
                TestSuite p = (TestSuite) o;
                TestCase ts = new TestCase("Test case", p);
                tree.expandToLevel(3);
                tree.getBreakdownUI().getTabFolder().openTabItem(ts);
            }
        }
        tree.refresh();
    }
}
