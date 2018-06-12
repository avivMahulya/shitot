package control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import entity.Course;
import entity.Exam;
import entity.ExecutedExam;
import entity.Question;
import entity.QuestionInExam;
import entity.RequestForChangingTimeAllocated;
import entity.TeachingProfessionals;
import javafx.application.Platform;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;

public class TeacherControl extends UserControl implements Initializable {

	private static ObservableList<QuestionInExam> questionInExamObservable = FXCollections.observableArrayList();
	private ObservableList<Question> questionObservableList;
	private Object[] messageToServer = new Object[3];
	private static boolean blockLeftButton;
	private ObservableList<Exam> exams;
	private Question questionSelected;
	private static String tempExamId;
	private Exam examSelected;
	private Exam oldExam;

	/* fxml variables */
	@FXML
	private Text userText;
	@FXML
	private Text allertText;
	@FXML
	private TextField teacherNameOnCreate;

	@FXML
	private Label pageLabel;

	@FXML
	private TextField answer1;
	@FXML
	private TextField answer2;
	@FXML
	private TextField answer3;
	@FXML
	private TextField answer4;

	@FXML
	private TextField questionName;
	@FXML
	private TextField questionID;
	@FXML
	private TextField teacherName;
	@FXML
	private TextField remarksForStudent;
	@FXML
	private TextField remarksForTeacher;
	@FXML
	private TextField timeForExamHours;
	@FXML
	private TextField timeForExamMinute;
	@FXML
	private TextField reasonForChange;
	@FXML
	private TextField examCode;

	/* RadioButton of display the correct answer */
	@FXML
	private RadioButton correctAns1;
	@FXML
	private RadioButton correctAns2;
	@FXML
	private RadioButton correctAns3;
	@FXML
	private RadioButton correctAns4;

	@FXML
	private ToggleGroup group;

	@FXML
	private TableView<QuestionInExam> questionsInExamTableView;
	@FXML
	private TableColumn<QuestionInExam, String> questionNameTableView;
	@FXML
	private TableColumn<QuestionInExam, Float> questionPointsTableView;

	@FXML
	private TableView<ExecutedExam> executedExamTableView;
	@FXML
	private TableColumn<ExecutedExam, String> exam_idTableView;
	@FXML
	private TableColumn<ExecutedExam, String> executedExamIDTableView;
	@FXML
	private TableColumn<ExecutedExam, String> teacherNameTableView;

	@FXML
	private TableView<Question> questionTableView;
	@FXML
	private TableColumn<Question, String> qid;
	@FXML
	private TableColumn<Question, String> tname;
	@FXML
	private TableColumn<Question, String> qtext;
	@FXML
	private TableColumn<Question, String> a1;
	@FXML
	private TableColumn<Question, String> a2;
	@FXML
	private TableColumn<Question, String> a3;
	@FXML
	private TableColumn<Question, String> a4;
	@FXML
	private TableColumn<Question, String> correctAns;

	@FXML
	private TableView<Exam> examsTableView;
	@FXML
	private TableColumn<Exam, String> examITable;
	@FXML
	private TableColumn<Exam, String> teacherNameTable;
	@FXML
	private TableColumn<Exam, String> remarksForTeacherTable;
	@FXML
	private TableColumn<Exam, String> solutionTimeTable;
	@FXML
	private TableColumn<Exam, String> typeTable;
	@FXML
	private TableColumn<Exam, String> questionIDTable;
	@FXML
	private TableColumn<Exam, String> remarksForStudentTable;

	@FXML
	private ComboBox<String> subjectsComboBox;
	@FXML
	private ComboBox<String> coursesComboBox;
	@FXML
	private ComboBox<String> examComboBox;
	@FXML
	private ComboBox<String> typeComboBox;
	@FXML
	private Button left;
	@FXML
	private Button right;
	private Question oldQuestion;

	/* check the content message from server */
	@SuppressWarnings("unchecked")
	public void checkMessage(Object message) {
		try {
			chat.closeConnection();// close the connection

			final Object[] msg = (Object[]) message;
			Platform.runLater(() -> {
				switch (msg[0].toString()) {
				case ("getSubjects"): /* get the subjects list from server */
				{
					ObservableList<String> observableList = FXCollections.observableArrayList();
					for (TeachingProfessionals tp : (ArrayList<TeachingProfessionals>) msg[1]) {
						observableList.add(tp.getTp_id() + " - " + tp.getName());
					}
					subjectsComboBox.setItems(observableList);

					break;
				}
				case ("setExecutedExamLocked"): /* get the subjects list from server */
				case ("setExamCode"): /* get the subject list from server */
				{
					if ((boolean) msg[1] == true) {
						allertText.setFill(Color.GREEN);
						allertText.setText("Exam code created successfully ✔");
					} else {
						allertText.setFill(Color.RED);
						allertText.setText("There is already a code like that, please choose another code ❌");
					}
					break;
				}
				case ("updateExam"): /* get the subjects list from server */
				{
					if ((boolean) msg[1] == true) {
						examsTableView.refresh();
					} else {
						exams.remove(exams.indexOf(examSelected));
						exams.add(oldExam);
						Platform.runLater(() -> openScreen("ErrorMessage", "This exam is in active exam."));
						examsTableView.getSortOrder().setAll(questionIDTable);
					}
					break;
				}
				case ("getQuestionInExam"): /* get the subjects list from server */
				{
					try {
						((ArrayList<QuestionInExam>) msg[1]).forEach(questionInExamObservable::add);
						Platform.runLater(() -> {
							blockLeftButton = true;
							openScreen("UpdateQuestionInExam");
						});
					} catch (NullPointerException exception) {
						Platform.runLater(() -> openScreen("ErrorMessage", "exam does not have any question"));
						blockLeftButton = false;
					}
					break;
				}

				case ("getExecutedExams"): /* get the subjects list from server */
				{
					ObservableList<ExecutedExam> observablelist = FXCollections
							.observableArrayList((ArrayList<ExecutedExam>) msg[1]);
					executedExamTableView.setItems(observablelist);
					executedExamIDTableView.setCellValueFactory(new PropertyValueFactory<>("executedExamID"));
					teacherNameTableView.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
					exam_idTableView.setCellValueFactory(new PropertyValueFactory<>("exam_id"));
					break;
				}
				case ("getCourses"): /* get the courses list from server */
				{
					ObservableList<String> observableList = FXCollections.observableArrayList();
					for (Course c : (ArrayList<Course>) msg[1]) {
						observableList.add(c.getCourseID() + " - " + c.getName());
					}
					coursesComboBox.setItems(observableList);
					break;
				}

				case ("getQuestionsToTable"): /* get the questions list from server */
				{
					questionObservableList = FXCollections.observableArrayList((ArrayList<Question>) msg[1]);
					qid.setCellValueFactory(new PropertyValueFactory<>("id"));
					tname.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
					qtext.setCellValueFactory(new PropertyValueFactory<>("questionContent"));
					if (pageLabel.getText().equals("Update question")) {
						a1.setCellValueFactory(new PropertyValueFactory<>("answer1"));
						a2.setCellValueFactory(new PropertyValueFactory<>("answer2"));
						a3.setCellValueFactory(new PropertyValueFactory<>("answer3"));
						a4.setCellValueFactory(new PropertyValueFactory<>("answer4"));
						correctAns.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
						questionTableView.setEditable(true);
						ObservableList<String> numbers = FXCollections.observableArrayList("1", "2", "3", "4");
						qtext.setCellFactory(TextFieldTableCell.forTableColumn());
						a1.setCellFactory(TextFieldTableCell.forTableColumn());
						a2.setCellFactory(TextFieldTableCell.forTableColumn());
						a3.setCellFactory(TextFieldTableCell.forTableColumn());
						a4.setCellFactory(TextFieldTableCell.forTableColumn());
						correctAns.setCellFactory(TextFieldTableCell.forTableColumn());
						correctAns.setCellFactory(
								ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), numbers));
						questionObservableList = FXCollections.observableArrayList((ArrayList<Question>) msg[1]);
					}

					questionTableView.setItems(questionObservableList);
					break;
				}
				case ("getExams"): /* get the subjects list from server */
				{
					if (pageLabel.getText().equals("Update exam")) {
						exams = FXCollections.observableArrayList((ArrayList<Exam>) msg[1]);
						questionIDTable.setCellValueFactory(new PropertyValueFactory<>("e_id"));
						teacherNameTable.setCellValueFactory(new PropertyValueFactory<>("teacherUserName"));
						solutionTimeTable.setCellValueFactory(new PropertyValueFactory<>("solutionTime"));
						remarksForTeacherTable.setCellValueFactory(new PropertyValueFactory<>("remarksForTeacher"));
						remarksForStudentTable.setCellValueFactory(new PropertyValueFactory<>("remarksForStudent"));
						typeTable.setCellValueFactory(new PropertyValueFactory<>("type"));
						ObservableList<String> type = FXCollections.observableArrayList("computerized", "manual");
						solutionTimeTable.setCellFactory(TextFieldTableCell.forTableColumn());
						remarksForTeacherTable.setCellFactory(TextFieldTableCell.forTableColumn());
						remarksForStudentTable.setCellFactory(TextFieldTableCell.forTableColumn());
						typeTable.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), type));
						examsTableView.setItems(exams);
					} else {
						ObservableList<String> observableList = FXCollections.observableArrayList();
						ArrayList<Exam> exams = (ArrayList<Exam>) msg[1];
						for (Exam e : exams) {
							observableList.add(e.getE_id());
						}
						examComboBox.setItems(observableList);

					}
					break;
				}
				case ("updateQuestion"): /* get the subject list from server */
				{
					if ((boolean) msg[1] == true) {
						questionTableView.refresh();
					} else {
						questionObservableList.remove(questionObservableList.indexOf(questionSelected));
						questionObservableList.add(oldQuestion);

						Platform.runLater(() -> openScreen("ErrorMessage", "This question is in active exam."));
						questionTableView.getSortOrder().setAll(qid);
					}
					break;
				}

				case ("deleteQuestion"): /* get the subject list from server */
				{
					if ((boolean) msg[1] == true) {
						int index = questionObservableList.indexOf(questionSelected);
						questionObservableList.remove(index);
						questionTableView.refresh();
					} else {
						Platform.runLater(() -> openScreen("ErrorMessage", "This question is in active exam."));
					}
					break;
				}

				default: {
					System.out.println("Error in input");
				}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/* intialize function go first after loading fxml */
	public void initialize(URL url, ResourceBundle rb) {

		switch (pageLabel.getText()) {
		case ("Home screen"): {
			userText.setText(Globals.getFullName());
			break;
		}
		case ("Update question in exam"): {
			if (blockLeftButton) {
				left.setDisable(true);
			}
			setToQuestionInExamTableView();
			connect(this); // connecting to server
			messageToServer[0] = "getQuestionsToTable";
			messageToServer[1] = questionInExamObservable.get(0).getQuestionID().substring(0, 2);
			messageToServer[2] = Globals.getuserName();
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
			break;
		}
		case ("Create question"):
		case ("Create exam"):
		case ("Update question"):
		case ("Create exam code"):
		case ("Extend exam time"):
		case ("Lock exam"):
		case ("Update exam"): {

			connect(this);

			switch (pageLabel.getText()) {
			case ("Create exam"): {
				typeComboBox.setItems(FXCollections.observableArrayList("computerized", "manual"));
				break;
			}

			case ("Extend exam time"):
			case ("Lock exam"): {
				messageToServer[0] = "getExecutedExams";
				messageToServer[1] = Globals.getuserName();
				chat.handleMessageFromClientUI(messageToServer);// send the message to server
				break;
			}
			case ("Create question"): {
				teacherNameOnCreate.setText(Globals.getuserName());
				break;
			}

			}
			messageToServer[0] = "getSubjects";
			messageToServer[1] = Globals.getuserName();
			messageToServer[2] = null;
			chat.handleMessageFromClientUI(messageToServer);// send the message to server
			break;

		}

		}

	}

	/* creatin an extend time request */
	public void createExtendTimeRequest(ActionEvent e) throws IOException {
		if (timeForExamHours.getText().equals("") || timeForExamMinute.getText().equals("")) {
			openScreen("ErrorMessage", "Please fill the time you want to extend by");
			return;
		}
		if (reasonForChange.getText().trim().equals("")) {
			openScreen("ErrorMessage", "Please fill the reason for changing the time");
			return;
		}
		ExecutedExam executedexam = executedExamTableView.getSelectionModel().getSelectedItem();
		if (executedexam == null) {
			openScreen("ErrorMessage", "Please choose an exam");
			return;
		}
		RequestForChangingTimeAllocated request = new RequestForChangingTimeAllocated();
		request.setIDexecutedExam(executedexam.getExecutedExamID());
		request.setReason(reasonForChange.getText());
		request.setMenagerApprove("waiting");
		request.setTeacherName(Globals.getuserName());
		request.setTimeAdded(timeForExamHours.getText() + "" + timeForExamMinute.getText());
		connect(this); // connecting to server
		messageToServer[0] = "createChangingRequest";
		messageToServer[1] = request;
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		chat.closeConnection();
	}

	/***************** Opening screens action-events *****************/
	/* open the screen ExtendExamTime */
	public void openExtendExamTimeScreen(ActionEvent e) {
		openScreen("ExtendExamTime");
	}

	/* open the screen UpdateQuestion */
	public void openUpdateQuestionScreen(ActionEvent e) {
		openScreen("UpdateQuestion");
	}

	/* open the screen CreateExamCode */
	public void openExamCodeScreen(ActionEvent e) {
		openScreen("CreateExamCode");
	}

	/* open the screen CreateExam */
	public void openCreateExam(ActionEvent e) {
		openScreen("CreateExam");
	}

	/* open the screen CreateQuestion */
	public void openCreateQuestion(ActionEvent e) {
		openScreen("CreateQuestion");
	}

	/* open the screen UpdateExam */
	public void openUpdateExamScreen(ActionEvent e) {
		openScreen("UpdateExam");
	}

	/* open the screen as requested */
	public void openScreen(String screen) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/boundary/" + screen + ".fxml"));
			Scene scene = new Scene(loader.load());
			Stage stage = Main.getStage();
			if (screen.equals("ErrorMessage")) {
				ErrorControl tController = loader.getController();
				tController.setBackwardScreen(stage.getScene());/* send the name to the controller */
				tController.setErrorMessage("ERROR");// send a the error to the alert we made
			}
			stage.setTitle(screen);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			System.out.println("Error in opening the page");
		}
	}

	/* open the screen ErrorMessage and sending an object */
	public void openScreen(String screen, Object message) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/boundary/" + screen + ".fxml"));
			Scene scene = new Scene(loader.load());
			Stage stage = Main.getStage();
			ErrorControl tController = loader.getController();
			tController.setBackwardScreen(stage.getScene());/* send the name to the controller */
			tController.setErrorMessage((String) message);// send a the error to the alert we made
			stage.setTitle("Error message");
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			System.out.println("Error in opening the page");
		}
	}

	/* deleting question from the tableview and from the database */
	public void deleteQuestion(ActionEvent e) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		messageToServer[0] = "deleteQuestion";
		messageToServer[1] = questionSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	/* locking the subject function (subject combobox) */
	public void lockSubject(ActionEvent e) {
		subjectsComboBox.setDisable(true);
	}

	/* moving the question to the question in exam table view */
	public void toQuestionInExam(ActionEvent e) {
		int flag = 0;
		if (questionTableView.getSelectionModel().getSelectedItem() == null) {
			openScreen("ErrorMessage", "Please choose question");
			return;
		}

		QuestionInExam questioninexam = new QuestionInExam();// creating new questioninexam
		Question questionDetails = questionTableView.getSelectionModel().getSelectedItem();
		questioninexam.setQuestionID(questionDetails.getId());
		questioninexam.setTeacherUserName(questionDetails.getTeacherName());
		questioninexam.setQuestionContent(questionDetails.getQuestionContent());
		questioninexam.setPoints(0);
		setToQuestionInExamTableView();
		for (QuestionInExam item : questionInExamObservable) {
			if (item.getQuestionID().equals(questionDetails.getId()))
				flag = 1;
		}
		if (flag == 0) {
			questionObservableList.remove(questionTableView.getSelectionModel().getSelectedIndex());
			questionInExamObservable.add(questioninexam);
			questionsInExamTableView.getSortOrder().setAll(questionNameTableView);
		}

	}

	/* setting the question in the table view (question in exam) */
	private void setToQuestionInExamTableView() {
		questionPointsTableView.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
		questionNameTableView.setCellValueFactory(new PropertyValueFactory<>("questionID"));// display the id in the
																							// table view
		questionPointsTableView.setCellValueFactory(new PropertyValueFactory<>("points"));// display the points in table
																							// view // the
		questionsInExamTableView.setItems(questionInExamObservable);
	}

	/* removing the question from the tableview */
	public void removeFromTableView(ActionEvent e) {
		ObservableList<QuestionInExam> questiontoremove;
		int flag = 0;
		try {
			questiontoremove = questionsInExamTableView.getSelectionModel().getSelectedItems();
			Question question = new Question();
			question.setQuestionContent(questiontoremove.get(0).getQuestionContent());
			question.setTeacherName(questiontoremove.get(0).getTeacherUserName());
			question.setId(questiontoremove.get(0).getQuestionID());
			for (Question item : questionObservableList) {
				if (item.getId().equals(question.getId()))
					flag = 1;
			}
			if (flag == 0) {
				questionObservableList.add(question);
				questionTableView.getSortOrder().setAll(qid);
			}

			questiontoremove.forEach(questionInExamObservable::remove);
		} catch (RuntimeException exception) {
			openScreen("ErrorMessage", "Please choose question to delete");
			return;
		}
		// add the question back to the tableview
	}

	/* creating exam */
	@SuppressWarnings("static-access")
	public void createExam(ActionEvent e) {
		int sumOfPoints = 0;
		if (timeForExamHours.getText().equals("") || timeForExamMinute.getText().equals("")
				|| Integer.valueOf(timeForExamHours.getText()) < 0) {
			openScreen("ErrorMessage", "Please fill time for exam");
			return;
		}
		if (typeComboBox.getValue() == null) {
			openScreen("ErrorMessage", "Please select the type of exam");
			return;
		}
		if ((Integer.parseInt(timeForExamHours.getText()) <= 0 && Integer.parseInt(timeForExamMinute.getText()) <= 0)
				|| (Integer.parseInt(timeForExamHours.getText()) > 99
						|| Integer.parseInt(timeForExamMinute.getText()) > 99)) {
			openScreen("ErrorMessage", "invalid time");
			return;
		}

		for (QuestionInExam q : questionInExamObservable) {
			sumOfPoints += q.getPoints();
			if (q.getPoints() == 0) {
				openScreen("ErrorMessage", "You cant set a question with 0 points.");
				return;
			}
		}
		if (sumOfPoints != 100) {
			openScreen("ErrorMessage", "Points are not match to 100");
			return;
		}
		Exam exam = new Exam();// creating a new exam;
		Time time = null;
		String courseID = questionInExamObservable.get(0).getQuestionID().substring(0, 2);// we want the course id
		String[] subjectSubString = subjectsComboBox.getValue().split("-");
		exam.setE_id(subjectSubString[0].trim() + "" + courseID);// making the start of the id of the exam
		ArrayList<QuestionInExam> questioninexam = (ArrayList<QuestionInExam>) questionInExamObservable.stream()
				.collect(Collectors.toList());// making the observable a lis
		exam.setRemarksForStudent(remarksForStudent.getText());
		exam.setRemarksForTeacher(remarksForTeacher.getText());
		exam.setTeacherUserName(Globals.getuserName());
		time = time.valueOf(timeForExamHours.getText() + ":" + timeForExamMinute.getText() + ":00");// making a Time
																									// class format
		exam.setSolutionTime(time.toString());
		exam.setType(typeComboBox.getValue());
		messageToServer[0] = "setExam";
		messageToServer[1] = questioninexam;
		messageToServer[2] = exam;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);// send the message to server
		try {
			chat.closeConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/* removing the question from the tableview */
	public void updateQuestionInExam(ActionEvent e) {
		int sumOfPoints = 0;
		for (QuestionInExam q : questionInExamObservable) {
			sumOfPoints += q.getPoints();
			if (q.getPoints() == 0) {
				openScreen("ErrorMessage", "You cant set a question with 0 points.");
				return;
			}
		}
		if (sumOfPoints != 100) {
			openScreen("ErrorMessage", "Points are not match to 100");
			return;
		}
		ArrayList<QuestionInExam> questioninexam = (ArrayList<QuestionInExam>) questionInExamObservable.stream()
				.collect(Collectors.toList());// making the observable a lis
		messageToServer[0] = "updateQuestionInExam";
		messageToServer[1] = questioninexam;
		messageToServer[2] = tempExamId;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);// send the message to server
		try {
			chat.closeConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	/* removing the question from the tableview */

	public void deleteExam(ActionEvent e) {
		examSelected = examsTableView.getSelectionModel().getSelectedItem();
		messageToServer[0] = "deleteExam";
		messageToServer[1] = examSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	public void createExamCode(ActionEvent e) {
		ExecutedExam exam;
		String examID = examComboBox.getValue();
		String executedExamId = examCode.getText();
		if (subjectsComboBox.getValue() == null) {
			openScreen("ErrorMessage", "Please choose subject");
			return;
		}
		if (coursesComboBox.getValue() == null) {
			openScreen("ErrorMessage", "Please choose course");
			return;
		}
		if (examComboBox.getValue() == null) {
			openScreen("ErrorMessage", "Please choose exam");
			return;
		}
		if (executedExamId.length() != 4) {
			openScreen("ErrorMessage", "You must enter exactly 4 letters & number");
			return;
		}
		for (int i = 0; i < executedExamId.length(); i++) {
			char ch = executedExamId.charAt(i);

			if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'))) {
				openScreen("ErrorMessage", "You must enter only letters and numbers.");
				return;
			}
		}
		exam = new ExecutedExam();
		exam.setExecutedExamID(executedExamId);
		exam.setTeacherName(Globals.getuserName());
		exam.setExam_id(examID);
		messageToServer[0] = "setExamCode";
		messageToServer[1] = exam;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);// send the message to server
		/*
		 * try { chat.closeConnection(); } catch (IOException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); } // close the connection
		 */
	}

	/* set new points in the table view */
	public void setPoints(CellEditEvent<QuestionInExam, Float> edittedCell) {
		QuestionInExam questionSelected = questionsInExamTableView.getSelectionModel().getSelectedItem();
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getPoints())) {
			questionSelected.setPoints(edittedCell.getNewValue());
		}
	}

	/* create a new question */
	public void createQuestionClick(ActionEvent e) throws IOException {
		if (subjectsComboBox.getValue() == null) {
			openScreen("ErrorMessage", "Please choose subject");
			return;
		}
		Question question;

		int correctAnswer = 0;
		if (correctAns1.isSelected()) {
			correctAnswer = 1;
		}
		if (correctAns2.isSelected()) {
			correctAnswer = 2;
		}
		if (correctAns3.isSelected()) {
			correctAnswer = 3;
		}
		if (correctAns4.isSelected()) {
			correctAnswer = 4;
		}

		if ((answer1.getText().equals("")) || ((answer2.getText().equals(""))) || (answer3.getText().equals(""))
				|| (answer4.getText().equals("")) || (correctAnswer == 0) || (questionName.getText().equals(""))) {
			openScreen("ErrorMessage", "Not all fields are completely full");
		} else {
			question = new Question(null, Globals.getuserName(), questionName.getText().trim(),
					answer1.getText().trim(), answer2.getText().trim(), answer3.getText().trim(),
					answer4.getText().trim(), String.valueOf(correctAnswer));
			String subject = subjectsComboBox.getValue();
			String[] subjectSubString = subject.split("-");
			connect(this); // connecting to server
			messageToServer[0] = "SetQuestion";
			messageToServer[1] = subjectSubString[0].trim();
			messageToServer[2] = question;
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
			chat.closeConnection();// close the connection
		}
	}

	/* request to load questions to table view */
	public void loadQuestions(ActionEvent e) throws IOException {
		/* ask for the qustions text */
		String subject = subjectsComboBox.getValue(); // get the subject code
		if (subject == null)
			return;
		String[] subjectSubString = subject.split("-");
		connect(this); // connecting to server
		messageToServer[0] = "getQuestionsToTable";
		messageToServer[1] = subjectSubString[0].trim();
		messageToServer[2] = Globals.getuserName();
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/* change the question content on table view */
	public void changeQuestionContentOnTable(CellEditEvent<Question, String> edittedCell) {

		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getQuestionContent())) {
			questionSelected.setQuestionContent(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/* change the answer 1 on table view */
	public void changeAnswer1OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer1())) {
			questionSelected.setAnswer1(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/* change the answer 2 content on table view */
	public void changeAnswer2OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer2())) {
			questionSelected.setAnswer2(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);

		}
	}

	/* change the answer 3 content on table view */
	public void changeAnswer3OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer3())) {
			questionSelected.setAnswer3(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);

		}
	}

	/* change the answer 4 content on table view */
	public void changeAnswer4OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer4())) {
			questionSelected.setAnswer4(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/* change the correct answer on table view */
	public void changeCorrectAnswerOnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getCorrectAnswer())) {
			questionSelected.setCorrectAnswer(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);

		}
	}

	public Question createBackUpQuestion(Question questionSelected) {
		return new Question(questionSelected.getId(), questionSelected.getTeacherName(),
				questionSelected.getQuestionContent(), questionSelected.getAnswer1(), questionSelected.getAnswer2(),
				questionSelected.getAnswer3(), questionSelected.getAnswer4(), questionSelected.getCorrectAnswer());
	}

	/* updating the question that has been selected */
	public void updateQuestion(Question questionSelected) {
		messageToServer[0] = "updateQuestion";
		messageToServer[1] = questionSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	/* close button was pressed */
	public void closeScreen(ActionEvent e) throws IOException, SQLException {
		final Node source = (Node) e.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		questionInExamObservable.clear();
		openScreen("HomeScreenTeacher");
	}

	/* close button was pressed */
	public void loadCourses(ActionEvent e) throws IOException {
		/* ask for the courses name */
		try {
			String subject = subjectsComboBox.getValue(); // get the subject code
			String[] subjectSubString = subject.split("-");
			coursesComboBox.getSelectionModel().clearSelection();
			connect(this); // connecting to server
			messageToServer[0] = "getCourses";
			messageToServer[1] = subjectSubString[0].trim();
			messageToServer[2] = Globals.getuserName();
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		} catch (NullPointerException exception) {
			return;
		}
	}

	public void loadExams(ActionEvent e) throws IOException {
		String examIDStart;
		/* ask for the exams name */
		if (pageLabel.getText().equals("Create exam code")) {
			if (coursesComboBox.getValue() == null) {
				return;
			}
			String[] subjectSubString = subjectsComboBox.getValue().split("-");
			String[] examSubString = coursesComboBox.getValue().split("-");
			examIDStart = subjectSubString[0].trim() + "" + examSubString[0].trim();
			if (examIDStart.equals("") || examIDStart == null)
				return;
		} else {
			String[] subjectSubString = subjectsComboBox.getValue().split("-");
			examIDStart = subjectSubString[0].trim();
		}
		connect(this); // connecting to server
		messageToServer[0] = "getExams";
		messageToServer[1] = examIDStart;
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	public void openLockExamScreen(ActionEvent e) throws IOException {
		openScreen("LockExam");
	}

	public void lockExam(ActionEvent e) throws IOException {
		ExecutedExam executedexam = executedExamTableView.getSelectionModel().getSelectedItem();
		if (executedexam == null) {
			openScreen("ErrorMessage", "Please choose an exam");
			return;
		}
		connect(this); // connecting to server
		messageToServer[0] = "setExecutedExamLocked";
		messageToServer[1] = executedexam.getExecutedExamID();
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	public void updateExam(Exam examSelected) {
		messageToServer[0] = "updateExam";
		messageToServer[1] = examSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	public void changeRemarksForTeacherOnTable(CellEditEvent<Exam, String> edittedCell) throws IOException {
		examSelected = examsTableView.getSelectionModel().getSelectedItem();
		oldExam = new Exam();
		oldExam.setE_id(examSelected.getE_id());
		oldExam.setSolutionTime(examSelected.getSolutionTime());
		oldExam.setRemarksForTeacher(examSelected.getRemarksForTeacher());
		oldExam.setRemarksForStudent(examSelected.getRemarksForStudent());
		oldExam.setType(examSelected.getType());
		oldExam.setTeacherUserName(examSelected.getTeacherUserName());

		if (!edittedCell.getNewValue().toString().equals(examSelected.getRemarksForTeacher())) {
			examSelected.setRemarksForTeacher(edittedCell.getNewValue().toString());
			updateExam(examSelected);
		}
	}

	public void changeRemarksForStudentOnTable(CellEditEvent<Exam, String> edittedCell) throws IOException {
		Exam examSelected = examsTableView.getSelectionModel().getSelectedItem();
		if (!edittedCell.getNewValue().toString().equals(examSelected.getRemarksForStudent())) {
			examSelected.setRemarksForStudent(edittedCell.getNewValue().toString());
			updateExam(examSelected);
		}
	}

	public void changeTypeOnTable(CellEditEvent<Exam, String> edittedCell) throws IOException {
		Exam examSelected = examsTableView.getSelectionModel().getSelectedItem();
		if (!edittedCell.getNewValue().toString().equals(examSelected.getType())) {
			examSelected.setType(edittedCell.getNewValue().toString());
			updateExam(examSelected);
		}
	}

	public void viewQuestion(ActionEvent e) throws IOException {
		try {
			Exam exam = examsTableView.getSelectionModel().getSelectedItem();
			connect(this); // connecting to server
			messageToServer[0] = "getQuestionInExam";
			messageToServer[1] = exam.getE_id();
			tempExamId = exam.getE_id();
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		} catch (NullPointerException exception) {
			openScreen("ErrorMessage", "Please select exam");
		}
	}
}
