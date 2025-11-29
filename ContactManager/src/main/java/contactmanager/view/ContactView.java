package contactmanager.view;

import contactmanager.model.*;
import contactmanager.service.ContactManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ContactView {
    private ContactManager manager;
    private TableView<Contact> table;
    private TextField searchField;
    
    // Input fields
    private TextField nameField, phoneField, emailField, addressField;
    private TextField relationshipField, birthdayField;
    private TextField companyField, jobTitleField;
    private RadioButton personalRadio, businessRadio;

    public ContactView() {
        this.manager = new ContactManager();
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Contact Management System");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: Search
        root.setTop(createSearchBox());

        // Center: Table
        root.setCenter(createTable());

        // Bottom: Form
        root.setBottom(createForm());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        refreshTable();
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        
        searchField = new TextField();
        searchField.setPromptText("Search by name or phone...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, old, newVal) -> refreshTable());
        
        searchBox.getChildren().add(new Label("Search:"));
        searchBox.getChildren().add(searchField);
        return searchBox;
    }

    private TableView<Contact> createTable() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Contact, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Contact, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Contact, String> infoCol = new TableColumn<>("Additional Info");
        infoCol.setCellValueFactory(new PropertyValueFactory<>("additionalInfo"));

        table.getColumns().addAll(nameCol, phoneCol, typeCol, infoCol);

        // Double-click to edit
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Contact selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    populateForm(selected);
                }
            }
        });

        return table;
    }

    private VBox createForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        // Type selection
        ToggleGroup group = new ToggleGroup();
        personalRadio = new RadioButton("Personal");
        businessRadio = new RadioButton("Business");
        personalRadio.setToggleGroup(group);
        businessRadio.setToggleGroup(group);
        personalRadio.setSelected(true);
        
        personalRadio.setOnAction(e -> toggleFields());
        businessRadio.setOnAction(e -> toggleFields());
        
        HBox typeBox = new HBox(10, new Label("Type:"), personalRadio, businessRadio);

        // Common fields
        GridPane commonGrid = new GridPane();
        commonGrid.setHgap(10);
        commonGrid.setVgap(5);
        
        nameField = new TextField();
        phoneField = new TextField();
        emailField = new TextField();
        addressField = new TextField();
        
        commonGrid.addRow(0, new Label("Name:"), nameField);
        commonGrid.addRow(1, new Label("Phone:"), phoneField);
        commonGrid.addRow(2, new Label("Email:"), emailField);
        commonGrid.addRow(3, new Label("Address:"), addressField);

        // Personal fields
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(10);
        personalGrid.setVgap(5);
        
        relationshipField = new TextField();
        birthdayField = new TextField();
        
        personalGrid.addRow(0, new Label("Relationship:"), relationshipField);
        personalGrid.addRow(1, new Label("Birthday:"), birthdayField);

        // Business fields
        GridPane businessGrid = new GridPane();
        businessGrid.setHgap(10);
        businessGrid.setVgap(5);
        
        companyField = new TextField();
        jobTitleField = new TextField();
        
        businessGrid.addRow(0, new Label("Company:"), companyField);
        businessGrid.addRow(1, new Label("Job Title:"), jobTitleField);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");
        
        addBtn.setOnAction(e -> addContact());
        updateBtn.setOnAction(e -> updateContact());
        deleteBtn.setOnAction(e -> deleteContact());
        clearBtn.setOnAction(e -> clearForm());
        
        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);

        form.getChildren().addAll(typeBox, commonGrid, personalGrid, businessGrid, buttonBox);
        toggleFields(); // Initial setup
        
        return form;
    }

    private void toggleFields() {
        boolean isPersonal = personalRadio.isSelected();
        relationshipField.setDisable(!isPersonal);
        birthdayField.setDisable(!isPersonal);
        companyField.setDisable(isPersonal);
        jobTitleField.setDisable(isPersonal);
    }

    private void addContact() {
        Contact contact = createContactFromForm();
        if (contact != null) {
            manager.addContact(contact);
            clearForm();
            refreshTable();
        }
    }

    private void updateContact() {
        Contact selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a contact to update.");
            return;
        }
        
        Contact updated = createContactFromForm();
        if (updated != null) {
            updated.setId(selected.getId());
            manager.updateContact(selected.getId(), updated);
            clearForm();
            refreshTable();
        }
    }

    private void deleteContact() {
        Contact selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a contact to delete.");
            return;
        }
        
        manager.deleteContact(selected.getId());
        refreshTable();
    }

    private Contact createContactFromForm() {
        if (nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            showAlert("Name and Phone are required!");
            return null;
        }

        if (personalRadio.isSelected()) {
            return new PersonalContact(
                null,
                nameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                addressField.getText(),
                relationshipField.getText(),
                birthdayField.getText()
            );
        } else {
            return new BusinessContact(
                null,
                nameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                addressField.getText(),
                companyField.getText(),
                jobTitleField.getText()
            );
        }
    }

    private void populateForm(Contact contact) {
        nameField.setText(contact.getName());
        phoneField.setText(contact.getPhone());
        emailField.setText(contact.getEmail());
        addressField.setText(contact.getAddress());

        if (contact instanceof PersonalContact) {
            PersonalContact pc = (PersonalContact) contact;
            personalRadio.setSelected(true);
            relationshipField.setText(pc.getRelationship());
            birthdayField.setText(pc.getBirthday());
        } else if (contact instanceof BusinessContact) {
            BusinessContact bc = (BusinessContact) contact;
            businessRadio.setSelected(true);
            companyField.setText(bc.getCompany());
            jobTitleField.setText(bc.getJobTitle());
        }
        toggleFields();
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        relationshipField.clear();
        birthdayField.clear();
        companyField.clear();
        jobTitleField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        String query = searchField != null ? searchField.getText() : "";
        table.setItems(FXCollections.observableArrayList(
            manager.searchContacts(query)
        ));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
