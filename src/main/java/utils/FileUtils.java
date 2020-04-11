package utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс-помощник в работе с файлами
 */
public class FileUtils {

    /**
     * @return изображение из буфера обмена
     */
    public static BufferedImage getPictureFromClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
    }

    /**
     * Удаляет все файлы, кроме БД и тех, которые есть в БД
     */
    public static void deleteUnusedFiles(List<String> picPathToSave, String pathDirectory) {
        File dir = new File(pathDirectory);
        File[] arrFiles = dir.listFiles();
        if (arrFiles != null) {
            List<String> redundantPic = Arrays.stream(arrFiles)
                    .map(File::getPath)
                    .filter(pic -> !picPathToSave.contains(pic) && pic.matches("[\\w\\W]*.png"))
                    .collect(Collectors.toList());
            deleteFiles(redundantPic);
        }
    }

    /**
     * Удаляет все файлы, кроме БД и тех, которые есть в БД
     */
    public static void deleteFiles(List<String> pathToDelete) {
        for (String path : pathToDelete) {
            File file = new File(path);
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }
}

