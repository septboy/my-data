package de.saxsys.mvvmfx.cdi.it;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import jakarta.inject.Inject;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class MyView implements FxmlView<MyViewModel> {
	@Inject
	Stage primaryStage;
	
	@Inject
	Application.Parameters parameters;
	
	@Inject
	NotificationCenter notificationCenter;
	
	@Inject
	HostServices hostServices;
	
	public static int instanceCounter = 0;
	
	public MyView() {
		instanceCounter++;
	}
}
