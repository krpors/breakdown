package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.Main;
import nl.rivium.breakdown.core.*;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
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

        shell.setSize(320, 240);
        shell.setText("Hello.");

        Display display = shell.getDisplay();

        // this centers the shell on the screen:
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /**
     * Creates the main application window's contents.
     *
     * @param parent
     * @return
     */
    @Override
    protected Control createContents(Composite parent) {
        TreeViewer viewer = new TreeViewer(parent);
        viewer.setContentProvider(new MyContentProvider());
        viewer.setLabelProvider(new MyLabelProvider());

        try {
            Project p = Main.createProject();
            Root root = new Root(p);
            viewer.setInput(root);
        } catch (BreakdownException e) {
            e.printStackTrace();
        }

        return super.createContents(parent);
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

    /**
     * Content provider for the TreeViewer.
     */
    class MyContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getElements(Object o) {
            LOG.debug("Getting elements of {}", o);

            // Sneaky way to add a root, so the Project node is visible.
            if (o instanceof Root) {
                Root root = (Root) o;
                return new Object[] { root.getProject()} ;
            }

            if (o instanceof GenericEntity) {
                GenericEntity genericEntity = (GenericEntity) o;
                return genericEntity.getChildren();
            }

            return null;
        }

        @Override
        public Object[] getChildren(Object o) {
            LOG.debug("Getting children of {}", o);
            if (o instanceof GenericEntity) {
                GenericEntity genericEntity = (GenericEntity) o;
                return genericEntity.getChildren();
            }

            return null;
        }

        @Override
        public Object getParent(Object o) {
            // TODO get parent, but when is it called?
            LOG.debug("getParent called... now what");
            return o;
        }

        @Override
        public boolean hasChildren(Object o) {
            LOG.debug("Has children: " + o);
            if (o instanceof GenericEntity) {
                GenericEntity genericEntity = (GenericEntity) o;
                return genericEntity.getChildren().length > 0;
            }

            return false;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void inputChanged(Viewer viewer, Object o, Object o1) {
            LOG.debug("inputChanged called");
        }
    }

    /**
     * Label provider for tree elements.
     */
    class MyLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            if (element instanceof Project) {
                return ImageCache.getImage(ImageCache.UIImage.Project);
            }

            if (element instanceof TestSuite) {
                return ImageCache.getImage(ImageCache.UIImage.TestSuite);
            }

            if (element instanceof TestCase) {
                return ImageCache.getImage(ImageCache.UIImage.TestCase);
            }

            if (element instanceof TestStep) {
                return ImageCache.getImage(ImageCache.UIImage.TestStep);
            }

            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof Project) {
                Project project = (Project) element;
                return "Project " + project.getName();
            }

            if (element instanceof TestSuite) {
                TestSuite testSuite = (TestSuite) element;
                return String.format("Test Suite '%s'", testSuite.getName());
            }

            if (element instanceof TestCase) {
                TestCase testCase = (TestCase) element;
                return String.format("Test Case '%s'", testCase.getName());
            }

            if (element instanceof TestStep) {
                TestStep testStep = (TestStep) element;
                return String.format("Test Step '%s'", testStep.getName());
            }

            return super.getText(element);
        }
    }
}
