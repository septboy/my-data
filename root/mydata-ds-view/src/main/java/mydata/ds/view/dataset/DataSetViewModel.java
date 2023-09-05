package mydata.ds.view.dataset;

import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.data.TableViewData;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnSet;
import ds.data.core.condition.ConditionInfo;
import ds.data.core.condition.ui.UIConditions;
import ds.ui.condition.DataSetUI;
import jakarta.inject.Inject;
import mydata.ds.view.scopes.DataSetScope;
import mydata.ds.view.util.ViewUtils;

public class DataSetViewModel implements ViewModel {

	public static final Logger logger = LoggerFactory.getLogger(DataSetViewModel.class);
	
	public static final String CLOSE_DATASET_NOTIFICATION = "closeDialog";

	static final String TITLE_LABEL_KEY = "dialog.addcontact.title";
	
	public static final String MEDICAL_VISIT_HISTORY = "appatEHRButton";

	public static final String TEXT_EMR_RECORD = "emrdocFormButton";
	
	@Inject
	private DataSetFactory dataSetFactory;

	@Inject
	private ResourceBundle defaultResourceBundle;
	
	@InjectScope
	private DataSetScope dataSetScope ;

	private DataSetUI dataSetUI ;

	public void initialize() {
		String dataSetId = dataSetScope.getDataSetId();
		logger.info("dataSetId is selected '{}'", dataSetId);
		
		dataSetUI = dataSetFactory.getDataSetUI(dataSetId);
	}

	public String getDataSetTitle() {
		return this.dataSetUI.getDataSetTitle();
	}

	public ColumnInfo[] getColumnInfos() {
		ColumnSet columnSet = dataSetUI.getColumnSet();
		ColumnInfo[] columnInfos = columnSet.getColumnInfos();
		return columnInfos;
	}

	public ConditionInfo[] getConditionInfos() {
		ConditionInfo[] conditionInfos = dataSetUI.getConditionInfos();
		return conditionInfos;
	}
	
	public TableViewData getTableViewData(Map<String, ?> uiValue) {
		UIConditions c = dataSetFactory.getUIConditions(uiValue);
		SubQueryExpression<?> query = dataSetUI.getQuerySearch(c);
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		return tableViewData;
	}

}
