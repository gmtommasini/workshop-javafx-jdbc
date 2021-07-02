package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	// View elements
	@FXML
	private TableView<Department> tableViewDepartments;
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // TableColumn gets the
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private TableColumn<Department, Department> tableColumnEdit;
	@FXML
	private Button btNew;

	// Controller elements
	private DepartmentService service;
	private ObservableList<Department> obsList;

	// Getters and Setters
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	// Methods
	public void onBtNewAction(ActionEvent event) {
		System.out.println("onBtNewAction");
		Department dep = new Department();
		createDialogForm(dep, "/gui/DepartmentForm.fxml", gui.util.Utils.currentStage(event));
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// fitting to window
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());
		tableViewDepartments.prefWidthProperty().bind(stage.widthProperty());
		// trying to fit the column...
		// tableColumnName.prefWidthProperty().bind(stage.widthProperty()); //does not
		// work very well this way
	}

	public void updateTableView() {
		if (service == null) { // being safe
			throw new IllegalStateException("Service is null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartments.setItems(obsList);
		initEditButtons();
	}

	private void createDialogForm(Department departmenteEntity, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(departmenteEntity);
			controller.setDepartmentService(service);
			controller.subscribedDataChangeListener(this); // This controller is subscribed to listen to data changes
			controller.updateFormData();

			// modal is a new Stage over the main Stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.initOwner(parentStage);
			// dialogStage.setAlwaysOnTop(true);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChange() {
		/*
		 * this method is currently being called by notifyDataChangeListeners from
		 * DepartmentFormController
		 */
		updateTableView();
	}

	private void initEditButtons() {
		// adds the Edit button to each line of the table 
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}
}
