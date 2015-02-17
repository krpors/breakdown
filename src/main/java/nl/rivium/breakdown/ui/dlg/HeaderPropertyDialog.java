package nl.rivium.breakdown.ui.dlg;

import nl.rivium.breakdown.ui.UITools;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Dialog for entrying property names and values.
 */
public class HeaderPropertyDialog extends TitleAreaDialog {

    private Label lblError;
    private Text txtName;
    private Text txtValue;

    private String name = "";
    private String value = "";

    public HeaderPropertyDialog(Shell shell) {
        super(shell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Add JMS property");
        setMessage("Enter the JMS property and its value.", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(2, false));

        txtName = UITools.createTextWithLabel(container, "Name:", "");
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtValue = UITools.createTextWithLabel(container, "Value:", "");
        txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        lblError = UITools.createLabel(container, "Any error must be displayed here");
        // TODO red color for label.
        GridData data = new GridData();
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        lblError.setLayoutData(data);

        return area;
    }

    @Override
    protected void okPressed() {
        if (!txtName.getText().trim().equals("")) {
            this.name = txtName.getText();
            this.value = txtValue.getText();
            super.okPressed();
        } else {
            lblError.setText("An empty property value is not allowed.");
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Add JMS property");
    }

    public String getPropertyName() {
        return name;
    }

    public String getPropertyValue() {
        return value;
    }

}
