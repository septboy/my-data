package mydata.ds.view.config;

import java.util.ResourceBundle;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * A singleton CDI provider that is used to load the resource bundle and provide
 * it for the CDI injection.
 */
@Singleton
public class ResourceProvider {

	/*
	 * Due to the @Produces annotation this resource bundle can be injected in all views.
	 */
	@Produces
	private ResourceBundle defaultResourceBundle = ResourceBundle.getBundle("default");

}
