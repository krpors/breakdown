package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.ui.BreakdownUI;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to exit the application.
 */
public class ActionExit extends BreakdownAction {

    private static Logger LOG = LoggerFactory.getLogger(ActionExit.class);

    public ActionExit(BreakdownUI ui) {
        super(ui, "&Exit");
        setAccelerator(SWT.ALT | SWT.F4);
    }

    @Override
    public void run() {
        // TODO: ask for save and shit.
        System.exit(0);
    }
}
