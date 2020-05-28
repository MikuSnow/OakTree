package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.util.ScreenVec;

import java.util.List;

public abstract class InteractiveControl<T extends InteractiveControl> extends Control<T> {

    protected boolean isMouseWithin = false;

    @Override
    public void preDraw(int mouseX, int mouseY, float deltaTime, ControlGui gui, int offsetX, int offsetY, int containerWidth, int containerHeight, List<Control<?>> controlList) {
        if (!visible) return;
        super.preDraw(mouseX, mouseY, deltaTime, gui, offsetX, offsetY, containerWidth, containerHeight, controlList);
        isMouseWithin = (mouseX >= trueX && mouseX <= (area.width + trueX)) && (mouseY >= trueY && mouseY <= (area.height + trueY));
    }

    protected ScreenVec relativeMousePosition(int mouseX, int mouseY) {
        return new ScreenVec(mouseX - trueX, mouseY - trueY);
    }

    /**
     * INTERNAL API!!! DO NOT CALL!!!
     * @param isMouseWithin [DATA EXPUNGED]
     */
    @Deprecated
    public void setMouseWithin(boolean isMouseWithin) {
        this.isMouseWithin = isMouseWithin;
    }
}
