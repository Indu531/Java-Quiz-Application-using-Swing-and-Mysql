package com.jdbcProgramming;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;
import com.jdbcProgramming.Model;
import com.jdbcProgramming.Question;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApp extends JFrame {

    private final Model m = new Model();

    private JTextField nameField = new JTextField(15);

    private JLabel questionLabel = new JLabel(
            "Enter your name and click Start Quiz",
            SwingConstants.CENTER);

    private JLabel rewardLabel = new JLabel("Reward: 0 points", SwingConstants.CENTER);
    private JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);

    private JButton[] optionButtons = new JButton[4];

    private JButton startButton = new JButton("Start Quiz");
    private JButton insertButton = new JButton("Insert Question");
    private JButton updateButton = new JButton("Update Question");
    private JButton deleteButton = new JButton("Delete Question");
    private JButton viewQuestionsButton = new JButton("View Questions");
    private JButton audienceButton = new JButton("Audience Poll");
    private JButton fiftyButton = new JButton("50:50");
    private JButton resultsButton = new JButton("View Results");
    private JButton exitButton = new JButton("Exit");

    // Questions are loaded from MySQL; there are no hardcoded question arrays.
    private ArrayList<Question> questions = new ArrayList<>();

    

    private int questionIndex = 0;
    private long currentEarnings = 0;
    private long safeEarnings = 0;
    private String playerName = "";

    private boolean gameRunning = false;
    private boolean audiencePollUsed = false;
    private boolean fiftyFiftyUsed = false;

    public MainApp() {
        setTitle("Java Quiz Application");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TOP PANEL
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Player Name:"));
        namePanel.add(nameField);
        namePanel.add(startButton);

        JPanel managementPanel = new JPanel();
        managementPanel.add(insertButton);
        managementPanel.add(updateButton);
        managementPanel.add(deleteButton);
        managementPanel.add(viewQuestionsButton);

        JPanel menuPanel = new JPanel();
        menuPanel.add(resultsButton);
        menuPanel.add(exitButton);

        topPanel.add(namePanel);
        topPanel.add(managementPanel);
        topPanel.add(menuPanel);

        // QUESTION AND ANSWER PANEL
        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        for (int i = 0; i < 4; i++) {
            final int selectedOption = i + 1;
            optionButtons[i] = new JButton("Option " + selectedOption);
            optionButtons[i].addActionListener(e -> checkAnswer(selectedOption));
            answerPanel.add(optionButtons[i]);
        }

        centerPanel.add(questionLabel, BorderLayout.NORTH);
        centerPanel.add(answerPanel, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel scorePanel = new JPanel();
        scorePanel.add(rewardLabel);
        scorePanel.add(scoreLabel);

        JPanel lifelinePanel = new JPanel();
        lifelinePanel.add(audienceButton);
        lifelinePanel.add(fiftyButton);

        bottomPanel.add(scorePanel);
        bottomPanel.add(lifelinePanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // BUTTON ACTIONS
        startButton.addActionListener(e -> startQuiz());
        insertButton.addActionListener(e -> insertQuestion());
        updateButton.addActionListener(e -> updateQuestion());
        deleteButton.addActionListener(e -> deleteQuestion());
        viewQuestionsButton.addActionListener(e -> viewQuestions());
        audienceButton.addActionListener(e -> useAudiencePoll());
        fiftyButton.addActionListener(e -> useFiftyFifty());
        resultsButton.addActionListener(e -> viewResults());
        exitButton.addActionListener(e -> exitApplication());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        setGameControls(false);
        setManagementControls(true);
        setVisible(true);
    }

    // Read question details through Swing dialogs.
    private Question collectQuestionDetails(int qid, Question existing) {
        String question = JOptionPane.showInputDialog(
                this, "Enter question:", existing == null ? "" : existing.getQuestion());
        if (question == null) return null;
        if (question.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question cannot be empty.");
            return null;
        }

        String option1 = JOptionPane.showInputDialog(
                this, "Enter option 1:", existing == null ? "" : existing.getOption1());
        if (option1 == null) return null;

        String option2 = JOptionPane.showInputDialog(
                this, "Enter option 2:", existing == null ? "" : existing.getOption2());
        if (option2 == null) return null;

        String option3 = JOptionPane.showInputDialog(
                this, "Enter option 3:", existing == null ? "" : existing.getOption3());
        if (option3 == null) return null;

        String option4 = JOptionPane.showInputDialog(
                this, "Enter option 4:", existing == null ? "" : existing.getOption4());
        if (option4 == null) return null;

        String answerText = JOptionPane.showInputDialog(
                this, "Enter correct option number (1-4):",
                existing == null ? "" : String.valueOf(existing.getCorrectAnswer()));
        if (answerText == null) return null;

        int answer;
        try {
            answer = Integer.parseInt(answerText.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Correct answer must be a number from 1 to 4.");
            return null;
        }

        if (answer < 1 || answer > 4) {
            JOptionPane.showMessageDialog(this, "Correct answer must be between 1 and 4.");
            return null;
        }

        Question q = new Question();
        q.setQid(qid);
        q.setQuestion(question.trim());
        q.setOption1(option1.trim());
        q.setOption2(option2.trim());
        q.setOption3(option3.trim());
        q.setOption4(option4.trim());
        q.setCorrectAnswer(answer);
        return q;
    }

    private void insertQuestion() {
        if (gameRunning) return;

        Question q = collectQuestionDetails(0, null);
        if (q == null) return;

        if (m.insertQuestion(q)) {
            JOptionPane.showMessageDialog(this, "Question inserted successfully into MySQL.");
        } else {
            JOptionPane.showMessageDialog(this, "Question could not be inserted.");
        }
    }

    private void updateQuestion() {
        if (gameRunning) return;

        String idText = JOptionPane.showInputDialog(this, "Enter Question ID (qid) to update:");
        if (idText == null) return;

        int qid;
        try {
            qid = Integer.parseInt(idText.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric question ID.");
            return;
        }

        Question existing = m.viewQuestionByID(qid);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Question ID not found.");
            return;
        }

        Question updated = collectQuestionDetails(qid, existing);
        if (updated == null) return;

        if (m.updateQuestion(updated)) {
            JOptionPane.showMessageDialog(this, "Question updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Question could not be updated.");
        }
    }

    private void deleteQuestion() {
        if (gameRunning) return;

        String idText = JOptionPane.showInputDialog(this, "Enter Question ID (qid) to delete:");
        if (idText == null) return;

        int qid;
        try {
            qid = Integer.parseInt(idText.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric question ID.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this, "Delete question ID " + qid + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (m.deleteQuestion(qid)) {
                JOptionPane.showMessageDialog(this, "Question deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Question ID not found or delete failed.");
            }
        }
    }

    private void viewQuestions() {
        ArrayList<Question> list = m.viewAllQuestions();

        if (list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found in quiz_questions.");
            return;
        }

        StringBuilder output = new StringBuilder();
        for (Question q : list) {
            output.append("Question ID: ").append(q.getQid()).append("\n")
                  .append("Question: ").append(q.getQuestion()).append("\n")
                  .append("1. ").append(q.getOption1()).append("\n")
                  .append("2. ").append(q.getOption2()).append("\n")
                  .append("3. ").append(q.getOption3()).append("\n")
                  .append("4. ").append(q.getOption4()).append("\n")
                  .append("Correct option: ").append(q.getCorrectAnswer())
                  .append("\n------------------------------------\n");
        }

        JTextArea area = new JTextArea(output.toString());
        area.setEditable(false);
        area.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));

        JOptionPane.showMessageDialog(
                this, scrollPane, "Questions from MySQL",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Start the quiz using questions fetched from MySQL.
    private void startQuiz() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.");
            return;
        }

        ArrayList<Question> dbQuestions = m.viewAllQuestions();

        if (dbQuestions == null || dbQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "No questions are available in quiz_questions. Please insert questions first.");
            return;
        }

        questions = dbQuestions;
        playerName = name;
        questionIndex = 0;
        currentEarnings = 0;
        safeEarnings = 0;
        audiencePollUsed = false;
        fiftyFiftyUsed = false;
        gameRunning = true;

        nameField.setEnabled(false);
        startButton.setEnabled(false);
        setManagementControls(false);
        resultsButton.setEnabled(false);

        scoreLabel.setText("Score: 0");
        showQuestion();
    }

    private long getReward(int index) {
        return 100L * (1L << index);
    }

    // Display the current question and its four options.
    private void showQuestion() {
        if (questionIndex >= questions.size()) {
            finishGame("Completed");
            return;
        }

        Question q = questions.get(questionIndex);

        questionLabel.setText(
                "<html><div style='text-align:center;width:700px'>"
                + "Question " + (questionIndex + 1) + " of " + questions.size()
                + "<br><br>" + q.getQuestion()
                + "</div></html>");

        rewardLabel.setText("Reward: " + getReward(questionIndex) + " points");

        String[] options = {
            q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()
        };

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((i + 1) + ". " + options[i]);
            optionButtons[i].setEnabled(true);
        }

        boolean lifelineAllowed = questionIndex < 7;
        audienceButton.setEnabled(lifelineAllowed && !audiencePollUsed);
        fiftyButton.setEnabled(lifelineAllowed && !fiftyFiftyUsed);
    }


private void checkAnswer(int selectedAnswer) {

    if (!gameRunning || questionIndex >= questions.size()) {
        return;
    }

    int correctAnswer = questions.get(questionIndex).getCorrectAnswer();

    if (selectedAnswer == correctAnswer) {

    	currentEarnings += getReward(questionIndex);
        // Save safe score after completing Question 4
        if (questionIndex == 3) {
            safeEarnings = currentEarnings;
        }

        questionIndex++;

        if (questionIndex == questions.size()) {
            finishGame("Completed");
        } else {
            showQuestion();
        }

    } else {

        // Wrong answer in Questions 1-4
        if (questionIndex < 4) {
            currentEarnings = 0;
        } 
        // Wrong answer in Questions 5-10
        else {
            currentEarnings = safeEarnings;
        }

        finishGame("Wrong Answer");
    }
}


    private void useAudiencePoll() {
        if (!gameRunning || audiencePollUsed || questionIndex >= 7) return;

        audiencePollUsed = true;
        int correct = questions.get(questionIndex).getCorrectAnswer();

        StringBuilder poll = new StringBuilder("Audience Poll Results:\n\n");
        for (int i = 1; i <= 4; i++) {
            int percentage = (i == correct) ? 70 : 10;
            poll.append("Option ").append(i).append(": ")
                .append(percentage).append("%\n");
        }

        JOptionPane.showMessageDialog(this, poll.toString());
        audienceButton.setEnabled(false);
    }

    private void useFiftyFifty() {
        if (!gameRunning || fiftyFiftyUsed || questionIndex >= 7) return;

        fiftyFiftyUsed = true;
        int correct = questions.get(questionIndex).getCorrectAnswer();
        int keptWrong = (correct % 4) + 1;

        for (int i = 0; i < 4; i++) {
            int optionNumber = i + 1;
            boolean keep = optionNumber == correct || optionNumber == keptWrong;
            optionButtons[i].setEnabled(keep);
        }

        fiftyButton.setEnabled(false);
        JOptionPane.showMessageDialog(this, "50:50 activated! Two options removed.");
    }

    // Save the result into quiz_result.
    private void finishGame(String status) {
        gameRunning = false;

        int attempted = "Completed".equals(status)
                ? questions.size() : Math.min(questionIndex + 1, questions.size());

        QuizApp q = new QuizApp(
                playerName,
                currentEarnings,
                attempted,
                status);

        boolean saved = m.saveResult(q);

        setGameControls(false);

        questionLabel.setText(
                "<html><div style='text-align:center;'>"
                + "Quiz Finished!<br><br>"
                + "Player: " + playerName
                + "<br>Result: " + status
                + "<br>Final score: " + currentEarnings
                + "</div></html>");

        rewardLabel.setText("Final score: " + currentEarnings);
        scoreLabel.setText("Result: " + status);

        JOptionPane.showMessageDialog(
                this,
                "Quiz Finished!\n"
                + "Player: " + playerName
                + "\nScore: " + currentEarnings
                + "\nResult: " + status
                + "\nSaved to MySQL: " + (saved ? "Yes" : "No"));

        nameField.setEnabled(true);
        startButton.setEnabled(true);
        resultsButton.setEnabled(true);
        setManagementControls(true);
    }

    private void setGameControls(boolean enabled) {
        for (JButton button : optionButtons) {
            if (button != null) button.setEnabled(enabled);
        }
        audienceButton.setEnabled(enabled);
        fiftyButton.setEnabled(enabled);
    }

    private void setManagementControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        viewQuestionsButton.setEnabled(enabled);
    }

    private void viewResults() {
        ArrayList<QuizApp> al = m.viewAllResults();

        if (al == null || al.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "No results found or database unavailable.");
            return;
        }

        StringBuilder output = new StringBuilder();
        for (QuizApp q : al) {
            output.append(q).append("\n--------------------------\n");
        }

        JTextArea area = new JTextArea(output.toString());
        area.setEditable(false);
        area.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new java.awt.Dimension(450, 350));

        JOptionPane.showMessageDialog(
                this, scrollPane, "All Quiz Results",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            m.closeConnection();
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}



