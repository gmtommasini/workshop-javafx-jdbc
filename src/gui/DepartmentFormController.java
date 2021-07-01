package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Constraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentFormController implements Initializable{
	// *** Class Attributes ***
	// Controller elements
	private Department departmentEntity;
	
	// View elements
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	
	//Getters and Setters
	public void setDepartment(Department entity) {
		this.departmentEntity = entity;
	}
	
	
	// *** Methods ***
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		System.out.println("onBtSaveAction");		
	}
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		System.out.println("onBtCancelAction");		
	}
	
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		if(departmentEntity==null) {		// defensive
			throw new IllegalStateException("Entity is null");
		}
		txtId.setText(String.valueOf(departmentEntity.getId())); //txtid is String
		txtName.setText(departmentEntity.getName());
	}
}
