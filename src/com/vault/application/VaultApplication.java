package com.vault.application;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.vault.account.Account;
import com.vault.encryption.AES;
import com.vault.jbcrypt.BCrypt;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class VaultApplication extends Application {

	private ArrayList<Account> accounts;
	private ObservableList<Account> data;
	private final Stage addCredStage = new Stage();
	private final File FILE_DATA = new File("data.dat");
	private String masterPass;
	private final TableView<Account> table = new TableView<Account>();
	private String actualPassword;
	private PasswordField pwBox;
	private final String SALT = BCrypt.gensalt();

	// private boolean confirmed;

	public VaultApplication() {
		actualPassword = "";
		// confirmed = false;
		accounts = new ArrayList<Account>();
		data = FXCollections.observableArrayList(accounts);
		masterPass = "";
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Alec's Password Vault");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text sceneTitle = new Text("Welcome");
		sceneTitle.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		Label password = new Label("Password:");
		grid.add(password, 0, 2);

		TextField passwordField = new TextField();
		grid.add(passwordField, 1, 2);

		pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);

		Button btn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);

		Button regBtn = new Button("Register");
		HBox hbBtn2 = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(regBtn);

		grid.add(hbBtn, 1, 4);
		grid.add(hbBtn2, 2, 4);

		regBtn.setOnAction((event) -> {
			register(primaryStage);
		});
		Text actiontarget = new Text();
		if (FILE_DATA.exists()) {
			grid.add(actiontarget, 1, 6);
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_DATA))) {
				// TODO HERES WHERE IT READS MASTERPASS AT LOGIN
				masterPass = ((Byte[]) ois.readObject()).toString();
			} catch (EOFException e) {
				e.printStackTrace();
			}
			btn.setOnAction((event) -> {
				if ((masterPass != null) && (!masterPass.isEmpty())) {
					if (BCrypt.checkpw(pwBox.getText(), masterPass)) {
						actualPassword = pwBox.getText();
						primaryStage.close();
						try {
							readData();
						} catch (Exception e) {
							e.printStackTrace();
						}
						credentialView();
					} else {
						actiontarget.setFill(Color.FIREBRICK);
						actiontarget.setText("Username or password is wrong");
					}
				} else {
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Username or password is wrong");
				}
			});
		} else {
			btn.setOnAction((event) -> {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Password is wrong");
			});
		}

		Scene scene = new Scene(grid, 300, 275);
		scene.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				btn.fire();
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@SuppressWarnings("unchecked")
	private void credentialView() {
		Scene scene = new Scene(new Group());
		Stage stage = new Stage();
		stage.setTitle("Credentials");
		stage.setWidth(440);
		stage.setHeight(540);

		final Label header = new Label("Credentials");
		header.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		header.setPadding(new Insets(0, 0, 0, 145));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setMaxWidth(389);
		table.setMinWidth(389);
		table.setEditable(true);
		// TODO fix coloring for tableview object
		table.setStyle("-fx-table-cell-border-color:#999999; -fx-background-color:#d9d9d9");

		TableColumn<Account, String> usernameCol = new TableColumn<Account, String>("Usernames");
		TableColumn<Account, String> passwordCol = new TableColumn<Account, String>("Passwords");

		usernameCol.setCellValueFactory(new PropertyValueFactory<Account, String>("username"));
		passwordCol.setCellValueFactory(new PropertyValueFactory<Account, String>("password"));

		table.getColumns().addAll(usernameCol, passwordCol);
		data = FXCollections.observableArrayList(accounts);
		table.setItems(data);

		Button addBtn = new Button("Add");
		addBtn.setOnAction((event) -> {
			addCredentials();
		});

		final Button delBtn = new Button("Delete");
		delBtn.setDisable(true);
		delBtn.setOnAction((event) -> {
			// confirmation();
			if (true) { // TODO Add in a confirmation screen once JavaFX
						// tableview refresh bug is fixed.
				Account selectedItem = table.getSelectionModel().getSelectedItem();
				accounts.remove(selectedItem);
				data.remove(selectedItem);
				table.setItems(data);
			}
			// confirmed = false;
		});

		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				delBtn.setDisable(false);
			}
		});

		usernameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		usernameCol.setOnEditCommit(new EventHandler<CellEditEvent<Account, String>>() {
			@Override
			public void handle(CellEditEvent<Account, String> t) {
				((Account) t.getTableView().getItems().get(t.getTablePosition().getRow())).setUsername(t.getNewValue());
			}

		});

		passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
		passwordCol.setOnEditCommit(new EventHandler<CellEditEvent<Account, String>>() {
			@Override
			public void handle(CellEditEvent<Account, String> t) {
				((Account) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setPassword((t.getNewValue()));
			}

		});

		Button exitBtn = new Button("Save and Exit");
		exitBtn.setOnAction((event) -> {
			try {
				saveData(actualPassword);
				System.exit(0);
			} catch (IOException e) {
				System.out.println("Error saving data: " + e.getMessage());
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}

		});

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 17));
		vbox.getChildren().addAll(header, table);
		HBox hbox = new HBox();
		hbox.setSpacing(20);
		hbox.setPadding(new Insets(10, 5, 5, 25));
		hbox.getChildren().addAll(addBtn, delBtn, exitBtn);
		GridPane grid = new GridPane();
		grid.add(vbox, 0, 0);
		grid.add(hbox, 0, 1);

		((Group) scene.getRoot()).getChildren().add(grid);

		stage.setScene(scene);
		stage.show();
	}

	private void addCredentials() {
		final GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 450, 200);
		final File f = new File("style.css");
		scene.getStylesheets().clear();
		scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

		final Button confirmBtn = new Button("Confirm");
		grid.add(confirmBtn, 2, 8);

		final Button cancelBtn = new Button("Cancel");
		grid.add(cancelBtn, 3, 8);

		final Label userLabel = new Label("Username:");
		grid.add(userLabel, 0, 2);

		final TextField usernameField = new TextField();
		grid.add(usernameField, 1, 2);

		final Label passwordLabel = new Label("Password:");
		grid.add(passwordLabel, 0, 6);

		final TextField passwordField = new TextField();
		grid.add(passwordField, 1, 6);

		// Split up the invalidInputLabels into 2 because the grid wont allow
		// the entire message to be visible on a single label
		final Label invalidInputLabel1 = new Label();
		final Label invalidInputLabel2 = new Label();
		final VBox vbox = new VBox();
		vbox.getChildren().addAll(invalidInputLabel1, invalidInputLabel2);
		grid.add(vbox, 1, 7);
		confirmBtn.setOnAction((event) -> {
			if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
				invalidInputLabel1.setText("Please fill out");
				invalidInputLabel2.setText("both fields.");
				invalidInputLabel1.setTextFill(Color.FIREBRICK);
				invalidInputLabel2.setTextFill(Color.FIREBRICK);
			} else {
				accounts.add(new Account(usernameField.getText(), passwordField.getText()));
				data.add(accounts.get(accounts.size() - 1));
				addCredStage.close();
			}
		});

		cancelBtn.setOnAction((event) -> {
			addCredStage.close();
		});

		addCredStage.setScene(scene);
		addCredStage.show();
	}

	private void saveData(String pass)
			throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		try (FileOutputStream fos = new FileOutputStream(FILE_DATA);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

			if (pass != null) {
				// TODO HERES WHERE THE MASTERPASS GETS SAVED TO FILE 
				masterPass = BCrypt.hashpw(pass, SALT);
				baos.write(masterPass.getBytes("UTF-8"));
			}
			byte[] encodedUser = null;
			byte[] encodedPass = null;
			for (Account acc : accounts) {
				encodedUser = Base64.getEncoder().encode(AES.encrypt(acc.getUsername(), actualPassword));
				encodedPass = Base64.getEncoder().encode(AES.encrypt(acc.getPassword(), actualPassword));
				baos.write(encodedUser);
				baos.write(encodedPass);

			}
			baos.writeTo(fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readData() throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
			ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_DATA));) {
			@SuppressWarnings("unchecked")
			List<byte[]> bytes = (List<byte[]>) in.readObject();
			byte[] user = null;
			byte[] pass = null;
			for (int i = 1; i < bytes.size(); i += 2) {
				user = Base64.getDecoder().decode(AES.decrypt(bytes.get(i), actualPassword));
				pass = Base64.getDecoder().decode(AES.decrypt(bytes.get(i + 1), actualPassword));
				accounts.add(new Account(user.toString(), pass.toString()));
			}
		} catch (IOException e) {
			System.err.println("Error reading file");
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	private void register(Stage primaryStage) {
		primaryStage.close();
		Stage regStage = new Stage();
		regStage.setTitle("Register");

		final GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(3);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 0, 0, 0));

		Scene scene = new Scene(grid, 530, 200);

		Label passLabel1 = new Label("Enter a password:");
		passLabel1.setPadding(new Insets(0, 0, 0, 100));
		grid.add(passLabel1, 0, 0);

		Label passLabel2 = new Label("Confirm password:");
		passLabel2.setPadding(new Insets(0, 0, 0, 100));
		grid.add(passLabel2, 0, 1);

		TextField field1 = new TextField();
		PasswordField pass1 = new PasswordField();
		grid.add(field1, 1, 0);
		grid.add(pass1, 1, 0);

		TextField field2 = new TextField();
		PasswordField pass2 = new PasswordField();
		grid.add(field2, 1, 1);
		grid.add(pass2, 1, 1);

		Label warning = new Label("Registering a new password will delete all existing entries.");
		warning.setMaxHeight(10);
		grid.add(warning, 0, 3);

		Text actiontarget = new Text();
		grid.add(actiontarget, 0, 5);
		GridPane.setMargin(actiontarget, new Insets(0, 0, 0, 120));
		Button regBtn = new Button("Register");
		regBtn.setOnAction((event) -> {
			if (pass1.getText().equals(pass2.getText())) {
				try {
					actualPassword = pass1.getText();
					saveData(pass1.getText());
					regStage.close();
					credentialView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Passwords do not match.");
			}
		});

		Button cancelBtn = new Button("Cancel");
		HBox hbBtn = new HBox();
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().addAll(regBtn, cancelBtn);
		hbBtn.setPadding(new Insets(0, 0, 0, 80));
		HBox.setMargin(regBtn, new Insets(0, 15, 0, 0));
		grid.add(hbBtn, 0, 4);
		cancelBtn.setOnAction((event) -> {
			regStage.close();
			primaryStage.show();
		});
		scene.setRoot(grid);
		regStage.setScene(scene);
		regStage.show();
	}

	// private void confirmation() {
	// Stage confirmationStage = new Stage();
	// GridPane grid = new GridPane();
	// grid.setAlignment(Pos.CENTER);
	// grid.setHgap(2);
	// grid.setVgap(2);
	// grid.setPadding(new Insets(1, 1, 1, 1));
	//
	// Label question = new Label("Are you sure you want to delete this
	// entry?");
	// question.setStyle("-fx-min-width: 90;");
	// Button yesBtn = new Button("Yes");
	// yesBtn.setStyle("-fx-min-width: 90;");
	// yesBtn.setOnAction((event) -> {
	// // confirmed = true;
	// confirmationStage.close();
	// });
	//
	// Button noBtn = new Button("No");
	// noBtn.setStyle("-fx-min-width: 90;");
	// noBtn.setOnAction((event) -> {
	// // confirmed = false;
	// confirmationStage.close();
	// });
	// grid.add(question, 0, 0);
	// grid.add(yesBtn, 0, 1);
	// grid.add(noBtn, 1, 1);
	// Scene scene = new Scene(grid, 300, 100);
	// scene.setRoot(grid);
	// confirmationStage.setScene(scene);
	// confirmationStage.show();
	//
	// }
}