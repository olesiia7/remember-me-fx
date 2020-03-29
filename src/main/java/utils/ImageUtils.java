package utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Класс-помощник в работе с изображениями
 */
public class ImageUtils {

    /**
     * @return изображение из буфера обмена
     */
    public static BufferedImage getPictureFromClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
    }
}
