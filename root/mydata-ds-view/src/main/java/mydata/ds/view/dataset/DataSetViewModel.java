package mydata.ds.view.dataset;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.ViewModel;
import ds.ui.condition.UIQuerySearch;
import jakarta.inject.Inject;

public class DataSetViewModel implements ViewModel {

	public static final Logger logger = LoggerFactory.getLogger(DataSetViewModel.class);
	
	public static final String CLOSE_DATASET_NOTIFICATION = "closeDialog";

	static final String TITLE_LABEL_KEY = "dialog.addcontact.title";
	
	public static final String MEDICAL_VISIT_HISTORY = "appatEHRButton";

	public static final String TEXT_EMR_RECORD = "emrdocFormButton";
	
	private String dataSetName;
	
	@Inject
	private DataSetService dataSetService;


	@Inject
	private ResourceBundle defaultResourceBundle;

	

	public void initialize() {
	}

	public SubQueryExpression<?> getQuerySearch() {
		UIQuerySearch uiQuery = dataSetService.selectDataSet(getDataSetName());
		
		
	}

	public DataSetService getDataSetService() {
		logger.debug("getDataSetService execute. and return {}", getDataSetName());
		return dataSetService;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName; 
	}
	
	public String getDataSetName() {
		return this.dataSetName ;
	}

}
