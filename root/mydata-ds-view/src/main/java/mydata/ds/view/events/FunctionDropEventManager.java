package mydata.ds.view.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ds.data.core.column.ColumnInfo;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import mydata.ds.view.util.ViewUtils;

public class FunctionDropEventManager {

	private static final Logger logger = LoggerFactory.getLogger(FunctionDropEventManager.class);

	public static final String FUNCTION_PARAMETER_LABEL_DRAG = "FUNCTION_PARAMETER_LABEL_DRAG";
	
	private boolean isExitedFromHbox = false ;
	private boolean isEnterdToHbox = false ;
	
	private HBox hBox;

	
	public void setParameterWrapperHBox(HBox hBox) {
		this.hBox = hBox ;
		
	}

	public void initialize() {
		
		hBox.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	
            	String dragBoardString = event.getDragboard().getString();
                if (event.getGestureSource() != hBox && event.getDragboard().hasString()) {
                	if ( dragBoardString.equals(ColumnEventHandler.COLUM_JOIN_SELECTED)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                	}
                }

                event.consume();
            }
        });

		hBox.setOnDragDropped((DragEvent event) -> {
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
            	columnInfo.setObject(hBox);
            	Label columnLabel = getColumnLabel(columnInfo);
            	initializeFunctionLabelDropEvent(columnLabel);
            	
            	hBox.setAlignment(Pos.CENTER);
            	hBox.getChildren().add(columnLabel);
            	hBox.setMargin(columnLabel, new Insets(10.0, 5.0, 10.0, 5.0));
            	
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            
            event.consume();
        });
		
		hBox.setOnDragExited((DragEvent event) -> {
			logger.debug("DragExited!");
			isExitedFromHbox = true ;
			isEnterdToHbox  = false ;
		});
		
		hBox.setOnDragEntered((DragEvent event) -> {
			logger.debug("DragEntered!");
			isEnterdToHbox  = true ;
			isExitedFromHbox = false ;
		});
	}

	protected Label getColumnLabel(ColumnInfo columnInfo) {
		String columnName = columnInfo.getColumnComment();
		Label fuctionParamLabel = new Label(columnName);
		fuctionParamLabel.setUserData(columnInfo);
		fuctionParamLabel.setAlignment(javafx.geometry.Pos.CENTER);
		fuctionParamLabel.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
		ViewUtils.setWidth(fuctionParamLabel, 80.0);
		ViewUtils.setHeight(fuctionParamLabel, 25.0);
		fuctionParamLabel.setStyle("-fx-background-color: orange;-fx-background-radius: 5;");
		fuctionParamLabel.setTextFill(Color.web("white"));

		// Set the font for the label
		Font font = Font.font("Arial Rounded MT Bold",FontWeight.BOLD, 14.0);
		fuctionParamLabel.setFont(font);
		
		return fuctionParamLabel;
	}
	
	public void initializeHBoxDragAndDropEvent(HBox hBox) {
		FunctionDropEventManager functionDropEventManager = new FunctionDropEventManager();
		functionDropEventManager.setParameterWrapperHBox(hBox);
		functionDropEventManager.initialize();
	}
	
	private void initializeFunctionLabelDropEvent(Label label) {

		label.addEventHandler(MouseDragEvent. DRAG_DETECTED, event -> {
			logger.debug("parameter label dreg detected!");

            WritableImage snapshot = label.snapshot(null, null);            
            
            ClipboardContent content = new ClipboardContent();
            content.putString(FUNCTION_PARAMETER_LABEL_DRAG);

            Dragboard db = label.startDragAndDrop(TransferMode.MOVE);            
            db.setContent(content);
            db.setDragView(snapshot);
            db.setDragViewOffsetX(snapshot.getWidth() / 2);
            db.setDragViewOffsetY(-snapshot.getHeight() / 2);

            event.consume();
            
		});
		
	}
}
