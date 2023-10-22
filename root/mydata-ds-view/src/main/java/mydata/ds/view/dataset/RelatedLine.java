package mydata.ds.view.dataset;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * gubun: 0: start, 1: end
 */
public record RelatedLine(CirclePos circlePos, int gubun, Line line, Polygon arrowhead, Pane relationPane) {

}
