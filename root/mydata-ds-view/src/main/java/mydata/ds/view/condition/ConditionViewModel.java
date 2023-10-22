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
	
	private final StringProperty text = new SimpleStringProperty();
	
	private BooleanProperty checkRegexp = new SimpleBooleanProperty(false);	
	
	@InjectScope
	private ConditionScope conditionScope ;
	
	public void initialize() {
		logger.debug("ConditionViewModel initialize!");
		
	}

	public ConditionViewInfo getConditionViewInfo() {
		return conditionScope.getConditionViewInfo();
	}

	public StringProperty textPropery() {
		return text;
	}

	public BooleanProperty  checkRegexpProverty() {
		return checkRegexp;
	}

}
