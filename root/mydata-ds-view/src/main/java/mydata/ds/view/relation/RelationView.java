package mydata.ds.view.relation;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import ds.data.core.column.C;
import ds.data.core.column.Col;
import ds.data.core.column.ColumnInfo;
import ds.data.core.column.ColumnType;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mydata.ds.view.events.BackgroundEventHandler;
import mydata.ds.view.events.ColumnEventHandler;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.util.ViewUtils;

public class RelationView implements FxmlView<RelationViewModel> {

	@FXML
	private AnchorPane relationViewPain;

	@FXML
	private AnchorPane relationTitle ;
	
	@FXML
	private VBox relationViewBox ;
	
	@FXML
	private Label relationPatnoLabel ;
	
	@InjectViewModel
	private RelationViewModel viewModel;

	@Inject
	private Stage primaryStage;

	public void initialize() {
		int relationHashcode = relationViewPain.hashCode();
		viewModel.setRelationHashCode(relationHashcode);
		
		initializeCloseButton();
		initializeRelationDragEvent();
		
		viewModel.putRelationViewModel(relationHashcode, viewModel);
		viewModel.putRelationView(relationHashcode, this);
		
		
		RelationInfo relationPatno = new RelationInfo("patno", ColumnType.String, "환자번호", C.s("patno"));
		viewModel.addRelationColumnInfo(relationPatno);
		
	}

	private void initializeCloseButton() {
		Button closeButton = ViewUtils.getImageButton("/image/close-x.png", 21);
		relationTitle.getChildren().add(closeButton);
		AnchorPane.setLeftAnchor(closeButton, 0.0);
		// 버튼의 배경색을 relationView title의 배경색과 같게 한다.
		closeButton.setStyle("-fx-background-color: #8b0000;");
		
		closeButton.setOnAction(this::handleEventCloseButton);
	}

	private void initializeRelationDragEvent() {
		relationViewPain.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	
            	String dragBoardString = event.getDragboard().getString();
                if (event.getGestureSource() != relationViewPain && event.getDragboard().hasString()) {
                	if ( dragBoardString.equals(ColumnEventHandler.COLUM_JOIN_SELECTED)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                	}
                }

                event.consume();
            }
        });

		relationViewPain.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
            	System.out.println("Dropped: " + db.getString());
            	
            	Object eventSourceObj = event.getGestureSource();
            	if (! (eventSourceObj instanceof Label) ) {
            		event.setDropCompleted(false);
            		event.consume();
            		return ;
            	}
            		
            	Label sourceLabel = (Label)eventSourceObj;
            	ColumnInfo columnInfo = (ColumnInfo)sourceLabel.getUserData();
            	
            	RelationInfo relationColumnInfo = new RelationInfo( //
            			columnInfo.getColumnName()      //
            			, columnInfo.getColumnType()    //
            			, columnInfo.getColumnComment() //
        			);//
            	relationColumnInfo.setRelationHashcode(viewModel.getRelationHashCode());
            	
            	Label relationColumnLabel = getRelationColumnLabel(relationColumnInfo);
            	// JoinOn 값을 생성하기 위한 데이터 저장
            	viewModel.addRelationColumnInfo(relationColumnInfo); //
            	
            	BackgroundEventHandler.bindDragAndRemoveOnBackgroundEvent(
            			relationColumnLabel
            			, BackgroundEventHandler.DRAG_REMOVE_ON_BACKGROUND_RELATION_COLUMN
            			) ;
                relationViewBox.getChildren().add(relationColumnLabel);
            	
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
	}

	private Label getRelationColumnLabel(RelationInfo relationColumnInfo) {
		String columnName = relationColumnInfo.getColumnComment();
		Label relationColumnLabel = new Label(columnName);
		relationColumnLabel.setUserData(relationColumnInfo);
		relationColumnLabel.setAlignment(javafx.geometry.Pos.CENTER);
		relationColumnLabel.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
		relationColumnLabel.setMaxHeight(23.0);
		relationColumnLabel.setMaxWidth(Double.MAX_VALUE);
		relationColumnLabel.setMinHeight(23.0);
		relationColumnLabel.setStyle("-fx-background-color: #d2b48c;");
		relationColumnLabel.setTextFill(Color.web("#40424a"));

		// Set the font for the label
		Font font = new Font("Arial Rounded MT Bold", 12.0);
		relationColumnLabel.setFont(font);

		// Set margin for the label using VBox.margin
		VBox.setMargin(relationColumnLabel, new Insets(0.0, 1.0, 1.0, 1.0));
		return relationColumnLabel;
	}

	private void handleEventCloseButton(ActionEvent event) {
		viewModel.closeRelationView(relationViewPain);
	}
	
	public Pane getRelationViewBox() {
		return this.relationViewBox;
	}
}
