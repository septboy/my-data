package mydata.ds.view.dataset;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.data.TableViewData;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import ds.data.core.column.ColumnInfo;
import ds.data.core.condition.ConditionInfo;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mydata.ds.view.condition.ConditionView;
import mydata.ds.view.condition.ConditionViewModel;
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

	private Stage dataSetStage;

	private double xOffset = 0;
	private double yOffset = 0;
	 
	private double initialX;
    private double initialY;
    private double initialWidth;
    private double initialHeight;
    
    private static final String css_column_label_border = "-fx-border-color: white; -fx-border-width: 1px 0px 1px 0px;";
    
	public void initialize() {
		// 데이터셋 타이틀
		String datasetTitle = viewModel.getDataSetTitle() ;
		LinkUtils.link(dataSetTitleLabel, datasetTitle);
		
		// 데이터셋 항목들
		ColumnInfo[] columnInfos = viewModel.getColumnInfos();
		LinkUtils.link(columInfoLabelVBox, columnInfos, this::handleMouseClickedColumnLabel);
		
		// 데이터셋 검색 조건들
		ConditionInfo[] conditionInfos = viewModel.getConditionInfos();
		LinkUtils.link(conditionInfoLabelVBox, conditionInfos, this::handleMouseClickedConditionLabel);
		
		dataSetTitlePane.setOnMousePressed(this::handleMousePressedTitlePane);
		dataSetTitlePane.setOnMouseDragged(this::handleMouseDraggedTitlePane);
		dataSetTitlePane.setOnMouseClicked(this::handleMouseClickedTitlePane);
		
		dataSetTitleLabel.setOnMousePressed(this::handleDummyEvent);
		dataSetTitleLabel.setOnMouseDragged(this::handleDummyEvent);
		dataSetTitleLabel.setOnMouseClicked(this::handleDummyEvent);
		
		dataSetTableView.setOnMousePressed(this::handleParentEvent);
		
		dataSetRootAnchorPane.setOnMousePressed(this::handleMousePressedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseDragged(this::handleMouseDraggedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseClicked(this::handleMouseClickedDataSetAnchorPane);
		
		dataSetColumnScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		viewModel.subscribe(DataSetViewModel.CLOSE_DATASET_NOTIFICATION, (key, payload) -> {
			((Group)mainRootStage.getScene().getRoot()).getChildren().remove(dataSetStage);
		});
		
		
	}

	@FXML
	private void search() {
		Map<String, ?> uiValue = getUIValue();		
		TableViewData tableViewData = viewModel.getTableViewData(uiValue);
		LinkUtils.link(dataSetTableView, tableViewData);
		
	}
	
	private Map<String, ?> getUIValue() {
		Map<String, ?> map = new HashMap<>();
		return map;
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
		    Label label = null;
		    if (node instanceof Label) {
				label = (Label)node;
		    } else {
		    	label = (Label)ViewUtils.searchParentNodeWithType(Label.class, node);
		    }
		    
		    if (label == null)
		    	return ;
		    
		    ConditionInfo conditionInfo = (ConditionInfo)label.getUserData();
			if (conditionInfo.isSelected()) {
				conditionInfo.setSelected(false);
				label.setStyle(css_column_label_border);
				closeInputComponent(conditionInfo);
			} else {
				conditionInfo.setSelected(true);
				label.setStyle(
			            "-fx-background-color: blue;" +
			            "-fx-text-fill: white;" +
			            css_column_label_border
			        );

				// root Scene를 기준으로 값을 가져온다.
				double buttonX = label.localToScene(label.getBoundsInLocal()).getMinX(); // 
		        double buttonY = label.localToScene(label.getBoundsInLocal()).getMinY(); // 
				openIputComponent(conditionInfo, buttonX, buttonY);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	// dataSetTitleLabel
	private void handleDummyEvent(MouseEvent event) {
		logger.debug(EventUtils.getNodeNameWhenMousePressed(event));
	}
	
	
	private void handleParentEvent(MouseEvent event) {
		EventUtils.fireNodeEvent(event, dataSetRootAnchorPane.getId());
	}
	
	///////////////////////////////////////////////////////////////////////
	// dataSetTitlePane
	private void handleMousePressedTitlePane(MouseEvent event) {
	  logger.debug("handleMousePressedTitlePane execute.");
	  
	  dataSetRootAnchorPane.toFront();
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
	}
	
	private void handleMouseDraggedTitlePane(MouseEvent event) {
	  logger.debug("handleMouseDraggedTitlePane execute.");
		
      double deltaX = event.getSceneX() - xOffset;
      double deltaY = event.getSceneY() - yOffset;
      dataSetRootAnchorPane.setLayoutX(dataSetRootAnchorPane.getLayoutX() + deltaX);
      dataSetRootAnchorPane.setLayoutY(dataSetRootAnchorPane.getLayoutY() + deltaY);
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();		
	}
	
	private void handleMouseClickedTitlePane(MouseEvent event) {
		logger.debug("handleMouseClickedTitlePane execute.");
		dataSetRootAnchorPane.toFront();

	}
	
	//////////////////////////////////////////////////////////////////////////
	// dataSetAnchorPane
	private void handleMousePressedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMousePressedDataSetAnchorPane execute.");
		 dataSetRootAnchorPane.toFront();
		 
		 initialX = event.getX();
         initialY = event.getY();
         initialWidth = ((AnchorPane) event.getSource()).getPrefWidth();
         initialHeight = ((AnchorPane) event.getSource()).getPrefHeight();
	}
	
	private void handleMouseDraggedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseDraggedDataSetAnchorPane execute.");
		 double deltaX_ = event.getX() - initialX;
         double deltaY_ = event.getY() - initialY;

         double newWidth = initialWidth + deltaX_;
         double newHeight = initialHeight + deltaY_;

         ((AnchorPane) event.getSource()).setPrefWidth(newWidth);
         ((AnchorPane) event.getSource()).setMaxWidth(newWidth);
         ((AnchorPane) event.getSource()).setPrefHeight(newHeight);
         ((AnchorPane) event.getSource()).setMaxHeight(newHeight);
	}
	
	private void handleMouseClickedDataSetAnchorPane(MouseEvent event) {
		logger.debug("handleMouseClickedDataSetAnchorPane execute.");
		dataSetRootAnchorPane.toFront();
		AnchorPane toolbar = (AnchorPane) mainRootStage.getScene().lookup("#main_toolbar");
		toolbar.toFront();
	}
	
	
	private void openIputComponent(ConditionInfo conditionInfo, double posX, double posY) {
		
		ViewTuple<ConditionView, ConditionViewModel> load = FluentViewLoader
				.fxmlView(ConditionView.class, "ConditionText.fxml")
				.load();
		
		Parent conditionView = load.getView();
		AnchorPane rootAnchorPane = (AnchorPane) mainRootStage.getScene().getRoot();

		DataSetHelper.openDataSet(rootAnchorPane, conditionView, posX, posY );	
		
	}

	private void closeInputComponent(ConditionInfo conditionInfo) {
		
		
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
