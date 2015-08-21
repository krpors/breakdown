package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Tab with test case information.
 */
public class TestCaseTab extends AbstractTab<TestCase> implements FocusListener, ResultListener {

    private Text txtName;
    private Text txtDescription;

    private Table table;

    public TestCaseTab(BreakdownUI ui, CTabFolder parent, TestCase p) {
        super(ui, parent, p);

        p.addResultListener(this);
    }

    /**
     * Creates the project tab contents.
     */
    @Override
    protected Composite createContents(CTabFolder parent) {
        final TestCase TestCase = getEntity();

        Composite compositeMain = new Composite(parent, SWT.NONE);
        FillLayout l = new FillLayout();
        l.marginWidth = 5;
        l.marginHeight = 5;
        compositeMain.setLayout(l);

        Composite compositeProperties = new Composite(compositeMain, SWT.NONE);

        GridLayout gl = new GridLayout(2, false);
        compositeProperties.setLayout(gl);

        // Create labels, textfields and such.
        txtName = UITools.createTextWithLabel(compositeProperties, "Name:", TestCase.getName());
        txtDescription = UITools.createTextWithLabel(compositeProperties, "Description:", TestCase.getDescription());

        txtName.addFocusListener(this);
        txtDescription.addFocusListener(this);

        Button b = new Button(compositeProperties, SWT.PUSH);
        b.setText("Run test case");
        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    table.removeAll();
                    getEntity().execute();
                } catch (BreakdownException e1) {
                    e1.printStackTrace();
                }
            }
        });

        getTabItem().addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                getEntity().removeResultListener(TestCaseTab.this);
            }
        });

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.horizontalSpan = 2;
        b.setLayoutData(data);

        table = new Table(compositeProperties, SWT.MULTI | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        for (String title : new String[]{"Name", "Value"}) {
            TableColumn col = new TableColumn(table, SWT.NONE);
            col.setText(title);
        }

        table.getColumn(0).setWidth(250);
        table.getColumn(1).setWidth(250);

        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        table.setLayoutData(data);

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.Icon.TestCase);
    }

    @Override
    public void saveChanges() {
        getEntity().setName(txtName.getText());
        getEntity().setDescription(txtDescription.getText());
        getBreakdownUI().getProjectTree().refresh();
    }

    @Override
    public void focusGained(FocusEvent e) {
        // no-op
    }

    @Override
    public void focusLost(FocusEvent e) {
        saveChanges();
    }

    @Override
    public void resultAcquired(Result result) {
        System.out.println("Test step " + result.getTestStep() + " resulted in " + result);
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, result.getTestStep().getName());
        item.setText(1, result.getMessage());
        if (result.isSuccess()) {
            item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
        } else {
            item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }
    }
}
