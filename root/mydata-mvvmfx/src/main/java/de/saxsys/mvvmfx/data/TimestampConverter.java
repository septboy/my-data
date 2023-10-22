package de.saxsys.mvvmfx.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javafx.util.StringConverter;

public class TimestampConverter extends StringConverter<Timestamp> {
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public String toString(Timestamp object) {
		if (object != null) {
			return timeFormat.format(object);
		}
		return "";
	}

	public String toDateString(Timestamp object) {
		if (object != null) {
			return dateFormat.format(object);
		}
		return "";
	}
	
	@Override
	public Timestamp fromString(String string) {
		// You may implement this method if you need two-way conversion
		return null;
	}
}