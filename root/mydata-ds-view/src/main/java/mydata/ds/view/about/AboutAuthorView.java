package mydata.ds.view.about;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import jakarta.inject.Singleton;
import javafx.fxml.FXML;

@Singleton
public class AboutAuthorView implements FxmlView<AboutAuthorViewModel> {

	@InjectViewModel
	private AboutAuthorViewModel viewModel;

	@FXML
	public void openBlog() {
		viewModel.openBlog();
	}

	@FXML
	public void openTwitter() {
		viewModel.openTwitter();
	}
}
