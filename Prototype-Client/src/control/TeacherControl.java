package control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import entity.Question;
import entity.QuestionInExam;
import entity.TeachingProfessionals;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TeacherControl extends  UserControl implements Initializable  {
	
	private Boolean trueAnsFlag=false;// 
	private String selectedQuestion;
	private String subject;
	private Object[] messageToServer=new Object[3];
	ObservableList<QuestionInExam> questionInExamObservable=FXCollections.observableArrayList();
	/* fxml variables */
	@FXML
	private Text userText;
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
	private TextField createAnswer1;
	@FXML
	private TextField createAnswer2;
	@FXML
	private TextField createAnswer3;
	@FXML
	private TextField createAnswer4;
	
	@FXML
	private TextField pointsText;
	@FXML
	private TextField questionName;
	@FXML
	private TextField questionID;
	@FXML
	private TextField teacherName;
/*buttons of display the correct answer*/
	@FXML
	private RadioButton correctAns1;
	@FXML
	private RadioButton correctAns2;
	@FXML
	private RadioButton correctAns3;
	@FXML
	private RadioButton correctAns4;
	
	@FXML
	private RadioButton createCorrectAnswer1;
	@FXML
	private RadioButton createCorrectAnswer2;
	@FXML
	private RadioButton createCorrectAnswer3;
	@FXML
	private RadioButton createCorrectAnswer4;
	
	@FXML
	private ToggleGroup group;
	@FXML
	private Button createQuestionBtn;
	
	@FXML
	private Tab createExamTab;
	@FXML
	private Tab createQuestion;
	@FXML
	private Tab changeQuestionTab;
	
	@FXML
	public void initializeQuestions() {
		//teacherNameOnCreate.setText(userNameFromDB);
		connect(this); 
		messageToServer[0]="getSubjects";
		messageToServer[1]=null;
		messageToServer[2]=null;
		chat.handleMessageFromClientUI(messageToServer);//send the message to server
	}

	@FXML
	private TableView<QuestionInExam> questionsInExamTableView;
	@FXML
	private TableColumn<QuestionInExam,String> questionNameTableView;
	@FXML
	private TableColumn<QuestionInExam,Float> questionPointsTableView;
	
	@FXML
	private ComboBox<String> subjectsComboBoxInCreate;
	@FXML
	private ComboBox<String> questionsComboBox;
	@FXML
	private ComboBox<String> subjectsComboBox;
	@FXML
	private ComboBox<String> subjectsComboBoxInBuildExam;
	@FXML
	private ComboBox<String> questionsComboBoxInCreate;
	
	public void initializeSubjectsForCreateQuestion() {
		teacherNameOnCreate.setText(userNameFromDB);
		connect(this); 
		messageToServer[0]="getSubjects";
		messageToServer[1]=null;
		messageToServer[2]=null;
		chat.handleMessageFromClientUI(messageToServer);//send the message to server
	}
/*initialized the update Question window*/
	public void createQuestionClick(ActionEvent e)throws IOException{
		Question question=new Question();
		ArrayList<String> answers=new ArrayList<String>();
		question.setTeacherName(userNameFromDB);
		question.setQuestionContent(questionName.getText().trim());
		answers.add(createAnswer1.getText().trim());
		answers.add(createAnswer2.getText().trim());
		answers.add(createAnswer3.getText().trim());
		answers.add(createAnswer4.getText().trim());
		
		int correctAnswer=0;
		if(createCorrectAnswer1.isSelected()) {
			correctAnswer=1;
		}
		if(createCorrectAnswer2.isSelected()) {
			correctAnswer=2;
		}
		if(createCorrectAnswer3.isSelected()) {
			correctAnswer=3;
		}
		if(createCorrectAnswer4.isSelected()) {
			correctAnswer=4;
		}
		
		if((answers.get(0).equals("")) ||((answers.get(1).equals("")))||(answers.get(2).equals(""))||(answers.get(3).equals(""))||(correctAnswer==0)||(question.getQuestionContent().equals(""))) {
			openScreen("ErrorMessage");
		}
		else {
			question.setAnswers(answers);
			question.setTrueAnswer(correctAnswer);
			subject = subjectsComboBoxInCreate.getValue();
			connect(this); //connecting to server
			messageToServer[0]="SetQuestion";
			messageToServer[1]=subject;
			messageToServer[2]=question;
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		}
			
	}
	
	public void loadQuestions(ActionEvent e) throws IOException {
		/*ask for the qustions text*/
		if(pageLabel.getText().equals("Update question"))
			subject = subjectsComboBox.getValue(); // get the subject code
		if(pageLabel.getText().equals("Create question"))
			subject = subjectsComboBoxInBuildExam.getValue(); // get the subject code
		if(subject.equals(null))
			return;
		if(pageLabel.getText().equals("Update question"))
			Platform.runLater(() -> questionsComboBox.getItems().clear());
		Platform.runLater(() -> questionsComboBox.getSelectionModel().clearSelection());
			
		if(pageLabel.getText().equals("Create exam"))
			questionsComboBoxInCreate.getSelectionModel().clearSelection(); 
		
		clearForm();
		
		connect(this); //connecting to server
		messageToServer[0]="getQuestions";
		messageToServer[1]=subject;
		messageToServer[2]=null;
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}
	
	public void toQuestionInExam(ActionEvent e) {
		//if(!questionInExamObservable.contains(questionsComboBoxInCreate.getValue())) {
			QuestionInExam questioninexam=new QuestionInExam();
			questioninexam.setName(questionsComboBoxInCreate.getValue());
			questioninexam.setPoints(Integer.parseInt(pointsText.getText()));
			questionInExamObservable.add(questioninexam);
			questionsInExamTableView.setItems(null);
			questionsInExamTableView.setItems(questionInExamObservable);
			questionNameTableView.setCellValueFactory(new PropertyValueFactory<>("name"));
			questionPointsTableView.setCellValueFactory(new PropertyValueFactory<>("points"));
		//}

	}
	/*public void uploadQuestion(ActionEvent e) {
		QuestionInExam questioninexam=questionInExamObservable.get(questionsInExamTableView.getSelectionModel().getSelectedIndex());
		pointsText.setText(String.valueOf(questioninexam.getPoints()));
	}*/
	
	public void askForQuestionDetails(ActionEvent e) throws IOException {
		
		selectedQuestion = questionsComboBox.getValue(); // get the selected question
		if(selectedQuestion==null)
			return;
		connect(this); // connecting to server
		messageToServer[0]="getQuestionDetails";
		messageToServer[1]=selectedQuestion;
		messageToServer[2]=null;
		clearForm();
		if(selectedQuestion!=null)
		chat.handleMessageFromClientUI(messageToServer);// ask for details of specific question
		
	}

	/* this method show the subjects list on the combobox */
	public void showSubjects(ArrayList<TeachingProfessionals> msg) {
		ObservableList<String> observableList = FXCollections.observableArrayList();
		for(TeachingProfessionals tp:msg) {
			observableList.add(tp.getTp_id());
		}				
		if(pageLabel.getText().equals("Create question")) {
			//clearForm();
			//subjectsComboBox.getItems().clear();
			//questionsComboBox.getItems().clear();
			subjectsComboBoxInCreate.setItems(observableList);
		}
		if(pageLabel.getText().equals("Update question")) {
			//subjectsComboBoxInCreate.getItems().clear();
			subjectsComboBox.setItems(observableList);
		}
		if(pageLabel.getText().equals("Create exam")) {
			subjectsComboBoxInBuildExam.setItems(observableList);

		}
	}

	/* this method show the questions list on the combobox */
	public void showQuestions(ArrayList<String> questionsList) 
	{
		ObservableList<String> observableList = FXCollections.observableArrayList(questionsList);
		if(pageLabel.getText().equals("Update question"))
			questionsComboBox.setItems(observableList);
		if(pageLabel.getText().equals("Create exam"))
			questionsComboBoxInCreate.setItems(observableList);
	}
		

	/* this method show the question details to user*/
	public void showQuestionDetails(Question q){
		try {
		trueAnsFlag=true;// Permit to user change the correct answer
		questionID.setText(q.getId());
		teacherName.setText(q.getTeacherName());
		ArrayList<String> answers = q.getAnswers();
		answer1.setText(answers.get(0));
		answer2.setText(answers.get(1));
		answer3.setText(answers.get(2));
		answer4.setText(answers.get(3));
		
		/*set up the correct answer button*/
		switch (q.getTrueAnswer()) {/*The number of the correct answers*/
		case (1): {
			correctAns1.setSelected(true);
			break;
		}
		case (2): {
			correctAns2.setSelected(true);
			break;
		}
		case (3): {
			correctAns3.setSelected(true);
			break;
		}
		case (4): {
			correctAns4.setSelected(true);
			break;
		}
		}
		}
		catch(Exception e) {}
	}

	public void openUpdateQuestionScreen(ActionEvent e) {
		openScreen("UpdateQuestion");
	}
	
	public void openCreateQuestion(ActionEvent e) {
		openScreen("CreateQuestion");
	}
	private void openScreen(String screen) {
		try{
			   Parent root = FXMLLoader.load(getClass().getResource("/boundary/"+screen+".fxml"));
	            Scene scene = new Scene(root);
	            Stage stage=Main.getStage();
	            stage.setTitle("Create question");
	            stage.setScene(scene);  
	            stage.show();
	          }catch(Exception exception) {
	        	  System.out.println("Error in opening the page");
	          }		
	}
	
	/*send to server request for update correct answer*/
	public void updateCorrectAnswer(ActionEvent e) throws IOException, SQLException {		
		if (trueAnsFlag) {//if there is permission to update the question
			String qID=questionID.getText();
			RadioButton selected = (RadioButton)group.getSelectedToggle();
			String selectedId=selected.getId();
			messageToServer[0]="updateCorrectAnswer";
			messageToServer[1]=qID;
			messageToServer[2]= selectedId;
			connect(this);
			chat.handleMessageFromClientUI(messageToServer); //send the request to the server
			chat.closeConnection();
		}
	}
	
	/*cancel button was pressed*/
	public void closeScreen(ActionEvent e) throws IOException, SQLException {
	    final Node source = (Node) e.getSource();
	    Stage stage = (Stage) source.getScene().getWindow();
	    stage.close();	
	    openScreen("HomeScreenTeacher");
	}
	
	/* check the content message from server */
	@SuppressWarnings("unchecked")
	public void checkMessage(Object message) {
		try {
			chat.closeConnection();//close the connection

			Object[] msg = (Object[]) message;
			switch (msg[0].toString()) {
			case ("getSubjects"): /* get the subjects list from server */
			{
				showSubjects((ArrayList<TeachingProfessionals>) msg[1]);
				break;
			}
			case ("getQuestions"): /* get the questions list from server */
			{
				showQuestions((ArrayList<String>) msg[1]);
				break;
			}
			case ("getQuestionDetails"): /* get the subject list from server */
			{
				showQuestionDetails((Question) msg[1]);
				break;
			}
			default:{
				System.out.println("Error in input");
			}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/*clear all the text fields and radio buttons*/
	public void clearForm()
	{
	answer1.clear();
	answer2.clear();
	answer3.clear();
	answer4.clear();
	questionID.clear();
	teacherName.clear();
	correctAns1.setSelected(false);
	correctAns2.setSelected(false);
	correctAns3.setSelected(false);
	correctAns4.setSelected(false);
	}
	
   public void initialize(URL url, ResourceBundle rb) {
		//userText.setText(userNameFromDB);//trouble in create new screen
	   if(pageLabel.getText().equals("Create question")||pageLabel.getText().equals("Update question")) {
		   initializeQuestions();
	   }
	}
 
}
