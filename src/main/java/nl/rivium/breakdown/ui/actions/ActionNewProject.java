package nl.rivium.breakdown.ui.actions;

import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import org.eclipse.jface.action.Action;

/**
 * Created by Kevin on 2/17/2015.
 */
public class ActionNewProject extends BreakdownAction {

    public ActionNewProject(BreakdownUI ui) {
        super(ui, "&New", ImageCache.getDescriptor(ImageCache.UIImage.Project));
    }

    @Override
    public void run() {
        System.out.println("Action is run");
    }
}
