package mydata.ds.view.dataset;

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
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mydata.ds.view.util.LinkUtils;
import mydata.ds.view.util.ViewUtils;

public class DataSetView implements FxmlView<DataSetViewModel> {

	@FXML
	public TableView<Tuple> dataSetTableView;
	
	@FXML
	public AnchorPane dataSetAnchorPane;
	
	@FXML
	public Pane dataSetTitlePane;
	
	@FXML
	public Button btn;
	
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
		
		dataSetAnchorPane.setOnMousePressed(this::handleMousePressedDataSetAnchorPane);
		dataSetAnchorPane.setOnMouseDragged(this::handleMouseDraggedDataSetAnchorPane);
		dataSetAnchorPane.setOnMouseClicked(this::handleMouseClickedDataSetAnchorPane);
		
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
	  dataSetAnchorPane.toFront();
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
	}
	
	private void handleMouseDraggedTitlePane(MouseEvent event) {
      double deltaX = event.getSceneX() - xOffset;
      double deltaY = event.getSceneY() - yOffset;
      dataSetAnchorPane.setLayoutX(dataSetAnchorPane.getLayoutX() + deltaX);
      dataSetAnchorPane.setLayoutY(dataSetAnchorPane.getLayoutY() + deltaY);
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();		
	}
	
	private void handleMouseClickedTitlePane(MouseEvent event) {
		dataSetAnchorPane.toFront();
		
		if (event.getClickCount() == 2) {
			 Scene scene = dataSetTitlePane.getScene();
            if (scene != null) {
                AnchorPane parentPane = (AnchorPane) scene.getRoot();
                parentPane.getChildren().remove(dataSetAnchorPane);
            }
        } 

	}
	
	//////////////////////////////////////////////////////////////////////////
	// dataSetAnchorPane
	private void handleMousePressedDataSetAnchorPane(MouseEvent event) {
		 dataSetAnchorPane.toFront();
		 
		 initialX = event.getX();
         initialY = event.getY();
         initialWidth = ((AnchorPane) event.getSource()).getPrefWidth();
         initialHeight = ((AnchorPane) event.getSource()).getPrefHeight();
	}
	
	private void handleMouseDraggedDataSetAnchorPane(MouseEvent event) {
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
		dataSetAnchorPane.toFront();
	}
	
	@FXML
	private void search() {
				
		SubQueryExpression<?> query = viewModel.getDataSetService().getEmrTerm();
		
		TableViewData tableViewData = ViewUtils.getTableViewData(query);
		
		LinkUtils.link(dataSetTableView, tableViewData);
	}
}
