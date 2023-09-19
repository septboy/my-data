package mydata.ds.view.scopes;

import jakarta.enterprise.context.ApplicationScoped;
import mydata.ds.view.dataset.MouseEventStatus;

@ApplicationScoped
public class AppContext {
	private MouseEventStatus mouseEventStatus ;
	
	public AppContext() {
		mouseEventStatus = new MouseEventStatus();
	}

	public MouseEventStatus getMouseEventStatus() {
		return mouseEventStatus;
	}
	
	
}
