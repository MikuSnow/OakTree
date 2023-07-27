package io.github.redstoneparadox.oaktree.control;


import com.mojang.blaze3d.glfw.Window;
import io.github.redstoneparadox.oaktree.painter.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A special {@link PanelControl} that manages an
 * entire tree of {@link Control} instances.
 * Note that mouse interaction with this control
 * is disabled.</p>
 *
 * <p>When your {@link Screen} is closed, make sure
 * to call {@link RootPanelControl#close()}</p>
 */
public class RootPanelControl extends PanelControl {
	protected Theme theme;
	private boolean dirty = true;
	private final List<Control> controls = new ArrayList<>();

	public RootPanelControl() {
		theme = Theme.vanilla();
	}

	/**
	 * Sets the theme for the entire GUI
	 *
	 * @param theme The theme to use
	 */
	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	/**
	 * Gets the current theme.
	 *
	 * @return The current theme.
	 */
	public Theme getTheme() {
		return theme.copy();
	}

	/**
	 * Renders this control and the entire {@link Control}
	 * tree.
	 *
	 * @param mouseX The mouse x position
	 * @param mouseY The mouse y position
	 * @param deltaTime The time since the last frame
	 */
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTime) {
		if (dirty) {
			MinecraftClient client = MinecraftClient.getInstance();
			Window window = client.getWindow();

			controls.clear();
			updateTree(controls, 0, 0, window.getScaledWidth(), window.getScaledHeight());

			dirty = false;
		}

		boolean captured = false;

		for (int i = controls.size() - 1; i >= 0; i--) {
			Control control = controls.get(i);

			if (!captured) {
				captured = control.interact(mouseX, mouseY, deltaTime, false);
			} else {
				control.interact(mouseX, mouseY, deltaTime, true);
			}
		}

		for (Control control: controls) {
			control.prepare();
		}

		for (Control control: controls) {
			control.draw(graphics, theme);
		}

		for (Control control: controls) {
			control.drawTooltip(graphics);
		}
	}

	@Override
	protected boolean interact(int mouseX, int mouseY, float deltaTime, boolean captured) {
		return false;
	}

	public void close() {
		cleanup();
	}

	@Override
	protected void markDirty() {
		this.dirty = true;
	}
}
