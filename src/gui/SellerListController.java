package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	// View elements
	@FXML
	private TableView<Seller> tableViewSellers;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // TableColumn gets the
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;
	@FXML
	private TableColumn<Seller, Seller> tableColumnRemove;
	@FXML
	private Button btNew;

	// Controller elements
	private SellerService service;
	private ObservableList<Seller> obsList;

	// Getters and Setters
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	// Methods
	public void onBtNewAction(ActionEvent event) {
		System.out.println("onBtNewAction");
		Seller seller = new Seller();
		createDialogForm(seller, "/gui/SellerForm.fxml", gui.util.Utils.currentStage(event));
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // the PropertyValueFactory<>("id") is the class attribute name
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "yyyy/MM/dd");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		// fitting to window
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSellers.prefHeightProperty().bind(stage.heightProperty());
		tableViewSellers.prefWidthProperty().bind(stage.widthProperty());
		// trying to fit the column...
		// tableColumnName.prefWidthProperty().bind(stage.widthProperty()); //does not
		// work very well this way
	}

	public void updateTableView() {
		if (service == null) { // being safe
			throw new IllegalStateException("Service is null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSellers.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller sellerEntity, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setSeller(sellerEntity);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subscribedDataChangeListener(this); // This controller is subscribed to listen to data changes
			controller.updateFormData();

			// modal is a new Stage over the main Stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.initOwner(parentStage);
			// dialogStage.setAlwaysOnTop(true); // make is in front of anything in the
			// screen
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChange() {
		/*
		 * this method is currently being called by notifyDataChangeListeners from
		 * SellerFormController
		 */
		updateTableView();
	}

	private void initEditButtons() {
		// adds the Edit button to each line of the table
		// code from
		// https://stackoverflow.com/questions/32282230/fxml-javafx-8-tableview-make-a-delete-button-in-each-row-and-delete-the-row-a
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller seller) {
		Optional<ButtonType> confirm = Alerts.showConfirmation("Confirmation", "Are you sure you want to delete?");
		if (confirm.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service is null");
			}
			try {
				service.remove(seller);
				updateTableView();
			} catch (DbIntegrityException e) { //We can always check the Exception type looking into the throwing method
				Alerts.showAlert("Error removing seller", null, e.getMessage(), AlertType.ERROR);
			}
		}

	}
}
