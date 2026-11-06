package edu.uga.cs.capitalsquiz;

public class Quiz {

    private long id;
    private long date;
    private int result;
    private int questionsAnswered;

    public Quiz() {
        this.id = -1;
        this.date = 0;
        this.result = 0;
        this.questionsAnswered = 0;
    } // Quiz

    public Quiz(long date, int result, int questionsAnswered) {
        this.id = -1;
        this.date = date;
        this.result = result;
        this.questionsAnswered = questionsAnswered;
    } // Quiz

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public long getDate() { return date; }

    public void setDate(long date) { this.date = date; }

    public int getResult() { return result; }

    public void setResult(int result) { this.result = result; }

    public int getQuestionsAnswered() { return questionsAnswered; }

    public void setQuestionsAnswered(int questionsAnswered) { this.questionsAnswered = questionsAnswered; }
}
