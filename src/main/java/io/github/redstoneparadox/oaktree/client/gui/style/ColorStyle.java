package io.github.redstoneparadox.oaktree.client.gui.style;

import io.github.redstoneparadox.oaktree.client.RenderHelper;
import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.Color;

public class ColorStyle extends Style {

    private Color color;
    private Color borderColor;
    private int borderWidth;

    public ColorStyle(Color color, Color borderColor, int borderWidth) {
        this.color = color;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
    }

    public ColorStyle(Color color) {
        this(color, null, 1);
    }

    @Override
    public void draw(int x, int y, int width, int height, ControlGui gui, boolean mirroredHorizontal, boolean mirroredVertical) {
        if (borderColor != null) RenderHelper.drawRectangle(x - borderWidth, y - borderWidth, width + 2 * borderWidth, height + 2* borderWidth, color);
        RenderHelper.drawRectangle(x, y, width, height, color);
    }
}
