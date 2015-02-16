package nl.rivium.breakdown.ui;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

public final class FormDataBuilder {

    private FormData data;

    public static FormDataBuilder newBuilder() {
        FormDataBuilder builder = new FormDataBuilder();
        builder.data = new FormData();
        return builder;
    }

    public FormDataBuilder left(int numerator) {
        data.left = new FormAttachment(numerator);
        return this;
    }

    public FormDataBuilder left(Control control) {
        data.left = new FormAttachment(control);
        return this;
    }

    public FormDataBuilder left(Control control, int offset) {
        data.left = new FormAttachment(control, offset);
        return this;
    }

    public FormDataBuilder right(int numerator) {
        data.right = new FormAttachment(numerator);
        return this;
    }

    public FormDataBuilder right(Control control, int offset) {
        data.right = new FormAttachment(control, offset);
        return this;
    }

    public FormDataBuilder top(int numerator) {
        data.top = new FormAttachment(numerator);
        return this;
    }

    public FormDataBuilder top(Control control, int offset) {
        data.top = new FormAttachment(control, offset);
        return this;
    }

    public FormDataBuilder bottom(int numerator) {
        data.bottom = new FormAttachment(numerator);
        return this;
    }

    public FormDataBuilder bottom(Control control, int offset) {
        data.bottom = new FormAttachment(control, offset);
        return this;
    }

    public FormData create() {
        return data;
    }
}
