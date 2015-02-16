package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.Map;

public class TestStepJMSRequestReplyTab extends AbstractTab<JMSRequestReply> implements FocusListener {

    private Text txtParent;
    private Text txtName;
    private Text txtDescription;

    private Text txtInputDestination;
    private Text txtReplyDestination;
    private Text txtPayload;
    private Table tblCustomProperties;

    public TestStepJMSRequestReplyTab(BreakdownUI ui, CTabFolder parent, JMSRequestReply entity) {
        super(ui, parent, entity);
    }

    @Override
    protected Composite createContents(CTabFolder parent) {
        final TestStep step = getEntity();
        final TestCase testCase = step.getParent();
        final TestSuite testSuite = testCase.getParent();

        Composite compositeMain = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        compositeMain.setLayout(layout);

        Group groupProperties = new Group(compositeMain, SWT.NONE);
        groupProperties.setText("Test step");

        GridLayout gl = new GridLayout(2, false);
        groupProperties.setLayout(gl);

        // Create labels, textfields and such.

        String path = String.format("%s -> %s ", testSuite.getName(), testCase.getName());
        txtParent = UITools.createTextWithLabel(groupProperties, "Parent test case: ", path);
        txtParent.setEditable(false);
        txtName = UITools.createTextWithLabel(groupProperties, "Name:", step.getName());
        txtDescription = UITools.createTextWithLabel(groupProperties, "Description:", step.getDescription());

        Group groupConfiguration = createConfigurationGroup(compositeMain);

        FormData data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(0);
        groupProperties.setLayoutData(data);

        data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(groupProperties);
        data.bottom = new FormAttachment(100);
        groupConfiguration.setLayoutData(data);

        registerFocusListeners();

        return compositeMain;
    }

    private Group createConfigurationGroup(Composite compositeMain) {
        JMSRequestReply jrr = getEntity();

        Group group = new Group(compositeMain, SWT.NONE);
        group.setText("JMS Request/Reply properties");
        group.setLayout(new FormLayout());

        // Top part, with the general properties of the JMSRequestReply.
        Composite topStuff = new Composite(group, SWT.NONE);
        topStuff.setLayout(new GridLayout(2, false));

        txtInputDestination = UITools.createTextWithLabel(topStuff, "Input destination:", jrr.getRequestDestination().getName());
        txtReplyDestination = UITools.createTextWithLabel(topStuff, "Reply destination:", jrr.getReplyDestination().getName());

        CTabFolder folder = new CTabFolder(group, SWT.BORDER);

        txtPayload = new Text(folder, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        txtPayload.setText(getEntity().getInput().getPayload());

        CTabItem item = new CTabItem(folder, SWT.NONE);
        item.setText("Payload");
        item.setControl(txtPayload);

        tblCustomProperties = new Table(folder, SWT.MULTI);
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

        CTabItem item2 = new CTabItem(folder, SWT.NONE);
        item2.setText("JMS custom properties");
        item2.setControl(tblCustomProperties);

        folder.setSelection(item);

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
     * Register focus listeners on objects which matter.
     */
    private void registerFocusListeners() {
        txtName.addFocusListener(this);
        txtPayload.addFocusListener(this);
        txtDescription.addFocusListener(this);
        tblCustomProperties.addFocusListener(this);
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.TestStep);
    }

    @Override
    public void assertionFailed(Project p, TestSuite testSuite, TestCase testCase, TestStep testStep) {

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
        JMSRequestReply rr = getEntity();
        rr.setName(txtName.getText());
        rr.setDescription(txtDescription.getText());
        rr.getInput().setPayload(txtPayload.getText());

        System.out.println("yarp");
        getBreakdownUI().getProjectTree().refresh();
    }
}
