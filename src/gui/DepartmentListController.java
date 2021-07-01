package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
	// View elements
	@FXML
	private TableView<Department> tableViewDepartments;
	@FXML
	private TableColumn<Department, Integer> tableColumnId; //TableColumn gets the 
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private Button btNew;
	
	// Controller elements
	private DepartmentService service;
	private ObservableList<Department> obsList;
	
	
	// Methods
	public void onBtNewAction(ActionEvent event) {
		System.out.println("onBtNewAction");
		createDialogForm("/gui/DepartmentForm.fxml", gui.util.Utils.currentStage(event));
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();		
	}
	
	public void setDepartmentService(DepartmentService service ) {
		this.service = service;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//fitting to window
		Stage stage = (Stage )Main.getMainScene().getWindow();
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());
		tableViewDepartments.prefWidthProperty().bind(stage.widthProperty());
		//trying to fit the column...
		//tableColumnName.prefWidthProperty().bind(stage.widthProperty()); //does not work very well this way
	}
	
	public void updateTableView() {
		// being safe
		if(service == null) {
			throw new IllegalStateException("Service is null");
		}
		List<Department> list = service.findAll();
		obsList =  FXCollections.observableArrayList(list);
		tableViewDepartments.setItems(obsList);
	}
	
	private void createDialogForm(String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			//modal is a new Stage over the main Stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.initOwner(parentStage);
			//dialogStage.setAlwaysOnTop(true);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}catch (IOException e) {
			// TODO: handle exception
			Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), AlertType.ERROR);
		}
	}

}
