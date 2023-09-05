package de.saxsys.mvvmfx.cdi.it;

import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;

public class MyViewModel implements ViewModel {
	public static int instanceCounter = 0;
	
	//@Inject
	private MyService myService;
	
	public MyViewModel() {
		instanceCounter++;
	}
	
	public MyService getMyService() {
		return myService ;
	}
	
}
