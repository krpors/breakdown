package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.FormDataBuilder;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Tab to configure a JMS connection.
 */
public class JMSConnectionTab extends AbstractTab<JMSConnection> implements FocusListener {

    private Text txtName;
    private Text txtDescription;
    private Text txtContextFactory;
    private Text txtConnectionUrl;
    private Text txtUsername;
    private Text txtPassword;
    private Text txtQueueConnectionFactory;
    private Text txtTopicConnectionFactory;

    private Button btnPasswordless;

    /**
     * Button to test the given connection with.
     */
    private Button btnTestConnection;
    /**
     * The logz0r.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JMSConnectionTab.class);


    public JMSConnectionTab(BreakdownUI ui, CTabFolder parent, JMSConnection c) {
        super(ui, parent, c);
    }

    /**
     * Creates the project tab contents.
     */
    @Override
    protected Composite createContents(CTabFolder parent) {
        final JMSConnection connection = getEntity();

        Composite compositeMain = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        compositeMain.setLayout(layout);

        Composite groupTop = new Composite(compositeMain, SWT.NONE);
        GridLayout gl = new GridLayout(2, false);
        groupTop.setLayout(gl);

        txtName = UITools.createTextWithLabel(groupTop, "Name:", connection.getName());
        txtDescription = UITools.createTextWithLabel(groupTop, "Description:", connection.getDescription());

        Group groupProperties = createConnectionGroup(compositeMain);

        // layout the main components:
        FormData data = FormDataBuilder.newBuilder().left(0).right(100).top(0).create();
        groupTop.setLayoutData(data);

        groupProperties.setLayoutData(FormDataBuilder.newBuilder().left(0).right(100).bottom(100).top(groupTop).create());

        registerFocusListeners();

        return compositeMain;
    }

    /**
     * Creates the UI group with the connection parameters and such.
     *
     * @param parent The parent composite.
     * @return The group.
     */
    private Group createConnectionGroup(Composite parent) {
        final JMSConnection entity = getEntity();

        Group group = new Group(parent, SWT.NONE);
        group.setText("Properties");
        group.setLayout(new GridLayout(2, false));

        txtContextFactory = UITools.createTextWithLabel(group, "Context factory:", entity.getContextFactory());
        txtConnectionUrl = UITools.createTextWithLabel(group, "Connection URL:", entity.getConnectionUrl());
        txtUsername = UITools.createTextWithLabel(group, "Username:", entity.getUsername());
        txtPassword = UITools.createTextWithLabel(group, "Password:", entity.getPassword() == null ? "" : entity.getPassword());
        txtPassword.setEchoChar('●');
        UITools.createLabel(group, ""); // filler
        btnPasswordless = new Button(group, SWT.CHECK);
        btnPasswordless.setText("Passwordless login");
        btnPasswordless.setToolTipText("Prevent sending an explicit password");
        btnPasswordless.setSelection(entity.getPassword() == null);
        txtPassword.setEnabled(entity.getPassword() != null);
        txtQueueConnectionFactory = UITools.createTextWithLabel(group, "Queue connection factory:", entity.getQueueConnectionFactory());
        txtTopicConnectionFactory = UITools.createTextWithLabel(group, "Topic connection factory:", entity.getTopicConnectionFactory());
        UITools.createLabel(group, "");
        btnTestConnection = new Button(group, SWT.PUSH);
        btnTestConnection.setText("Test connection settings");

        btnPasswordless.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtPassword.setEnabled(!btnPasswordless.getSelection());
                txtPassword.setText("");
                entity.setPassword(null);
            }
        });

        btnTestConnection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String txtOld = btnTestConnection.getText();
                btnTestConnection.setText("Please wait ...");
                try {
                    Connection c = entity.createQueueConnection();
                    MessageDialog.openInformation(getBreakdownUI().getShell(), "OK", "Creating a queue connection OK.");
                    c.close();
                } catch (NamingException | JMSException e1) {
                    LOG.warn("Cannot create a queue connection", e1);
                    UITools.showException(getBreakdownUI().getShell(), "Oops...", "Could not create a queue connection.", e1);
                }

                try {
                    Connection c = entity.createTopicConnection();
                    MessageDialog.openInformation(getBreakdownUI().getShell(), "OK", "Creating a topic connection OK.");
                    c.close();
                } catch (NamingException | JMSException e1) {
                    LOG.warn("Cannot create a topic connection", e1);
                    UITools.showException(getBreakdownUI().getShell(), "Oops...", "Could not create a topic connection.", e1);
                }

                btnTestConnection.setText(txtOld);
            }
        });

        return group;
    }

    /**
     * Registers focus listeners.
     */
    private void registerFocusListeners() {
        txtName.addFocusListener(this);
        txtDescription.addFocusListener(this);
        txtContextFactory.addFocusListener(this);
        txtConnectionUrl.addFocusListener(this);
        txtUsername.addFocusListener(this);
        txtPassword.addFocusListener(this);
        txtQueueConnectionFactory.addFocusListener(this);
        txtTopicConnectionFactory.addFocusListener(this);
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.Icon.JMSConnection);
    }

    @Override
    public void saveChanges() {
        JMSConnection c = getEntity();
        c.setName(txtName.getText());
        c.setDescription(txtDescription.getText());
        c.setConnectionUrl(txtConnectionUrl.getText());
        c.setContextFactory(txtContextFactory.getText());
        c.setUsername(txtUsername.getText());
        c.setPassword(txtPassword.getText());
        c.setQueueConnectionFactory(txtQueueConnectionFactory.getText());
        c.setTopicConnectionFactory(txtTopicConnectionFactory.getText());
        getBreakdownUI().getProjectTree().refresh();

        // TODO: refactor references automatically to jms connections.
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    /**
     * Updates internal values when focus is lost on elements.
     *
     * @param e
     */
    @Override
    public void focusLost(FocusEvent e) {
        saveChanges();
    }

}
