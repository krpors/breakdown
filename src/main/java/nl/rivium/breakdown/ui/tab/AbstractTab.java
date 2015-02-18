package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.GenericEntity;
import nl.rivium.breakdown.ui.BreakdownUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * Every tab added to the tabfolder has some basic properties. This class implements those.
 */
public abstract class AbstractTab<E extends GenericEntity> implements Observer {

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

    /**
     * Creates the tab.
     *
     * @param ui     The parent BreakdownUI class.
     * @param parent The parent tab folder.
     * @param entity The parameterized entity.
     */
    public AbstractTab(BreakdownUI ui, CTabFolder parent, E entity) {
        this.ui = ui;
        this.tabFolder = parent;
        this.entity = entity;

        createComponents();
    }

    /**
     * Creates the default tab item component. This will call the createContents() function to fill in the tab itself.
     */
    private void createComponents() {
        // register ourselves as an observer.
        LOG.debug("Adding observer {} to {}", getEntity(), this);
        getEntity().addObserver(this);

        tabItem = new CTabItem(tabFolder, SWT.CLOSE);
        // setData is used here so it contains a 'loosely coupled' reference to the entity. That way, we can
        // check if a tab is trying to be opened using the same entity. If so, we bring it to the front instead
        // of opening it twice. See ProjectTree:ProjectTreeOpenListener:bringToFront().
        tabItem.setData(entity);
        tabItem.setText(entity.getName());
        tabItem.setImage(getImage());
        tabItem.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                LOG.debug("Removing observer {} from {}", AbstractTab.this, getEntity());
                saveChanges(); // force to save changes to prevent loss of data.
                // make sure to REMOVE ourselves as an observer, when the tabitem is disposed.
                getEntity().deleteObserver(AbstractTab.this);
            }
        });

        Composite contents = createContents(tabFolder);

        tabItem.setControl(contents);
    }

    /**
     * Gets the main BreakdownUI.
     *
     * @return The main UI ref.
     */
    public BreakdownUI getBreakdownUI() {
        return ui;
    }

    public E getEntity() {
        return entity;
    }

    /**
     * Gets the Tabitem associated with this tab.
     *
     * @return The tab item.
     */
    public CTabItem getTabItem() {
        return tabItem;
    }

    /**
     * Will be invoked when the widgets needs content updating. This can mainly happen when the core model is updated
     * outside of the tab's influence. It should generally set textfield contents etc.
     */
//    protected abstract void updateWidgets();

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

    /**
     * Should be invoked to save changes to the model.
     */
    public abstract void saveChanges();

    /**
     * Notified from observers. Subclasses may need to override this to get notified.
     *
     * @param o   The Observable class.
     * @param arg The argument
     */
    @Override
    public void update(Observable o, Object arg) {
    }
}
