package mydata.ds.view.dataset;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ds.common.util.CommonUtil;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import mydata.ds.view.dataset.record.RelationHashCodePair;
import mydata.ds.view.util.ViewUtils;

public class DataSetRelation {

	private static final Logger logger = LoggerFactory.getLogger(DataSetRelation.class);
	
	List<RelatedLine> relatedLineList = new ArrayList<>();
	
	private List<RelatedPane> relatedPainList = new ArrayList<>();
	

	public void moveRelationLine(double deltaX, double deltaY) {
		for (RelatedLine relatedLine : relatedLineList) {
			adjustRelationArrowLine(relatedLine, deltaX, deltaY);
		}
	}
	
	public void moveRelationLine(RelationPointCenters relationPointCentersScene,
			double topCenterX, double topCenterY, double rightCenterX, double rightCenterY,
			double bottomCenterX, double bottomCenterY, double leftCenterX, double leftCenterY) {
		
		double deltaX = 0L;
		double deltaY = 0L;
		
		for (RelatedLine arrowLine : relatedLineList) {
			if (arrowLine.circlePos() == CirclePos.Top) {
				deltaX = topCenterX - relationPointCentersScene.topCenterX();
				deltaY = topCenterY - relationPointCentersScene.topCenterY();
				
				
			} else if (arrowLine.circlePos() == CirclePos.Right) {
				deltaX = rightCenterX - relationPointCentersScene.rightCenterX();
				deltaY = rightCenterY - relationPointCentersScene.rightCenterY();
				
			} else if (arrowLine.circlePos() == CirclePos.Bottom) {
				deltaX = bottomCenterX - relationPointCentersScene.bottomCenterX();
				deltaY = bottomCenterY - relationPointCentersScene.bottomCenterY();
				
				
			} else if (arrowLine.circlePos() == CirclePos.Left) {
				deltaX = leftCenterX - relationPointCentersScene.leftCenterX();
				deltaY = leftCenterY - relationPointCentersScene.leftCenterY();
				
				logger.debug("arrowLine.circlePos()={}, leftCenterX={}, relationPointCentersScene.leftCenterX()={}, deltaX={} , leftCenterY={}, relationPointCentersScene.leftCenterY()={}, deltaY={}"
						, arrowLine.circlePos().name()
						, leftCenterX, relationPointCentersScene.leftCenterX(), deltaX 
						, leftCenterY, relationPointCentersScene.leftCenterY(), deltaY 
						);
			} else {
				throw new RuntimeException("관계 선에 대한 위치가 정의되지 않았습니다.!");
			}
				
			
			adjustRelationArrowLine(arrowLine, deltaX, deltaY);
		}
	}

	public DataSetRelation addRelatedLine(CirclePos circlePos, int gubun, Line line, Polygon arrowhead, Pane relationPane) {
		this.relatedLineList.add(new RelatedLine(circlePos, gubun, line, arrowhead, relationPane));
		return this ;
	}

	private void adjustRelationArrowLine(RelatedLine relatedLine, double deltaX, double deltaY) {

		Line line = relatedLine.line();
		Polygon arrowhead = relatedLine.arrowhead();
		
		// start circle
		double fromCircleX = CommonUtil.decode(relatedLine.gubun(), 0, line.getStartX() + deltaX, line.getStartX()); // 0: start
		double fromCircleY = CommonUtil.decode(relatedLine.gubun(), 0, line.getStartY() + deltaY, line.getStartY()); //

		// end circle
		double toCircleX = CommonUtil.decode(relatedLine.gubun(), 1, line.getEndX() + deltaX, line.getEndX()); // 1: end
		double toCircleY = CommonUtil.decode(relatedLine.gubun(), 1, line.getEndY() + deltaY, line.getEndY()); //
		
		line.setStartX(fromCircleX);// rightCircle.getLayoutX()
		line.setStartY(fromCircleY);// rightCircle.getLayoutY()
		line.setEndX(toCircleX);// leftCircle.getLayoutX()
		line.setEndY(toCircleY);// leftCircle.getLayoutX()

		double angle = Math.atan2((toCircleY - fromCircleY), (toCircleX - fromCircleX));
		angle = Math.toDegrees(angle);

		// Position the arrowhead at the end of the line
		arrowhead.setLayoutX(toCircleX - 7.5);
		arrowhead.setLayoutY(toCircleY - 7.5);

		// Rotate the arrowhead
		arrowhead.setRotate(angle);

		Pane relationPane = relatedLine.relationPane();
		double posX = (fromCircleX + toCircleX) / 2 ;
		double posY = (fromCircleY + toCircleY) / 2 ;
		double halfWidth = ViewUtils.getRigionalWidth(relationPane)/2;
		double halfHeight =ViewUtils.getRigionalHeight(relationPane)/2;
		relationPane.setLayoutX(posX-halfWidth);
		relationPane.setLayoutY(posY-halfHeight);
		relationPane.toFront();
		
	}

	public void addRelatedPane(RelatedPane relatedPane) {
		this.relatedPainList.add(relatedPane);
	}

	public List<RelatedPane> getRelatedPaneList() {
		return this.relatedPainList;
	}

	public List<RelatedLine> getRelatedLineList() {
		return this.relatedLineList;
	}

	public List<RelatedLine> getRelatedLineList(int relationPaneHahscode) {
		return this.relatedLineList.stream()
				.filter(item -> item.relationPane().hashCode() == relationPaneHahscode)
				.toList();
	}

	public void removeRelatedLineList() {
		this.relatedLineList.removeAll(relatedLineList);
	}

	public void reflashRelatedLine(List<RelatedLine> relatedLineList) {
		if (this.relatedLineList == relatedLineList)
			this.relatedLineList.removeAll(relatedLineList);
		
		else 
			for(RelatedLine relatedLine: relatedLineList) {
				this.relatedLineList.removeIf(item -> item.line() == relatedLine.line());
			}
	}

	public void reflashRelatedPane(int hashcode) {
		this.relatedPainList.removeIf(item -> item.endPane().hashCode() == hashcode);
		this.relatedPainList.removeIf(item -> item.startPane().hashCode() == hashcode);
		this.relatedPainList.removeIf(item -> item.relationPane().hashCode() == hashcode);
	}

	public boolean isOkWhenRemove() {
		return this.relatedLineList.size() == 0;
	}
}
