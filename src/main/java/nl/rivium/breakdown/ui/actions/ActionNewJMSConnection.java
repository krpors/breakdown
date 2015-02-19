package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.ProjectTree;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to add a new JMS connection.
 */
public class ActionNewJMSConnection extends Action {

    private static Logger LOG = LoggerFactory.getLogger(ActionNewJMSConnection.class);

    private ProjectTree tree;

    public ActionNewJMSConnection(ProjectTree tree) {
        super("&Create JMS connection", ImageCache.getDescriptor(ImageCache.UIImage.JMSConnection));
        this.tree = tree;
    }

    @Override
    public void run() {
        Object o = tree.getFirstSelection();
        if (o != null) {
            if (o instanceof Project) {
                Project p = (Project) o;
                JMSConnection j = new JMSConnection("JMS connection", p);
                tree.expandToLevel(2);
                tree.getBreakdownUI().getTabFolder().openTabItem(j);
            }
        }
        tree.refresh();
    }
}
