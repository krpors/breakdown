package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.jms.JMSConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

/**
 * The ProjectTree contains the tree with the structured project hierarchy.
 * <p/>
 * Work in progress, replacement of the original ProjectTree. Using TreeViewer proved it to be difficult to add custom
 * nodes (e.g. "Jms Connections", "Stubs").
 */
public class ProjectTree {

    private static final String JMS_CONN_TREEITEM = "JMS_CONN_TREEITEM";

    /**
     * Logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(ProjectTree.class);

    /**
     * The UI.
     */
    private BreakdownUI breakdownUI;

    /**
     * Tree with components.
     */
    private Tree tree;

    /**
     * The project being displayed.
     */
    private Project project;

    public ProjectTree(BreakdownUI breakdownUI, Composite parent) {
        this.breakdownUI = breakdownUI;
        createContents(parent);
    }

    /**
     * Creates the main contents of the project tree.
     *
     * @param parent The parent composite.
     */
    private void createContents(Composite parent) {
        tree = new Tree(parent, SWT.SINGLE | SWT.BORDER);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (e.button == 1) { // left button, isn't 'constantized' anywhere.
                    Object o = getFirstSelection();
                    if (o instanceof GenericEntity) {
                        GenericEntity ge = (GenericEntity) o;
                        getBreakdownUI().getTabFolder().openTabItem(ge);
                    }
                }
            }
        });

        // To prevent expansions of tree items on double click. Check the source of the SWT Tree class, inside the
        // wmNotifyChild method in the case statement OS.NM_DBLCLK:
        //
        // "When the user double clicks on a tree item or a line beside the item, the window proc for the tree
        // collapses or expand the branch. When application code associates an action with double clicking, then
        // the tree expand is unexpected and unwanted.  The fix is to avoid the operation by testing to see whether
        // the mouse was inside a tree item."
        tree.addListener(SWT.MeasureItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // no-op, we don't want to do anything at this point.
            }
        });


        final Menu menu = new Menu(tree);
        tree.setMenu(menu);
        menu.addMenuListener(new TreeMenuListener(menu));
    }

    public BreakdownUI getBreakdownUI() {
        return breakdownUI;
    }

    public Project getProject() {
        return project;
    }

    /**
     * Sets the project. Removes all tree items, initializes the tree.
     *
     * @param project The project.
     */
    public void setProject(Project project) {
        this.project = project;

        TreeItem mainRoot = new TreeItem(tree, SWT.NONE);
        mainRoot.setText(project.getName());
        mainRoot.setImage(ImageCache.getImage(ImageCache.Icon.Project));
        mainRoot.setData(project);

        createTreeItemWrapperJMSConnections(mainRoot);
        createTreeItemWrapperTestSuites(mainRoot);
    }

    /**
     * Creates the JMS connections wrapper node and all JMS connections belonging to the project.
     *
     * @param root The root tree item.
     */
    private void createTreeItemWrapperJMSConnections(final TreeItem root) {
        TreeItem item = new TreeItem(root, SWT.NONE);
        item.setText("JMS connections");
        item.setData(JMS_CONN_TREEITEM);
        item.setImage(ImageCache.getImage(ImageCache.Icon.Folder));

        createTreeItemJMSConnections(item);
    }

    /**
     * Creates all JMS connection items present in the current project.
     *
     * @param root The root. Should be the "JMS connections" tree item.
     */
    private void createTreeItemJMSConnections(final TreeItem root) {
        for (JMSConnection c : project.getJmsConnections()) {
            TreeItem i = createTreeItem(root, c);
        }
    }

    private void createTreeItemWrapperTestSuites(final TreeItem root) {
        TreeItem item = new TreeItem(root, SWT.NONE);
        item.setText("Test suites");
        item.setImage(ImageCache.getImage(ImageCache.Icon.Create));
        for (TestSuite ts : project.getTestSuites()) {
            TreeItem i = createTreeItem(item, ts);
            createTreeItemTestCases(i, ts);
        }
    }

    private void createTreeItemTestCases(final TreeItem root, final TestSuite ts) {
        for (TestCase tc : ts.getTestCases()) {
            TreeItem i = createTreeItem(root, tc);
            createTreeItemTestSteps(i, tc);
        }
    }

    /**
     * Creates test steps.
     *
     * @param root The root.
     * @param tc   Testcase parent.
     */
    private void createTreeItemTestSteps(final TreeItem root, final TestCase tc) {
        for (TestStep ts : tc.getTestSteps()) {
            TreeItem i = createTreeItem(root, ts);
        }
    }

    /**
     * Generic utility method for creating a tree item in the tree.
     *
     * @param root The root to create the item under.
     * @param e    The generic entity.
     * @return The created tree item, with text, data and image.
     */
    private TreeItem createTreeItem(TreeItem root, GenericEntity e) {
        TreeItem item = new TreeItem(root, SWT.NONE);

        Image i = ImageCache.getImage(e.getClass());

        item.setText(e.getName());
        item.setData(e);
        if (i != null) {
            item.setImage(i);
        }

        return item;
    }

    /**
     * Gets the first selection of the tree.
     *
     * @return The first selection (getData() on the TreeItem).
     */
    public Object getFirstSelection() {
        if (tree.getSelection() != null) {
            return tree.getSelection()[0].getData();
        }

        // nothing selected.
        return null;
    }

    /**
     * This will only refresh the display data of existing items. It will not remove, re-add or whatever. Use setProject
     * for that. It will iterate through all the existing tree items, and refresh the names of them.
     */
    public void refresh() {
        if (tree.isDisposed()) {
            return;
        }

        Deque<TreeItem> q = new ArrayDeque<>();
        Collections.addAll(q, tree.getItems());
        while (!q.isEmpty()) {
            TreeItem item = q.remove();
            Collections.addAll(q, item.getItems());

            Object data = item.getData();
            if (data instanceof GenericEntity) {
                GenericEntity ge = (GenericEntity) data;
                item.setText(ge.getName()); // update the name.
            }
        }
    }

    /**
     * The menu listener, responsible for showing menus and responding to the events.
     */
    class TreeMenuListener extends MenuAdapter {

        private final Menu menu;

        public TreeMenuListener(final Menu menu) {
            this.menu = menu;

        }

        @Override
        public void menuShown(MenuEvent e) {
            for (MenuItem item : menu.getItems()) {
                item.dispose();
            }

            Object d = getFirstSelection();
            if (JMS_CONN_TREEITEM.equals(d)) {
                createMenuItemsJMSConnectionsWrapper();
                return;
            } else if (d instanceof JMSConnection) {
                createMenuItemsJMSConnections();
            } else if (d instanceof TestCase) {
                createMenuItemsTestCase();
            }
        }

        // TODO: MOVE THE SELECTION LISTENERS TO SOMETHING GENERIC CUS THIS SUX ASS

        /**
         * Create menu items for the 'JMS Connections' tree item.
         */
        private void createMenuItemsJMSConnectionsWrapper() {
            MenuItem itemAdd = UITools.createMenuItem(menu, "Add JMS connection", ImageCache.getImage(ImageCache.Icon.Create));
            itemAdd.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem item = (TreeItem) tree.getSelection()[0];
                    item.removeAll();

                    JMSConnection c = new JMSConnection("JMS Connection", getProject());
                    getBreakdownUI().getTabFolder().openTabItem(c);

                    createTreeItemJMSConnections(item);
                }
            });
        }

        /**
         * Creates the mnenu item for JMS connections.
         */
        private void createMenuItemsJMSConnections() {
            MenuItem itemDelete = UITools.createMenuItem(menu, "Delete JMS connection", ImageCache.getImage(ImageCache.Icon.Delete));
            itemDelete.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem item = tree.getSelection()[0];
                    JMSConnection conn = (JMSConnection) item.getData();

                    item.dispose();
                    getProject().getJmsConnections().remove(conn);
                }
            });
        }

        private void createMenuItemsTestCase() {
            MenuItem itemDelete = UITools.createMenuItem(menu, "Execute", ImageCache.getImage(ImageCache.Icon.Play));
            itemDelete.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem item = tree.getSelection()[0];
                    TestCase tc = (TestCase) item.getData();
                    try {
                        tc.execute();

                    } catch (AssertionException e1) {
                        e1.printStackTrace();
                    } catch (BreakdownException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
    }
}