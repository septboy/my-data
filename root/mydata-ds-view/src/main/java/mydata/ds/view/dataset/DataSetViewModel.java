package mydata.ds.view.dataset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.data.TableViewData;
import ds.common.util.ArrayUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnSet;
import ds.data.core.column.ColumnType;
import ds.data.core.condition.ConditionInfo;
import ds.data.core.condition.ConditionValue;
import ds.data.core.condition.ConditionValueSetter;
import ds.data.core.condition.ui.UIConditions;
import ds.data.core.join.JoinOn;
import ds.data.core.regexp.AnalyzedType;
import ds.ui.condition.DataSetUI;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import mydata.ds.view.condition.ConditionViewInfo;
import mydata.ds.view.events.DataSetEventHander;
import mydata.ds.view.model.DSViewModel;
import mydata.ds.view.relation.RelationInfo;
import mydata.ds.view.relation.RelationViewModel;
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


	private DataSetFactory dataSetFactory;
	
	@InjectScope
	private DataSetScope dataSetScope;
	
	private DataSetUI dataSetUI;

	private UIConditions uiConditions;

	/*
	 * dataSet들의 관계를 다른다.
	 */
	private DataSetRelation dataSetRelation;

	private int dataSetHashcode;

	private int baseDataSetHashcode = 0;

	private int relationHashcode = 0 ;
	
	private ConditionInfo[] joinConditionInfos;

	private List<Integer> targetDataSetHashcodeList;

	private ChoiceBox<String> hospitalChoiceBox;

	private StringProperty dbLinkNameProperty = new SimpleStringProperty();

	private ConditionInfo[] conditionInfosFromRowSelected;
	
	public DataSetViewModel() {
		this.targetDataSetHashcodeList = new ArrayList<>();
		this.dataSetFactory = new DataSetFactory();
	}
	
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
		if(this.dataSetUI == null)
			return "is not prepared specific DataSet.";
		
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
		tableViewData.setDatabaseManager(getDatabaseManager());
		return tableViewData;
	}

	public TableViewData getTableViewData(SubQueryExpression<?> baseQuery,  UIConditions conditions) {
		
		RelationViewModel relationViewModel = getAppContext().getRelationViewModel(relationHashcode);
		JoinOn joinOn = relationViewModel.getJoinOn();
		SubQueryExpression<?> query = dataSetUI.getQuerySearch(baseQuery, joinOn , conditions);
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		tableViewData.setDatabaseManager(getDatabaseManager());
		return tableViewData;
	}
	
	public TableViewData getTableViewDataIntegrated(SubQueryExpression<?>...queries) {
		SubQueryExpression<?> query = dataSetUI.getQueryIntegrateLeftJoin(queries);
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		tableViewData.setDatabaseManager(getDatabaseManager());
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
			Col<?> colSelected = columnInfo.getColIfSelected();
			colsSelected = ArrayUtil.addArrayOne(colsSelected, colSelected, Col.class);
		}

		return colsSelected;
	}

	public ConditionInfo[] getConditionInfos(VBox conditionInfoLabelVBox) {
		ObservableList<Node> conditionLabels = conditionInfoLabelVBox.getChildren();
		ConditionInfo[] conditionInfos = null ;
		for (Node node : conditionLabels) {
			Object object = ((Control) node).getUserData();
			ConditionInfo conditionInfo = (ConditionInfo) object;
			if (conditionInfo != null && conditionInfo.isSelected()) {
				conditionInfos = ArrayUtil.addArrayOne(conditionInfos, conditionInfo, ConditionInfo.class);
			}
		}
		return conditionInfos;
	}
	
	public UIConditions getValueBindedConditions(ConditionInfo...conditionInfos) {
		if (ArrayUtils.isNotEmpty(conditionInfos))
			for ( ConditionInfo conditionInfo: conditionInfos ) {
				conditionInfo.fillConditionValue(this.uiConditions);
			}
		return this.uiConditions;
	}

	public DataSetEventHander getMouseEventStatus() {

		return getAppContext().getMouseEventStatus();
	}

	public void setDataSetHashcode(int dataSetHashcode) {
		this.dataSetHashcode = dataSetHashcode;
		this.dataSetRelation = new DataSetRelation();
		getAppContext().putDataSetRelation(dataSetHashcode, this.dataSetRelation);
		getAppContext().addDataSetHashcode(dataSetHashcode);
	}

	public int getDataSetHashcode() {
		return this.dataSetHashcode ;
	}
	
	public void moveRelationLine(double deltaX, double deltaY) {
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

	public void setBaseDataSetHashcode(int baseDataSetHashcode) {
		this.baseDataSetHashcode = baseDataSetHashcode;
		
	}
	
	public void setRelationHashcode(int relationHashcode ) {
		this.relationHashcode = relationHashcode;
	}

	public boolean hasBaseDataSet() {
		return this.baseDataSetHashcode > 0;
	}

	public DataSetView getBaseDataSetView() {
		return getAppContext().getDataSetView(this.baseDataSetHashcode);
	}
	
	public DataSetViewModel getBaseDataSetViewModel() {
		return getAppContext().getDataSetViewModel(this.baseDataSetHashcode);
	}

	public List<DataSetViewModel> getTargetDataSetViewModel() {
		return getAppContext().getDataSetViewModelList(this.targetDataSetHashcodeList);
	}

	public boolean haveTargetDataSet() {
		return this.joinConditionInfos != null;
	}

	public void copyToClipboard(String text) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
	}

	public void removeRelations(int removedPaneHashcode) {
		DataSetRelation datasetRelation = getAppContext().getDataSetRelation(removedPaneHashcode);
		List<RelatedLine> relatedLineList = datasetRelation.getRelatedLineList();

		for (RelatedPane relatedPane : datasetRelation.getRelatedPaneList()) {
			// relationView 삭제
			ViewUtils.removeFromScene(relatedPane.relationPane());

			int endPaneKey = relatedPane.endPane().hashCode();
			int startPaneKey = relatedPane.startPane().hashCode();
			int realtionKey = relatedPane.relationPane().hashCode();

			if (removedPaneHashcode == endPaneKey) {
				DataSetRelation datasetRelationStart = getAppContext().getDataSetRelation(startPaneKey);
				datasetRelationStart.reflashRelatedLine(relatedLineList);
				datasetRelationStart.reflashRelatedPane(removedPaneHashcode);
				datasetRelationStart.reflashRelatedPane(realtionKey);

			} else if (removedPaneHashcode == startPaneKey) {
				DataSetRelation datasetRelationEnd = getAppContext().getDataSetRelation(endPaneKey);
				datasetRelationEnd.reflashRelatedLine(relatedLineList);
				datasetRelationEnd.reflashRelatedPane(removedPaneHashcode);
				datasetRelationEnd.reflashRelatedPane(realtionKey);
			}
		}

		for (RelatedLine relatedLine : relatedLineList) {
			//0:source //1:target
			ViewUtils.removeFromScene(relatedLine.line());
			ViewUtils.removeFromScene(relatedLine.arrowhead());
		}
		
		datasetRelation.reflashRelatedLine(relatedLineList);
	}

	public DataSetRelation getDataSetRelation(int dataSetHashcode) {
		return getAppContext().getDataSetRelation(dataSetHashcode);
	}

	public void setDbLinkCode(String dbLinkName) {
		logger.debug("newvalue={}",dbLinkName);
		
		this.dataSetFactory.setDbLinkCode(dbLinkName);
	}

	public void addTargetDataSetHashcode(Integer targetPaneHashcode) {
		this.targetDataSetHashcodeList.add(targetPaneHashcode);
		
	}

	public void removeTargetDataSetHashcode(Integer targetPaneHashcode) {
		this.targetDataSetHashcodeList.remove(targetPaneHashcode);
	}

	public ChoiceBox<String> getHospitalChoiceBox() {

		return this.hospitalChoiceBox;
	}

	public void setHospitalChoiceBox(ChoiceBox<String> hospitalChoiceBox) {
		this.hospitalChoiceBox = hospitalChoiceBox;
	}
	

	public StringProperty dbLinkNameProperty() {
		return this.dbLinkNameProperty ;
	}
	
	public void updatedbLinkNameProperty() {
		String databaseCode = this.dataSetFactory.getDbLinkCode();
		this.dbLinkNameProperty.setValue(getDatabaseManager().getDatabaseName(databaseCode));
	}
	
	protected String[] getDatabaseNames() {
		return getDatabaseManager().getDatabaseNames();
	}
	
	protected String getDatabaseCode(int index) {
		return getDatabaseManager().getDatabaseCode(getDatabaseNames()[index]);
	}
	
	@Override
	public String toString() {
		return String.format("%s [%d]: %s", this.getClass().getSimpleName(), this.hashCode(), getDataSetTitle());
	}

	public ConditionInfo[] getJoinConditionInfos(RelatedPane relatedPane, TableViewData tableViewData, Tuple row) {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 진행중 관계 맺기 코드 작성 진행 중...
		int relationHashcode = relatedPane.relationPane().hashCode();
		RelationViewModel relationViewModel = getAppContext().getRelationViewModel(relationHashcode);
		
		Map<String, Object> columnNameMap = new HashMap<>();
		for (RelationInfo relationInfo: relationViewModel.getRelationInfoList()) {
			String columnName = relationInfo.getColumnName();
			ColumnType columnType = relationInfo.getColumnType();
			
			Object value = null ;
			if (columnType == ColumnType.Date)
				value = row.get(tableViewData.getColumnExpression(columnName, java.sql.Date.class));
			
			else if (columnType == ColumnType.String)
				value = row.get(tableViewData.getColumnExpression(columnName, String.class));
			
			else if (columnType == ColumnType.Time)
				value = row.get(tableViewData.getColumnExpression(columnName, java.sql.Timestamp.class));
			
			else if (columnType == ColumnType.Integer)
				value = row.get(tableViewData.getColumnExpression(columnName, Integer.class));
			
			else if (columnType == ColumnType.Long)
				value = row.get(tableViewData.getColumnExpression(columnName, Long.class));
			
			else if (columnType == ColumnType.Decimal)
				value = row.get(tableViewData.getColumnExpression(columnName, BigDecimal.class));
			
			columnNameMap.put(columnName, value );
		}
		
		logger.debug("selected record Join status -> {}", columnNameMap);
		
		String[] joinColumnNames = columnNameMap.keySet().toArray(String[]::new);
		
		ConditionInfo[] conditionInfos = UIConditions.getConditionInfosFromCondtions(getUIConditions(), joinColumnNames);
		
		for (ConditionInfo conditionInfo : conditionInfos) {
			for( String joinColumnName: joinColumnNames ) {
				if ( conditionInfo.getColumnName().equals(joinColumnName) ) {
					// TODO 20231110-1321 조인 함수에 대한 처리 개선 예정 
					// 현재는 equal만 가능
					if (conditionInfo.getColumnType() == ColumnType.String) {
						String value = (String)columnNameMap.get(joinColumnName);
						ConditionValue conditionValue = new ConditionValue(AnalyzedType.equal, value);
						conditionInfo.setValue(conditionValue);
					} else
						conditionInfo.setValue(columnNameMap.get(joinColumnName));
				}
			}
		}
		
		return conditionInfos;
	}

	public ConditionInfo[] getJoinConditionInfos() {
		return this.joinConditionInfos ;
	}

	public void setJoinConditionInfos(ConditionInfo... conditionInfos) {
		this.joinConditionInfos = conditionInfos;
		
	}

	public void setConditionInfosFromRowSelected(ConditionInfo[] conditionInfos) {
		this.conditionInfosFromRowSelected = conditionInfos;
	}
	
	public ConditionInfo[] getConditionInfosFromRowSelected() {
		return this.conditionInfosFromRowSelected;
	}
	
	public boolean isBaseTableRowSelected() {
		return ArrayUtils.isNotEmpty(this.conditionInfosFromRowSelected);
	}
}
