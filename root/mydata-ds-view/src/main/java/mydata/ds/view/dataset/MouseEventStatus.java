package mydata.ds.view.dataset;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class MouseEventStatus {
    
	private static final long duringTimeForRelation = 2000L;
	private Timeline timeline ; 
    private boolean isMousePressed = false;
    private boolean isMouseDragged = false ;
    private long mousePressedTime;
    private AnchorPane dataSetRootAnchorPaneTarget ;
    private Scene scene;
	private boolean isRelationMode; 
    
    public MouseEventStatus() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> checkMousePressDuration()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }
    

	public void setEventTarget(AnchorPane dataSetRootAnchorPane) {
		dataSetRootAnchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
            	this.isMousePressed = true;
                this.mousePressedTime = System.currentTimeMillis();
                dataSetRootAnchorPaneTarget = dataSetRootAnchorPane;
                timeline.play();
            }
        });
		
		dataSetRootAnchorPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
            	this.isMousePressed = false;
            	this.isMouseDragged = false;
            	dataSetRootAnchorPane.setDisable(false);
            }
        });
		
		dataSetRootAnchorPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			this.isMouseDragged = true ;
        });
        
	}

	private void checkMousePressDuration() {
        if (this.isMousePressed) {
            long currentTime = System.currentTimeMillis();
            if(this.isMouseDragged)
            	currentTime = 0L;
            
            if (currentTime - mousePressedTime >= duringTimeForRelation) {
                scene.setCursor(getCustomCursor());
                this.isRelationMode = true ;
                dataSetRootAnchorPaneTarget.setDisable(true);
            }
        } else {
        	scene.setCursor(Cursor.DEFAULT);
        	this.timeline.stop();
        	dataSetRootAnchorPaneTarget.setDisable(false);
        	this.isRelationMode = false;
        }
        
    }
	
	public ImageCursor getCustomCursor() {
		Image cursorImage = new Image("/image/grid-ic.png");

		double hotspotX = cursorImage.getWidth() / 2;
		double hotspotY = cursorImage.getHeight() / 2;

		// Create a custom cursor
		ImageCursor customCursor = new ImageCursor(cursorImage, hotspotX, hotspotY);
		return customCursor;

	}

	public void setScene(Scene scene) {
		if(this.scene == null)
			this.scene = scene;
	}


	public boolean isRelationMode() {
		return this.isRelationMode;
	}
}
