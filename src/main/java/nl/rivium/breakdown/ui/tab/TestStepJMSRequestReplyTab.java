package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.jms.DestinationType;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.FormDataBuilder;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import nl.rivium.breakdown.ui.dlg.HeaderPropertyDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.Map;

/**
 * Configuration unit for a JMS request reply step.
 */
public class TestStepJMSRequestReplyTab extends AbstractTab<JMSRequestReply> implements FocusListener {

    private Text txtName;
    private Text txtDescription;
    private Combo cmbJMSConnection;
    private Text txtRequestDestination;
    private Text txtReplyDestination;
    private Text txtPayload;
    private Table tblCustomProperties;
    /**
     * Combobox with the request type.
     */
    private Combo cmbRequestType;
    /**
     * Combobox with the response type.
     */
    private Combo cmbResponseType;

    public TestStepJMSRequestReplyTab(BreakdownUI ui, CTabFolder parent, JMSRequestReply entity) {
        super(ui, parent, entity);
    }

    /**
     * Creates contents.
     *
     * @param parent The parent folder.
     * @return
     */
    @Override
    protected Composite createContents(CTabFolder parent) {
        final TestStep step = getEntity();
        final TestCase testCase = step.getParent();
        final TestSuite testSuite = testCase.getParent();

        Composite compositeMain = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        compositeMain.setLayout(layout);

        Group groupProperties = createDefaultGroup(compositeMain);
        Group groupConfiguration = createConfigurationGroup(compositeMain);

        FormData data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(0);
        groupProperties.setLayoutData(data);

        data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(groupProperties, 5);
        data.bottom = new FormAttachment(100);
        groupConfiguration.setLayoutData(data);

        registerFocusListeners();

        return compositeMain;
    }

    /**
     * Creates the default group up top (name and description);
     *
     * @param compositeMain The main composite parent.
     * @return The group created.
     */
    private Group createDefaultGroup(Composite compositeMain) {
        final TestStep step = getEntity();
        final TestCase testCase = step.getParent();
        final TestSuite testSuite = testCase.getParent();

        Group groupProperties = new Group(compositeMain, SWT.NONE);
        groupProperties.setText("Test step");

        GridLayout gl = new GridLayout(2, false);
        groupProperties.setLayout(gl);

        // Create labels, textfields and such.

        String path = String.format("%s -> %s ", testSuite.getName(), testCase.getName());
        Text txtParent = UITools.createTextWithLabel(groupProperties, "Parent test case: ", path);
        txtParent.setEditable(false);
        txtName = UITools.createTextWithLabel(groupProperties, "Name:", step.getName());
        txtDescription = UITools.createTextWithLabel(groupProperties, "Description:", step.getDescription());

        return groupProperties;
    }

    /**
     * Creates the configuration group, which contains the main configuration parts of a JMS Request Reply test step.
     *
     * @param compositeMain The composite main parent.
     * @return The Group with JMS request reply properties.
     */
    private Group createConfigurationGroup(Composite compositeMain) {
        JMSRequestReply jrr = getEntity();
        Project parent = jrr.getParent().getParent().getParent();

        Group group = new Group(compositeMain, SWT.NONE);
        group.setText("JMS Request/Reply properties");
        group.setLayout(new FormLayout());

        // Top part, with the general properties of the JMSRequestReply.
        Composite topStuff = new Composite(group, SWT.NONE);
        topStuff.setLayout(new GridLayout(2, false));

        // Combobox to choose from existing JMS connections.
        UITools.createLabel(topStuff, "JMS connection:");
        cmbJMSConnection = new Combo(topStuff, SWT.READ_ONLY);
        for (int i = 0; i < parent.getJmsConnections().size(); i++) {
            JMSConnection conn = parent.getJmsConnections().get(i);
            cmbJMSConnection.add(conn.getName());
            if (jrr.getJmsConnectionName() != null && jrr.getJmsConnectionName().equals(conn.getName())) {
                cmbJMSConnection.select(i);
            }
        }

        // Request destination, with the combo right next to it
        UITools.createLabel(topStuff, "Request destination:");
        Composite compositeRequestDest = new Composite(topStuff, SWT.NONE);
        compositeRequestDest.setLayout(new GridLayout(2, false));
        txtRequestDestination = new Text(compositeRequestDest, SWT.BORDER);
        txtRequestDestination.setText(jrr.getRequestDestination().getName());
        cmbRequestType = new Combo(compositeRequestDest, SWT.READ_ONLY);
        txtRequestDestination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeRequestDest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Reply destination, with the combo right next to it
        UITools.createLabel(topStuff, "Reply destination:");
        Composite compositeReplyDest = new Composite(topStuff, SWT.NONE);
        compositeReplyDest.setLayout(new GridLayout(2, false));
        txtReplyDestination = new Text(compositeReplyDest, SWT.BORDER);
        txtReplyDestination.setText(jrr.getReplyDestination().getName());
        cmbResponseType = new Combo(compositeReplyDest, SWT.READ_ONLY);
        txtReplyDestination.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        compositeReplyDest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Initialize values for the comboboxes
        for (int i = 0; i < DestinationType.values().length; i++) {
            DestinationType d = DestinationType.values()[i];
            cmbRequestType.add(d.name());
            cmbResponseType.add(d.name());

            if (jrr.getRequestDestination().getType() == d) {
                cmbRequestType.select(i);
            }

            if (jrr.getReplyDestination().getType() == d) {
                cmbResponseType.select(i);
            }
        }

        // Tabfolder with payload and settable properties
        CTabFolder folder = new CTabFolder(group, SWT.BORDER);

        txtPayload = new Text(folder, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        txtPayload.setText(getEntity().getInput().getPayload());

        CTabItem tabItemPayload = new CTabItem(folder, SWT.NONE);
        tabItemPayload.setText("Payload");
        tabItemPayload.setControl(txtPayload);

        Composite compositeProperties = createPropertyTab(folder);
        CTabItem tabItemProperties = new CTabItem(folder, SWT.NONE);
        tabItemProperties.setText("JMS custom properties");
        tabItemProperties.setControl(compositeProperties);

        folder.setSelection(tabItemPayload);

        FormData data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(0);
        topStuff.setLayoutData(data);

        data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(topStuff);
        data.bottom = new FormAttachment(100);
        folder.setLayoutData(data);

        return group;
    }

    /**
     * Creates the property tab, containing the property table and some buttons.
     *
     * @param parent The parent tab.
     * @return The composite containing the tab and such.
     */
    private Composite createPropertyTab(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new FormLayout());

        Button btnAdd = new Button(c, SWT.PUSH);
        btnAdd.setText("Add");
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                HeaderPropertyDialog dlg = new HeaderPropertyDialog(getBreakdownUI().getShell());
                // if cancel is pressed, just return.
                if (dlg.open() != TitleAreaDialog.OK) {
                    return;
                }

                // try to update properties:
                Map<String, String> props = getEntity().getInput().getProperties();
                String found = props.get(dlg.getPropertyName());
                if (found == null) {
                    // does not exist yet, so we can add it:
                    TableItem item = new TableItem(tblCustomProperties, SWT.NONE);
                    item.setText(0, dlg.getPropertyName());
                    item.setText(1, dlg.getPropertyValue());

                    // force model update
                    focusLost(null);
                } else {
                    String what = String.format("The given property '%s' already exists (value is '%s')", dlg.getPropertyName(), found);
                    MessageDialog.openError(getBreakdownUI().getShell(), "Property already exists", what);
                }
            }
        });

        Button btnRemove = new Button(c, SWT.PUSH);
        btnRemove.setText("Remove");
        // remove selected rows from the table.
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (TableItem item : tblCustomProperties.getSelection()) {
                    String key = item.getText(0);
                    getEntity().getInput().getProperties().remove(key);
                    item.dispose();
                }

                // Force an update to the model.
                focusLost(null);
            }
        });

        Button btnEdit = new Button(c, SWT.PUSH);
        btnEdit.setText("Edit");

        tblCustomProperties = new Table(c, SWT.MULTI | SWT.FULL_SELECTION);
        tblCustomProperties.setLinesVisible(true);
        tblCustomProperties.setHeaderVisible(true);
        for (String title : new String[]{"Name", "Value"}) {
            TableColumn col = new TableColumn(tblCustomProperties, SWT.NONE);
            col.setText(title);
        }

        tblCustomProperties.getColumn(0).setWidth(250);
        tblCustomProperties.getColumn(1).setWidth(250);

        Map<String, String> props = getEntity().getInput().getProperties();
        for (String k : props.keySet()) {
            TableItem it = new TableItem(tblCustomProperties, SWT.NONE);
            it.setText(new String[]{k, props.get(k)});
        }

        btnAdd.setLayoutData(FormDataBuilder.newBuilder().left(0).top(0).create());
        btnRemove.setLayoutData(FormDataBuilder.newBuilder().left(btnAdd).top(0).create());
        btnEdit.setLayoutData(FormDataBuilder.newBuilder().left(btnRemove).top(0).create());
        tblCustomProperties.setLayoutData(FormDataBuilder.newBuilder().left(0).top(btnAdd).right(100).bottom(100).create());

        return c;
    }

    /**
     * Register focus listeners on objects which matter.
     */
    private void registerFocusListeners() {
        txtName.addFocusListener(this);
        txtPayload.addFocusListener(this);
        txtDescription.addFocusListener(this);
        txtReplyDestination.addFocusListener(this);
        txtRequestDestination.addFocusListener(this);
        cmbJMSConnection.addFocusListener(this);
        cmbRequestType.addFocusListener(this);
        cmbResponseType.addFocusListener(this);
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.TestStep);
    }

    @Override
    public void saveChanges() {
        JMSRequestReply rr = getEntity();
        rr.setName(txtName.getText());
        rr.setDescription(txtDescription.getText());
        rr.getInput().setPayload(txtPayload.getText());
        if (cmbJMSConnection.getSelectionIndex() >= 0) {
            rr.setJmsConnectionName(cmbJMSConnection.getItem(cmbJMSConnection.getSelectionIndex()));
        }
        for (TableItem props : tblCustomProperties.getItems()) {
            getEntity().getInput().getProperties().put(props.getText(0), props.getText(1));
        }

        getBreakdownUI().getProjectTree().refresh();
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    /**
     * Whenever focus is lost on components, update the object values using the text fields and whatnot.
     * TODO: is this a workable solution for User Experience?
     *
     * @param e
     */
    @Override
    public void focusLost(FocusEvent e) {
        saveChanges();
    }

}
