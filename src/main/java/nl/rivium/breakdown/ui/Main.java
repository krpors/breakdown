package nl.rivium.breakdown.ui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public class Main extends ApplicationWindow {

    public Main() {
        super(null);
    }

    /**
     * Configures the shell, centers it on screen etc.
     *
     * @param shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        shell.setSize(320, 240);
        shell.setText("Hello.");

        Display display = shell.getDisplay();

        // this centers the shell on the screen:
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /**
     * Creates the main application window's contents.
     *
     * @param parent
     * @return
     */
    @Override
    protected Control createContents(Composite parent) {
        return super.createContents(parent);
    }

    private void run() {
        setBlockOnOpen(true);
        open();
        Display.getCurrent().dispose();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
}
