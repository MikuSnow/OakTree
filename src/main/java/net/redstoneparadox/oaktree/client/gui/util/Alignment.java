package net.redstoneparadox.oaktree.client.gui.util;

public enum Alignment {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    public ScreenVec getOffset(float width, float height) {
        ScreenVec offset = new ScreenVec();

        if (this == TOP_CENTER || this == CENTER || this == BOTTOM_CENTER) {
            offset.x = width/2;
        }
        else if (this == TOP_RIGHT || this == CENTER_RIGHT || this == BOTTOM_RIGHT) {
            offset.x = width;
        }

        if (this == CENTER_LEFT || this == CENTER || this == CENTER_RIGHT)
        {
            offset.y = height/2;
        }
        else if (this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT)
        {
            offset.y = height;
        }

        return offset;
    }
}
