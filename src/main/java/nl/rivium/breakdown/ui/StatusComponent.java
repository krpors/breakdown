package nl.rivium.breakdown.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Component on the bottom of the screen, with several tab items to display statusses and whatnot.
 */
public class StatusComponent {

    private CTabFolder folder;

    private TestRunnerTab testRunnerTab;

    public StatusComponent(Composite parent) {
        createContents(parent);
    }

    /**
     *
     * @param parent
     */
    private void createContents(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        c.setLayout(layout);

        Composite compositeTabFolder = createTabFolder(c);
        FormData fd = FormDataBuilder.newBuilder().bottom(100).left(0).right(100).top(0 ).create();
        compositeTabFolder.setLayoutData(fd);
    }

    /**
     * Creates tab folder.
     *
     * @param parent The parent composite.
     */
    private Composite createTabFolder(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new FillLayout());

        folder = new CTabFolder(c, SWT.BORDER);

        testRunnerTab = new TestRunnerTab(folder);

        // Test runner always selected by default.
        folder.setSelection(0);

        return c;
    }

    public TestRunnerTab getTestRunnerTab() {
        return this.testRunnerTab;
    }
}
