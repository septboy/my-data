package mydata.ds.view.dataset;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.data.TableViewData;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import ds.common.util.NumberUtil;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.condition.ui.UIConditions;
import jakarta.inject.Inject;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import mydata.ds.view.condition.ConditionView;
import mydata.ds.view.condition.ConditionViewInfo;
import mydata.ds.view.condition.ConditionViewModel;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ConditionScope;
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
	private Context context ;
	private Stage dataSetStage;

	private double xOffsetOfMousePressedTitlePane = 0;
	private double yOffsetOfMousePressedTitlePane = 0;
	 
	private double initialPressedX;
    private double initialPressedY;
    private double initialPressedWidth;
    private double initialPressedHeight;
    private Node tmpConditionView ;
    private Control tmpConditionControlButton ;
    private ConditionViewModel tmpConditionViewModel ;
    
    public static final String css_column_label_border = "-fx-border-color: white; -fx-border-width: 1px 0px 1px 0px;";
    
    private MouseEventStatus mouseEventStatus ;

	private RelationPointCenters relationPointCentersPressed;

	private RelationPointCenters relationPointCentersScene;
	
	//테스트
	private double xOffsetOfMousePressedDataSetAnchorPane;

	private double yOffsetOfMousePressedDataSetAnchorPane;

	
    
	public void initialize() {
		
		dataSetTableView.setPlaceholder(new Label("데이터를 검색하세요."));
		
		// 데이터셋 타이틀
		String datasetTitle = viewModel.getDataSetTitle() ;
		LinkUtils.link(dataSetTitleLabel, datasetTitle);
		
		// 데이터셋 항목들
		ColumnInfo[] columnInfos = viewModel.getColumnInfos();
		LinkUtils.link(columInfoLabelVBox, columnInfos, this::handleMouseClickedColumnLabel);
		
		// 데이터셋 검색 조건들
		ConditionViewInfo[] conditionInfos = viewModel.getConditionViewInfos();
		LinkUtils.link(conditionInfoLabelVBox, conditionInfos, this::handleMouseClickedConditionLabel);
		
		dataSetTitlePane.setOnMousePressed(this::handleMousePressedTitlePane);
		dataSetTitlePane.setOnMouseDragged(this::handleMouseDraggedTitlePane);
		dataSetTitlePane.setOnMouseClicked(this::handleMouseClickedTitlePane);
		
		dataSetTitleLabel.setOnMousePressed(this::handleDummyEvent);
		dataSetTitleLabel.setOnMouseDragged(this::handleDummyEvent);
		dataSetTitleLabel.setOnMouseClicked(this::handleDummyEvent);
		
		dataSetTableView.setOnMousePressed(this::handleParentEvent);
		
		viewModel.setDataSetIdNumber(dataSetRootAnchorPane.hashCode());
		dataSetRootAnchorPane.setUserData(datasetTitle);
		dataSetRootAnchorPane.setOnMousePressed(this::handleMousePressedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseDragged(this::handleMouseDraggedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseClicked(this::handleMouseClickedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseReleased(this::handleMouseReleaseDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseDragReleased(this::handleMouseDragReleasedDataSetAnchorPane);
		
		
		dataSetColumnScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		viewModel.subscribe(DataSetViewModel.CLOSE_DATASET_NOTIFICATION, (key, payload) -> {
			if(tmpConditionViewModel != null)
		    	  tmpConditionViewModel.publish(
		    			  ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION
		    			  , tmpConditionControlButton
		    			  , tmpConditionView
		    			  );
			
			((Group)mainRootStage.getScene().getRoot()).getChildren().  remove(dataSetStage);
		});
		
		// 관계 맺기 관련 Event 장착
		mouseEventStatus = viewModel.getMouseEventStatus();
		mouseEventStatus.setScene(mainRootStage.getScene());
		mouseEventStatus.setEventTarget(dataSetRootAnchorPane);
	}

	@FXML
	private void search() {
		UIConditions conditions = getUIConditions();		
		TableViewData tableViewData = viewModel.getTableViewData(conditions);
		LinkUtils.link(dataSetTableView, tableViewData);
		
	}
	
	private UIConditions getUIConditions() {
		UIConditions c = viewModel.getConditions(conditionInfoLabelVBox);
		
		Col<?>[] columns = viewModel.getColumnCols(columInfoLabelVBox);
		if ( ArrayUtils.isNotEmpty(columns) )
			c.select(columns);
		
		return c;
	}

	
	public void setDisplayingStage(Stage openDataSet) {
		this.dataSetStage = openDataSet;
	}

	
	///////////////////////////////////////////////////////////////////////
	// Column Labels
	private void handleMouseClickedColumnLabel(MouseEvent event) {
		if(event.getButton() == MouseButton.PRIMARY ) {
		    Node node = (Node)event.getTarget();
		    Label label = null;
		    if (node instanceof Label) {
				label = (Label)node;
		    } else {
		    	label = (Label)ViewUtils.searchParentNodeWithType(Label.class, node);
		    }
		    
		    if (label == null)
		    	return ;
		    
			ColumnInfo columnInfo = (ColumnInfo)label.getUserData();
			if (columnInfo.isSelected()) {
				columnInfo.setSelected(false);
				label.setStyle(css_column_label_border);
			} else {
				columnInfo.setSelected(true);
				label.setStyle(
			            "-fx-background-color: blue;" +
			            "-fx-text-fill: white;" +
			            css_column_label_border	
			        );
			}
		}
	}
	
	private void handleMouseClickedConditionLabel(MouseEvent event) {
		if(event.getButton() == MouseButton.PRIMARY ) {
		    Node node = (Node)event.getTarget();
		    Label conditionLabel = null;
		    if (node instanceof Label) {
				conditionLabel = (Label)node;
		    } else {
		    	conditionLabel = (Label)ViewUtils.searchParentNodeWithType(Label.class, node);
		    }
		    
		    if (conditionLabel == null)
		    	return ;
		    
		    double buttonX = conditionLabel.localToScene(conditionLabel.getBoundsInLocal()).getMinX(); // 
	        double buttonY = conditionLabel.localToScene(conditionLabel.getBoundsInLocal()).getMinY(); // 
	        
		    if(conditionLabel == tmpConditionControlButton) {
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

	private void openConditionView(Label conditionLabel, double buttonX, double buttonY) {
		ConditionViewInfo conditionViewInfo = (ConditionViewInfo)conditionLabel.getUserData();
		
		conditionLabel.setStyle(
		        "-fx-background-color: blue;" +
		        "-fx-text-fill: white;" +
		        css_column_label_border
		    );

		conditionViewInfo.setPrevControlButton(tmpConditionControlButton);
		// root Scene를 기준으로 값을 가져온다.
		
		conditionViewInfo.setControlButton(conditionLabel);
		openConditionView(conditionViewInfo, buttonX, buttonY);
		
		// open한 다음에는 이전버튼으로 저장한다.
		tmpConditionControlButton = conditionLabel ;
	}
	
	
	///////////////////////////////////////////////////////////////////////
	// dataSetTitleLabel
	private void handleDummyEvent(MouseEvent event) {
		logger.debug("handleDummyEvent >> {}", EventUtils.getNodeNameWhenMousePressed(event));
		
		if(tmpConditionViewModel != null)
	    	  tmpConditionViewModel.publish(
	    			  ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION
	    			  , tmpConditionControlButton
	    			  , tmpConditionView
	    			  );
	}
	
	
	private void handleParentEvent(MouseEvent event) {
		EventUtils.fireNodeEvent(event, dataSetRootAnchorPane.getId());
		
		if(tmpConditionViewModel != null)
	    	  tmpConditionViewModel.publish(
	    			  ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION
	    			  , tmpConditionControlButton
	    			  , tmpConditionView
	    			  );
	}
	
	///////////////////////////////////////////////////////////////////////
	// dataSetTitlePane
	private void handleMousePressedTitlePane(MouseEvent event) {
	  logger.debug("handleMousePressedTitlePane execute.");
	  
	  dataSetRootAnchorPane.toFront();
      xOffsetOfMousePressedTitlePane = event.getSceneX();
      yOffsetOfMousePressedTitlePane = event.getSceneY();
      
      if(tmpConditionViewModel != null)
    	  tmpConditionViewModel.publish(
    			  ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION
    			  , tmpConditionControlButton
    			  , tmpConditionView
    			  );
      
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
		
		DataSetRelation dataSetRelation = viewModel.getAppContext().getDataSetRelation(dataSetRootAnchorPane.hashCode());
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
		 this.relationPointCentersPressed = new RelationPointCenters(
				  topRelationCircle.getCenterX()
				 ,topRelationCircle.getCenterY()
				 ,rightRelationCircle.getCenterX()
				 ,rightRelationCircle.getCenterY()
				 ,bottomRelationCircle.getCenterX()
				 ,bottomRelationCircle.getCenterY()
				 ,leftRelationCircle.getCenterX()
				 ,leftRelationCircle.getCenterY()
				 );
         
		 // 관계선의 초기 설정값을 저장한다.
		 this.relationPointCentersScene = new RelationPointCenters(
				  ViewUtils.getSceneCenterX(topRelationCircle)
				 ,ViewUtils.getSceneCenterY(topRelationCircle)
				 ,ViewUtils.getSceneCenterX(rightRelationCircle)
				 ,ViewUtils.getSceneCenterY(rightRelationCircle)
				 ,ViewUtils.getSceneCenterX(bottomRelationCircle)
				 ,ViewUtils.getSceneCenterY(bottomRelationCircle)
				 ,ViewUtils.getSceneCenterX(leftRelationCircle)
				 ,ViewUtils.getSceneCenterY(leftRelationCircle)
				 );
		 
         if(tmpConditionViewModel != null)
       	  tmpConditionViewModel.publish(
    			  ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION
    			  , tmpConditionControlButton
    			  , tmpConditionView
    			  );

         event.consume();
	}
	
	private void handleMouseDraggedDataSetAnchorPane(MouseEvent event) {
		if (viewModel.getMouseEventStatus().isRelationMode())
			return;
		
		logger.debug("handleMouseDraggedDataSetAnchorPane execute.");
		
		AnchorPane dataSetPane = (AnchorPane) event.getSource();
		
		 double deltaX_ = event.getX() - this.initialPressedX;
         double deltaY_ = event.getY() - this.initialPressedY;

         // 관계 선을 움직이고
         viewModel.moveRelationLine(
        		 this.relationPointCentersScene
        		 ,ViewUtils.getSceneCenterX(topRelationCircle)
				 ,ViewUtils.getSceneCenterY(topRelationCircle)
				 ,ViewUtils.getSceneCenterX(rightRelationCircle)
				 ,ViewUtils.getSceneCenterY(rightRelationCircle)
				 ,ViewUtils.getSceneCenterX(bottomRelationCircle)
				 ,ViewUtils.getSceneCenterY(bottomRelationCircle)
				 ,ViewUtils.getSceneCenterX(leftRelationCircle)
				 ,ViewUtils.getSceneCenterY(leftRelationCircle)
        		 );
         
         // 움직인 후에 관계선의 위치를 저장하고.
         // important position, test rightRelationCircle, 나머지 3개도 처리해야 함.
         this.relationPointCentersScene = new RelationPointCenters(
				  ViewUtils.getSceneCenterX(topRelationCircle)
				 ,ViewUtils.getSceneCenterY(topRelationCircle)
				 ,ViewUtils.getSceneCenterX(rightRelationCircle)
				 ,ViewUtils.getSceneCenterY(rightRelationCircle)
				 ,ViewUtils.getSceneCenterX(bottomRelationCircle)
				 ,ViewUtils.getSceneCenterY(bottomRelationCircle)
				 ,ViewUtils.getSceneCenterX(leftRelationCircle)
				 ,ViewUtils.getSceneCenterY(leftRelationCircle)
				);
		 
         
        // saveRelationCircleCoordinateXY();
         
         double newWidth = this.initialPressedWidth + deltaX_;
         double newHeight = this.initialPressedHeight + deltaY_;

         dataSetPane.setPrefWidth(newWidth);
         dataSetPane.setPrefHeight(newHeight);
         dataSetPane.setMaxWidth(newWidth);
         dataSetPane.setMaxHeight(newHeight);
         dataSetPane.setMinWidth(newWidth);
         dataSetPane.setMinHeight(newHeight);
         
         topRelationCircle.setCenterX(relationPointCentersPressed.topCenterX()+deltaX_/2);
         
         bottomRelationCircle.setCenterX(relationPointCentersPressed.bottomCenterX()+deltaX_/2);
         
         leftRelationCircle.setCenterY(relationPointCentersPressed.leftCenterY()+deltaY_/2);
         
         rightRelationCircle.setCenterY(relationPointCentersPressed.rightCenterY()+deltaY_/2);
         
         event.consume();
	}
	
	private void handleMouseClickedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseClickedDataSetAnchorPane execute.");
		dataSetRootAnchorPane.toFront();
		AnchorPane toolbar = (AnchorPane) mainRootStage.getScene().lookup("#main_toolbar");
		toolbar.toFront();
	}
	
	private void handleMouseReleaseDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseReleaseDataSetAnchorPane execute.");
		
		
	}
	
	private void handleMouseDragReleasedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseDragReleasedDataSetAnchorPane execute.");
		
		
	}
	
	private void openConditionView(ConditionViewInfo conditionViewInfo, double posX, double posY) {
		
		ConditionScope conditionScope = new ConditionScope(); 
		conditionScope.setConditionViewInfo(conditionViewInfo);
		
		ViewTuple<ConditionView, ConditionViewModel> load = FluentViewLoader
				.fxmlView(ConditionView.class, "ConditionText.fxml")
				.providedScopes(conditionScope)
				.load();
		
		if (existConditionViewOpened())
			tmpConditionViewModel.publish(ConditionViewModel.CLOSE_CONDITION_VIEW_NOTIFICATION);
		
		Parent conditionView = load.getView();

		AnchorPane rootAnchorPane = (AnchorPane) mainRootStage.getScene().getRoot();

		DataSetHelper.openDataSet(rootAnchorPane, conditionView, posX, posY );	
		// Open 한 다음은 ConditionView를 이전 뷰로 임시 저장
		tmpConditionView = conditionView ;		
		tmpConditionViewModel = load.getViewModel();
	}

	private boolean existConditionViewOpened() {
		return tmpConditionView != null;
	}

	@FXML
	private void close() {
		 Scene scene = dataSetTitleLabel.getScene();
         if (scene != null) {
             AnchorPane parentPane = (AnchorPane) scene.getRoot();
             parentPane.getChildren().remove(dataSetRootAnchorPane);
         }
	}
}
