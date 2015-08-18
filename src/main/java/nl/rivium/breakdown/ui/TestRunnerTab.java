package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestSuite;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * The tab, containing the widgets for displaying a test run. This class manages the creation of the CTabItem and
 * the addition of itself to the CTabFolder parent.
 */
public class TestRunnerTab {

    private Composite compositeMain;

    private Project project;

    private List list1;
    private List list2;
    private List list3;

    public TestRunnerTab(CTabFolder parent) {
        createContents(parent);
    }

    /**
     * Creates contents, add the main composite as a tab item to the tab folder parent.
     *
     * @param parent The tab folder parent.
     */
    private void createContents(CTabFolder parent) {
        CTabItem item = new CTabItem(parent, SWT.NONE);
        item.setText("Testrunner");
        item.setToolTipText("Displays the test runner");

        compositeMain = new Composite(parent, SWT.NONE);
        compositeMain.setLayout(new FillLayout());

        SashForm sf = new SashForm(compositeMain, SWT.BORDER | SWT.HORIZONTAL);

        list1 = new List(sf, SWT.SINGLE);
        list1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        list2 = new List(sf, SWT.SINGLE);
        list2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        ListViewer viewer = new ListViewer(sf, SWT.SINGLE);


        item.setControl(compositeMain);
    }

    public void setProject(Project project) {
        System.out.println("p roject is set");
        this.project = project;
    }
}
