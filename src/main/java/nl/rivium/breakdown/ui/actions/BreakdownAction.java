package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.ui.BreakdownUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Default abstract class for every JFace-able action.
 */
public abstract class BreakdownAction extends Action {

    private BreakdownUI ui;

    public BreakdownAction(BreakdownUI ui, String text) {
        this(ui, text, null);
    }

    public BreakdownAction(BreakdownUI ui, String text, ImageDescriptor desc) {
        super(text, desc);
        this.ui = ui;
    }

    /**
     * Returns the reference to the BreakdownUI class.
     *
     * @return The main user interface shell holding instance.
     */
    public BreakdownUI getBreakdownUI() {
        return ui;
    }
}
