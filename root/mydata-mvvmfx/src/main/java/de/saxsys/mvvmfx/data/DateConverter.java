package de.saxsys.mvvmfx.data;

import java.sql.Date;
import java.text.SimpleDateFormat;

import javafx.util.StringConverter;

public class DateConverter extends StringConverter<Date> {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String toString(Date object) {
		if (object != null) {
			return dateFormat.format(object);
		}
		return "";
	}

	@Override
	public Date fromString(String string) {
		// You may implement this method if you need two-way conversion
		return null;
	}
}