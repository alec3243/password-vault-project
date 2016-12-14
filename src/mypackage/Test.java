package mypackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.mindrot.jbcrypt.BCrypt;

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
import javafx.stage.Stage;

public class Test extends Application {

	private ArrayList<Account> accounts1;
	private ObservableList<Account> data;
	private final Stage addCredStage = new Stage();
	private final File FILE_DATA = new File("data.dat");
	private String masterPass;
	private final TableView<Account> table = new TableView<Account>();
	private String actualPassword;

	// private boolean confirmed;

	public Test() throws CryptoException {
		actualPassword = "";
		// confirmed = false;
		accounts1 = new ArrayList<Account>();
		data = FXCollections.observableArrayList(accounts1);
		masterPass = "";
	}

	public static void main(String[] args) throws IOException {
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

		PasswordField pwBox = new PasswordField();
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
			BufferedReader br = new BufferedReader(new FileReader(FILE_DATA));
			grid.add(actiontarget, 1, 6);
			masterPass = br.readLine();
			br.close();
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
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@SuppressWarnings("unchecked")
	private void credentialView() {
		Scene scene = new Scene(new Group());
		Stage stage = new Stage();
		stage.setTitle("Credentials");
		stage.setWidth(300);
		stage.setHeight(540);

		final Label header = new Label("Credentials");
		header.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		header.setPadding(new Insets(0, 0, 0, 75));

		table.setEditable(true);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Account, String> usernameCol = new TableColumn<Account, String>(
				"Usernames");
		TableColumn<Account, String> passwordCol = new TableColumn<Account, String>(
				"Passwords");

		usernameCol
				.setCellValueFactory(new PropertyValueFactory<Account, String>(
						"username"));
		passwordCol
				.setCellValueFactory(new PropertyValueFactory<Account, String>(
						"password"));

		table.getColumns().addAll(usernameCol, passwordCol);
		data = FXCollections.observableArrayList(accounts1);
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
				Account selectedItem = table.getSelectionModel()
						.getSelectedItem();
				accounts1.remove(selectedItem);
				data.remove(selectedItem);
				table.setItems(data);
			}
			// confirmed = false;
		});

		table.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						delBtn.setDisable(false);
					}
				});

		usernameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		usernameCol
				.setOnEditCommit(new EventHandler<CellEditEvent<Account, String>>() {
					@Override
					public void handle(CellEditEvent<Account, String> t) {
						((Account) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setUsername(t.getNewValue());
					}

				});

		passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
		passwordCol
				.setOnEditCommit(new EventHandler<CellEditEvent<Account, String>>() {
					@Override
					public void handle(CellEditEvent<Account, String> t) {
						((Account) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setPassword((t.getNewValue()));
					}

				});

		Button exitBtn = new Button("Save and Exit");
		exitBtn.setOnAction((event) -> {
			try {
				saveData();
				System.exit(0);
			} catch (IOException | CryptoException e) {
				System.out.println("Error saving data: " + e.getMessage());
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
		File f = new File("style.css");
		scene.getStylesheets().clear();
		scene.getStylesheets().add(
				"file:///" + f.getAbsolutePath().replace("\\", "/"));

		Button confirmBtn = new Button("Confirm");
		grid.add(confirmBtn, 2, 8);

		Button cancelBtn = new Button("Cancel");
		grid.add(cancelBtn, 3, 8);

		Label userLabel = new Label("Username:");
		grid.add(userLabel, 0, 2);

		TextField usernameField = new TextField();
		grid.add(usernameField, 1, 2);

		Label passwordLabel = new Label("Password:");
		grid.add(passwordLabel, 0, 6);

		TextField passwordField = new TextField();
		grid.add(passwordField, 1, 6);

		confirmBtn.setOnAction((event) -> {
			accounts1.add(new Account(usernameField.getText(), passwordField
					.getText()));
			data.add(accounts1.get(accounts1.size() - 1));
			addCredStage.close();
		});

		cancelBtn.setOnAction((event) -> {
			addCredStage.close();
		});

		addCredStage.setScene(scene);
		addCredStage.show();
	}

	private void saveData() throws IOException, CryptoException {
		FileWriter fw = new FileWriter(FILE_DATA);
		fw.write(masterPass);
		fw.write("\n");
		for (Account acc : accounts1) {
			String username = XOREncryption.encryptDecrypt(acc.getUsername(),
					actualPassword);
			String password = XOREncryption.encryptDecrypt(acc.getPassword(),
					actualPassword);
			fw.write(username);
			fw.write(",");
			fw.write(password);
			fw.write("\n");
		}
		fw.close();
	}

	private void readData() throws IOException, CryptoException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, ClassNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(FILE_DATA));
		String username = "";
		String password = "";
		String[] accData = new String[2];
		br.readLine();
		while (br.ready()) {
			accData = br.readLine().split(",");
			accData[0] = accData[0].replace("\n", "").replace("\r", "");
			accData[1] = accData[1].replace("\n", "").replace("\r", "");
			username = XOREncryption.encryptDecrypt(accData[0], actualPassword);
			password = XOREncryption.encryptDecrypt(accData[1], actualPassword);
			accounts1.add(new Account(username, password));
		}
		br.close();
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

		Label warning = new Label(
				"Registering a new password will delete all existing entries.");
		warning.setMaxHeight(10);
		grid.add(warning, 0, 3);

		Text actiontarget = new Text();
		grid.add(actiontarget, 0, 5);
		GridPane.setMargin(actiontarget, new Insets(0, 0, 0, 120));
		Button regBtn = new Button("Register");
		regBtn.setOnAction((event) -> {
			System.out.println(pass1.getText());
			System.out.println(pass2.getText());
			if (pass1.getText().equals(pass2.getText())) {
				try {
					actualPassword = pass1.getText();
					saveMasterPass(new String(pass1.getText()));
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

	public void saveMasterPass(String pass) throws IOException {
		FileWriter fw = new FileWriter(FILE_DATA);
		masterPass = BCrypt.hashpw(pass, BCrypt.gensalt());
		fw.write(masterPass);
		fw.write("\n");
		fw.close();
	}

	public void confirmation() {
		Stage confirmationStage = new Stage();
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(2);
		grid.setVgap(2);
		grid.setPadding(new Insets(1, 1, 1, 1));

		Label question = new Label(
				"Are you sure you want to delete this entry?");
		question.setStyle("-fx-min-width: 90;");
		Button yesBtn = new Button("Yes");
		yesBtn.setStyle("-fx-min-width: 90;");
		yesBtn.setOnAction((event) -> {
			// confirmed = true;
			confirmationStage.close();
		});

		Button noBtn = new Button("No");
		noBtn.setStyle("-fx-min-width: 90;");
		noBtn.setOnAction((event) -> {
			// confirmed = false;
			confirmationStage.close();
		});
		grid.add(question, 0, 0);
		grid.add(yesBtn, 0, 1);
		grid.add(noBtn, 1, 1);
		Scene scene = new Scene(grid, 300, 100);
		scene.setRoot(grid);
		confirmationStage.setScene(scene);
		confirmationStage.show();

	}
}
