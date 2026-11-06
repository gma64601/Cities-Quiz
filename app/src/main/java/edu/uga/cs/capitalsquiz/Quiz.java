package edu.uga.cs.capitalsquiz;

public class Quiz {

    private long id;
    private String date;
    private String result;
    private String questionsAnswered;

    public Quiz() {
        this.id = -1;
        this.date = null;
        this.result = null;
        this.questionsAnswered = null;
    } // Quiz

    public Quiz(String date, String result, String questionsAnswered) {
        this.id = -1;
        this.date = date;
        this.result = result;
        this.questionsAnswered = questionsAnswered;
    } // Quiz

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }

    public String getQuestionsAnswered() { return questionsAnswered; }

    public void setQuestionsAnswered(String questionsAnswered) { this.questionsAnswered = questionsAnswered; }
}
