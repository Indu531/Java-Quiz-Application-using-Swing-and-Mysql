package com.jdbcProgramming;

	public class QuizApp {



	    private int id;
	    private String playerName;
	    private long score;
	    private int questionsAttempted;
	    private String result;

	   

	    public  QuizApp(String playerName, long currentEarnings,int questionsAttempted, String result) {

	        this.playerName = playerName;
	        this.score = currentEarnings;
	        this.questionsAttempted = questionsAttempted;
	        this.result = result;
	    }

	    public QuizApp() {
			// TODO Auto-generated constructor stub
		}

		public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getPlayerName() {
	        return playerName;
	    }

	    public void setPlayerName(String playerName) {
	        this.playerName = playerName;
	    }

	    public long getScore() {
	        return score;
	    }

	    public void setScore(long score) {
	        this.score = score;
	    }

	    public int getQuestionsAttempted() {
	        return questionsAttempted;
	    }

	    public void setQuestionsAttempted(int questionsAttempted) {
	        this.questionsAttempted = questionsAttempted;
	    }

	    public String getResult() {
	        return result;
	    }

	    public void setResult(String result) {
	        this.result = result;
	    }

	    @Override
	    public String toString() {
	        return "ID: " + id
	                + "\nPlayer Name: " + playerName
	                + "\nScore: " + score
	                + "\nQuestions Attempted: " + questionsAttempted
	                + "\nResult: " + result;
	    }
	}






