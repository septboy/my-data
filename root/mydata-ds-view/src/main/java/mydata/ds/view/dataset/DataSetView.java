package mydata.ds.view.dataset;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.data.TableViewData;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import ds.common.util.ArrayUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.condition.ConditionInfo;
import ds.data.core.condition.ui.UIConditions;
import ds.data.core.context.IntegratedContext;
import ds.ehr.dao.constant.EHR;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import mydata.ds.view.condition.ConditionView;
import mydata.ds.view.condition.ConditionViewInfo;
import mydata.ds.view.condition.ConditionViewModel;
import mydata.ds.view.events.BackgroundEventHandler;
import mydata.ds.view.events.DataSetEventHander;
import mydata.ds.view.executor.Executor;
import mydata.ds.view.function.FunctionInfo;
import mydata.ds.view.function.FunctionView;
import mydata.ds.view.function.FunctionViewModel;
import mydata.ds.view.grid.IntegratedIcon;
import mydata.ds.view.scopes.ConditionScope;
import mydata.ds.view.service.DataSetService;
import mydata.ds.view.util.DataSetHelper;
import mydata.ds.view.util.EventUtils;
import mydata.ds.view.util.LinkUtils;
import mydata.ds.view.util.ViewUtils;

public class DataSetView implements FxmlView<DataSetViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(DataSetView.class);

	@FXML
	public TableView<Tuple> dataSetTableView;

	@FXML
	public AnchorPane dataSetRootAnchorPane;

	@FXML
	public Label dataSetTitleLabel;

	@FXML
	public AnchorPane dataSetTitlePane;

	@FXML
	public VBox columInfoLabelVBox;

	@FXML
	public VBox conditionInfoLabelVBox;

	@FXML
	public ScrollPane dataSetColumnScrollPane;

	@FXML
	public Circle topRelationCircle;

	@FXML
	public Circle bottomRelationCircle;

	@FXML
	public Circle leftRelationCircle;

	@FXML
	public Circle rightRelationCircle;
	
	@FXML
	public Button openFunctionViewButton;

	@FXML
	private Button searchOrCancelButton;

	@FXML
	public ProgressIndicator progressIndicator;

	@FXML
	private ChoiceBox<String> hospitalChoiceBox;
	
	@Inject
	Stage mainRootStage;

	@Inject
	Application.Parameters parameters;

	@Inject
	NotificationCenter notificationCenter;

	@Inject
	HostServices hostServices;

	@InjectViewModel
	private DataSetViewModel viewModel;

	@InjectContext
	private Context context;

	private double xOffsetOfMousePressedTitlePane = 0;
	private double yOffsetOfMousePressedTitlePane = 0;

	private double initialPressedX;
	private double initialPressedY;
	private double initialPressedWidth;
	private double initialPressedHeight;
	private Node tmpConditionView;

	private Control tmpConditionControlButton;

	private ConditionViewModel tmpConditionViewModel;

	public static final String css_column_label_border = "-fx-border-color: white; -fx-border-width: 1px 0px 1px 0px;";

	private DataSetEventHander mouseEventStatus;

	private RelationPointCenters relationPointCentersPressed;

	private RelationPointCenters relationPointCentersScene;

	private TableViewData tableViewData;

	private DataSetService dataSetService;

	public void initialize() {

		progressIndicator.setVisible(false);
		int dataSetHashcode = dataSetRootAnchorPane.hashCode();

		initializeTitle();

		initializeColumns();

		initializeConditions();

		initializeDataSetRootPane();

		initializeCloseButton(dataSetHashcode);
		
		initializeOpenFunctionViewButton();

		initializeDataSetRelation();

		initializeTableView();
		
		initializeHospitalChoiceBox();
		
		initializeRelationCircle();

		viewModel.getAppContext().putDataSetViewModel(dataSetHashcode, viewModel);
		viewModel.getAppContext().putDataSetView(dataSetHashcode, this);
		viewModel.setDataSetHashcode(dataSetHashcode);
		viewModel.setHospitalChoiceBox(this.hospitalChoiceBox);

	}

	@FXML
	private void codeTest() {
		viewModel.setDbLinkCode(EHR.DataSource.GREHRTEST);
		viewModel.updatedbLinkNameProperty();
	}

	private void initializeTitle() {
		// 데이터셋 타이틀
		String datasetTitle = viewModel.getDataSetTitle();
		LinkUtils.link(dataSetTitleLabel, datasetTitle);
	
		dataSetTitlePane.setOnMousePressed(this::handleMousePressedTitlePane);
		dataSetTitlePane.setOnMouseDragged(this::handleMouseDraggedTitlePane);
		dataSetTitlePane.setOnMouseClicked(this::handleMouseClickedTitlePane);
	
		dataSetTitleLabel.setOnMousePressed(this::handleDummyEvent);
		dataSetTitleLabel.setOnMouseDragged(this::handleDummyEvent);
		dataSetTitleLabel.setOnMouseClicked(this::handleDummyEvent);
	
	}

	private void initializeTableView() {
		dataSetTableView.setPlaceholder(new Label("데이터를 검색하세요."));
	
		dataSetTableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.isShiftDown()) {
				dataSetTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			} else {
				dataSetTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			}
		});
	
		dataSetTableView.setOnMousePressed(this::handleParentEvent);
	
		dataSetTableView.setOnMouseClicked(event -> {
			if (event.isControlDown() && event.getClickCount() == 1) { // Single click
				TablePosition<Tuple, ?> pos = dataSetTableView.getSelectionModel().getSelectedCells().get(0);
				int row = pos.getRow();
				TableColumn<Tuple, ?> col = pos.getTableColumn();
				String cellText = col.getCellData(row).toString();
				viewModel.copyToClipboard(cellText);
	
				event.consume();
			}
		});
	
		this.viewModel.getAppContext().getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.C) {
				copySelectedRows(dataSetTableView);
			}
		});
	
		// Handle row selection
		dataSetTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldRowSelected, newRowSelected) -> {
	
			if (newRowSelected != null) {
				//////////////////////////////////////////////////////////////////////////////
				int baseHashcode = this.dataSetRootAnchorPane.hashCode();
				DataSetRelation dataSetRelation = viewModel.getAppContext().getDataSetRelation(baseHashcode);
				List<RelatedPane> RelatedPaneList = dataSetRelation.getRelatedPaneList();
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// for 문 상단에서의 viewModel.setJoinConditionInfos(conditionInfos); 코드를
				// for 문 내에서 처리해야 한다.
				for (RelatedPane relatedPane : RelatedPaneList) {
					// 연결된 데이터 셋에서의 
					int targetHashcode = relatedPane.endPane().hashCode();
	
					if (targetHashcode == baseHashcode)
						continue;
	
					ConditionInfo[] conditionInfos = viewModel.getJoinConditionInfos(relatedPane, this.tableViewData, newRowSelected);
					viewModel.setJoinConditionInfos(conditionInfos);
					
					DataSetView targetDataSetView = viewModel.getAppContext().getDataSetView(targetHashcode);
					DataSetViewModel targetDataSetViewModel = viewModel.getAppContext().getDataSetViewModel(targetHashcode);
					if (targetDataSetView != null) {
						targetDataSetViewModel.setConditionInfosFromRowSelected(conditionInfos);
						targetDataSetView.searchOrCancel();
					}
					
				}
	
				// 처리후 원복
				viewModel.setJoinConditionInfos(null);
			}
		});
	}

	private void initializeRelationCircle() {
		topRelationCircle.setVisible(false);
		bottomRelationCircle.setVisible(false);
		leftRelationCircle.setVisible(false);
		rightRelationCircle.setVisible(false);
	}

	private void initializeHospitalChoiceBox() {
		this.hospitalChoiceBox.setItems(FXCollections.observableArrayList(
				viewModel.getDatabaseNames()
			    )
			);
		
		this.hospitalChoiceBox.valueProperty().bindBidirectional(viewModel.dbLinkNameProperty());
		viewModel.setDbLinkCode(EHR.DataSource.AAEHRTEST);
		viewModel.updatedbLinkNameProperty();
		
		this.hospitalChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						
						String dbLinkCode = getDbLinkCode((int)newValue);
						viewModel.setDbLinkCode(dbLinkCode);
						
						List<DataSetViewModel> targetDataSetViewModelList = viewModel.getTargetDataSetViewModel();
						for(DataSetViewModel targetDataSetViewModel: targetDataSetViewModelList) {
							targetDataSetViewModel.setDbLinkCode(dbLinkCode);
							targetDataSetViewModel.updatedbLinkNameProperty();
						}
						
					}

					private String getDbLinkCode(int newValue) {
						String dbLinkName = viewModel.getDatabaseCode(newValue);
						return dbLinkName;
					}
				});
	}

	private void initializeDataSetRelation() {
		// 관계 맺기 관련 Event 장착
		mouseEventStatus = viewModel.getMouseEventStatus();
		mouseEventStatus.setScene(mainRootStage.getScene());
		mouseEventStatus.setEventTarget(dataSetRootAnchorPane);
	}

	private void initializeCloseButton(int dataSetHashcode) {

		viewModel.subscribe(DataSetViewModel.CLOSE_DATASET_NOTIFICATION, (key, payload) -> {
			if (tmpConditionViewModel != null)
				tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION,
						tmpConditionControlButton, tmpConditionView);

			viewModel.removeRelations(dataSetHashcode);

			IntegratedIcon relatedIcon = viewModel.getAppContext().getIntegratedIcon(dataSetHashcode);
			if (relatedIcon != null) {
				ViewUtils.removeFromPane(relatedIcon.dataSetIconParent(), relatedIcon.dataSetIcon());
				ViewUtils.removeFromPane(relatedIcon.gridBarIconParent(), relatedIcon.gridBarIcon());
			}

			ViewUtils.removeFromScene(dataSetRootAnchorPane);
			DataSetRelation datasetRelation = viewModel.getDataSetRelation(dataSetHashcode);
			datasetRelation.getRelatedPaneList().clear();
			
			viewModel.getAppContext().removeDataSetHashcode(dataSetHashcode);

			if (dataSetService != null && dataSetService.isRunning()) {
				logger.info("검색중인 서비스가 취소되었습니다.");
				dataSetService.cancel();
			}

		});
	}

	private DataSetRelation removeRelations(int dataSetHashcode) {
		DataSetRelation datasetRelation = viewModel.getAppContext().getDataSetRelation(dataSetHashcode);
		List<RelatedLine> relatedLineList = datasetRelation.getRelatedLineList();

		for (RelatedPane relatedPane : datasetRelation.getRelatedPaneList()) {
			ViewUtils.removeFromScene(relatedPane.relationPane());

			int endPaneKey = relatedPane.endPane().hashCode();
			int startPaneKey = relatedPane.startPane().hashCode();
			int realtionKey = relatedPane.relationPane().hashCode();

			if (dataSetHashcode == endPaneKey) {
				DataSetRelation datasetRelationStart = viewModel.getAppContext().getDataSetRelation(startPaneKey);
				datasetRelationStart.reflashRelatedLine(relatedLineList);
				datasetRelationStart.reflashRelatedPane(dataSetHashcode);
				datasetRelationStart.reflashRelatedPane(realtionKey);

			} else if (dataSetHashcode == startPaneKey) {
				DataSetRelation datasetRelationEnd = viewModel.getAppContext().getDataSetRelation(endPaneKey);
				datasetRelationEnd.reflashRelatedLine(relatedLineList);
				datasetRelationEnd.reflashRelatedPane(dataSetHashcode);
				datasetRelationEnd.reflashRelatedPane(realtionKey);
			}
		}

		for (RelatedLine relatedLine : relatedLineList) {
			ViewUtils.removeFromScene(relatedLine.line());
			ViewUtils.removeFromScene(relatedLine.arrowhead());
		}
		datasetRelation.reflashRelatedLine(relatedLineList);
		return datasetRelation;
	}

	private void initializeDataSetRootPane() {
		dataSetRootAnchorPane.setUserData(viewModel.getDataSetTitle());
		dataSetRootAnchorPane.setOnMousePressed(this::handleMousePressedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseDragged(this::handleMouseDraggedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseClicked(this::handleMouseClickedDataSetAnchorPane);
	}

	private void initializeConditions() {
		// 데이터셋 검색 조건들
		ConditionViewInfo[] conditionInfos = viewModel.getConditionViewInfos();
		LinkUtils.link(conditionInfoLabelVBox, conditionInfos, this::handleMouseClickedConditionLabel);
	}

	private void initializeOpenFunctionViewButton() {
		this.openFunctionViewButton.setOnAction(event -> {
			openFunctionView();
		});
		
	}

	private void initializeColumns() {
		// 데이터셋 항목들
		ColumnInfo[] columnInfos = viewModel.getColumnInfos();
		LinkUtils.link(columInfoLabelVBox, columnInfos, new Executor<Integer>() {

			@Override
			public void execute(Integer index) {
				scrollToColumn(dataSetTableView, index);
			}

			@Override
			public TableViewData getTableViewData() {
				return tableViewData;
			}
		});

		dataSetColumnScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		columInfoLabelVBox.setOnDragOver(event -> {
			//logger.debug("columInfoLabelVBox setOnDragOver");
			
			//아래 코드가 빠진면 setOnDragDropped가 실행이 안됨.
			 event.acceptTransferModes(TransferMode.MOVE);
			event.consume();
	    });
		
		columInfoLabelVBox.setOnDragDropped(event -> {
			logger.debug("columInfoLabelVBox setOnDragDropped");
			
			Dragboard db = event.getDragboard();
	        boolean success = false;
	
	        Node source = (Node)event.getGestureSource();
	        
	        if (db.hasString()) {
	            logger.debug("Dropped: [{}] {}", source.getUserData().getClass().getSimpleName(), db.getString());
	            
	            Node node = event.getPickResult().getIntersectedNode();
	            Label label = (Label) ViewUtils.searchParentNodeWithType(Label.class, node);
	            
	            int position = getColumnLabelPosition(label);
	            
	            FunctionInfo functionInfo = (FunctionInfo)source.getUserData();
				
				Label functionLabel = functionInfo.getFunctionLabel();
				BackgroundEventHandler.bindDragAndRemoveOnBackgroundEvent(
						functionLabel
						, BackgroundEventHandler.DRAG_REMOVE_ON_BACKGROUND_FUNCTION_COLUMN
						) ;
				
				double width = ViewUtils.getRigionalWidth(columInfoLabelVBox);
				if (width > 0) {
					ViewUtils.setWidth(functionLabel, width);
				}
				
				columInfoLabelVBox.getChildren().add(position+1, functionLabel);
	            success = true;
	        }
	        
	        event.setDropCompleted(success);
	        event.consume();
	    });
	}
	
	
	private int getColumnLabelPosition(Label label) {
		ObservableList<Node> list = columInfoLabelVBox.getChildren();
		int i = 0 ;
		for (Node node: list ) {
			if (node == label) {
				return i ;
			}
			i++;
		}
		return i ;
	}

	public TableColumn<Tuple, ?>[] getTableColumns() {
		ObservableList<Node> columnLabels = this.columInfoLabelVBox.getChildren();
		TableColumn<Tuple, ?>[] tableColumnSelected = null;
		TableColumn<Tuple, ?>[] tableColumnAll = null;

		for (Node node : columnLabels) {
			Object object = ((Control) node).getUserData();
			ColumnInfo columnInfo = (ColumnInfo) object;
			if (columnInfo == null)
				continue;

			TableColumn<Tuple, ?> tableColumn = new TableColumn<>(columnInfo.getColumnComment());

			if (columnInfo.isSelected()) {
				tableColumnSelected = ArrayUtil.addArrayOne(tableColumnSelected, tableColumn, TableColumn.class);
			} else {
				tableColumnAll = ArrayUtil.addArrayOne(tableColumnAll, tableColumn, TableColumn.class);
			}

		}

		if (ArrayUtils.isNotEmpty(tableColumnSelected))
			return tableColumnSelected;
		else
			return tableColumnAll;

	}

	public UIConditions getUIConditions() {
		return getUIConditions(null);
	}

	private UIConditions getUIConditions(ConditionInfo[] conditionInfosFromRowSelected) {
		
		viewModel.addDataSetContext();

		UIConditions c = null;
		ConditionInfo[] conditionInfos = null; 
		if ( conditionInfosFromRowSelected != null ) {
			conditionInfos = viewModel.getConditionInfos(conditionInfoLabelVBox);
			conditionInfos = ArrayUtil.mergeArray(conditionInfos, conditionInfosFromRowSelected);
			c = viewModel.getValueBindedConditions(conditionInfos);
			
		} else if (viewModel.haveTargetDataSet()) {
			ConditionInfo[] joinConditionInfos = viewModel.getJoinConditionInfos();
			conditionInfos = viewModel.getConditionInfos(conditionInfoLabelVBox);
			conditionInfos = ArrayUtil.mergeArray(conditionInfos, joinConditionInfos);
			c = viewModel.getValueBindedConditions(conditionInfos);
			
		} else {
			conditionInfos = viewModel.getConditionInfos(conditionInfoLabelVBox);
			c = viewModel.getValueBindedConditions(conditionInfos);
		}
		
		Col<?>[] columns = viewModel.getColumnCols(columInfoLabelVBox);
		if (ArrayUtils.isNotEmpty(columns))
			c.select(columns);

		return c;
	}

	/**
	 * Query를 생성한다.
	 * @return
	 */
	private TableViewData getTableViewData() {

		TableViewData tableViewData = null;

		if (viewModel.hasBaseDataSet()) {
			
			if ( viewModel.isBaseTableRowSelected()) {
				UIConditions conditions = getUIConditions(viewModel.getConditionInfosFromRowSelected());			
				tableViewData = viewModel.getTableViewData(conditions);
				
			} else {
				DataSetView baseDataSetView = viewModel.getBaseDataSetView();
				TableViewData baseTableViewData = baseDataSetView.getTableViewData();
				SubQueryExpression<?> baseQuery = baseTableViewData.getQuery();
				UIConditions conditions = getUIConditions();			
				tableViewData = viewModel.getTableViewData(baseQuery, conditions);
			}

		} else {
			UIConditions conditions = getUIConditions();
			tableViewData = viewModel.getTableViewData(conditions);
		}

		return tableViewData;
	}

	@FXML
	private void searchOrCancel() {
		logger.debug("searchOrCancel.");
		
		if (this.searchOrCancelButton.getText().equals("검색취소")) {
			if (this.dataSetService != null && this.dataSetService.isRunning()) {
				logger.debug("검색 취소가 실행되었습니다.");
				this.dataSetService.cancel();
			}
			return;
		} else {
			this.searchOrCancelButton.setText("검색취소");
		}

		this.dataSetTableView.getItems().clear();
		this.dataSetTableView.refresh();

		try {
			this.tableViewData = getTableViewData();
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		} finally {
			// query 생성후 query 생성관련 context clear
			IntegratedContext.getInstance().clear();
			this.searchOrCancelButton.setText("검색");
		}
		
		this.progressIndicator.visibleProperty().bind((new SimpleBooleanProperty(true)));

		this.dataSetService = new DataSetService();
		this.dataSetService.setTableViewData(this.tableViewData);
		this.dataSetService.start();
		this.dataSetService.setOnSucceeded(this::handleSearchSucceed);
		this.dataSetService.setOnCancelled(this::handleSearchCancel);
		this.dataSetService.setOnFailed(this::handelSearchFail);
		
	}

	private void handleSearchSucceed(WorkerStateEvent event) {
		if (this.dataSetService.getState() == State.CANCELLED)
			return;

		logger.debug("검색이 정상적으로 완료되었습니다.");
		tableViewData.setColumnInfos(viewModel.getColumnInfos());
		LinkUtils.link(dataSetTableView, tableViewData);

		progressIndicator.visibleProperty().bind((new SimpleBooleanProperty(false)));
		searchOrCancelButton.setText("검색");
	}

	private void handleSearchCancel(WorkerStateEvent event) {
		logger.debug("handleSearchCancel.");
		
		
		progressIndicator.visibleProperty().bind((new SimpleBooleanProperty(false)));
		searchOrCancelButton.setText("검색");
		
	}

	private void handelSearchFail(WorkerStateEvent event) {
		logger.debug("검색시 오류가 발생했습니다.");
		searchOrCancelButton.setText("검색");
		progressIndicator.visibleProperty().bind((new SimpleBooleanProperty(false)));

		Throwable exception = dataSetService.getException();
		if (exception != null) {
			System.out.println("Exception occurred: " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	private void scrollToColumn(TableView<Tuple> tableView, int columnIndex) {
		// Ensure the column index is valid
		if (columnIndex >= 0 && columnIndex < tableView.getColumns().size()) {
			// Scroll to the specified column
			tableView.scrollToColumn(tableView.getColumns().get(columnIndex));
		}
	}

	@FXML
	private void close() {
		viewModel.publish(DataSetViewModel.CLOSE_DATASET_NOTIFICATION);
	}

	private boolean existConditionViewOpened() {
		return tmpConditionView != null;
	}

	private void openConditionView(Label conditionLabel, double buttonX, double buttonY) {
		ConditionViewInfo conditionViewInfo = (ConditionViewInfo) conditionLabel.getUserData();

		conditionLabel.setStyle("-fx-background-color: blue;" + "-fx-text-fill: white;" + css_column_label_border);

		conditionViewInfo.setPrevControlButton(tmpConditionControlButton);
		// root Scene를 기준으로 값을 가져온다.

		conditionViewInfo.setControlButton(conditionLabel);
		openConditionView(conditionViewInfo, buttonX, buttonY);

		// open한 다음에는 이전버튼으로 저장한다.
		tmpConditionControlButton = conditionLabel;
	}

	private void openConditionView(ConditionViewInfo conditionViewInfo, double posX, double posY) {

		ConditionScope conditionScope = new ConditionScope();
		conditionScope.setConditionViewInfo(conditionViewInfo);

		ViewTuple<ConditionView, ConditionViewModel> load = FluentViewLoader
				.fxmlView(ConditionView.class, "ConditionText.fxml").providedScopes(conditionScope).load();

		
		if (existConditionViewOpened())
			tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION);

		Parent conditionViewParent = load.getView();
		AnchorPane rootAnchorPane = (AnchorPane) mainRootStage.getScene().getRoot();
		
		DataSetHelper.openDataSet(rootAnchorPane, conditionViewParent, posX, posY);
		
		load.getCodeBehind().getFocusTargetNode().requestFocus();
		
		// Open 한 다음은 ConditionView를 이전 뷰로 임시 저장
		tmpConditionView = conditionViewParent;
		tmpConditionViewModel = load.getViewModel();
	}

	///////////////////////////////////////////////////////////////////////
	// Column Labels
	private void handleMouseClickedColumnLabel(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			Node node = (Node) event.getTarget();
			Label label = null;
			if (node instanceof Label) {
				label = (Label) node;
			} else {
				label = (Label) ViewUtils.searchParentNodeWithType(Label.class, node);
			}

			if (label == null)
				return;

			ColumnInfo columnInfo = (ColumnInfo) label.getUserData();
			if (columnInfo.isSelected()) {
				columnInfo.setSelected(false);
				label.setStyle(css_column_label_border);
			} else {
				columnInfo.setSelected(true);
				label.setStyle("-fx-background-color: blue;" + "-fx-text-fill: white;" + css_column_label_border);
			}

			viewModel.ifLinkedIntegratedGridModifyTableColumn(columnInfo);
		}
	}

	private void handleMouseClickedConditionLabel(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			Node node = (Node) event.getTarget();
			Label conditionLabel = null;
			if (node instanceof Label) {
				conditionLabel = (Label) node;
			} else {
				conditionLabel = (Label) ViewUtils.searchParentNodeWithType(Label.class, node);
			}

			if (conditionLabel == null)
				return;

			double buttonX = conditionLabel.localToScene(conditionLabel.getBoundsInLocal()).getMinX(); //
			double buttonY = conditionLabel.localToScene(conditionLabel.getBoundsInLocal()).getMinY(); //

			if (conditionLabel == tmpConditionControlButton) {
				if (existConditionViewOpened()) {
					tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION);
					tmpConditionView = null; // 같은 조건버튼을 여러번 클릭할 때 필요함.
				} else
					openConditionView(conditionLabel, buttonX, buttonY);

				return;
			}

			openConditionView(conditionLabel, buttonX, buttonY);

		}
	}

	///////////////////////////////////////////////////////////////////////
	// dataSetTitleLabel
	private void handleDummyEvent(MouseEvent event) {
		logger.debug("handleDummyEvent >> {}", EventUtils.getNodeNameWhenMousePressed(event));

		if (tmpConditionViewModel != null)
			tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION,
					tmpConditionControlButton, tmpConditionView);
	}

	private void handleParentEvent(MouseEvent event) {
		EventUtils.fireNodeEvent(event, dataSetRootAnchorPane.getId());

		if (tmpConditionViewModel != null)
			tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION,
					tmpConditionControlButton, tmpConditionView);
	}

	///////////////////////////////////////////////////////////////////////
	// dataSetTitlePane
	private void handleMousePressedTitlePane(MouseEvent event) {
		logger.debug("handleMousePressedTitlePane execute.");

		dataSetRootAnchorPane.toFront();
		xOffsetOfMousePressedTitlePane = event.getSceneX();
		yOffsetOfMousePressedTitlePane = event.getSceneY();

		if (tmpConditionViewModel != null)
			tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION,
					tmpConditionControlButton, tmpConditionView);

		event.consume();
	}

	private void handleMouseDraggedTitlePane(MouseEvent event) {
		logger.debug("handleMouseDraggedTitlePane execute.");

		double deltaX = event.getSceneX() - xOffsetOfMousePressedTitlePane; // X축으로 이전 위치와 현재 위치에서의 이동한 만큼의 차이
		double deltaY = event.getSceneY() - yOffsetOfMousePressedTitlePane; // Y축으로 이전 위치와 현재 위치에서의 이동한 만큼의 차이
		dataSetRootAnchorPane.setLayoutX(dataSetRootAnchorPane.getLayoutX() + deltaX);
		dataSetRootAnchorPane.setLayoutY(dataSetRootAnchorPane.getLayoutY() + deltaY);
		viewModel.moveRelationLine(deltaX, deltaY);

		// important
		xOffsetOfMousePressedTitlePane = event.getSceneX();
		yOffsetOfMousePressedTitlePane = event.getSceneY();

		event.consume();
	}

	private void handleMouseClickedTitlePane(MouseEvent event) {
		logger.debug("handleMouseClickedTitlePane execute.");
		dataSetRootAnchorPane.toFront();

		DataSetRelation dataSetRelation = viewModel.getAppContext()
				.getDataSetRelation(dataSetRootAnchorPane.hashCode());

		viewModel.getMouseEventStatus().remakeRelationLine(dataSetRelation);
		event.consume();
	}

	//////////////////////////////////////////////////////////////////////////
	// dataSetAnchorPane
	private void handleMousePressedDataSetAnchorPane(MouseEvent event) {

		logger.debug("handleMousePressedDataSetAnchorPane execute.");
		dataSetRootAnchorPane.toFront();

		this.initialPressedX = event.getX();
		this.initialPressedY = event.getY();

		this.initialPressedWidth = ((AnchorPane) event.getSource()).getPrefWidth();
		this.initialPressedHeight = ((AnchorPane) event.getSource()).getPrefHeight();

		// 관계포인터의 중앙 정렬을 위해 초기값 설정
		this.relationPointCentersPressed = new RelationPointCenters(topRelationCircle.getCenterX(),
				topRelationCircle.getCenterY(), rightRelationCircle.getCenterX(), rightRelationCircle.getCenterY(),
				bottomRelationCircle.getCenterX(), bottomRelationCircle.getCenterY(), leftRelationCircle.getCenterX(),
				leftRelationCircle.getCenterY());

		// 관계선의 초기 설정값을 저장한다.
		this.relationPointCentersScene = new RelationPointCenters(ViewUtils.getSceneCenterX(topRelationCircle),
				ViewUtils.getSceneCenterY(topRelationCircle), ViewUtils.getSceneCenterX(rightRelationCircle),
				ViewUtils.getSceneCenterY(rightRelationCircle), ViewUtils.getSceneCenterX(bottomRelationCircle),
				ViewUtils.getSceneCenterY(bottomRelationCircle), ViewUtils.getSceneCenterX(leftRelationCircle),
				ViewUtils.getSceneCenterY(leftRelationCircle));

		if (tmpConditionViewModel != null)
			tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION,
					tmpConditionControlButton, tmpConditionView);

		event.consume();
	}

	private void handleMouseDraggedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseDraggedDataSetAnchorPane execute.");
		if (viewModel.getMouseEventStatus().isRelationMode())
			return;

		AnchorPane dataSetPane = (AnchorPane) event.getSource();

		double deltaX_ = event.getX() - this.initialPressedX;
		double deltaY_ = event.getY() - this.initialPressedY;

		// 관계 선을 움직이고
		viewModel.moveRelationLine(this.relationPointCentersScene, ViewUtils.getSceneCenterX(topRelationCircle),
				ViewUtils.getSceneCenterY(topRelationCircle), ViewUtils.getSceneCenterX(rightRelationCircle),
				ViewUtils.getSceneCenterY(rightRelationCircle), ViewUtils.getSceneCenterX(bottomRelationCircle),
				ViewUtils.getSceneCenterY(bottomRelationCircle), ViewUtils.getSceneCenterX(leftRelationCircle),
				ViewUtils.getSceneCenterY(leftRelationCircle));

		// 움직인 후에 관계선의 위치를 저장하고.
		// important position, test rightRelationCircle, 나머지 3개도 처리해야 함.
		this.relationPointCentersScene = new RelationPointCenters(ViewUtils.getSceneCenterX(topRelationCircle),
				ViewUtils.getSceneCenterY(topRelationCircle), ViewUtils.getSceneCenterX(rightRelationCircle),
				ViewUtils.getSceneCenterY(rightRelationCircle), ViewUtils.getSceneCenterX(bottomRelationCircle),
				ViewUtils.getSceneCenterY(bottomRelationCircle), ViewUtils.getSceneCenterX(leftRelationCircle),
				ViewUtils.getSceneCenterY(leftRelationCircle));

		// saveRelationCircleCoordinateXY();

		double newWidth = this.initialPressedWidth + deltaX_;
		double newHeight = this.initialPressedHeight + deltaY_;

		// dataSet pane의 크기를 조절한다.
		dataSetPane.setPrefWidth(newWidth);
		dataSetPane.setPrefHeight(newHeight);
		dataSetPane.setMaxWidth(newWidth);
		dataSetPane.setMaxHeight(newHeight);
		dataSetPane.setMinWidth(newWidth);
		dataSetPane.setMinHeight(newHeight);

		topRelationCircle.setCenterX(relationPointCentersPressed.topCenterX() + deltaX_ / 2);

		bottomRelationCircle.setCenterX(relationPointCentersPressed.bottomCenterX() + deltaX_ / 2);

		leftRelationCircle.setCenterY(relationPointCentersPressed.leftCenterY() + deltaY_ / 2);

		rightRelationCircle.setCenterY(relationPointCentersPressed.rightCenterY() + deltaY_ / 2);

		event.consume();
	}

	private void handleMouseClickedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseClickedDataSetAnchorPane execute.");
		dataSetRootAnchorPane.toFront();
		AnchorPane toolbar = (AnchorPane) mainRootStage.getScene().lookup("#main_toolbar");
		toolbar.toFront();
	}

	private void copySelectedRows(TableView<Tuple> tableView) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();

		StringBuilder copiedText = new StringBuilder();

		for (Tuple rowData : tableView.getSelectionModel().getSelectedItems()) {
			Expression<?>[] colExpres = this.tableViewData.getColumnExpressions();

			for (Expression<?> colExpre : colExpres) {
				copiedText.append(rowData.get(colExpre)).append("	");
			}
			copiedText.append("\n");
		}

		content.putString(copiedText.toString());

		clipboard.setContent(content);
	}
	
	private void openFunctionView() {

		ViewTuple<FunctionView, FunctionViewModel> load = FluentViewLoader
				.fxmlView(FunctionView.class)
				.load();
		Parent functionViewParent = load.getView();
		FunctionView functionView = load.getCodeBehind();
		functionView.setSourceDataSetPane(dataSetRootAnchorPane);
		
		dataSetRootAnchorPane.getChildren().add(functionViewParent);
		functionViewParent.setLayoutX(135);
		//functionViewParent.setLayoutY(80);
		AnchorPane.setTopAnchor(functionViewParent, 80.0);
		AnchorPane.setBottomAnchor(functionViewParent, 80.0);
		
	}
}
