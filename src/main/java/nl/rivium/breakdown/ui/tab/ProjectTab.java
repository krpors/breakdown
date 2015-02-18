package nl.rivium.breakdown.ui.tab;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.ui.BreakdownUI;
import nl.rivium.breakdown.ui.FormDataBuilder;
import nl.rivium.breakdown.ui.ImageCache;
import nl.rivium.breakdown.ui.UITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

public class ProjectTab extends AbstractTab<Project> implements FocusListener {

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

        txtProjectName.addFocusListener(this);
        txtAuthorName.addFocusListener(this);
        txtDescription.addFocusListener(this);

        groupProperties.setLayoutData(FormDataBuilder.newBuilder().left(0).right(100).top(0).bottom(100).create());

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.Project);
    }

    /**
     * Updates widgets based on the model.
     */
    @Override
    protected void updateWidgets() {
        Project p = getEntity();
        txtFilename.setText(p.getFilename());
    }

    @Override
    public void update(Observable o, Object arg) {
        updateWidgets();
    }

    @Override
    public void focusGained(FocusEvent e) {
        // no-op
    }

    @Override
    public void focusLost(FocusEvent e) {
        Project p = getEntity();
        p.setAuthor(txtAuthorName.getText());
        p.setDescription(txtDescription.getText());
        p.setName(txtProjectName.getText());
        getBreakdownUI().getProjectTree().refresh();
    }
}

