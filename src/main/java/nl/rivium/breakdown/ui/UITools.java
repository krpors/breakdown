package nl.rivium.breakdown.ui;

import ch.qos.logback.core.helpers.ThrowableToStringArray;
import org.eclipse.core.internal.runtime.Activator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with utilities regarding user interface creation etc.
 */
public final class UITools {

    /**
     * Shows an exception with stacktrace elements.
     * @param parent The parent shell.
     * @param title Title of the dialog.
     * @param message
     * @param ex
     */
    public static void showException(Shell parent, String title, String message, Throwable ex) {
        List<IStatus> statusses = new ArrayList<>();
        for (StackTraceElement ste : ex.getStackTrace()) {
            statusses.add(new Status(Status.ERROR, "plz", ste.toString()));
        }
        IStatus[] array = statusses.toArray(new IStatus[statusses.size()]);

        String exmsg = ex.getMessage();
        if (exmsg == null || exmsg.trim().equals("")) {
            exmsg = "Please inspect the details for further information.";
        }

        MultiStatus status = new MultiStatus("plg", Status.ERROR, array, exmsg, ex);

        ErrorDialog.openError(parent, title, message, status);
    }

    /**
     * Simplifies creating a label, because we don't need a reference to it.
     * @param parent The parent composite.
     * @param labelText The text for the label itself.
     * @return The created label.
     */
    public static Label createLabel(Composite parent, String labelText) {
        Label l = new Label(parent, SWT.NONE);
        l.setText(labelText);
        return l;
    }

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
        txt.setText(initialValue == null ? "" : initialValue);
        GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
        txt.setLayoutData(data);
        return txt;
    }

}
