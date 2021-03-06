package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.GenericEntity;
import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.ui.tab.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

/**
 * Class where the component configuration tab folder is created.
 */
public class ComponentTabFolder {

    /**
     * Our logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(ComponentTabFolder.class);

    /**
     * The BreakdownUI main.
     */
    private BreakdownUI breakdownUI;

    /**
     * The tab folder containing open configurations.
     */
    private CTabFolder tabFolder;

    public ComponentTabFolder(BreakdownUI breakdownUI, Composite parent) {
        this.breakdownUI = breakdownUI;

        createContents(parent);
    }

    /**
     * Create tab contents.
     *
     * @param parent The parente composite.
     */
    private void createContents(Composite parent) {
        tabFolder = new CTabFolder(parent, SWT.BORDER);
        tabFolder.setBorderVisible(true);
        tabFolder.setTabHeight(25);
        // Middle click to dispose of a tab item.
        tabFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 2) {
                    disposeTabAtPoint(new Point(e.x, e.y));
                }
            }
        });
    }

    /**
     * Attempts to bring a tab with the generic entity to front, if a tab exists with that data. If it did, the
     * tab will be brought to the front, and true is returned. Else, false is returned.
     *
     * @param entity The entity trying to be opened;
     * @return true when a tab with the entity was found and brought to the front. False if otherwise.
     */
    public boolean bringToFront(GenericEntity entity) {
        for (CTabItem item : tabFolder.getItems()) {
            if (item.getData().equals(entity)) {
                tabFolder.setSelection(item);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the current open entity (tab), or null when none are open.
     *
     * @return The currently visible/open entity.
     */
    public GenericEntity getVisibleEntity() {
        CTabItem item = tabFolder.getSelection();
        if (item != null) {
            return (GenericEntity) item.getData();
        }
        return null;
    }

    /**
     * Opens a new tab item of the specified entity.
     *
     * @param entity The entity to create a new tab item for.
     */
    public void openTabItem(GenericEntity entity) {
        BreakdownUI ui = this.breakdownUI;

        // If the entity is already opened, bring it to front to prevent multiple tabs from being opened.
        if (bringToFront(entity)) {
            return;
        }

        AbstractTab createdTab = null;
        if (entity instanceof Project) {
            createdTab = new ProjectTab(ui, tabFolder, (Project) entity);
        } else if (entity instanceof TestSuite) {
            createdTab = new TestSuiteTab(ui, tabFolder, (TestSuite) entity);
        } else if (entity instanceof TestCase) {
            createdTab = new TestCaseTab(ui, tabFolder, (TestCase) entity);
        } else if (entity instanceof JMSRequestReply) {
            createdTab = new TestStepJMSRequestReplyTab(ui, tabFolder, (JMSRequestReply) entity);
        } else if (entity instanceof JMSConnection) {
            createdTab = new JMSConnectionTab(ui, tabFolder, (JMSConnection) entity);
        }

        if (createdTab != null) {
            tabFolder.setSelection(createdTab.getTabItem());
        }
    }

    /**
     * Disposes a tab item which exists at the given point.
     *
     * @param p The point.
     */
    private void disposeTabAtPoint(Point p) {
        CTabItem item = tabFolder.getItem(p);
        if (item != null) {
            item.dispose();
        }
    }

    /**
     * Disposes a tab with the given GenericEntity. This will also traverse through the children of the entity, and
     * closes possible open tabs of those children as well. In other words, it recurses through the children, except
     * it's not done with recursion but with a deque.
     *
     * @param entity The entity.
     */
    public void disposeTabEntity(GenericEntity entity) {
        // First, get all children of the given entity in the argument (no recursion, simple deque).
        Queue<GenericEntity> q = new ArrayDeque<>();
        q.add(entity);
        while (q.peek() != null) {
            GenericEntity ge = q.remove();
            Collections.addAll(q, ge.getChildren());

            // now iterate through all the open tab items, and see if their getData() returns
            // the entity found as a child.
            for (CTabItem item : tabFolder.getItems()) {
                if (item.getData().equals(ge)) {
                    item.dispose();
                }
            }
        }
    }

    /**
     * Disposes of all tabs.
     */
    public void disposeAllTabs() {
        for (CTabItem item : tabFolder.getItems()) {
            item.dispose();
        }
    }

    /**
     * Dispose of the selected tab, if there is any selected.
     */
    public void disposeSelectedTab() {
        if (tabFolder.getSelection() != null) {
            tabFolder.getSelection().dispose();
        }
    }
}
