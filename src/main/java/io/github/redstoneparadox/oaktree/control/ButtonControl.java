package io.github.redstoneparadox.oaktree.control;

import io.github.redstoneparadox.oaktree.listeners.ClientListeners;
import io.github.redstoneparadox.oaktree.listeners.MouseButtonListener;
import io.github.redstoneparadox.oaktree.math.Vector2;
import io.github.redstoneparadox.oaktree.painter.Theme;
import io.github.redstoneparadox.oaktree.util.Action;
import io.github.redstoneparadox.oaktree.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Holder;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * A Control that can be interacted with by
 * the user.
 */
public class ButtonControl extends Control implements MouseButtonListener {
	public static final PainterKey HELD = new PainterKey();
	public static final PainterKey HOVERED = new PainterKey();
	protected boolean toggleable = false;
	protected Text text = Text.empty();
	protected Holder.Reference<SoundEvent> clickSound = SoundEvents.UI_BUTTON_CLICK;
	protected @NotNull Action onClick = () -> {};
	protected @NotNull Action whileHeld = () -> {};
	protected @NotNull Action onRelease = () -> {};
	private boolean mouseClicked = false;
	private boolean mouseHeld = false;
	private boolean buttonHeld = false;
	protected Vector2 mousePose = null;
	protected Vector2 mousePressPose = null;

	public ButtonControl() {
		this.name = "button";

		ClientListeners.MOUSE_BUTTON_LISTENERS.add(this);
	}

	/**
	 * Sets whether this control behaves like
	 * a toggle button or a regular button.
	 *
	 * @param toggleable Whether this button is toggleable
	 */
	public void setToggleable(boolean toggleable) {
		this.toggleable = toggleable;
	}

	public boolean isToggleable() {
		return toggleable;
	}

	/**
	 * Sets the text for this button to
	 * display.
	 *
	 * @param text The text to display
	 */
	public void setText(String text) {
		this.text = Text.of(text);
	}

	/**
	 * Sets the text for this button to
	 * display.
	 *
	 * @param text The text to display
	 */
	public void setText(Text text) {
		this.text = text;
	}

	public Text getText() {
		return text;
	}

	/**
	 * Sets the sound to be played when this
	 * button is clicked. By default, it will
	 * play the vanilla click sound.
	 *
	 * @param clickSound The sound event reference
	 */
	public void setClickSound(Holder.Reference<SoundEvent> clickSound) {
		this.clickSound = clickSound;
	}

	public Holder.Reference<SoundEvent> getClickSound() {
		return clickSound;
	}

	/**
	 * Sets an {@link Action} to be run
	 * when this button is clicked.
	 *
	 * @param onClick The action to run
	 */
	public void onClick(@NotNull Action onClick) {
		this.onClick = onClick;
	}

	/**
	 * Sets an {@link Action} to be run
	 * while this button is held
	 *
	 * @param whileHeld The action to run
	 */
	public void whileHeld(@NotNull Action whileHeld) {
		this.onClick = whileHeld;
	}

	/**
	 * Sets an {@link Action} to be run
	 * when this button is released.
	 *
	 * @param onRelease The action to run.
	 */
	public void onRelease(@NotNull Action onRelease) {
		this.onRelease = onRelease;
	}

	@Override
	protected boolean interact(int mouseX, int mouseY, float deltaTime, boolean captured) {
		captured = super.interact(mouseX, mouseY, deltaTime, captured);
		mousePose = new Vector2(mouseX, mouseY);

		int x = trueArea.getX();
		int y = trueArea.getY();
		int width = trueArea.getWidth();
		int height = trueArea.getHeight();
		boolean pressOnButton = false;
		if (mousePressPose != null) {
			pressOnButton = mousePressPose.getX() >= x && mousePressPose.getX() <= x + width && mousePressPose.getY() >= y && mousePressPose.getY() <= y + height;
		}
		if (toggleable) {
			if (captured) {
				if (mouseClicked) {
					buttonHeld = !buttonHeld;

					if (buttonHeld && pressOnButton) {
						onClick.run();
					}
					else {
						onRelease.run();
					}
				}
			}

			if (buttonHeld) {
				whileHeld.run();
			}
		}
		else if (captured) {
			if (mouseHeld) {
				if (!buttonHeld && pressOnButton) {
					buttonHeld = true;
					onClick.run();
				}

				whileHeld.run();
			} else {
				buttonHeld = false;
				onRelease.run();
			}
		} else if (buttonHeld) {
			buttonHeld = false;
			onRelease.run();
		}

		if (buttonHeld) {
			painterKey = HELD;
		}
		else if (captured) {
			painterKey = HOVERED;
		}
		else {
			painterKey = DEFAULT;
		}

		return captured;
	}

	@Override
	protected void draw(GuiGraphics graphics, Theme theme) {
		super.draw(graphics, theme);

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		int textX = trueArea.getX() + trueArea.getWidth()/2 - textRenderer.getWidth(text)/2;
		int textY = trueArea.getY() + trueArea.getHeight()/2 - textRenderer.fontHeight/2;

		graphics.drawText(textRenderer, text, textX, textY, Color.WHITE.toInt(), false);
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		ClientListeners.MOUSE_BUTTON_LISTENERS.remove(this);
	}

	@Override
	public void onMouseButton(int button, boolean justPressed, boolean released) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			mouseClicked = justPressed && !released;
			mouseHeld = !released;

			if (mouseClicked) {
				mousePressPose = mousePose;
				MinecraftClient client = MinecraftClient.getInstance();
				client.getSoundManager().play(PositionedSoundInstance.create(clickSound, 1.0F));
			} else {
				mousePressPose = null;
			}
		}
	}
}
