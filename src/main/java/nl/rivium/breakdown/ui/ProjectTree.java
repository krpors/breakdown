package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.ui.actions.*;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ProjectTree contains the tree with the structured project hierarchy.
 */
public class ProjectTree {

    private static Logger LOG = LoggerFactory.getLogger(ProjectTree.class);

    private BreakdownUI breakdownUI;
    private TreeViewer treeViewer;
    private Project project;

    public ProjectTree(BreakdownUI breakdownUI, Composite parent) {
        this.breakdownUI = breakdownUI;
        createContents(parent);
    }

    private void createContents(Composite parent) {
        treeViewer = new TreeViewer(parent, SWT.SINGLE);
        treeViewer.setContentProvider(new ProjectTreeContentProvider());
        treeViewer.setLabelProvider(new ProjectTreeLabelProvider());
        treeViewer.addOpenListener(new ProjectTreeOpenListener(this));
        final MenuManager mgr = new MenuManager("YO!");
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager iMenuManager) {
                if (getFirstSelection() instanceof JMSConnection) {
                    mgr.add(new ActionDeleteEntity(ProjectTree.this));
                } else if (getFirstSelection() instanceof TestSuite) {
                    mgr.add(new ActionExecuteEntity(ProjectTree.this));
                    mgr.add(new Separator());
                    mgr.add(new ActionNewTestCase(ProjectTree.this));
                    mgr.add(new Separator());
                    mgr.add(new ActionDeleteEntity(ProjectTree.this));
                } else if (getFirstSelection() instanceof TestCase) {
                    mgr.add(new ActionExecuteEntity(ProjectTree.this));
                    mgr.add(new Separator());
                    mgr.add(new ActionDeleteEntity(ProjectTree.this));
                } else if (getFirstSelection() instanceof Project) {
                    mgr.add(new ActionExecuteEntity(ProjectTree.this));
                    mgr.add(new Separator());
                    mgr.add(new ActionNewJMSConnection(ProjectTree.this));
                    mgr.add(new ActionNewTestSuite(ProjectTree.this));
                }
            }
        });
        Menu menu = mgr.createContextMenu(treeViewer.getTree());

        treeViewer.getTree().setMenu(menu);

    }

    public Object getFirstSelection() {
        TreeSelection s = (TreeSelection) treeViewer.getSelection();
        if (s == null) {
            return null;
        }
        return s.getFirstElement();
    }

    /**
     * Quick hack to expand to a level after an item is added.
     *
     * @param level The level.
     */
    public void expandToLevel(int level) {
        treeViewer.expandToLevel(level);
    }

    /**
     * Gets the breakdown UI.
     *
     * @return The BreakdownUI.
     */
    public BreakdownUI getBreakdownUI() {
        return breakdownUI;
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
        if (!treeViewer.getTree().isDisposed()) {
            treeViewer.refresh(true);
        }
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

        @Override
        public void open(OpenEvent openEvent) {
            BreakdownUI ui = ProjectTree.this.breakdownUI;

            TreeSelection selection = (TreeSelection) openEvent.getSelection();
            if (!(selection.getFirstElement() instanceof GenericEntity)) {
                return;
            }

            GenericEntity first = (GenericEntity) selection.getFirstElement();
            ui.getTabFolder().openTabItem(first);
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
            LOG.debug("getParent: no-op");
            return null;
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
         *
         * @param viewer The viewer.
         * @param o      ?
         * @param o1     ?
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
