package nl.rivium.breakdown.ui;

import nl.rivium.breakdown.core.Project;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;
import nl.rivium.breakdown.core.TestSuite;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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

    private static Map<Class, Icon> imageMap = new HashMap<>();

    public ImageCache() {
        ImageCache.display = Display.getDefault();

        imageRegistry = new ImageRegistry(display);

        registerImages();
        registerClassImageMap();
    }

    private static void registerClassImageMap() {
        imageMap.put(Project.class, Icon.Project);
        imageMap.put(TestCase.class, Icon.TestCase);
        imageMap.put(TestSuite.class, Icon.TestSuite);
        imageMap.put(TestStep.class, Icon.TestStep);
        imageMap.put(JMSConnection.class, Icon.JMSConnection);
        imageMap.put(JMSRequestReply.class, Icon.Delete);
    }

    /**
     * Registers all the images which are currently (manually) configured in the
     * UIImage enumeration, and output a log line when it has been registered.
     */
    private static void registerImages() {
        for (Icon img : Icon.values()) {
            imageRegistry.put(img.name(), new Image(display, ImageCache.class.getResourceAsStream(img.path)));
            LOG.debug("Registered image '{}' from path '{}'", img.name(), img.path);
        }
    }

    /**
     * Gets an image from the image registry.
     *
     * @param icon The image from the enum to get.
     * @return The Image.
     */
    public static Image getImage(Icon icon) {
        return imageRegistry.get(icon.name());
    }

    /**
     * Gets an image descriptor from an image. Descriptors are frequently used in JFace actions.
     *
     * @param icon The image from the enum.
     * @return The ImageDescriptor.
     */
    public static ImageDescriptor getDescriptor(Icon icon) {
        return imageRegistry.getDescriptor(icon.name());
    }

    public static Image getImage(Class clazz) {
        Icon i = imageMap.get(clazz);
        if (i == null) {
            return null;
        }

        return getImage(i);
    }

    /**
     * Enumeration with all image icons.
     */
    public enum Icon {

        Folder("/images/small/Folder.png"),
        Project("/images/small/Home.png"),
        TestCase("/images/small/3d bar chart.png"),
        TestSuite("/images/small/Yes.png"),
        TestStep("/images/small/No.png"),
        JMSConnection("/images/small/Network connection.png"),
        Delete("/images/small/Delete.png"),
        Create("/images/small/Create.png"),
        Save("/images/small/Save.png"),
        Preferences("/images/small/Application.png"),
        Exit("/images/small/Exit.png"),
        Play("/images/small/Play.png");

        public final String path;

        private Icon(String path) {
            this.path = path;
        }
    }
}
