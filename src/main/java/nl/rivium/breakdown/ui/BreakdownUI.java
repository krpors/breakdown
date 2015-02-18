package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.ui.actions.ActionNewProject;
import nl.rivium.breakdown.ui.actions.ActionOpenProject;
import nl.rivium.breakdown.ui.actions.ActionSaveProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entrypoint for the configuration user interface.
 */
public class BreakdownUI extends ApplicationWindow {

    /**
     * Our logger instance.
     */
    private static Logger LOG = LoggerFactory.getLogger(BreakdownUI.class);

    /**
     * Image
     */
    private ImageCache imageCache;

    /**
     * The project tree.
     */
    private ProjectTree projectTree;

    /**
     * The tab folder containing open configurations.
     */
    private ComponentTabFolder tabFolder;

    /**
     * Action to create a new project.
     */
    private Action actionNewProject;
    private ActionSaveProject actionSaveProject;
    private ActionOpenProject actionOpenProject;

    public BreakdownUI() {
        super(null); // no parent shell; this is the parent.

        // Initialize images.
        new ImageCache();

        // Add menu bar (see createMenuManager()). This will also invoke createActions().
        addMenuBar();
    }

    /**
     * Configures the shell, centers it on screen etc.
     *
     * @param shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        shell.setSize(800, 600);
        shell.setText("Breakdown 0.0.1");

        Display display = shell.getDisplay();
        // Global key listener to close the active tab, if any.
        display.addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.stateMask == SWT.CTRL && event.keyCode == SWT.F4) {
                    tabFolder.disposeSelectedTab();
                }
            }
        });

        // this centers the shell on the screen:
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /**
     * Creates the top menu bar.
     *
     * @return The menu.
     */
    @Override
    protected MenuManager createMenuManager() {
        createActions();

        MenuManager mgr = new MenuManager();

        MenuManager menuFile = new MenuManager("File");
        menuFile.add(actionNewProject);
        menuFile.add(actionOpenProject);
        menuFile.add(actionSaveProject);
        mgr.add(menuFile);

        return mgr;
    }

    /**
     * Creates the menu actions and such.
     */
    private void createActions() {
        actionNewProject = new ActionNewProject(this);
        actionOpenProject = new ActionOpenProject(this);
        actionSaveProject = new ActionSaveProject(this);
        actionSaveProject.setEnabled(false);
    }

    /**
     * Creates the main application window's contents.
     *
     * @param parent
     * @return
     */
    @Override
    protected Control createContents(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);

        projectTree = new ProjectTree(this, sashForm);
        tabFolder = new ComponentTabFolder(this, sashForm);

        sashForm.setWeights(new int[]{30, 70});

        return super.createContents(parent);
    }

    /**
     * Gets the component tab folder for the component configuration etc.
     *
     * @return The component tab folder.
     */
    public ComponentTabFolder getTabFolder() {
        return tabFolder;
    }

    /**
     * Prototype function to update widgets on a model change.
     */
    public void updateWidgets() {
        if (projectTree.getProject() != null) {
            actionSaveProject.setEnabled(true);
        }
    }

    /**
     * Returns the project tree instance.
     *
     * @return The project tree.
     */
    public ProjectTree getProjectTree() {
        return projectTree;
    }

    private void run() {
        setBlockOnOpen(true);
        open();
        Display.getCurrent().dispose();
    }

    public static void main(String[] args) {
        BreakdownUI ui = new BreakdownUI();
        ui.run();
    }

}
