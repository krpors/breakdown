package nl.rivium.breakdown.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Class with utilities regarding user interface creation etc.
 */
public final class UITools {

    /**
     * Creates a Text object with a label before that. The function expects that the parent Composite's layout is a
     * GridLayout.
     *
     * @param parent       The parent composite. Must have a gridlayout assigned.
     * @param label        The label value for the text.
     * @param initialValue The initial value of the text field.
     * @return The Text object.
     */
    public static Text createTextWithLabel(Composite parent, String label, String initialValue) {
        if (parent.getLayout() == null || !(parent.getLayout() instanceof GridLayout)) {
            throw new IllegalArgumentException("Composite must have a GridLayout. Current is: " + parent.getLayout());
        }

        Label l = new Label(parent, SWT.NONE);
        l.setText(label);
        Text txt = new Text(parent, SWT.BORDER);
        txt.setText(initialValue);
        GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
        txt.setLayoutData(data);
        return txt;
    }

}
