package gui;

import java.io.Console;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	// *** Class Attributes ***
	// Controller elements
	private Seller sellerEntity;
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	private ObservableList<Department> obsList;

	// View elements
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Label labelErrorBaseSalary;
	@FXML
	private Label labelErrorDepartment;

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	// Getters and Setters
	public void setSeller(Seller entity) {
		this.sellerEntity = entity;
	}

	public void setServices(SellerService serv, DepartmentService depServ) {
		this.service = serv;
		this.departmentService = depServ;
	}

	// *** Methods ***
	// View Methods
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		System.out.println("onBtSaveAction");
		if (sellerEntity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		try {
			sellerEntity = getFormData();
			service.saveOrUpdate(sellerEntity);
			Utils.currentStage(event).close();
			notifyDataChangeListeners();
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
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
		Constraints.setTextFieldMaxLength(txtName, 50);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "yyyy/MM/dd");
		initializeComboBoxDepartment();
	}

	// *** Controller Methods ***
	public void subscribedDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void updateFormData() {
		if (sellerEntity == null) { // defensive
			throw new IllegalStateException("Entity is null");
		}
		txtId.setText(String.valueOf(sellerEntity.getId())); // txtid is String
		txtName.setText(sellerEntity.getName());
		txtEmail.setText(sellerEntity.getEmail());
		txtBaseSalary.setText(String.format("%.2f", sellerEntity.getBaseSalary()));
		if (sellerEntity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(sellerEntity.getBirthDate().toInstant(), ZoneId.systemDefault()));
			// ZoneId.systemDefault() gets the host timezone
		}
		if (sellerEntity.getDepartment() == null) { // if new
			//comboBoxDepartment.getSelectionModel().selectFirst();
			comboBoxDepartment.getSelectionModel().clearSelection();
		} else { // if existing seller
			comboBoxDepartment.setValue(sellerEntity.getDepartment());
		}
	}

	private Seller getFormData() {
		Seller seller = new Seller();
		ValidationException exception = new ValidationException("Validation error");

		seller.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field Name cannot be empty.");
		}
		seller.setName(txtName.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field Email cannot be empty.");
		}
		seller.setEmail(txtEmail.getText());

		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Date cannot be empty.");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			seller.setBirthDate(Date.from(instant));
		}

		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field Base Salary cannot be empty.");
		}
		// seller.setBaseSalary(Double.parseDouble(txtBaseSalary.getText()));
		seller.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

		if (comboBoxDepartment.getSelectionModel().isEmpty()) {
			exception.addError("comboBoxDepartment", "Department cannot be empty.");
		}
		seller.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return seller;
	}

	private void notifyDataChangeListeners() {
		/* onBtSaveAction calls this method */
//		for (DataChangeListener listener : dataChangeListeners) {
//			listener.onDataChange();
//		}		
		dataChangeListeners.forEach(DataChangeListener::onDataChange);
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService is null.");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	private void setErrorMessages(Map<String, String> errors) {
		//Set<String> fieldsWithErrors = errors.keySet();
		//labelXPTO.setText( fields.contains("xpto") ? do one thing : do another thing ); 
		//System.out.println(errors.get("name"));

		labelErrorName.setText(errors.get("name")); // testing without IF
		labelErrorEmail.setText(errors.get("email")); // testing without IF
		labelErrorBaseSalary.setText(errors.get("baseSalary")); // testing without IF
		labelErrorBirthDate.setText(errors.get("birthDate")); // testing without IF
		labelErrorDepartment.setText(errors.get("comboBoxDepartment"));
		/* if erros.get() returns null, setText sets to empty*/
	}
}
