package mydata.ds.view.dataset;

import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.data.TableViewData;
import ds.common.util.ArrayUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnSet;
import ds.data.core.condition.Condition;
import ds.data.core.condition.ConditionInfo;
import ds.data.core.condition.ui.UIConditions;
import ds.ehr.research.dataset.UIDataSetEHR;
import ds.ui.condition.DataSetUI;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import mydata.ds.view.condition.ConditionViewInfo;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ConditionScope;
import mydata.ds.view.scopes.DataSetScope;
import mydata.ds.view.util.ViewUtils;

@ScopeProvider(scopes = {ConditionScope.class})
public class DataSetViewModel implements ViewModel {

	public static final Logger logger = LoggerFactory.getLogger(DataSetViewModel.class);
	
	public static final String CLOSE_DATASET_NOTIFICATION = "closeDialog";

	static final String TITLE_LABEL_KEY = "dialog.addcontact.title";
	
	public static final String MEDICAL_VISIT_HISTORY = "appatEHRButton";

	public static final String TEXT_EMR_RECORD = "emrdocFormButton";

	@Inject
	private AppContext appContext ;
	
	@Inject
	private DataSetFactory dataSetFactory;

	@Inject
	private ResourceBundle defaultResourceBundle;
	
	@InjectScope
	private DataSetScope dataSetScope ;

	private DataSetUI dataSetUI ;
	
	private UIConditions c;

	public void initialize() {
		String dataSetId = dataSetScope.getDataSetId();
		logger.info("dataSetId is selected '{}'", dataSetId);
		
		dataSetUI = dataSetFactory.getDataSetUI(dataSetId);
		c = dataSetFactory.getUIConditions(dataSetId);
		
	}

	public String getDataSetTitle() {
		return this.dataSetUI.getDataSetTitle();
	}

	public ColumnInfo[] getColumnInfos() {
		ColumnSet columnSet = dataSetUI.getColumnSet();
		ColumnInfo[] columnInfos = columnSet.getColumnInfos();
		return columnInfos;
	}

	public ConditionViewInfo[] getConditionViewInfos() {
		ConditionInfo[] conditionInfos = dataSetUI.getConditionInfos();
		ConditionViewInfo[] conditionViewInfos = null ;
		
		for(ConditionInfo conditionInfo: conditionInfos) {
			ConditionViewInfo conditionViewInfo = new ConditionViewInfo(conditionInfo);
			conditionViewInfos = ArrayUtil.addArrayOne(conditionViewInfos, conditionViewInfo, ConditionViewInfo.class);
		}
		return conditionViewInfos;
	}
	
	public TableViewData getTableViewData(UIConditions conditions) {
		SubQueryExpression<?> query = dataSetUI.getQuerySearch(conditions);
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		return tableViewData;
	}

	public Col<?>[] getColumnCols(VBox columInfoLabelVBox) {
		ObservableList<Node> columnLabels = columInfoLabelVBox.getChildren();
		Col<?>[] colsSelected = null ;
		for (Node node: columnLabels) {
			Object object = ((Control)node).getUserData();
			ColumnInfo columnInfo = (ColumnInfo)object ;
			if (columnInfo == null)
				continue ;
			Col<?> col = columnInfo.getCol();
			Col<?> colSelected = columnInfo.getColIfSelected();
			colsSelected = ArrayUtil.addArrayOne(colsSelected, colSelected, Col.class);
		}
		
		return colsSelected;
	}

	public UIConditions getConditions(VBox conditionInfoLabelVBox) {
		ObservableList<Node> conditionLabels = conditionInfoLabelVBox.getChildren();
		
		for (Node node: conditionLabels) {
			Object object = ((Control)node).getUserData();
			ConditionInfo conditionInfo = (ConditionInfo)object ;
			if(conditionInfo != null && conditionInfo.isSelected())
				conditionInfo.fillConditionValue(this.c);
		}
		
		return this.c;
	}


	public MouseEventStatus getMouseEventStatus() {
		
		return appContext.getMouseEventStatus();
	}
}
