package mydata.ds.view.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import mydata.ds.view.scopes.ConditionScope;

public class ConditionViewModel implements ViewModel {
	
	private static final Logger logger = LoggerFactory.getLogger(ConditionViewModel.class);

	public static final String CLOSE_CONDITION_VIEW_NOTIFICATION = "CLOSE_CONDITION_VIEW_NOTIFICATION";
	
	private final StringProperty firstInputTextProperty = new SimpleStringProperty();
	
	private final StringProperty firstLabelTextProperty = new SimpleStringProperty();

	private final StringProperty secondInputTextProperty = new SimpleStringProperty();
	
	private final StringProperty secondLabelTextProperty = new SimpleStringProperty();
	
	private BooleanProperty checkRegexp = new SimpleBooleanProperty(false);	
	
	@InjectScope
	private ConditionScope conditionScope ;
	
	public void initialize() {
		logger.debug("ConditionViewModel initialize!");
		
	}

	public ConditionViewInfo getConditionViewInfo() {
		return conditionScope.getConditionViewInfo();
	}

	public StringProperty firstInputTextPropery() {
		return firstInputTextProperty;
	}
	
	public StringProperty firstLabelTextProperty() {
		if ( firstLabelTextProperty.get() == null)
			firstLabelTextProperty.set("=");
		
		return firstLabelTextProperty;
	}
	
	public StringProperty secondInputTextPropery() {
		return secondInputTextProperty;
	}

	public StringProperty secondLabelTextProperty() {
		if ( secondLabelTextProperty.get() == null)
			secondLabelTextProperty.set("<");
		return secondLabelTextProperty;
	}
	
	public BooleanProperty  checkRegexpProverty() {
		return checkRegexp;
	}

}
