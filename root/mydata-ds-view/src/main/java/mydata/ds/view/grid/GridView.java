package mydata.ds.view.grid;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.data.TableViewData;
import ds.common.util.ArrayUtil;
import ds.data.core.condition.ui.UIConditions;
import ds.data.core.context.IntegratedContext;
import jakarta.inject.Inject;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import mydata.ds.view.dataset.DataSetView;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.util.LinkUtils;
import mydata.ds.view.util.ViewUtils;

public class GridView implements FxmlView<GridViewModel> {

	private static final Logger logger = LoggerFactory.getLogger(GridView.class);

	@FXML
	private AnchorPane gridView;

	@FXML
	private AnchorPane gridBarPane;

	@FXML
	private HBox gridBarHBox;

	@FXML
	private TableView<Tuple> mainTableView;

	@FXML
	private Button IntegratedSearchButton;

	@FXML
	private ProgressIndicator gridViewProgressIndicator;
	
	@Inject
	Stage mainRootStage;
	
	@InjectViewModel
	private GridViewModel viewModel;

	private Label gridArrowButton;

	@InjectContext
	private Context context;
	

	private static final long DOUBLE_CLICK_TIME_THRESHOLD = 300_000_000; // 300 milliseconds

	private long lastClickTime = 0;

	private TableViewData tableViewDataIntegrated ;
	
	public void initialize() {
		logger.info("initialize start!");
		
		gridViewProgressIndicator.setVisible(false);
		
		
		initializeGridButton();
		
		initializeGridBar();
		
		initializeRelationIcon();
	}

	private void initializeRelationIcon() {
		
	}

	private void initializeGridBar() {
		gridBarHBox.setOnMouseClicked(this::handleGridBarHBoxDoubleClicked);
		double tableHeight = ViewUtils.getRigionalHeight(mainTableView);
		double topLineHeight = ViewUtils.getRigionalHeight(gridBarPane);
		AnchorPane.setBottomAnchor(gridView, -tableHeight + topLineHeight);

		setEventTarget(gridBarPane);
	}

	private void initializeGridButton() {
		// up button
		gridArrowButton = getArrowGridButton(1);
		gridBarPane.getChildren().add(gridArrowButton);
		AnchorPane.setRightAnchor(gridArrowButton, 0.0);
		AnchorPane.setTopAnchor(gridArrowButton, 0.0);
		AnchorPane.setBottomAnchor(gridArrowButton, 0.0);

		// movePaneUpDown(gridView, 1);
		gridArrowButton.setOnMouseClicked(this::handleUpButtonClicked);
	}

	@FXML
	public void integratedSearch() {
		gridViewProgressIndicator.visibleProperty().bind((new SimpleBooleanProperty(true)));
		
		mainTableView.getItems().clear();
		mainTableView.refresh();
		
		this.tableViewDataIntegrated = getTableViewDataIntegrated();
		// query 생성후 query 생성관련 context clear
		IntegratedContext.getInstance().clear();
		
        BackgroundTaskService backgroundTaskService = new BackgroundTaskService();
        backgroundTaskService.start();
        backgroundTaskService.setOnSucceeded(this::handelSearchSucces);
        backgroundTaskService.setOnFailed(this::handelSearchFail);
	}

	public void setEventTarget(AnchorPane gridBar) {
	
		gridBar.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, event -> {
	
			logger.debug("gridBar MOUSE_ENTERED_TARGET execute!");
	
			if (viewModel.isNotConnectableDataSet()) {
				return;
			}
	
			int number = gridBarHBox.getChildren().size() + 1;
	
			Pane integratedGridBarIcon = getNumberCircle(number);
			gridBarHBox.getChildren().add(integratedGridBarIcon);
	
			
			
			// DataSetView 안에 IntegratedNumber 삽입
			Pane sourcePane = viewModel.getConnectableDataSetPane();
			int sourcePaneHashcode = sourcePane.hashCode();
			
			Pane integratedDataSetIcon = getNumberCircle(number);
			sourcePane.getChildren().add(integratedDataSetIcon);
			AnchorPane.setBottomAnchor(integratedDataSetIcon, 8.0);
			AnchorPane.setLeftAnchor(integratedDataSetIcon, 8.0);
	
			integratedGridBarIcon.setUserData(sourcePaneHashcode);
			bindDraggableEvents(integratedGridBarIcon);
			
			integratedDataSetIcon.setUserData(sourcePaneHashcode);
			bindDraggableEvents(integratedDataSetIcon);
			/////////////////////////////////////////////////////////////////
			viewModel.putRelatedIcon(//
					sourcePaneHashcode//
					,new RelatedIcon(sourcePane, integratedDataSetIcon, gridBarHBox, integratedGridBarIcon)//
				);//
	
			/// 통합그리드에 컬럼 생성
			DataSetView dataSetView = viewModel.getDataSetView(sourcePaneHashcode);
			TableColumn<Tuple, ?>[] columns = dataSetView.getTableColumns();
			mainTableView.getColumns().addAll(columns);
			
			viewModel.addIntegratedDataSetHashCode(sourcePaneHashcode);
	
			viewModel.clearMouseStatus();
	
			if (Integer.valueOf(gridArrowButton.getUserData().toString()) == 1)
				handleUpButtonClicked(null);
		});
	}

	/**
	 * 
	 * @param updown 0: down, 1: up
	 * @return
	 */
	public Label getArrowGridButton(int updown) {
	
		Group group = getDirectionShape(updown);
		Label label = new Label();
		label.setGraphic(group);
		label.setStyle("-fx-background-color: #2E8B57;");
		label.setUserData(updown);
		return label;
	}

	private void handleGridBarHBoxDoubleClicked(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			if (event.getClickCount() > 1) {
				handleUpButtonClicked(event);
			}

		}
	}

	private void handleUpButtonClicked(MouseEvent event) {
		logger.debug("handleUpButtonPressed start! button arrow{}", gridArrowButton.getUserData());
		if (Integer.valueOf(gridArrowButton.getUserData().toString()) == 1) {
			gridArrowButton.setGraphic(getDirectionShape(-1)); // grid를 올린다.
			gridArrowButton.setUserData(-1);
			movePaneUpDown(gridView, -1);
		} else {
			gridArrowButton.setGraphic(getDirectionShape(1)); // grid를 내린다.
			gridArrowButton.setUserData(1);
			movePaneUpDown(gridView, 1);
		}
	}

	private Group getDirectionShape(int updown) {
		// Create a Circle
		double circleRadius = 17;
		double centerX = 17.0;
		double centerY = 17.0;
		Circle circle = new Circle(centerX, centerY, circleRadius);
		circle.setFill(Color.TRANSPARENT);
		circle.setStroke(Color.WHITE);
		circle.setFill(Color.WHITE);

		// Create a Polygon representing a triangle inside the circle
		double triangleSideLength = 15.0;
		Polygon triangle = new Polygon(centerX, centerY - circleRadius, // Top vertex
				centerX - triangleSideLength, centerY + circleRadius / 2, // Bottom-left vertex
				centerX + triangleSideLength, centerY + circleRadius / 2 // Bottom-right vertex
		);
		triangle.setFill(Color.GREEN);

		// Create a Group to hold the circle and triangle
		Group group = new Group(circle, triangle);

		if (updown == -1) {
			group.setRotate(180);
		}
		return group;
	}

	private Pane getNumberCircle(int number) {
		// Create a Circle
		Circle circle = new Circle(15, Color.rgb(255, 102, 051));

		// Create a Text node with the number
		Text text = new Text(String.valueOf(number));
		text.setFont(Font.font(20)); // Set the font size
		text.setFill(Color.WHITE); // Set the text color

		// Create a StackPane to stack the Circle and Text
		StackPane stackPane = new StackPane(circle, text);
		return stackPane;
	}

	/**
	 * 
	 * @param pane
	 * @param updown 1: up, -1:down
	 */
	private void movePaneUpDown(Pane pane, int updown) {
		double tableHeight = ViewUtils.getRigionalHeight(mainTableView);
		// Create a TranslateTransition for moving the pane down
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);

		// Set the translation on the Y-axis to 100 (move down by 100 points)
		transition.setByY(tableHeight * updown);

		// Play the animation
		transition.play();
	}
	
	private TableViewData getTableViewDataIntegrated() {

		TableViewData tableViewData = null;

		List<Integer> dataSetHashcodeList = viewModel.getAppContextDataSetHashcodeList();
		List<Integer> iDataSetHashcodeList = viewModel.getIntegratedDataSetHashcodeList();
		SubQueryExpression<?> baseQuery = null ;
		SubQueryExpression<?>[] integratedQueries = null ;
		
		DataSetViewModel dataSetViewModel = null ;
		
		for (int hashcode: dataSetHashcodeList) {
			dataSetViewModel = viewModel.getBaseDataSetViewModel(hashcode);  
			DataSetView dataSetView = viewModel.getBaseDataSetView(hashcode);
			
			if (dataSetViewModel.hasBaseDataSet()) {
				UIConditions conditions = dataSetView.getUIConditions();
				tableViewData = dataSetViewModel.getTableViewData(baseQuery, conditions);
				baseQuery = tableViewData.getQuery();
				
			} else {
				UIConditions conditions = dataSetView.getUIConditions();
				tableViewData = dataSetViewModel.getTableViewData(conditions);
				baseQuery = tableViewData.getQuery();
				
			}
			if( iDataSetHashcodeList.contains(hashcode))
				integratedQueries = ArrayUtil.addArrayOne(integratedQueries, baseQuery, SubQueryExpression.class);
		}
		
		tableViewData = dataSetViewModel.getTableViewDataIntegrated(integratedQueries);
		
		return tableViewData;
	}

	private void handelSearchSucces(WorkerStateEvent event) {
		logger.info("handelSearch succeed1");
		LinkUtils.link(this.mainTableView, tableViewDataIntegrated);
		
		gridViewProgressIndicator.visibleProperty().bind((new SimpleBooleanProperty(false)));
	}
	
	private void handelSearchFail(WorkerStateEvent event) {
		
		logger.info("handelSearch fail!");
	}
	
	private void bindDraggableEvents(Node node) {
		
		viewModel.bindDraggableEvents(node);
        
    }
	
	// Service for the background task
    private class BackgroundTaskService extends Service<TableViewData> {
        @Override
        protected Task<TableViewData> createTask() {
            return new Task<>() {
                @Override
                protected TableViewData call() throws InterruptedException {
                	tableViewDataIntegrated.fetch();
                    return tableViewDataIntegrated;
                }
            };
        }
    }
}
