package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class TestSuiteTab extends AbstractTab<TestSuite> {

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

        Group compositeProperties = new Group(compositeMain, SWT.NONE);
        compositeProperties.setText("Test suite");

        GridLayout gl = new GridLayout(2, false);
        compositeProperties.setLayout(gl);

        // Create labels, textfields and such.
        txtName = UITools.createTextWithLabel(compositeProperties, "Name:", testSuite.getName());
        txtDescription = UITools.createTextWithLabel(compositeProperties, "Description:", testSuite.getDescription());

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.TestSuite);
    }

    @Override
    public void assertionFailed(Project p, TestSuite testSuite, TestCase testCase, TestStep testStep) {

    }
}