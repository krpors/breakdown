package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.Project;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ProjectTab extends AbstractTab<Project> {

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
        FillLayout l = new FillLayout();
        l.marginWidth = 5;
        l.marginHeight = 5;
        compositeMain.setLayout(l);

        Group compositeProperties = new Group(compositeMain, SWT.NONE);
        compositeProperties.setText("Project");

        GridLayout gl = new GridLayout(2, false);
        compositeProperties.setLayout(gl);

        // Create labels, textfields and such.
        txtFilename = UITools.createTextWithLabel(compositeProperties, "Filename:", project.getFilename());
        txtProjectName = UITools.createTextWithLabel(compositeProperties, "Project name:", project.getName());
        txtAuthorName = UITools.createTextWithLabel(compositeProperties, "Author name:", project.getAuthor());
        txtDescription = UITools.createTextWithLabel(compositeProperties, "Description:", project.getDescription());
        txtFilename.setEditable(false);

        return compositeMain;
    }

    @Override
    protected Image getImage() {
        return ImageCache.getImage(ImageCache.UIImage.Project);
    }
}
