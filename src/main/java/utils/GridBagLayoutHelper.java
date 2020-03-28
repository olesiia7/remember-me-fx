package utils;

import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;

/**
 * Помогает работать с {@link GridBagLayout}.
 * Содержит несколько конструкторов для упрощения кода.
 *
 * @author olesiia7
 * @since 13.05.2020
 */
public class GridBagLayoutHelper {

    /**
     * @param x      положение по оси X (право-лево)
     * @param y      положение по оси Y (верх-вниз)
     * @param xCount количество занимаемых ячеек по X
     * @param yCount количество занимаемых ячеек по Y
     *               <p>
     *               По умолчанию выравнивание по левому краю, растягивание по горизонтали и вертикали
     * @return экземпляр {@link GridBagConstraints}
     */
    public static GridBagConstraints setGridBagConstraints(int x, int y, int xCount, int yCount) {
        return setGridBagConstraints(x, y, xCount, yCount, 0);
    }

    /**
     * @param x      положение по оси X (право-лево)
     * @param y      положение по оси Y (верх-вниз)
     * @param xCount количество занимаемых ячеек по X
     * @param yCount количество занимаемых ячеек по Y
     * @param height высота элемента
     *               <p>
     *               По умолчанию выравнивание по левому краю, растягивание по горизонтали и вертикали
     * @return экземпляр {@link GridBagConstraints}
     */
    public static GridBagConstraints setGridBagConstraints(int x, int y, int xCount, int yCount, int height) {
        return setGridBagConstraints(x, y, xCount, yCount, WEST, BOTH, height);
    }

    /**
     * @param x         положение по оси X (право-лево)
     * @param y         положение по оси Y (верх-вниз)
     * @param xCount    количество занимаемых ячеек по X
     * @param yCount    количество занимаемых ячеек по Y
     * @param alignment расположение элемента (NORTH - верх, SOUTH - низ, WEST - слева, EAST - справа)
     * @param fill      растягивание (по горизонтали, вертикали, обоим)
     * @return экземпляр {@link GridBagConstraints}
     */
    public static GridBagConstraints setGridBagConstraints(int x, int y, int xCount, int yCount, int alignment, int fill) {
        return setGridBagConstraints(x, y, xCount, yCount, alignment, BOTH, fill);
    }

    /**
     * @param x         положение по оси X (право-лево)
     * @param y         положение по оси Y (верх-вниз)
     * @param xCount    количество занимаемых ячеек по X
     * @param yCount    количество занимаемых ячеек по Y
     * @param alignment расположение элемента (NORTH - верх, SOUTH - низ, WEST - слева, EAST - справа)
     * @param fill      растягивание (по горизонтали, вертикали, обоим)
     * @param height    высота элемента
     * @return экземпляр {@link GridBagConstraints}
     */
    public static GridBagConstraints setGridBagConstraints(int x, int y, int xCount, int yCount, int alignment, int fill, int height) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;

        c.gridwidth = xCount;
        c.gridheight = yCount;

        c.weightx = 0.0;
        c.weighty = 0.9;

        c.anchor = alignment;
        c.fill = fill;

        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = height;
        return c;
    }
}