package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
