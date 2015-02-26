package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class TestSuiteTab extends AbstractTab<TestSuite> implements FocusListener {

    private Text txtName;
    private Text txtDescription;

    public TestSuiteTab(BreakdownUI ui, CTabFolder parent, TestSuite p) {
        super(ui, parent, p);
    }

    /**
     * Creates the project tab contents.
     */
    @Override
    protected Composite createContents(CTabFolder parent) {
        final TestSuite testSuite = getEntity();

        Composite compositeMain = new Composite(parent, SWT.NONE);
        FillLayout l = new FillLayout();
        l.marginWidth = 5;
        l.marginHeight = 5;
        compositeMain.setLayout(l);

        Composite compositeProperties = new Composite(compositeMain, SWT.NONE);

        GridLayout gl = new GridLayout(2, false);
        compositeProperties.setLayout(gl);

        // Create labels, textfields and such.
        txtName = UITools.createTextWithLabel(compositeProperties, "Name:", testSuite.getName());
        txtDescription = UITools.createTextWithLabel(compositeProperties, "Description:", testSuite.getDescription());

        txtName.addFocusListener(this);
        txtDescription.addFocusListener(this);

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.TestSuite);
    }

    @Override
    public void saveChanges() {
        getTabItem().setText(txtName.getText());

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
}
