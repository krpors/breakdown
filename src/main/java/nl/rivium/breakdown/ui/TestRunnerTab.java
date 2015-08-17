package nl.rivium.breakdown.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * Contains the widgets for displaying a test run.
 */
public class TestRunnerTab {

    private Composite compositeMain;

    public TestRunnerTab(Composite parent) {
        createContents(parent);
    }

    private void createContents(Composite parent) {
        compositeMain = new Composite(parent, SWT.NONE);
        FormLayout fl = new FormLayout();
        fl.marginHeight = 5;
        fl.marginWidth = 5;
        compositeMain.setLayout(fl);

        ProgressBar bar = new ProgressBar(compositeMain, SWT.HORIZONTAL);
        bar.setMinimum(0);
        bar.setMaximum(100);
        bar.setSelection(100);

        Color z = new Color(parent.getShell().getDisplay(), 255, 0, 0);

        bar.setLayoutData(FormDataBuilder.newBuilder().top(0).left(0).right(100).create());
    }

    /**
     * Returns the composite the children are added to.
     *
     * @return The main composite of the TestRunnerComponent.
     */
    public Composite getComposite() {
        return compositeMain;
    }
}
