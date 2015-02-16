package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.Main;
import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ProjectTree contains the tree with the structured project hierarchy.
 */
public class ProjectTree {

    private static Logger LOG = LoggerFactory.getLogger(ProjectTree.class);

    private BreakdownUI ui;
    private TreeViewer treeViewer;
    private Project project;

    public ProjectTree(BreakdownUI ui, Composite parent) {
        this.ui = ui;
        createContents(parent);
    }

    private void createContents(Composite parent) {
        treeViewer = new TreeViewer(parent);
        treeViewer.setContentProvider(new ProjectTreeContentProvider());
        treeViewer.setLabelProvider(new ProjectTreeLabelProvider());
        treeViewer.addOpenListener(new ProjectTreeOpenListener(this));

        try {
            Project project = Main.createProject();
            setProject(project);
        } catch (BreakdownException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the project configuration associated with the tree. Can be null if it's not set yet.
     *
     * @return The project instance.
     */
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
     * Rereshes the tree.
     */
    public void refresh() {
        treeViewer.refresh(true);
    }

    /**
     * Class for listening for open events when elements on the tree are double clicked, or the Enter key was used.
     * Will open up a new tab, or bring an existing tab to the front.
     */
    class ProjectTreeOpenListener implements IOpenListener {
        /**
         * Project tree reference.
         */
        private final ProjectTree tree;

        public ProjectTreeOpenListener(ProjectTree tree) {
            this.tree = tree;
        }

        /**
         * Attempts to bring a tab with the generic entity to front, if a tab exists with that data. If it did, the
         * tab will be brought to the front, and true is returned. Else, false is returned.
         *
         * @param entity The entity trying to be opened;
         * @return true when a tab with the entity was found and brought to the front. False if otherwise.
         */
        public boolean bringToFront(GenericEntity entity) {
            for (CTabItem item : tree.ui.getTabFolder().getItems()) {
                System.out.println("Open tab: " + item.getData());
                if (item.getData().equals(entity)) {
                    tree.ui.getTabFolder().setSelection(item);
                    return true;
                }
            }

            return false;
        }

        @Override
        public void open(OpenEvent openEvent) {
            BreakdownUI ui = ProjectTree.this.ui;
            CTabFolder folder = ui.getTabFolder();

            TreeSelection selection = (TreeSelection) openEvent.getSelection();
            if (!(selection.getFirstElement() instanceof GenericEntity)) {
                return;
            }

            GenericEntity first = (GenericEntity) selection.getFirstElement();

            if (bringToFront(first)) {
                return;
            }

            AbstractTab createdTab = null;
            if (first instanceof Project) {
                Project project = (Project) first;
                createdTab = new ProjectTab(ui, folder, project);
            } else if (first instanceof TestSuite) {
                TestSuite testSuite = (TestSuite) first;
                createdTab = new TestSuiteTab(ui, folder, testSuite);
            } else if (first instanceof TestCase) {
                TestCase testCase = (TestCase) first;
                createdTab = new TestCaseTab(ui, folder, testCase);
            } else if (first instanceof JMSRequestReply) {
                JMSRequestReply testStep = (JMSRequestReply) first;
                createdTab = new TestStepJMSRequestReplyTab(ui, folder, testStep);
            }

            if (createdTab != null) {
                folder.setSelection(createdTab.getTabItem());
            }

        }
    }

    /**
     * Content provider for the TreeViewer.
     */
    class ProjectTreeContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getElements(Object o) {
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
            if (o instanceof GenericEntity) {
                GenericEntity genericEntity = (GenericEntity) o;
                return genericEntity.getChildren().length > 0;
            }

            return false;
        }

        @Override
        public void dispose() {

        }

        /**
         * Called whenever the tree is refreshed with new data.
         * @param viewer The viewer.
         * @param o ?
         * @param o1 ?
         */
        @Override
        public void inputChanged(Viewer viewer, Object o, Object o1) {
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

            if (element instanceof JMSConnection) {
                return ImageCache.getImage(ImageCache.UIImage.JMSConnection);
            }

            return ImageCache.getImage(ImageCache.UIImage.Folder);
        }

        @Override
        public String getText(Object element) {
            if (element instanceof GenericEntity) {
                GenericEntity genericEntity = (GenericEntity) element;
                return genericEntity.getName();
            }

            return super.getText(element);
        }
    }
}
