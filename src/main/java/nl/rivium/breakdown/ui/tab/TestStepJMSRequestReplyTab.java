package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.assertion.PayloadAssertion;
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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Configuration unit for a JMS request reply step.
 */
public class TestStepJMSRequestReplyTab extends AbstractTab<JMSRequestReply> implements FocusListener {

    /**
     * Logger for this tab.
     */
    private static Logger LOG = LoggerFactory.getLogger(TestStepJMSRequestReplyTab.class);

    private Text txtName;
    private Text txtDescription;
    private Spinner spinTimeout;
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
    private Combo cmbReplyType;

    public TestStepJMSRequestReplyTab(BreakdownUI ui, CTabFolder parent, JMSRequestReply entity) {
        super(ui, parent, entity);
    }

    /**
     * Creates contents.
     *
     * @param parent The parent folder.
     * @return The composite with the main contents.
     */
    @Override
    protected Composite createContents(CTabFolder parent) {
        Composite compositeMain = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        compositeMain.setLayout(layout);

        Composite compositeDefault = createDefaultGroup(compositeMain);
        Composite compositeConfiguration = createCompositeConfiguration(compositeMain);

        compositeDefault.setLayoutData(FormDataBuilder.newBuilder().left(0).right(100).create());
        compositeConfiguration.setLayoutData(FormDataBuilder.newBuilder().left(0).right(100).top(compositeDefault).bottom(100).create());

        registerFocusListeners();

        return compositeMain;
    }

    /**
     * Creates the default group up top (name and description);
     *
     * @param compositeMain The main composite parent.
     * @return The group created.
     */
    private Composite createDefaultGroup(Composite compositeMain) {
        final TestStep step = getEntity();
        final TestCase testCase = step.getParent();
        final TestSuite testSuite = testCase.getParent();

        Composite groupProperties = new Composite(compositeMain, SWT.NONE);

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
    private Composite createCompositeConfiguration(Composite compositeMain) {
        Composite c = new Composite(compositeMain, SWT.NONE);
        c.setLayout(new FillLayout());

        CTabFolder tabFolder = new CTabFolder(c, SWT.BORDER);

        // creation of tab items:
        CTabItem tabItemConnSettings = new CTabItem(tabFolder, SWT.NONE);
        tabItemConnSettings.setText("Connection settings");
        CTabItem tabItemPayload = new CTabItem(tabFolder, SWT.NONE);
        tabItemPayload.setText("Payload data");
        CTabItem tabItemProperties = new CTabItem(tabFolder, SWT.NONE);
        tabItemProperties.setText("JMS Properties");
        CTabItem tabItemAssertions = new CTabItem(tabFolder, SWT.NONE);
        tabItemAssertions.setText("Assertions");

        Composite conf = createCompositeGenericProperties(tabFolder);
        Composite payload = createCompositePayload(tabFolder);
        Composite zz = createCompositeProperties(tabFolder);
        Composite assertions = createAssertionComposite(tabFolder);

        tabItemConnSettings.setControl(conf);
        tabItemPayload.setControl(payload);
        tabItemProperties.setControl(zz);
        tabItemAssertions.setControl(assertions);

        tabFolder.setSelection(tabItemConnSettings);

        return c;
    }

    /**
     * Creates the composite containing the JMS generic properties, like timeout, input and output destinations.
     *
     * @param compositeParent The composite parent.
     * @return The composite containing properties.
     */
    public Composite createCompositeGenericProperties(Composite compositeParent) {
        JMSRequestReply jrr = getEntity();
        Project parent = jrr.getParent().getParent().getParent();

        Composite c = new Composite(compositeParent, SWT.NONE);
        c.setLayout(new GridLayout(2, false));

        // Combobox to choose from existing JMS connections.
        UITools.createLabel(c, "JMS connection:");
        cmbJMSConnection = new Combo(c, SWT.READ_ONLY);
        for (int i = 0; i < parent.getJmsConnections().size(); i++) {
            JMSConnection conn = parent.getJmsConnections().get(i);
            cmbJMSConnection.add(conn.getName());
            if (jrr.getJmsConnectionName() != null && jrr.getJmsConnectionName().equals(conn.getName())) {
                cmbJMSConnection.select(i);
            }
        }

        UITools.createLabel(c, "Request timeout:");
        spinTimeout = new Spinner(c, SWT.BORDER);
        spinTimeout.setMinimum(100);
        spinTimeout.setMaximum(Integer.MAX_VALUE);
        spinTimeout.setSelection((int) jrr.getTimeout());

        // Request destination, with the combo right next to it
        UITools.createLabel(c, "Request destination:");
        Composite compositeRequestDest = new Composite(c, SWT.NONE);
        compositeRequestDest.setLayout(new GridLayout(2, false));
        txtRequestDestination = new Text(compositeRequestDest, SWT.BORDER);
        txtRequestDestination.setText(jrr.getRequestDestination().getName());
        cmbRequestType = new Combo(compositeRequestDest, SWT.READ_ONLY);
        txtRequestDestination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeRequestDest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Reply destination, with the combo right next to it
        UITools.createLabel(c, "Reply destination:");
        Composite compositeReplyDest = new Composite(c, SWT.NONE);
        compositeReplyDest.setLayout(new GridLayout(2, false));
        txtReplyDestination = new Text(compositeReplyDest, SWT.BORDER);
        txtReplyDestination.setText(jrr.getReplyDestination().getName());
        cmbReplyType = new Combo(compositeReplyDest, SWT.READ_ONLY);
        txtReplyDestination.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        compositeReplyDest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Initialize values for the comboboxes
        for (int i = 0; i < DestinationType.values().length; i++) {
            DestinationType d = DestinationType.values()[i];
            cmbRequestType.add(d.name());
            cmbReplyType.add(d.name());

            if (jrr.getRequestDestination().getType() == d) {
                cmbRequestType.select(i);
            }

            if (jrr.getReplyDestination().getType() == d) {
                cmbReplyType.select(i);
            }
        }

        return c;
    }

    private Composite createCompositePayload(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new FillLayout());
        txtPayload = new Text(c, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        txtPayload.setText(getEntity().getInput().getPayload());
        return c;
    }

    /**
     * Creates the property tab, containing the property table and some buttons.
     *
     * @param parent The parent tab.
     * @return The composite containing the tab and such.
     */
    private Composite createCompositeProperties(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new FormLayout());

        Button btnAdd = new Button(c, SWT.PUSH);
        btnAdd.setImage(ImageCache.getImage(ImageCache.Icon.Create));
        btnAdd.setToolTipText("Add a JMS property");
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
        btnRemove.setImage(ImageCache.getImage(ImageCache.Icon.Delete));
        btnRemove.setToolTipText("Remove selected JMS properties");
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
        btnEdit.setImage(ImageCache.getImage(ImageCache.Icon.Preferences));
        btnEdit.setToolTipText("Edit selected JMS property");
        // TODO: editing of an existing prop using button click.

        tblCustomProperties = new Table(c, SWT.MULTI | SWT.FULL_SELECTION);
        tblCustomProperties.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                TableItem item = tblCustomProperties.getItem(new Point(e.x, e.y));
                // TODO: double click to edit
                LOG.debug("TODO: start editing a JMS property here.");
            }
        });
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

    private Composite createAssertionComposite(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new FillLayout());

        Table lol = new Table(c, SWT.MULTI | SWT.FULL_SELECTION);
        lol.setLinesVisible(true);
        lol.setHeaderVisible(true);
        for (String title : new String[]{"Assertion name", "Type"}) {
            TableColumn col = new TableColumn(lol, SWT.NONE);
            col.setText(title);
        }
        lol.getColumn(0).setWidth(250);
        lol.getColumn(1).setWidth(250);

        for (PayloadAssertion pass : getEntity().getPayloadAssertions()) {
            TableItem tc = new TableItem(lol, SWT.NONE);
            tc.setText(0, pass.getClass().getName());
            tc.setText(1, "String payload assertion");
        }

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
        cmbReplyType.addFocusListener(this);
        spinTimeout.addFocusListener(this);
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.Icon.TestStep);
    }

    @Override
    public void saveChanges() {
        JMSRequestReply rr = getEntity();
        rr.setName(txtName.getText());
        rr.setDescription(txtDescription.getText());
        rr.getRequestDestination().setName(txtRequestDestination.getText());
        rr.getRequestDestination().setType(DestinationType.valueOf(cmbRequestType.getItem(cmbRequestType.getSelectionIndex())));
        rr.getReplyDestination().setName(txtReplyDestination.getText());
        rr.getReplyDestination().setType(DestinationType.valueOf(cmbReplyType.getItem(cmbReplyType.getSelectionIndex())));
        rr.getInput().setPayload(txtPayload.getText());
        rr.setTimeout((long) spinTimeout.getSelection());
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
