package de.saxsys.mvvmfx.data;

import java.text.DecimalFormat;

import javafx.util.StringConverter;

public class RoundedFloatConverter extends StringConverter<Float> {
	private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

	@Override
	public String toString(Float object) {
		if (object != null) {
			float roundedValue = (float) Math.ceil(object * 10) / 10;
			return decimalFormat.format(roundedValue);
		}
		return "";
	}

	@Override
	public Float fromString(String string) {
		return null;
	}
}