package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.Main;
import nl.rivium.breakdown.core.*;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ProjectTree contains the tree with the structured project hierarchy.
 */
public class ProjectTree {

    private static Logger LOG = LoggerFactory.getLogger(ProjectTree.class);

    private TreeViewer treeViewer;
    private Project project;

    public ProjectTree(Composite parent) {
        createContents(parent);
    }

    private void createContents(Composite parent) {
        treeViewer = new TreeViewer(parent);
        treeViewer.setContentProvider(new ProjectTreeContentProvider());
        treeViewer.setLabelProvider(new ProjectTreeLabelProvider());

        try {
            Project p = Main.createProject();
            setProject(p);
        } catch (BreakdownException e) {
            e.printStackTrace();
        }
    }

    public Project getProject() {
        return this.project;
    }

    /**
     * Loads up the tree using the given Project.
     *
     * @param p The Project to load into the tree.
     */
    public void setProject(Project p) {
        Root root = new Root(p);
        this.project = p;
        treeViewer.setInput(root);
        treeViewer.expandAll();
    }

    /**
     * Content provider for the TreeViewer.
     */
    class ProjectTreeContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getElements(Object o) {
            LOG.debug("Getting elements of {}", o);

            // Sneaky way to add a root, so the Project node is visible.
            if (o instanceof Root) {
                Root root = (Root) o;
                return new Object[]{root.getProject()};
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
    class ProjectTreeLabelProvider extends LabelProvider {

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
