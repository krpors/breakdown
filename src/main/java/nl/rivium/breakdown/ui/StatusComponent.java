package nl.rivium.breakdown.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Component on the bottom of the screen, with several tab items to display statusses and whatnot.
 */
public class StatusComponent {

    private CTabFolder folder;

    public StatusComponent(Composite parent) {
        createContents(parent);
    }

    /**
     * @param parent
     */
    private void createContents(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        c.setLayout(layout);
    }

}
