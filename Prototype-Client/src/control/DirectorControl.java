package control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import entity.QuestionInExam;
import entity.RequestForChangingTimeAllocated;
import entity.TeachingProfessionals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ocsf.server.ConnectionToClient;

public class DirectorControl extends UserControl implements Initializable {
	ObservableList<RequestForChangingTimeAllocated> addingTimeRequestsObservable = FXCollections.observableArrayList();
	/// HOME TAB
	@FXML
	private Label userText1;
	@FXML
	private Label pageLabel;
	@FXML
	private Button AddingTime;
	@FXML
	private Button StatisticReport;
	@FXML
	private Button SystemInformation;
	@FXML
	private Button backButton;
	// FAML Time Requests Table window
	@FXML
	private TableView<RequestForChangingTimeAllocated> requestsTable;
	@FXML
	private TableColumn<RequestForChangingTimeAllocated, String> examIDColumn;
	@FXML
	private TableColumn<RequestForChangingTimeAllocated, String> teacherNameColumn;
	@FXML
	private TableColumn<RequestForChangingTimeAllocated, String> timeAddedColumn;
	@FXML
	private Button showDetailsButton;
	// FAML Adding Time Requests window
	@FXML
	private TextField txtFATRexecutedExamId;
	@FXML
	private TextField txtFATRTeachName;
	@FXML
	private TextField txtFATRrequestId;
	@FXML
	private TextField txtFATRTimeAdded;
	@FXML
	private TextField txtFATRreasonAddingTime;
	@FXML
	private Button btnATRApprove;
	@FXML
	private Button btnATRreject;

	// FXML Statistic Reports

	// FXML System information
	/******************************************************************
	 * homePageButtons
	 **********************************************************/
	public void initialize(URL url, ResourceBundle rb) {
		if (pageLabel.getText().equals("Home screen"))
			userText1.setText(Globals.getFullName());
		else if (pageLabel.getText().contentEquals("requests")) {
			connect(this);
			messageToServer[0] = "getTimeRequestList";
			messageToServer[1] = null;
			messageToServer[2] = null;
			chat.handleMessageFromClientUI(messageToServer);// send the message to server
		} else if (pageLabel.getText().contentEquals("Adding time requests")) {
			connect(this);
			messageToServer[0] = "getTimeRequestDetails";
			messageToServer[1] = Globals.getRequestId();
			messageToServer[2] = null;
			chat.handleMessageFromClientUI(messageToServer);// send the message to server
		}
	}

	public void openTimeRequestTable(ActionEvent e) {
		((Node) e.getSource()).getScene().getWindow().hide(); // hiding homePage window
		openScreen("TimeRequestTable");

	}

	public void openStatisticReport(ActionEvent e) {
		((Node) e.getSource()).getScene().getWindow().hide(); // hiding homePage window
		openScreen("statisticReportDirector");
	}

	public void openSystemInformation(ActionEvent e) {
		((Node) e.getSource()).getScene().getWindow().hide(); // hiding homePage window
		openScreen("systemInformationDirector");
	}

	private void openScreen(String screen) {// open the windows after login
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/directorBoundary/" + screen + ".fxml").openStream());/////////// *נצטרך
																													/////////// לשנות
																													/////////// לבונדרי
																													/////////// רגיל
																													/////////// או
																													/////////// להתאים
																													/////////// לארור
																													/////////// מסג'*/////////////

			Scene scene = new Scene(root);
			if (screen.equals("ErrorMessage")) {
				ErrorControl tController = loader.getController();
				tController.setBackwardScreen(primaryStage.getScene());// send the name to the controller
				tController.setErrorMessage("ERROR"); // send a the error to the alert we made
			}
			primaryStage.setTitle(screen);
			// primaryStage.getIcons().add(new
			// Image("/Prototype-Client/Copywriting-Master-Class-Owl-200x175.png"));
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println("Error in opening the page");
		}
	}

	private void openScreen(String screen, String message) {// for error message
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/boundary/" + screen + ".fxml"));
			Scene scene = new Scene(loader.load());
			Stage stage = Main.getStage();
			ErrorControl tController = loader.getController();
			tController.setBackwardScreen(stage.getScene());/* send the name to the controller */
			tController.setErrorMessage(message);// send a the error to the alert we made
			stage.setTitle("ErrorMessage");
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			System.out.println("Error in opening the page");
		}
	}

	/* cancel button was pressed */
	public void backButtonPressed(ActionEvent e) throws IOException, SQLException {
		final Node source = (Node) e.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.hide();
		openScreen("HomeScreenDirector");
	}

	/////// ***************************check the message that arrived from
	/////// server***************************/
	@SuppressWarnings("unchecked")
	public void checkMessage(Object message) {
		try {
			chat.closeConnection();// close the connection

			Object[] msg = (Object[])message;

			switch (msg[0].toString()) {
			case ("getTimeRequestList"): { /* get the subjects list from server */
				initAddingTimeRequests((ArrayList<RequestForChangingTimeAllocated>) msg[1]);
				break;
			}
			case ("getTimeRequestDetails"): { /* get the subjects list from server */
				initAddingTimeRequestDetails((RequestForChangingTimeAllocated) msg[1]);
				break;
			}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/*******************************************************
	 * listeners on TimeRequestTable
	 ***********************************************************/
	public void showDetailsButtonPressed(ActionEvent e) throws IOException, SQLException {
		Globals.setRequestId(requestsTable.getSelectionModel().getSelectedItems().get(0).getRequestID());
		;
		final Node source = (Node) e.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		openScreen("‪addingTimeRequest‬"); 
	}

	@SuppressWarnings("unchecked")
	public void initAddingTimeRequests(ArrayList<RequestForChangingTimeAllocated> requestsList) {
		for (RequestForChangingTimeAllocated i : requestsList) {
			addingTimeRequestsObservable.add(i);

		}
		if (requestsTable.getColumns() != null)
			requestsTable.getColumns().clear();
		requestsTable.setItems(addingTimeRequestsObservable);
		// display the id in the table view
		examIDColumn.setCellValueFactory(new PropertyValueFactory<>("IDexecutedExam"));

		teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

		timeAddedColumn.setCellValueFactory(new PropertyValueFactory<>("timeAdded"));
		// requestsTable.getColumns().clear();
		requestsTable.getColumns().addAll(examIDColumn, teacherNameColumn, timeAddedColumn);

	}

	public void initAddingTimeRequestDetails(RequestForChangingTimeAllocated request) {
		txtFATRTeachName.setText(request.getTeacherName());
		txtFATRTimeAdded.setText(request.getTimeAdded());
		txtFATRreasonAddingTime.setText(request.getReason());
		txtFATRrequestId.setText(request.getRequestID());
		txtFATRexecutedExamId.setText(request.getIDexecutedExam());

	}

	/*******************************************************
	 * listeners on addingTimeRequest
	 ***********************************************************/
	public void answerRequest(ActionEvent e) {
		String requestID = txtFATRrequestId.getText();
		connect(this);
		if (e.getSource() == btnATRApprove)//press on approved button
			messageToServer[0] = "SetStatusToApproved";
		else if (e.getSource() == btnATRreject)//press on reject button
			messageToServer[0] = "SetStatusToReject";
		messageToServer[1] = requestID;
		messageToServer[2]=null;
		this.openTimeRequestTable(e);
		chat.handleMessageFromClientUI(messageToServer);
	}

}