package entity;

import java.io.Serializable;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;

public class ExamDetailsMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String examID ;
	private final String examGrade;
	private final String examDate;
	private final String examCourse;
	
	public ExamDetailsMessage(String eID,String eGrade,String eDate ) {
		examID = eID;
		examDate = eDate;
		examGrade = eGrade;
		examCourse = eID.substring(2, 4);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getExamID() {
		return examID;
	}

	public String getExamGrade() {
		return examGrade;
	}

	public String getExamDate() {
		return examDate;
	}

	public String getExamCourse() {
		return examCourse;
	}

	


}