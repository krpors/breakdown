package nl.rivium.breakdown.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Since SWT needs disposing of resources, we don't want to create memory leaks by re-creating SWT Resources all the time.
 * An Image is a Resource, so we register them beforehand so we don't create redundant/double copies.
 */
public final class ImageCache {

    /**
     * Our logger object.
     */
    private static Logger LOG = LoggerFactory.getLogger(ImageCache.class);

    private static Display display;

    private static ImageRegistry imageRegistry;

    public ImageCache() {
        ImageCache.display = Display.getDefault();

        imageRegistry = new ImageRegistry(display);

        registerImages();
    }

    /**
     * Registers all the images which are currently (manually) configured in the
     * UIImage enumeration, and output a log line when it has been registered.
     */
    private static void registerImages() {
        for (UIImage img : UIImage.values()) {
            imageRegistry.put(img.id, new Image(display, ImageCache.class.getResourceAsStream(img.path)));
            LOG.debug("Registered image '{}' from path '{}'", img.id, img.path);
        }
    }

    public static Image getImage(UIImage img) {
        return imageRegistry.get(img.id);
    }

    /**
     * Enumeration with all images.
     */
    public enum UIImage {

        Folder("folder", "/images/small/Folder.png"),
        Project("project", "/images/small/Home.png"),
        TestCase("testcase", "/images/small/3d bar chart.png"),
        TestSuite("testsuite", "/images/small/Yes.png"),
        TestStep("teststep", "/images/small/No.png");

        public final String id;

        public final String path;

        private UIImage(String id, String path) {
            this.id = id;
            this.path = path;
        }
    }
}
