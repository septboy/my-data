package mydata.ds.view.events;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.internal.viewloader.View;
import jakarta.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventTarget;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import mydata.ds.view.condition.ConditionView;
import mydata.ds.view.condition.ConditionViewInfo;
import mydata.ds.view.condition.ConditionViewModel;
import mydata.ds.view.dataset.CirclePair;
import mydata.ds.view.dataset.DataSetRelation;
import mydata.ds.view.dataset.DataSetViewModel;
import mydata.ds.view.dataset.RelatedLine;
import mydata.ds.view.dataset.RelatedPane;
import mydata.ds.view.dataset.record.RelationHashCodePair;
import mydata.ds.view.relation.RelationView;
import mydata.ds.view.relation.RelationViewModel;
import mydata.ds.view.scopes.AppContext;
import mydata.ds.view.scopes.ConditionScope;
import mydata.ds.view.util.DataSetHelper;
import mydata.ds.view.util.ViewUtils;

public class DataSetEventHander {

	private static final Logger logger = LoggerFactory.getLogger(DataSetEventHander.class);

	private static final long duringTimeForRelation = 1300L;
	private Timeline timeline;
	private boolean isMousePressed = false;
	private boolean isMouseDragged = false;
	private long mousePressedTime;
	private AnchorPane dataSetRootAnchorPaneSource;
	private AnchorPane dataSetRootAnchorPaneTarget;
	private Scene scene;
	private boolean isRelatingMode;

	private boolean isMouseDraggedThenReleased = false;

	private AppContext appContext;

	public DataSetEventHander(AppContext appContext) {
		this.appContext = appContext;
		this.timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> checkMousePressDuration()));
		this.timeline.setCycleCount(Timeline.INDEFINITE);
	}

	public void setEventTarget(AnchorPane dataSetRootAnchorPane) {

		dataSetRootAnchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			logger.debug("dataSetRootAnchorPane MOUSE_PRESSED DataSet is {}", dataSetRootAnchorPane.getUserData());
			if (event.getButton() == MouseButton.PRIMARY) {
				this.isMousePressed = true;
				this.mousePressedTime = System.currentTimeMillis();
				timeline.play();
			}
			event.consume();
		});

		dataSetRootAnchorPane.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
			if (!this.isRelatingMode) {
				statusClear();
				return;
			}

			logger.debug("DRAG_DETECTED DataSet is {}", dataSetRootAnchorPane.getUserData());
			dataSetRootAnchorPaneSource = dataSetRootAnchorPane;
		});

		dataSetRootAnchorPane.addEventFilter(MouseDragEvent.MOUSE_DRAGGED, event -> {
			logger.debug("MOUSE_DRAGGED DataSet is {}",dataSetRootAnchorPane.getUserData());
			if (!this.isRelatingMode) {
				statusClear();
				return;
			}
			
			this.isMouseDragged = true;
		});

				
		dataSetRootAnchorPane.addEventFilter(MouseDragEvent.MOUSE_RELEASED, event -> {
			if (!this.isRelatingMode) {
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

		Node backgroundNode = this.scene.lookup("#appBackground");
		
		backgroundNode.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET,this::handleDataSetMouseEnterdTarget);
		
		dataSetRootAnchorPane.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET,this::handleDataSetMouseEnterdTarget);
	}

	private void handleDataSetMouseEnterdTarget(MouseEvent event) {
		Node eventTarget = (Node)event.getTarget();
		
		if (!this.isRelatingMode) {
			statusClear();
			event.consume();
			return;
		}

		if (!this.isMouseDraggedThenReleased) {
			statusClear();
			event.consume();
			return;
		}

		// 데이터셋 관계 맺을 때 처리
		if (eventTarget instanceof AnchorPane && eventTarget.getId().equals("dataSetRootAnchorPane")) {

			if (dataSetRootAnchorPaneSource != event.getTarget()) {
				dataSetRootAnchorPaneTarget = (AnchorPane) eventTarget;
				logger.debug("MOUSE_ENTERED_TARGET eventTarget {} is saved !!",
						dataSetRootAnchorPaneTarget.getUserData());

				AnchorPane relationPain = (AnchorPane)ViewUtils.openView(RelationView.class);
				
				makeRelationLine(dataSetRootAnchorPaneSource, dataSetRootAnchorPaneTarget, relationPain);
				
				int sourcePaneHashcode = dataSetRootAnchorPaneSource.hashCode();
				int targetPaneHashcode = dataSetRootAnchorPaneTarget.hashCode();
				int relationPainHashcode = relationPain.hashCode();

				DataSetViewModel baseDataSetViewModel = appContext.getDataSetViewModel(sourcePaneHashcode);
				baseDataSetViewModel.addTargetDataSetHashcode(targetPaneHashcode);//
				
				DataSetViewModel targetDataSetViewModel = appContext.getDataSetViewModel(targetPaneHashcode);
				targetDataSetViewModel.setBaseDataSetHashcode(sourcePaneHashcode);//
				targetDataSetViewModel.setRelationHashcode(relationPainHashcode);
				
				appContext.putRelationHashCode(
						  relationPainHashcode
						, new RelationHashCodePair(sourcePaneHashcode, targetPaneHashcode)
						);
				
				statusClear();
			}

		} else {
			statusClear();
		}
		
		logger.debug("handleDataSetMouseEnterdTarget start. entered target is {}", eventTarget.getClass().getName());
		
		
	}
	
	public void makeRelationLine(Pane startPane, Pane endPane, Pane relationPane) {
		makeRelationLine(false , startPane, endPane, relationPane);
	}
	
	private void makeRelationLine(boolean isRedraw, Pane startPane, Pane endPane, Pane relationPane) {
		if (startPane == null || endPane == null)
			return;

		List<Circle> sourceCircles = ViewUtils.getNodeList(startPane, Circle.class);
		List<Circle> targetCircles = ViewUtils.getNodeList(endPane, Circle.class);

		javafx.scene.shape.Line line = new javafx.scene.shape.Line();

		CirclePair circlePair = getClosestCirclePair(sourceCircles, targetCircles);
		Circle fromCircle = circlePair.start();
		Circle toCircle = circlePair.end();

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
			
			double posX = (fromCircleX + toCircleX) / 2 ;
			double posY = (fromCircleY + toCircleY) / 2 ;
			double halfWidth = ViewUtils.getRigionalWidth(relationPane)/2;
			double halfHeight =ViewUtils.getRigionalHeight(relationPane)/2;
			relationPane.setLayoutX(posX-halfWidth);
			relationPane.setLayoutY(posY-halfHeight);
			
			AnchorPane parentPane = (AnchorPane) scene.getRoot();
			parentPane.getChildren().addAll(line, arrowhead);
			relationPane.toFront();

			// 같은 관계 line을 두개의 데이터셋에서 공유하고 잇음
			DataSetRelation dataSetRelationStart = this.appContext.getDataSetRelation(startPane.hashCode())
					.addRelatedLine(circlePair.getStartCirclePos(), 0, line, arrowhead, relationPane) ;
			
			DataSetRelation dataSetRelationEnd = this.appContext.getDataSetRelation(endPane.hashCode())
					.addRelatedLine(circlePair.getEndCirclePos(), 1, line, arrowhead, relationPane);
			
			if ( !isRedraw ) { // 처음으로 관계를 맺는 경우
				parentPane.getChildren().addAll(relationPane);
				relationPane.toFront();
				dataSetRelationStart.addRelatedPane(new RelatedPane(startPane, endPane, relationPane)); 	// start
				dataSetRelationEnd.addRelatedPane(new RelatedPane(startPane, endPane, relationPane)); 	// end
			}
		}
	}

	public void remakeRelationLine(DataSetRelation dataSetRelation) {
		if ( dataSetRelation == null )
			return ;
		
		List<RelatedPane> relatedPaneList = dataSetRelation.getRelatedPaneList();
		List<RelatedLine> relatedLineList = dataSetRelation.getRelatedLineList();

		for (RelatedLine relatedLine : relatedLineList) {
			ViewUtils.removeFromScene(relatedLine.line());
			ViewUtils.removeFromScene(relatedLine.arrowhead());
		}
		dataSetRelation.removeRelatedLineList();
		
		for (RelatedPane relatedPane : relatedPaneList) {
			Pane startPane = relatedPane.startPane();
			Pane endPane = relatedPane.endPane();
			Pane relationPane = relatedPane.relationPane();
			makeRelationLine(true, startPane, endPane, relationPane);
		}
		
	}

	private CirclePair getClosestCirclePair(List<Circle> sourceCircles, List<Circle> targetCircles) {

		Circle startCircle = null;
		Circle endCircle = null;

		double distanceTmp = -1D;
		for (Circle sourceCircle : sourceCircles) {
			for (Circle targetCircle : targetCircles) {
				double distance = distance(sourceCircle, targetCircle);
				if (distanceTmp == -1D) {
					distanceTmp = distance;
					startCircle = sourceCircle;
					endCircle = targetCircle;
				} else {
					if (distance < distanceTmp) {
						distanceTmp = distance;
						startCircle = sourceCircle;
						endCircle = targetCircle;
					}
				}
			}
		}

		return new CirclePair(startCircle, endCircle);
	}

	private double distance(Circle sourceCircle, Circle targetCircle) {
		double sourceX = ViewUtils.getSceneCenterX(sourceCircle);
		double sourceY = ViewUtils.getSceneCenterY(sourceCircle);
		double targetX = ViewUtils.getSceneCenterX(targetCircle);
		double targetY = ViewUtils.getSceneCenterY(targetCircle);

		double deltaX = targetX - sourceX;
		double deltaY = targetY - sourceY;

		// Calculate the distance using the Pythagorean theorem
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		return distance;
	}

	private void checkMousePressDuration() {
		if (this.isMousePressed) {
			long currentTime = System.currentTimeMillis();
			if (this.isMouseDragged)
				currentTime = 0L;

			if (currentTime - mousePressedTime >= duringTimeForRelation) {
				scene.setCursor(getCustomCursor());
				this.isRelatingMode = true;
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
		return this.isRelatingMode;
	}

	private Circle findCircleById(Pane anchorPane, String id) {
		for (javafx.scene.Node node : anchorPane.getChildren()) {
			if (node instanceof Circle && node.getId() != null && node.getId().equals(id)) {
				return (Circle) node;
			}
		}
		return null;
	}

	public void statusClear() {
		this.isMousePressed = false;
		this.isMouseDragged = false;
		this.mousePressedTime = 0L;
		this.dataSetRootAnchorPaneSource = null;
		this.dataSetRootAnchorPaneTarget = null;
		this.isRelatingMode = false;
		this.isMouseDraggedThenReleased = false;
	}

	public boolean isMouseDraggedThenReleased() {
		return this.isMouseDraggedThenReleased;
	}

	public Pane getStartPane() {
		return this.dataSetRootAnchorPaneSource;
	}
}
