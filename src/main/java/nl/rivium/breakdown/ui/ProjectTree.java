package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.jms.JMSConnection;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.win32.MENUITEMINFO;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.image.ImageAccessException;

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
        treeViewer.getTree().setMenu(createProjectMenu(treeViewer.getTree()));
    }

    private Menu createProjectMenu(Composite parent) {
        final Menu menu = new Menu(parent);

        // This is a tricky way to add context sensitive menu to the tree.
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {
                // Remove all
                for (MenuItem item : menu.getItems()) {
                    item.dispose();
                }
                Object first = getFirstSelection();
                if (first instanceof Project) {
                    createMenuItemsForProject(menu);
                } else if (first instanceof TestSuite) {
                    createMenuItemsForTestSuite(menu);
                }
            }
        });
        return menu;
    }

    public Object getFirstSelection() {
        TreeSelection s = (TreeSelection) treeViewer.getSelection();
        if (s == null) {
            return null;
        }
        return s.getFirstElement();
    }

    /**
     * Creates context sensitive menu items for a Project entity.
     *
     * @param menu The menu to add the items to.
     */
    private void createMenuItemsForProject(Menu menu) {
        MenuItem itemAddSuite = UITools.createMenuItem(menu, "Add test suite", ImageCache.getImage(ImageCache.UIImage.Create));
        MenuItem itemAddConnection = UITools.createMenuItem(menu, "Add JMS connection", ImageCache.getImage(ImageCache.UIImage.JMSConnection));

        new MenuItem(menu, SWT.SEPARATOR);

        itemAddSuite.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TestSuite s = new TestSuite("Test suite", getProject());
                getProject().getTestSuites().add(s);
                refresh();

                breakdownUI.getTabFolder().openTabItem(s);
            }
        });

        itemAddConnection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JMSConnection c = new JMSConnection("JMS connection", getProject());
                getProject().getJmsConnections().add(c);
                refresh();
                breakdownUI.getTabFolder().openTabItem(c);
            }
        });
    }

    /**
     * Creates context sensitive menu items for a TestSuite entity.
     *
     * @param menu The menu to add items to.
     */
    private void createMenuItemsForTestSuite(Menu menu) {
        MenuItem itemAddCase = UITools.createMenuItem(menu, "Add test case", ImageCache.getImage(ImageCache.UIImage.Create));

        MenuItem sep = new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemDelete = UITools.createMenuItem(menu, "Delete", ImageCache.getImage(ImageCache.UIImage.Delete));

        itemDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TestSuite selected = (TestSuite) getFirstSelection();
                selected.getParent().getTestSuites().remove(selected);

                refresh();
            }
        });
        itemAddCase.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TestSuite item = (TestSuite) getFirstSelection();
                TestCase c = new TestCase("Test case", item);
                item.getTestCases().add(new TestCase("Test case", ""));
                refresh();

                breakdownUI.getTabFolder().openTabItem(c);
            }
        });
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
