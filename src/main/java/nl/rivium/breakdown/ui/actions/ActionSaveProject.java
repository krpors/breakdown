package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

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

        LOG.debug("Observers: {}", project.countObservers());

        try {
            if (project.getFilename() == null || project.getFilename().equals("")) {
                // ask for filename to save to.
                FileDialog dlg = new FileDialog(getBreakdownUI().getShell(), SWT.SAVE);
                dlg.setFilterExtensions(new String[]{"*.xml"});
                dlg.setText("Specify file to save the Breakdown project to");
                String file = dlg.open();
                if (file == null) {
                    // cancel has been hit, bail out.
                    return;
                }
                LOG.info("Saving project to {}", file);
                project.setFilename(file);
                project.write();
            }
        } catch (JAXBException e) {
            UITools.showException(getBreakdownUI().getShell(),
                    "Unable to write to file",
                    "Unable to write to file " + project.getFilename(),
                    e);
        }
    }
}
