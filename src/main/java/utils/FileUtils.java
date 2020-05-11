package utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
     * Перезаписывает лог файл
     *
     * @param logs логи
     * @param path путь к сохранению файла логов
     * @throws IOException исключение, если что-т оне так
     */
    public static void createLogFile(List<String> logs, String path) throws IOException {
        deleteFiles(Collections.singletonList(path));
        File file = new File(path);
        file.createNewFile();
        FileWriter writer = new FileWriter(path, true);
        logs.forEach(text -> {
            try {
                writer.write(text + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.flush();
        writer.close();
    }

    /**
     * Перемещает изображения из старой локации в новую
     */
    public static void moveFiles(String oldDirectoryPath, String newDirectoryPath) {
        File dir = new File(oldDirectoryPath);
        File[] arrFiles = dir.listFiles();
        if (arrFiles != null) {
            List<File> picList = Arrays.stream(arrFiles)
                    .filter(pic -> pic.getPath().matches("[\\w\\W]*.png"))
                    .collect(Collectors.toList());
            picList.forEach(file -> {
                String newPath = newDirectoryPath + "\\" + file.getName();
                boolean isMoved = file.renameTo(new File(newPath));
                System.out.println(file.getName() + " перенесен: " + isMoved);
            });
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

