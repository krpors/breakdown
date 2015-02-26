package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;

/**
 * Action to open a new project.
 */
public class ActionNewProject extends BreakdownAction {

    public ActionNewProject(BreakdownUI ui) {
        super(ui, "&New Project", ImageCache.getDescriptor(ImageCache.Icon.Project));
        setAccelerator(SWT.CTRL | 'N');
    }

    @Override
    public void run() {
        Project p = new Project();
        p.setName("New project");

        getBreakdownUI().getTabFolder().disposeAllTabs();
        getBreakdownUI().getProjectTree().setProject(p);
        getBreakdownUI().updateWidgets();
        getBreakdownUI().getTabFolder().openTabItem(p);
    }
}
