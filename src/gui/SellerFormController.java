package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	// *** Class Attributes ***
	// Controller elements
	private Seller sellerEntity;
	private SellerService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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
	public void setSeller(Seller entity) {
		this.sellerEntity = entity;
	}

	public void setSellerService(SellerService serv) {
		this.service = serv;
	}

	// *** Methods ***
	//View Methods
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
			gui.util.Utils.currentStage(event).close();
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
		Constraints.setTextFieldMaxLength(txtName, 30);
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
	}

	private Seller getFormData() {
		Seller seller = new Seller();
		ValidationException exception = new ValidationException("Validation error");
		
		seller.setId(gui.util.Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field Name cannot be empty.");
		}
		seller.setName(txtName.getText());
		
		if(exception.getErrors().size() >0) {
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

	private void setErrorMessages(Map<String, String> errors) {
		labelErrorName.setText(errors.get("name")); //testing without IF
	}
}
