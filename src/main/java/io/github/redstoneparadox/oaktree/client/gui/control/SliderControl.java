package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.style.Style;
import io.github.redstoneparadox.oaktree.client.gui.style.Theme;
import io.github.redstoneparadox.oaktree.client.gui.util.GuiFunction;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public class SliderControl extends InteractiveControl<SliderControl> {
    public Style sliderStyle = null;
    public float scrollPercent = 0.0f;
    public boolean horizontal = false;

    @NotNull public GuiFunction<SliderControl> onSlide = (gui, control) -> {};

    public int barLength = 1;

    public SliderControl() {
        this.id = "slider";
    }

    public SliderControl sliderStyle(Style sliderStyle) {
        this.sliderStyle = sliderStyle;
        return this;
    }

    public SliderControl scrollPercent(float scrollPercent) {
        this.scrollPercent = scrollPercent;
        return this;
    }

    public SliderControl horizontal(boolean horizontal) {
        this.horizontal = horizontal;
        return this;
    }

    public SliderControl barLength(int barLength) {
        this.barLength = barLength;
        return this;
    }

    public SliderControl onSlide(@NotNull GuiFunction<SliderControl> onSlide) {
        this.onSlide = onSlide;
        return this;
    }

    @Override
    public void preDraw(ControlGui gui, int offsetX, int offsetY, int containerWidth, int containerHeight, int mouseX, int mouseY) {
        super.preDraw(gui, offsetX, offsetY, containerWidth, containerHeight, mouseX, mouseY);

        if (isMouseWithin && gui.mouseButtonHeld("left")) {
            if (horizontal) {
                scrollPercent = Math.max(0.0f, Math.min(((float)mouseX - trueX)/(area.width - barLength) * 100.0f, 100.0f));
            }
            else {
                scrollPercent = Math.max(0.0f, Math.min(((float)mouseY - trueY)/(area.height - barLength) * 100.0f, 100.0f));
            }

            onSlide.invoke(gui, this);
        }
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float deltaTime, ControlGui gui) {
        super.draw(matrices, mouseX, mouseY, deltaTime, gui);

        int sliderX = trueX;
        int sliderY = trueY;

        int sliderWidth = area.width;
        int sliderHeight = area.height;

        if (horizontal) {
            sliderX += (int)((scrollPercent)/100 * (area.width - barLength));
            sliderWidth = barLength;
        }
        else {
            sliderY += (int)((scrollPercent)/100 * (area.height - barLength));
            sliderHeight = barLength;
        }

        if (sliderStyle != null) sliderStyle.draw(sliderX, sliderY, sliderWidth, sliderHeight, gui);
    }

    @Override
    void applyTheme(Theme theme) {
        defaultStyle = getStyle(theme, "default");
        sliderStyle = getStyle(theme, "slider");
    }
}
