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
     * Пропорционально уменьшает изображение, чтобы каждая сторона была не больше максимального размера
     *
     * @param originalImage         изображение, которое нужно уменьшить
     * @param maxAvailableImageSide максимальный размер стороны
     * @return уменьшенное изображение
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int maxAvailableImageSide) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        if (width < maxAvailableImageSide && height < maxAvailableImageSide) {
            return originalImage;
        }
        System.out.println("Один или оба размера превышают установленную норму (300 пикселей)");
        int k = 0;
        if (width > maxAvailableImageSide) {
            k = width / maxAvailableImageSide;
        }
        if (height > maxAvailableImageSide) {
            if ((height / maxAvailableImageSide) > k) {
                k = height / maxAvailableImageSide;
            }
        }
        int newWidth = width / k;
        int newHeight = height / k;
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * @return изображение из буфера обмена
     */
    public static BufferedImage getPictureFromClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
    }
}
