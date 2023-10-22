package de.saxsys.mvvmfx.data;

import java.text.DecimalFormat;

import javafx.util.StringConverter;

public class RoundedNumberConverter extends StringConverter<Double> {
	
	private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

	@Override
	public String toString(Double object) {
		if (object != null) {
			double roundedValue = Math.ceil(object * 10) / 10;
			return decimalFormat.format(roundedValue);
		}
		return "";
	}

	@Override
	public Double fromString(String string) {
		return null;
	}
}