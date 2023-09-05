package mydata.ds.view.util;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class EventUtils {
	
	public static void fireNodeEvent(MouseEvent event, String nodeId) {
		EventTarget target = event.getTarget();
        if (target instanceof Node) {
        	Node targetNode = (Node) target;
        	if (nodeId.equals(targetNode.getId()))
        		Event.fireEvent(targetNode, event);
        	else {
        		Node parentNode = ViewUtils.searchPartentNodeWithId(nodeId, targetNode);
        		if (parentNode != null)
        			Event.fireEvent(parentNode, event);
        	}
        }
	}

	public static String getNodeNameWhenMousePressed(MouseEvent event) {
		EventTarget target = event.getTarget();
        if (target instanceof Node) {
            Node targetNode = (Node) target;
            return String.format(
            		  "Node is %s when Mouse is pressed!"
            		, targetNode.toString()
            		) ;
        }
        
        return "not a Node!";
	}
}
