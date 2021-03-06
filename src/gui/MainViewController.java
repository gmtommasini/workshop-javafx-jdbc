package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	@FXML
	private MenuItem menuItemAbout2;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService()); // dependency injection
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService()); // dependency injection
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		System.out.println("onMenuItemAboutAction");
		loadView("/gui/AboutView.fxml", x -> {
		}); // About does not require controller
	}

	@FXML
	public void onMenuItemAbout2Action() {
		System.out.println("onMenuItemAbout2Action");
		loadViewNewWindow("/gui/AboutView.fxml");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			// getContent returns a reference to whatever is inside <content>, in this case,
			// a VBox

			// Saving the Menu in a Node
			Node mainMenu = mainVBox.getChildren().get(0); // getting the first child (Node) if mainVbox

			mainVBox.getChildren().clear();
			// Reinserting elements in the Scene (mainScene / mainVBox)
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			// Preciso estudar isso melhor
			T controller = loader.getController();
			initializingAction.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	// temp
	private synchronized void loadView2(String absoluteName) { // temporary
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			// getContent returns a reference to whatever is inside <content>, in this case,
			// a VBox

			// Saving the Menu in a Node
			Node mainMenu = mainVBox.getChildren().get(0); // getting the first child (Node) if mainVbox

			mainVBox.getChildren().clear();
			// Reinserting elements in the Scene (mainScene / mainVBox)
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());

			DepartmentListController controller = loader.getController();
			controller.setDepartmentService(new DepartmentService()); // dependency injection
			controller.updateTableView();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	private synchronized void loadViewNewWindow(String absoluteName) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene newScene = new Scene(newVBox);
			Stage newStage = new Stage();
			newStage.setScene(newScene);
			// newStage.setScene(new Scene((new
			// FXMLLoader(getClass().getResource(absoluteName))).load()));
			newStage.show();

		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
			// e.printStackTrace();
		} catch (Exception e) {
			Alerts.showAlert("Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
