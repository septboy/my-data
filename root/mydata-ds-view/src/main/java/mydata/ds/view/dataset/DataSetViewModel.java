package mydata.ds.view.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.data.TableViewData;
import ds.common.util.ArrayUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnSet;
import ds.data.core.condition.ConditionInfo;
import ds.data.core.condition.ui.UIConditions;
import ds.data.core.join.JoinOn;
import ds.ui.condition.DataSetUI;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import mydata.ds.view.condition.ConditionViewInfo;
import mydata.ds.view.model.DSViewModel;
import mydata.ds.view.scopes.ApplicationScope;
import mydata.ds.view.scopes.ConditionScope;
import mydata.ds.view.scopes.DataSetScope;
import mydata.ds.view.util.ViewUtils;

@ScopeProvider(scopes = { ConditionScope.class })
public class DataSetViewModel extends DSViewModel {

	public static final Logger logger = LoggerFactory.getLogger(DataSetViewModel.class);

	public static final String CLOSE_DATASET_NOTIFICATION = "closeDialog";

	static final String TITLE_LABEL_KEY = "dialog.addcontact.title";

	public static final String MEDICAL_VISIT_HISTORY = "appatEHRButton";

	public static final String TEXT_EMR_RECORD = "emrdocFormButton";
	
	public static final String PRESCRIPTION = "prescriptionButton";



	@Inject
	private DataSetFactory dataSetFactory;
	
	@InjectScope
	private DataSetScope dataSetScope;
	
	private DataSetUI dataSetUI;

	private UIConditions uiConditions;

	/*
	 * dataSet들의 관계를 다른다.
	 */
	private DataSetRelation dataSetRelation;

	private int dataSetIdNumber;

	private int relationBaseHashcode = 0;

	private ConditionInfo[] joinConditionInfos;

	public void initialize() {
		String dataSetId = dataSetScope.getDataSetId();
		logger.info("dataSetId is selected '{}'", dataSetId);

		this.dataSetUI = dataSetFactory.getDataSetUI(dataSetId);
		
	}
	
	public void addDataSetContext() {
		this.uiConditions = dataSetFactory.getUIConditionsAndAddDataSetContext(dataSetScope.getDataSetId());
	}

	public UIConditions getUIConditions() {
		return dataSetFactory.getUIConditions(dataSetScope.getDataSetId());
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
		ConditionViewInfo[] conditionViewInfos = null;

		for (ConditionInfo conditionInfo : conditionInfos) {
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

	public TableViewData getTableViewData(SubQueryExpression<?> baseQuery,  UIConditions conditions) {
		SubQueryExpression<?> query = dataSetUI.getQuerySearch(baseQuery, JoinOn.PATNO , conditions);
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		return tableViewData;
	}
	
	public TableViewData getTableViewDataIntegrated(SubQueryExpression<?>...queries) {
		SubQueryExpression<?> query = dataSetUI.getQueryIntegrateLeftJoin(queries);
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		return tableViewData;
	}
	
	public Col<?>[] getColumnCols(VBox columInfoLabelVBox) {
		ObservableList<Node> columnLabels = columInfoLabelVBox.getChildren();
		Col<?>[] colsSelected = null;
		for (Node node : columnLabels) {
			Object object = ((Control) node).getUserData();
			ColumnInfo columnInfo = (ColumnInfo) object;
			if (columnInfo == null)
				continue;
			Col<?> col = columnInfo.getCol();
			Col<?> colSelected = columnInfo.getColIfSelected();
			colsSelected = ArrayUtil.addArrayOne(colsSelected, colSelected, Col.class);
		}

		return colsSelected;
	}

	public UIConditions getValueBindedConditions(VBox conditionInfoLabelVBox) {
		ObservableList<Node> conditionLabels = conditionInfoLabelVBox.getChildren();

		for (Node node : conditionLabels) {
			Object object = ((Control) node).getUserData();
			ConditionInfo conditionInfo = (ConditionInfo) object;
			if (conditionInfo != null && conditionInfo.isSelected()) {
				conditionInfo.fillConditionValue(this.uiConditions);
			}
		}

		return this.uiConditions;
	}

	public UIConditions getValueBindedConditions(ConditionInfo...conditionInfos) {
		
		for ( ConditionInfo conditionInfo: joinConditionInfos ) {
			conditionInfo.fillConditionValue(this.uiConditions);
		}
		return this.uiConditions;
	}

	public DataSetEvent getMouseEventStatus() {

		return getAppContext().getMouseEventStatus();
	}

	public void setDataSetHashcode(int dataSetIdNumber) {
		this.dataSetIdNumber = dataSetIdNumber;
		this.dataSetRelation = new DataSetRelation();
		getAppContext().putDataSetRelation(dataSetIdNumber, this.dataSetRelation);
		getAppContext().addDataSetHashcode(dataSetIdNumber);
	}

	public void moveRelationLine(double deltaX, double deltaY) {
		logger.debug("moveRelationLine( {}, {} )", deltaX, deltaY);
		dataSetRelation.moveRelationLine(deltaX, deltaY);

	}

	/**
	 * 
	 * @param relationPointCentersScene
	 * @param topCenterX
	 * @param topCenterY
	 * @param rightCenterX
	 * @param rightCenterY
	 * @param bottomCenterX
	 * @param bottomCenterY
	 * @param leftCenterX
	 * @param leftCenterY
	 */
	public void moveRelationLine(RelationPointCenters relationPointCentersScene, double topCenterX, double topCenterY,
			double rightCenterX, double rightCenterY, double bottomCenterX, double bottomCenterY, double leftCenterX,
			double leftCenterY) {

		dataSetRelation.moveRelationLine(relationPointCentersScene, topCenterX, topCenterY, rightCenterX, rightCenterY,
				bottomCenterX, bottomCenterY, leftCenterX, leftCenterY);

	}
	

	public void ifLinkedIntegratedGridModifyTableColumn(ColumnInfo columnInfo) {
		getApplicationScope().publish(ApplicationScope.ADD_OR_REMOVE_GRID_COLUMN, columnInfo);
	}

	public void setRelationBaseHashcode(int relationBaseHashcode) {
		this.relationBaseHashcode = relationBaseHashcode;
		
	}

	public boolean hasBaseDataSet() {
		return this.relationBaseHashcode > 0;
	}

	public DataSetView getBaseDataSetView() {
		return getAppContext().getDataSetView(relationBaseHashcode);
	}
	
	public DataSetViewModel getBaseDataSetViewModel() {
		return getAppContext().getDataSetViewModel(relationBaseHashcode);
	}

	public boolean haveTargetDataSet() {
		return this.joinConditionInfos != null;
	}

	public void setJoinConditionInfos(ConditionInfo... conditionInfos) {
		this.joinConditionInfos = conditionInfos;
		
	}

	public ConditionInfo[] getJoinConditionInfos() {
		return this.joinConditionInfos ;
	}

	public void copyToClipboard(String text) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
	}

}
