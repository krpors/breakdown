package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.Project;

public class Root {
    private Project project;

    public Root(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
