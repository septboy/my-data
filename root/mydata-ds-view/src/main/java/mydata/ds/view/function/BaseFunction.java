package mydata.ds.view.function;

import ds.data.core.column.Col;
import javafx.beans.property.StringProperty;

public interface BaseFunction {
	Col<?> getFunctionCol();
	StringProperty functionNameProperty();
}
