package mydata.ds.view.dataset;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.data.TableViewData;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mydata.ds.view.util.LinkUtils;
import mydata.ds.view.util.ViewUtils;

public class DataSetView implements FxmlView<DataSetViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(DataSetView.class);
	
	@FXML
	public TableView<Tuple> dataSetTableView;
	
	@FXML
	public AnchorPane dataSetRootAnchorPane;
	
	@FXML
	public AnchorPane dataSetTitlePane;
	
	@Inject
	Stage primaryStage;
	
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
    
	public void initialize() {
		
		
		dataSetTitlePane.setOnMousePressed(this::handleMousePressedTitlePane);
		dataSetTitlePane.setOnMouseDragged(this::handleMouseDraggedTitlePane);
		dataSetTitlePane.setOnMouseClicked(this::handleMouseClickedTitlePane);
		
		dataSetRootAnchorPane.setOnMousePressed(this::handleMousePressedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseDragged(this::handleMouseDraggedDataSetAnchorPane);
		dataSetRootAnchorPane.setOnMouseClicked(this::handleMouseClickedDataSetAnchorPane);
		
		viewModel.subscribe(DataSetViewModel.CLOSE_DATASET_NOTIFICATION, (key, payload) -> {
			dataSetStage.close();
		});
		
		
	}

	public void setDisplayingStage(Stage openDataSet) {
		this.dataSetStage = openDataSet;
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
	}
	
	@FXML
	private void search() {
		Map<String, ?> uiValue = getUIValue();		
		SubQueryExpression<?> query = viewModel.getDataSetService().getQuerySearch(uiValue);
		
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		
		LinkUtils.link(dataSetTableView, tableViewData);
	}
	
	private Map<String, ?> getUIValue() {
		Map<String, ?> map = new HashMap<>();
		return map;
	}

	@FXML
	private void close() {
		 Scene scene = dataSetTitlePane.getScene();
         if (scene != null) {
             AnchorPane parentPane = (AnchorPane) scene.getRoot();
             parentPane.getChildren().remove(dataSetRootAnchorPane);
         }
	}
}
