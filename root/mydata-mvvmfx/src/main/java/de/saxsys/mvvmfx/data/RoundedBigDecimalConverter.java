package de.saxsys.mvvmfx.data;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javafx.util.StringConverter;

public class RoundedBigDecimalConverter extends StringConverter<BigDecimal> {
	private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

	@Override
	public String toString(BigDecimal object) {
		if (object != null) {
			BigDecimal roundedValue = new BigDecimal(Math.ceil(object.doubleValue() * 10) / 10);
			return decimalFormat.format(roundedValue);
		}
		return "";
	}

	@Override
	public BigDecimal fromString(String string) {
		return null;
	}
}