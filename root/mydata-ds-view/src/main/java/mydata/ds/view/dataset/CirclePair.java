package mydata.ds.view.dataset;

import javafx.scene.shape.Circle;

public record CirclePair(Circle start, Circle end) {

	public CirclePos getStartCirclePos() {
		return circlePos(start());
	}

	public CirclePos getEndCirclePos() {
		return circlePos(end());
	}

	private CirclePos circlePos(Circle circle) {
		if (circle.getId().indexOf("top") >= 0)
			return CirclePos.Top;
		
		else if (circle.getId().indexOf("right") >= 0)
			return CirclePos.Right;
		
		else if (circle.getId().indexOf("bottom") >= 0)
			return CirclePos.Bottom;
		
		else
			return CirclePos.Left;
	}
	
}
