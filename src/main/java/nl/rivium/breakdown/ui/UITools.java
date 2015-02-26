package nl.rivium.breakdown.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Class with utilities regarding user interface creation etc.
 */
public final class UITools {

    /**
     * Shows an exception with stacktrace elements.
     *
     * @param parent  The parent shell.
     * @param title   Title of the dialog.
     * @param message
     * @param ex
     */
    public static void showException(Shell parent, String title, String message, Throwable ex) {
        List<IStatus> statusses = new ArrayList<>();

        Queue<Throwable> causes = new ArrayDeque<>();
        // add causes:
        if (ex.getCause() != null) {
            causes.add(ex.getCause());
            while (causes.peek() != null) {
                Throwable cause = causes.remove();
                statusses.add(new Status(SWT.ERROR, "pluginid", "Caused by: " + cause.toString()));
                if (cause.getCause() != null) {
                    causes.add(cause.getCause());
                }
            }
        }

        IStatus[] array = statusses.toArray(new IStatus[statusses.size()]);

        String reason = ex.getMessage();
        if (reason == null || reason.trim().equals("")) {
            reason = "Please inspect the details for further information.";
        }

        MultiStatus status = new MultiStatus("plg", Status.ERROR, array, reason, ex);

        ErrorDialog.openError(parent, title, message, status);
    }

    /**
     * Simplifies creating a label, because we don't need a reference to it.
     *
     * @param parent    The parent composite.
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

    /**
     * Single line menu item creation.
     *
     * @param parent The parent menu.
     * @param text   The text for the item.
     * @param image  The image for the item, or null for none.
     * @return The MenuItem.
     */
    public static MenuItem createMenuItem(Menu parent, String text, Image image) {
        MenuItem item = new MenuItem(parent, SWT.NONE);
        item.setText(text);
        item.setImage(image);
        return item;
    }
}
