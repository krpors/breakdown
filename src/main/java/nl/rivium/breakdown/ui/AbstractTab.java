package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.GenericEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

/**
 * Every tab added to the tabfolder has some basic properties. This class implements those.
 */
public abstract class AbstractTab {

    private GenericEntity entity;
    private BreakdownUI ui;

    private CTabFolder tabFolder;

    public AbstractTab(BreakdownUI ui, CTabFolder parent, GenericEntity entity) {
        this.ui = ui;
        this.tabFolder = parent;
        this.entity = entity;

        createComponents();
    }

    private void createComponents() {
        CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
        item.setText(entity.getName());
    }
}
