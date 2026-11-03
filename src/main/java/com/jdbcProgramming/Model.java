package com.jdbcProgramming;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


	
	
	import util.Credentials;

	public class Model {

	    private Connection con;

	    public Model() {
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            System.out.println("Driver is loaded successfully");

	            con = DriverManager.getConnection(
	                    Credentials.url,
	                    Credentials.user,
	                    Credentials.pwd
	            );

	            System.out.println("Connection is established successfully");

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    // Insert a question
	    public boolean insertQuestion(Question q) {

	        String query = "INSERT INTO quiz_questions "
	                + "(question, option1, option2, option3, option4, correct_answer) "
	                + "VALUES (?, ?, ?, ?, ?, ?)";

	        try (PreparedStatement pstmt = con.prepareStatement(query)) {

	            pstmt.setString(1, q.getQuestion());
	            pstmt.setString(2, q.getOption1());
	            pstmt.setString(3, q.getOption2());
	            pstmt.setString(4, q.getOption3());
	            pstmt.setString(5, q.getOption4());
	            pstmt.setInt(6, q.getCorrectAnswer());

	            return pstmt.executeUpdate() > 0;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    // Update an existing question
	    public boolean updateQuestion(Question q) {

	        String query = "UPDATE quiz_questions SET "
	                + "question=?, option1=?, option2=?, "
	                + "option3=?, option4=?, correct_answer=? "
	                + "WHERE qid=?";

	        try (PreparedStatement pstmt = con.prepareStatement(query)) {

	            pstmt.setString(1, q.getQuestion());
	            pstmt.setString(2, q.getOption1());
	            pstmt.setString(3, q.getOption2());
	            pstmt.setString(4, q.getOption3());
	            pstmt.setString(5, q.getOption4());
	            pstmt.setInt(6, q.getCorrectAnswer());
	            pstmt.setInt(7, q.getQid());

	            return pstmt.executeUpdate() > 0;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    // Delete a question using its ID
	    public boolean deleteQuestion(int qid) {

	        String query = "DELETE FROM quiz_questions WHERE qid=?";

	        try (PreparedStatement pstmt = con.prepareStatement(query)) {

	            pstmt.setInt(1, qid);

	            return pstmt.executeUpdate() > 0;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }



	    // Retrieve all questions
	    public ArrayList<Question> viewAllQuestions() {

	        String query = "SELECT * FROM quiz_questions";

	        ArrayList<Question> questions = new ArrayList<>();

	        try (PreparedStatement pstmt = con.prepareStatement(query);
	             ResultSet res = pstmt.executeQuery()) {

	            while (res.next()) {

	                Question q = new Question();

	                q.setQid(res.getInt("qid"));
	                q.setQuestion(res.getString("question"));
	                q.setOption1(res.getString("option1"));
	                q.setOption2(res.getString("option2"));
	                q.setOption3(res.getString("option3"));
	                q.setOption4(res.getString("option4"));
	                q.setCorrectAnswer(res.getInt("correct_answer"));

	                questions.add(q);
	            }

	            return questions;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    // Retrieve one question by ID
	    public Question viewQuestionByID(int qid) {

	        String query = "SELECT * FROM quiz_questions WHERE qid=?";

	        try (PreparedStatement pstmt = con.prepareStatement(query)) {

	            pstmt.setInt(1, qid);

	            try (ResultSet res = pstmt.executeQuery()) {

	                if (res.next()) {

	                    Question q = new Question();

	                    q.setQid(res.getInt("qid"));
	                    q.setQuestion(res.getString("question"));
	                    q.setOption1(res.getString("option1"));
	                    q.setOption2(res.getString("option2"));
	                    q.setOption3(res.getString("option3"));
	                    q.setOption4(res.getString("option4"));
	                    q.setCorrectAnswer(res.getInt("correct_answer"));

	                    return q;
	                }
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return null;
	    }

	    // Save a player's quiz result
	    public boolean saveResult(QuizApp q) {

	        String query = "INSERT INTO quiz_result "
	                + "(player_name, score, questions_attempted, result) "
	                + "VALUES (?, ?, ?, ?)";

	        try (PreparedStatement pstmt = con.prepareStatement(query)) {

	            pstmt.setString(1, q.getPlayerName());
	            pstmt.setLong(2, q.getScore());
	            pstmt.setInt(3, q.getQuestionsAttempted());
	            pstmt.setString(4, q.getResult());

	            return pstmt.executeUpdate() > 0;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    // Retrieve all saved player results
	    public ArrayList<QuizApp> viewAllResults() {

	        String query = "SELECT * FROM quiz_result ORDER BY id DESC";

	        ArrayList<QuizApp> results = new ArrayList<>();

	        try (PreparedStatement pstmt = con.prepareStatement(query);
	             ResultSet res = pstmt.executeQuery()) {

	            while (res.next()) {

	                QuizApp q = new QuizApp();

	                q.setId(res.getInt("id"));
	                q.setPlayerName(res.getString("player_name"));
	                q.setScore(res.getLong("score"));
	                q.setQuestionsAttempted(res.getInt("questions_attempted"));
	                q.setResult(res.getString("result"));

	                results.add(q);
	            }

	            return results;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    // Close database connection
	    public void closeConnection() {

	        try {
	            if (con != null && !con.isClosed()) {
	                con.close();
	            }

	            System.out.println("Database connection closed");

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}



