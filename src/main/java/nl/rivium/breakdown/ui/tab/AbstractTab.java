package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.ExecutionListener;
import nl.rivium.breakdown.core.GenericEntity;
import nl.rivium.breakdown.ui.BreakdownUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Every tab added to the tabfolder has some basic properties. This class implements those.
 */
public abstract class AbstractTab<E extends GenericEntity> implements ExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTab.class);
    private E entity;
    private BreakdownUI ui;

    /**
     * The tab folder parent.
     */
    private CTabFolder tabFolder;

    /**
     * The tab item which holds the contents of this tab.
     */
    private CTabItem tabItem;

    public AbstractTab(BreakdownUI ui, CTabFolder parent, E entity) {
        this.ui = ui;
        this.tabFolder = parent;
        this.entity = entity;

        createComponents();
    }

    private void createComponents() {
        tabItem = new CTabItem(tabFolder, SWT.CLOSE);
        // setData is used here so it contains a 'loosely coupled' reference to the entity. That way, we can
        // check if a tab is trying to be opened using the same entity. If so, we bring it to the front instead
        // of opening it twice. See ProjectTree:ProjectTreeOpenListener:bringToFront().
        tabItem.setData(entity);
        tabItem.setText(entity.getName());
        tabItem.setImage(getImage());

        // Add ONE executionlistener for this tab instance.
        entity.addExecutionListener(this);
        // .. and also, remove the execution listener when the tab item is disposed:
        tabItem.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                entity.getExecutionListeners().remove(AbstractTab.this);
            }
        });


        Composite contents = createContents(tabFolder);

        tabItem.setControl(contents);
    }

    public BreakdownUI getBreakdownUI() {
        return ui;
    }

    public void setUi(BreakdownUI ui) {
        this.ui = ui;
    }

    public E getEntity() {
        return entity;
    }

    public CTabItem getTabItem() {
        return tabItem;
    }

    /**
     * Subclasses must override this function to create the tab's contents.
     *
     * @param parent The parent folder.
     * @return The composite containing the widgets and such.
     */
    protected abstract Composite createContents(CTabFolder parent);

    /**
     * Returns the image applicable for this tab.
     *
     * @return The image for the tab type.
     */
    protected abstract Image getImage();
}
