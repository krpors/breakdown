package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.Map;

public class TestStepJMSRequestReplyTab extends AbstractTab<JMSRequestReply> {

    private Text txtParent;
    private Text txtName;
    private Text txtDescription;
    private Text txtPayload;
    private Table tblCustomProperties;
    private Button btnApplyChanges;

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

        Group groupConfiguration = new Group(compositeMain, SWT.NONE);
        groupConfiguration.setText("JMS Request/Reply properties");
        FillLayout fl = new FillLayout();
        fl.marginWidth = 5;
        fl.marginHeight = 5;
        groupConfiguration.setLayout(fl);

        CTabFolder folder = new CTabFolder(groupConfiguration, SWT.BORDER);

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
            it.setText(new String[]{ k, props.get(k)});
        }

        CTabItem item2 = new CTabItem(folder, SWT.NONE);
        item2.setText("JMS custom properties");
        item2.setControl(tblCustomProperties);

        folder.setSelection(item);

        btnApplyChanges = new Button(compositeMain, SWT.PUSH);
        btnApplyChanges.setText("Apply changes");

        FormData data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(0);
        groupProperties.setLayoutData(data);

        data = new FormData();
        data.left = new FormAttachment(0);
        data.right = new FormAttachment(100);
        data.top = new FormAttachment(groupProperties);
        data.bottom = new FormAttachment(100, -30);
        groupConfiguration.setLayoutData(data);

        data = new FormData();
//        data.right = new FormAttachment(50);
        data.left = new FormAttachment(0, 10);
        data.top = new FormAttachment(groupConfiguration, 3);
        data.bottom = new FormAttachment(100, -3);
        btnApplyChanges.setLayoutData(data);

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.TestStep);
    }

    @Override
    public void assertionFailed(Project p, TestSuite testSuite, TestCase testCase, TestStep testStep) {

    }
}
