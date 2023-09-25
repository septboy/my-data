package mydata.ds.view.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventTarget;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import mydata.ds.view.scopes.AppContext;

public class MouseEventStatus {

	private static final Logger logger = LoggerFactory.getLogger(MouseEventStatus.class);

	private static final long duringTimeForRelation = 2000L;
	private Timeline timeline;
	private boolean isMousePressed = false;
	private boolean isMouseDragged = false;
	private long mousePressedTime;
	private AnchorPane dataSetRootAnchorPaneSource;
	private AnchorPane dataSetRootAnchorPaneTarget;
	private Scene scene;
	private boolean isRelationMode;

	private boolean isMouseDraggedThenReleased = false;

	private AppContext appContext;

	public MouseEventStatus(AppContext appContext) {
		this.appContext = appContext;
		this.timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> checkMousePressDuration()));
		this.timeline.setCycleCount(Timeline.INDEFINITE);
	}

	public void setEventTarget(AnchorPane dataSetRootAnchorPane) {

		dataSetRootAnchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			logger.debug("MOUSE_PRESSED DataSet is {}", dataSetRootAnchorPane.getUserData());
			if (event.getButton() == MouseButton.PRIMARY) {
				this.isMousePressed = true;
				this.mousePressedTime = System.currentTimeMillis();
				timeline.play();
			}
		});

		dataSetRootAnchorPane.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
			if (!this.isRelationMode) {
				statusClear();
				return;
			}

			logger.debug("DRAG_DETECTED DataSet is {}", dataSetRootAnchorPane.getUserData());
			dataSetRootAnchorPaneSource = dataSetRootAnchorPane;
		});

		dataSetRootAnchorPane.addEventFilter(MouseDragEvent.MOUSE_DRAGGED, event -> {
			if (!this.isRelationMode) {
				statusClear();
				return;
			}
			// logger.debug("MOUSE_DRAGGED DataSet is {}",
			// dataSetRootAnchorPane.getUserData());
			this.isMouseDragged = true;
		});

		dataSetRootAnchorPane.addEventFilter(MouseDragEvent.MOUSE_RELEASED, event -> {
			if (!this.isRelationMode) {
				statusClear();
				return;
			}

			logger.debug("MOUSE_RELEASED DataSet is {}", dataSetRootAnchorPane.getUserData());
			if (event.getButton() == MouseButton.PRIMARY) {
				if (this.isMouseDragged)
					this.isMouseDraggedThenReleased = true;

				this.isMousePressed = false;
				this.isMouseDragged = false;

			}
		});

		dataSetRootAnchorPane.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, event -> {
			if (!this.isRelationMode) {
				statusClear();
				return;
			}

			if (!this.isMouseDraggedThenReleased) {
				statusClear();
				return;
			}

			EventTarget eventTarget = event.getTarget();
			if (eventTarget instanceof AnchorPane) {
				String id = ((AnchorPane) eventTarget).getId();
				if (id != null && id.equals("dataSetRootAnchorPane")) {

					if (dataSetRootAnchorPaneSource != event.getTarget()) {
						dataSetRootAnchorPaneTarget = (AnchorPane) eventTarget;
						logger.debug("MOUSE_ENTERED_TARGET eventTarget {} is saved !!",
								dataSetRootAnchorPaneTarget.getUserData());

						javafx.scene.shape.Line line = new javafx.scene.shape.Line();

						Circle fromCircle = findCircleById(dataSetRootAnchorPaneSource, "rightRelationCircle");
						Circle toCircle = findCircleById(dataSetRootAnchorPaneTarget, "leftRelationCircle");

						double fromCircleX = fromCircle.localToScene(fromCircle.getBoundsInLocal()).getCenterX(); //
						double fromCircleY = fromCircle.localToScene(fromCircle.getBoundsInLocal()).getCenterY(); //

						double toCircleX = toCircle.localToScene(toCircle.getBoundsInLocal()).getCenterX(); //
						double toCircleY = toCircle.localToScene(toCircle.getBoundsInLocal()).getCenterY(); //

						if (fromCircle != null && toCircle != null) {
							line.setStartX(fromCircleX);// rightCircle.getLayoutX()
							line.setStartY(fromCircleY);// rightCircle.getLayoutY()
							line.setEndX(toCircleX);// leftCircle.getLayoutX()
							line.setEndY(toCircleY);// leftCircle.getLayoutX()
							line.setStroke(Color.GREEN);
							// Set the stroke type to DOTTED
							line.setStrokeType(StrokeType.CENTERED);
							// Set the line cap to ROUND for better dot appearance
							line.setStrokeLineCap(StrokeLineCap.ROUND);
							// Set the dot pattern (5 pixels on, 5 pixels off)
							line.getStrokeDashArray().addAll(5.0, 5.0);

							// Create an arrowhead polygon
							Polygon arrowhead = new Polygon( //
									0.0, 0.0, // // Vertex 1 (x, y)
									15.0, 7.5, // Vertex 2 (x, y)
									0.0, 15.0 // Vertex 3 (x, y)
							);
							arrowhead.setFill(Color.GREEN);

							// Calculate the rotation angle for the arrowhead
							double angle = Math.atan2(//
									(toCircleY - fromCircleY), //
									(toCircleX - fromCircleX)//
							);//
							angle = Math.toDegrees(angle);

							// Position the arrowhead at the end of the line
							arrowhead.setLayoutX(toCircleX - 7.5);
							arrowhead.setLayoutY(toCircleY - 7.5);

							// Rotate the arrowhead
							arrowhead.setRotate(angle);

							AnchorPane parentPane = (AnchorPane) scene.getRoot();
							parentPane.getChildren().addAll(line, arrowhead);
							line.toFront();
							this.appContext.getDataSetRelation(dataSetRootAnchorPaneSource.hashCode())
									.setRelationLine(CirclePos.Right, 0, line, arrowhead); // start
							this.appContext.getDataSetRelation(dataSetRootAnchorPaneTarget.hashCode())
									.setRelationLine(CirclePos.Left, 1, line, arrowhead); // end
						}

						statusClear();
					}
					event.consume();

				}
			}
		});
	}

	private void checkMousePressDuration() {
		if (this.isMousePressed) {
			long currentTime = System.currentTimeMillis();
			if (this.isMouseDragged)
				currentTime = 0L;

			if (currentTime - mousePressedTime >= duringTimeForRelation) {
				scene.setCursor(getCustomCursor());
				this.isRelationMode = true;
			}
		} else {
			scene.setCursor(Cursor.DEFAULT);
			this.timeline.stop();
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
		if (this.scene == null)
			this.scene = scene;
	}

	public boolean isRelationMode() {
		return this.isRelationMode;
	}

	private Circle findCircleById(Pane anchorPane, String id) {
		for (javafx.scene.Node node : anchorPane.getChildren()) {
			if (node instanceof Circle && node.getId() != null && node.getId().equals(id)) {
				return (Circle) node;
			}
		}
		return null;
	}

	private void statusClear() {
		this.isMousePressed = false;
		this.isMouseDragged = false;
		this.mousePressedTime = 0L;
		this.dataSetRootAnchorPaneSource = null;
		this.dataSetRootAnchorPaneTarget = null;
		this.isRelationMode = false;
		this.isMouseDraggedThenReleased = false;
	}
}
