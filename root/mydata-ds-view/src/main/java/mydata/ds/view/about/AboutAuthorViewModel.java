package mydata.ds.view.about;

import de.saxsys.mvvmfx.ViewModel;
import jakarta.inject.Inject;
import javafx.application.HostServices;

public class AboutAuthorViewModel implements ViewModel {

	@Inject
	private HostServices hostServices;

	public void openBlog() {
		hostServices.showDocument("http://www.lestard.eu");
	}

	public void openTwitter() {
		hostServices.showDocument("https://twitter.com/manuel_mauky");
	}

}
