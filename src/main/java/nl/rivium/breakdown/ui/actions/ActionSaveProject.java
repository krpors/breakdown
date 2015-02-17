package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.ui.BreakdownUI;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to open a project from a file.
 */
public class ActionSaveProject extends BreakdownAction {

    private static Logger LOG = LoggerFactory.getLogger(ActionSaveProject.class);

    public ActionSaveProject(BreakdownUI ui) {
        super(ui, "&Save");
    }

    @Override
    public void run() {
        Project project = getBreakdownUI().getProjectTree().getProject();
        if (project == null) {
            LOG.warn("No project to save. How is this possible?");
            return;
        }
        LOG.info("Saving project");
    }
}
