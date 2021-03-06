package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.core.BreakdownException;
import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

/**
 * Action to open a project from a file.
 */
public class ActionOpenProject extends BreakdownAction {

    private static Logger LOG = LoggerFactory.getLogger(ActionOpenProject.class);

    public ActionOpenProject(BreakdownUI ui) {
        super(ui, "&Open Project...");
        setImageDescriptor(ImageCache.getDescriptor(ImageCache.Icon.Folder));
        setAccelerator(SWT.CTRL | 'O');
    }

    @Override
    public void run() {
        FileDialog dlg = new FileDialog(getBreakdownUI().getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String[]{"*.xml"});
        String path = dlg.open();
        if (path != null) {
            LOG.info("Opening project '{}'", path);
            try {
                Project p = Project.read(path);
                getBreakdownUI().loadProject(p);
            } catch (JAXBException e) {
                String s = String.format("Unable to parse or deserialize file '%s'", path);
                LOG.error(s, e);
                UITools.showException(getBreakdownUI().getShell(), "Unable to open file", s, e);
            } catch (BreakdownException e) {
                String s = String.format("Unable to open file '%s'", path);
                LOG.error(s, e);
                UITools.showException(getBreakdownUI().getShell(), "Unable to open file", s, e);
            }
        }
    }
}
