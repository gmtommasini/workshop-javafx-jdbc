package gui;

import java.net.URL;
import java.util.ResourceBundle;



import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	// *** Class Attributes ***
	// Controller elements
	private Department departmentEntity;
	private DepartmentService service;

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

	// Getters and Setters
	public void setDepartment(Department entity) {
		this.departmentEntity = entity;
	}

	public void setDepartmentService(DepartmentService serv) {
		this.service = serv;
	}

	// *** Methods ***
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		System.out.println("onBtSaveAction");
		if (departmentEntity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		try {
			departmentEntity = getFormdata();
			service.saveOrUpdate(departmentEntity);
			gui.util.Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		System.out.println("onBtCancelAction");
		gui.util.Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	// *** Controller Methods ***

	public void updateFormData() {
		if (departmentEntity == null) { // defensive
			throw new IllegalStateException("Entity is null");
		}
		txtId.setText(String.valueOf(departmentEntity.getId())); // txtid is String
		txtName.setText(departmentEntity.getName());
	}

	private Department getFormdata() {
		Department dep = new Department();
		dep.setId(gui.util.Utils.tryParseToInt(txtId.getText()));
		dep.setName(txtName.getText());
		return dep;
	}

}
