package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectTab extends AbstractTab<Project> implements ExecutionListener {

    /**
     * The logz0r.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ProjectTab.class);

    private Text txtProjectName;
    private Text txtAuthorName;
    private Text txtDescription;
    private Text txtFilename;

    public ProjectTab(BreakdownUI ui, CTabFolder parent, Project p) {
        super(ui, parent, p);
    }

    /**
     * Creates the project tab contents.
     */
    @Override
    protected Composite createContents(CTabFolder parent) {
        final Project project = getEntity();

        Composite compositeMain = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        compositeMain.setLayout(layout);

        Group groupProperties = new Group(compositeMain, SWT.NONE);
        groupProperties.setText("Project");

        GridLayout gl = new GridLayout(2, false);
        groupProperties.setLayout(gl);

        // Create labels, textfields and such.
        txtFilename = UITools.createTextWithLabel(groupProperties, "Filename:", project.getFilename());
        txtProjectName = UITools.createTextWithLabel(groupProperties, "Project name:", project.getName());
        txtAuthorName = UITools.createTextWithLabel(groupProperties, "Author name:", project.getAuthor());
        txtDescription = UITools.createTextWithLabel(groupProperties, "Description:", project.getDescription());
        txtFilename.setEditable(false);

        Group groupActions = new Group(compositeMain, SWT.NONE);
        groupActions.setText("Actions");
        groupActions.setLayout(new FillLayout());
        Button button = new Button(groupActions, SWT.PUSH);
        button.setText("RUN THIS DAMN PROJECT");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Project p = getEntity();
                try {
                    p.execute();
                } catch (BreakdownException e1) {
                    MessageBox box = new MessageBox(getBreakdownUI().getShell(), SWT.ICON_ERROR);
                    box.setText("Error!");
                    box.setMessage(e1.getMessage());
                    box.open();
                } catch (AssertionException e1) {
                    MessageBox box = new MessageBox(getBreakdownUI().getShell(), SWT.ICON_WARNING);
                    box.setText("Welp!");
                    box.setText("Assertions failed!");
                    LOG.warn("Something failed {}", e1.getMessage());
                    box.open();
                }
            }
        });

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
        groupActions.setLayoutData(data);

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.Project);
    }

    @Override
    public void assertionFailed(Project p, TestSuite testSuite, TestCase testCase, TestStep testStep) {
        LOG.warn("Assertion failed lol!!!");
        MessageBox box = new MessageBox(getBreakdownUI().getShell(), SWT.ICON_WARNING);
        box.setText("Whoops!");
        box.setMessage("An assertion has failed in " + testStep.getName());
        box.open();
    }
}
