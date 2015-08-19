package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.jms.JMSConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

/**
 * The ProjectTree contains the tree with the structured project hierarchy.
 *
 * Work in progress, replacement of the original ProjectTree. Using TreeViewer proved it to be difficult to add custom
 * nodes (e.g. "Jms Connections", "Stubs").
 */
public class ProjectTree2 {

    private static Logger LOG = LoggerFactory.getLogger(ProjectTree2.class);

    private BreakdownUI breakdownUI;
    private Tree tree;
    private Project project;

    public ProjectTree2(BreakdownUI breakdownUI, Composite parent) {
        this.breakdownUI = breakdownUI;
        createContents(parent);
    }

    private void createContents(Composite parent) {
        tree = new Tree(parent, SWT.SINGLE);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        tree.removeAll();

        this.project = project;

        TreeItem mainRoot = new TreeItem(tree, SWT.NONE);
        mainRoot.setText(project.getName());
        mainRoot.setImage(ImageCache.getImage(ImageCache.Icon.Project));

        createJMSConnections(mainRoot);
        createTestSuites(mainRoot);
    }

    private void createJMSConnections(final TreeItem root) {
        TreeItem item = new TreeItem(root, SWT.NONE);
        item.setText("JMS connections");
        item.setImage(ImageCache.getImage(ImageCache.Icon.Folder));

        for (JMSConnection c : project.getJmsConnections()) {
            TreeItem i = createItem(item, c);
        }
    }

    private void createTestSuites(final TreeItem root) {
        TreeItem item = new TreeItem(root, SWT.NONE);
        item.setText("Test suites");
        item.setImage(ImageCache.getImage(ImageCache.Icon.Create));
        for (TestSuite ts : project.getTestSuites()) {
            TreeItem i = createItem(item, ts);
            createTestCases(i, ts);
        }
    }

    private void createTestCases(final TreeItem root, final TestSuite ts) {
        for (TestCase tc : ts.getTestCases()) {
            TreeItem i = createItem(root, tc);
            createTestSteps(i, tc);
        }
    }

    private void createTestSteps(final TreeItem root, final TestCase tc) {
        for (TestStep ts : tc.getTestSteps()) {
            TreeItem i = createItem(root, ts);
        }
    }

    private TreeItem createItem(TreeItem root, GenericEntity e) {
        TreeItem item = new TreeItem(root, SWT.NONE);

        item.setText(e.getName());
        item.setData(e);
        item.setImage(getImage(e));

        return item;
    }

    private Image getImage(Object element) {
        if (element instanceof Project) {
            return ImageCache.getImage(ImageCache.Icon.Project);
        }

        if (element instanceof TestSuite) {
            return ImageCache.getImage(ImageCache.Icon.TestSuite);
        }

        if (element instanceof TestCase) {
            return ImageCache.getImage(ImageCache.Icon.TestCase);
        }

        if (element instanceof TestStep) {
            return ImageCache.getImage(ImageCache.Icon.TestStep);
        }

        if (element instanceof JMSConnection) {
            return ImageCache.getImage(ImageCache.Icon.JMSConnection);
        }

        return ImageCache.getImage(ImageCache.Icon.Folder);
    }

    public void refresh() {
        if (tree.isDisposed()) {
            return;
        }

        // TODO: iterate through all items, reset data.
        Deque<TreeItem> q = new ArrayDeque<>();
        Collections.addAll(q, tree.getItems());
        while (!q.isEmpty()) {
            TreeItem item = q.remove();
            Collections.addAll(q, item.getItems());

            System.out.println(item.getData());

            Object data = item.getData();
            if (data instanceof GenericEntity) {
                GenericEntity ge = (GenericEntity) data;
                item.setText(ge.getName()); // update the name.
            }
        }
    }
}