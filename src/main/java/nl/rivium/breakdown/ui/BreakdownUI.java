package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.ui.dlg.HeaderPropertyDialog;
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

    private ImageCache imageCache;

    private ProjectTree projectTree;

    private CTabFolder tabFolder;

    public BreakdownUI() {
        super(null);

        imageCache = new ImageCache();
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
                    if (getTabFolder().getSelection() != null) {
                        getTabFolder().getSelection().dispose();
                    }
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

    @Override
    protected MenuManager createMenuManager() {
        MenuManager mgr = new MenuManager();

        MenuManager menuFile = new MenuManager("File");

        return mgr;
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
        tabFolder = new CTabFolder(sashForm, SWT.BORDER);
        tabFolder.setBorderVisible(true);
        tabFolder.setTabHeight(25);

        sashForm.setWeights(new int[]{30, 70});

        return super.createContents(parent);
    }

    /**
     * Gets the tab folder for the component configuration etc.
     *
     * @return The tab folder.
     */
    public CTabFolder getTabFolder() {
        return tabFolder;
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
