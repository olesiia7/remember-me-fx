import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static utils.ImageUtils.getPictureFromClipboard;

public class SaveImagesTests {

    @Test
    public void saveImageTest() throws IOException, UnsupportedFlavorException {
        BufferedImage pictureFromClipboard1 = getPictureFromClipboard();
        Image image = SwingFXUtils.toFXImage(pictureFromClipboard1, null);

        ImageView imageView = new ImageView();
        imageView.setImage(image);

        String uuid = UUID.randomUUID().toString();
        File filePath = new File("src/test/resources");
        File file = new File(filePath + "/" + uuid + "_" + 1 + ".png");
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            BufferedImage im = fromFXImage(image, null);
            ImageIO.write(im, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}