package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.GenericEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Every tab added to the tabfolder has some basic properties. This class implements those.
 */
public abstract class AbstractTab<E extends GenericEntity> {

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
        tabItem.setText(entity.getName());
        tabItem.setImage(getImage());

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
