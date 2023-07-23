package io.github.redstoneparadox.oaktree.util;

import java.util.Objects;

/**
 * Represents a non-mutable color value. Can be constructed
 * using RGB(A), HSV(A), or integer values. Can be converted
 * into an integer for use by Minecraft's internals.
 */
public final class Color {
	public static final Color BLACK = rgb(0.0f, 0.0f, 0.0f);
	public static final Color DARK_BLUE = rgb(170);
	public static final Color DARK_GREEN = rgb(43520);
	public static final Color DARK_AQUA = rgb(43690);
	public static final Color DARK_RED = rgb(11141120);
	public static final Color DARK_PURPLE = rgb(11141290);
	public static final Color GOLD = rgb(16755200);
	public static final Color GREY = rgb(11184810);
	public static final Color DARK_GREY = rgb(5592405);
	public static final Color BLUE = rgb(0.0f, 0.0f, 1.0f);
	public static final Color GREEN = rgb(0.0f, 1.0f, 0.0f);
	public static final Color AQUA = rgb(5636095);
	public static final Color RED = rgb(1.0f, 0.0f, 0.0f);
	public static final Color LIGHT_PURPLE = rgb(16733695);
	public static final Color YELLOW = rgb(16777045);
	public static final Color WHITE = rgb(1.0f, 1.0f, 1.0f);

	public final float red;
	public final float green;
	public final float blue;
	public final float alpha;

	private Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	/**
	 * Constructs a color using RGB values. Inputs
	 * should be in the range of 0-255.
	 *
	 * @param red The value of the red channel
	 * @param green The value of the green channel
	 * @param blue The value of the blue channel
	 * @return A {@code Color} instance.
	 */
	public static Color rgb(int red, int green, int blue) {
		return rgba(red, green, blue, 1.0f);
	}

	/**
	 * Constructs a color using RGB values. Inputs
	 * should be in the range of 0-1.
	 *
	 * @param red The value of the red channel
	 * @param green The value of the green channel
	 * @param blue The value of the blue channel
	 * @return A {@code Color} instance.
	 */
	public static Color rgb(float red, float green, float blue) {
		return rgba(red, green, blue, 1.0f);
	}

	/**
	 * Constructs a color using RGBA values. Color channel
	 * inputs should be in the range of 0-255 and alpha
	 * should be in the range of 0-1.
	 *
	 * @param red The value of the red channel
	 * @param green The value of the green channel
	 * @param blue The value of the blue channel
	 * @param alpha The value of the alpha channel
	 * @return A {@code Color} instance.
	 */
	public static Color rgba(int red, int green, int blue, float alpha) {
		return rgba(red/255.0f, green/255.0f, blue/255.0f, alpha);
	}

	/**
	 * Constructs a color using RGB values. Inputs
	 * should be in the range of 0-1.
	 *
	 * @param red The value of the red channel
	 * @param green The value of the green channel
	 * @param blue The value of the blue channel
	 * @param alpha The value of the alpha channel
	 * @return A {@code Color} instance.
	 */
	public static Color rgba(float red, float green, float blue, float alpha) {
		return new Color(clamp(red), clamp(green), clamp(blue), clamp(alpha));
	}

	/**
	 * Constructs a color using HSV values.
	 *
	 * @param hue The value of the hue channel
	 * @param saturation The value of the saturation channel
	 * @param value The value of the value
	 * @return A {@code Color} instance.
	 */
	public static Color hsv(float hue, int saturation, int value) {
		return hsv(hue, saturation, value, 1.0f);
	}

	/**
	 * Constructs a color using HSVA values.
	 *
	 * @param hue The value of the hue channel
	 * @param saturation The value of the saturation channel
	 * @param value The value of the value
	 * @param alpha The value of the alpha channel
	 * @return A {@code Color} instance.
	 */
	public static Color hsv(float hue, int saturation, int value, float alpha) {
		hue = Math.max(0.0f, Math.min(hue, 360.0f));
		saturation = Math.max(0, Math.min(saturation, 100));
		value = Math.max(0, Math.min(value, 100));

		int hi = (int) Math.floor(hue/60.0) % 6;
		double f = (hue/60.0) - Math.floor(hue/60.0);

		double p = value * (1.0 - saturation);
		double q = value * (1.0 - (f * saturation));
		double t = value * (1.0 - ((1.0 - f) * saturation));

		Color ret;

		switch (hi) {
			case 0:
				ret = toRGBA(value, t, p, alpha);
				break;
			case 1:
				ret = toRGBA(q, value, p, alpha);
				break;
			case 2:
				ret = toRGBA(p, value, t, alpha);
				break;
			case 3:
				ret = toRGBA(p, q, value, alpha);
				break;
			case 4:
				ret = toRGBA(t, p, value, alpha);
				break;
			case 5:
				ret = toRGBA(value, p, q, alpha);
				break;
			default:
				ret = BLACK.withAlpha(alpha);
				break;
		}

		return ret;
	}

	/**
	 * Constructs a {@code Color} using an integer.
	 * Note that integers are what Minecraft uses
	 * to represent colors.
	 *
	 * @param integer The color integer
	 * @return A {@code Color} instance.
	 */
	public static Color rgb(int integer) {
		int red = (integer >> 16) & 0xff;
		int green = (integer >> 8) & 0xff;
		int blue = (integer) & 0xff;

		return rgb(red, green, blue);
	}

	private static Color toRGBA(double r, double b, double g, float a) {
		return rgba((byte) (r * 255.0) + 128, (byte) (r * 255.0) + 128, (byte) (r * 255.0) + 128, a);
	}

	/**
	 * Returns a copy of a {@code Color} with the
	 * specified alpha value.
	 *
	 * @param alpha The value of the alpha channel.
	 * @return The new color
	 */
	public Color withAlpha(float alpha) {
		return new Color(this.red, this.blue, this.green, clamp(alpha));
	}

	/**
	 * Converts this {@code Color} instance to
	 * an integer representation.
	 *
	 * @return The integer representation.
	 */
	public int toInt() {
		int r = (int) (red * 255.0f);
		int g = (int) (green * 255.0f);
		int b = (int) (blue * 255.0f);
		int a = (int) (alpha * 255.0f);

		return (a << 24) + (r << 16) + (g << 8) + b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Color color = (Color) o;
		return Float.compare(color.red, red) == 0 && Float.compare(color.green, green) == 0 && Float.compare(color.blue, blue) == 0 && Float.compare(color.alpha, alpha) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(red, green, blue, alpha);
	}

	private static float clamp(float value) {
		if (value < 0.0f) {
			return 0.0f;
		} else if (value > 1.0f) {
			return 1.0f;
		}

		return value;
	}
}
