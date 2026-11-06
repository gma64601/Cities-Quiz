package edu.uga.cs.capitalsquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CapitalsData {

    public static final String DEBUG_TAG = "CapitalsData";

    // reference to the database
    private SQLiteDatabase db;
    private SQLiteOpenHelper capitalsDbHelper;
    private static final String[] questionColumns = {
            CapitalsDBHelper.QUESTIONS_COLUMN_ID,
            CapitalsDBHelper.QUESTIONS_COLUMN_STATE,
            CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_CITY,
            CapitalsDBHelper.QUESTIONS_COLUMN_SECOND_CITY,
            CapitalsDBHelper.QUESTIONS_COLUMN_THIRD_CITY,
            CapitalsDBHelper.QUESTIONS_COLUMN_STATEHOOD,
            CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_SINCE
    };

    public CapitalsData( Context context ) {
        capitalsDbHelper = CapitalsDBHelper.getInstance(context);
    } // CapitalsData

    // open database
    public void open() {
        db = capitalsDbHelper.getWritableDatabase();
        Log.d( DEBUG_TAG, "CapitalsData: db open");
    } // open

    // close database
    public void close() {
        if (capitalsDbHelper != null) {
            capitalsDbHelper.close();
            Log.d( DEBUG_TAG, "CapitalsData: db closed");
        } // if
    } // close

    // boolean for if the database is open or not
    public boolean isDbOpen() {
        return db.isOpen();
    } // isDbOpen

    // retrieve the questions
    public List<Question> retrieveAllQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;

        try {
            // execute the select query and get Cursor to iterate over retrieved rows
            cursor = db.query( CapitalsDBHelper.TABLE_QUESTIONS, questionColumns,
                    null, null, null, null, null);

            // collect all questions into list
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getColumnCount() >= 7) {

                        // get all attributes
                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_ID);
                        long id = cursor.getLong(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_STATE);
                        String state = cursor.getString(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_CITY);
                        String capitalCity = cursor.getString(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_SECOND_CITY);
                        String secondCity = cursor.getString(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_THIRD_CITY);
                        String thirdCity = cursor.getString(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_STATEHOOD);
                        int statehood = cursor.getInt(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_SINCE);
                        int capitalSince = cursor.getInt(columnIndex);

                        // create new Question object and set state to retrieved values
                        Question question = new Question(state, capitalCity, secondCity, thirdCity, statehood, capitalSince);
                        question.setId(id);
                        questions.add(question);
                        Log.d( DEBUG_TAG, "Retrieved Question: " + question);

                    } // if
                } // while
            } // if
            if (cursor != null) {
                Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount());
            } else {
                Log.d( DEBUG_TAG, "Number of records from DB: 0");
            } // if else
        } catch (Exception e){
            Log.d( DEBUG_TAG, "Exception caught: " + e);
        }
        finally {
            if (cursor != null)
                cursor.close();
        } // finally

        return questions;
    } // retrieveAllQuestions

    public long storeQuiz() {
        ContentValues values = new ContentValues();

        values.put(CapitalsDBHelper.QUIZZES_COLUMN_DATE, 0);
        values.put(CapitalsDBHelper.QUIZZES_COLUMN_QUESTIONS_ANSWERED, 0);
        values.put(CapitalsDBHelper.QUIZZES_COLUMN_RESULT, 0);

        long id = db.insert(CapitalsDBHelper.TABLE_QUIZZES, null, values);

        Log.d( DEBUG_TAG, "Stored new quiz with id: " + id);

        return id;
    } // storeQuiz

    public void storeQuizQuestion(long questionId, long quizId, String userAnswer) {

        ContentValues values = new ContentValues();

        values.put(CapitalsDBHelper.RELATIONSHIP_COLUMN_QUESTION_ID, questionId);
        values.put(CapitalsDBHelper.RELATIONSHIP_COLUMN_QUIZ_ID, quizId);
        values.put(CapitalsDBHelper.RELATIONSHIP_COLUMN_USER_ANSWER, userAnswer);

        // Insert the relationship
        long id = db.insert(CapitalsDBHelper.TABLE_RELATIONSHIP, null, values);

        Log.d(DEBUG_TAG, "Stored quiz question relationship with id: " + id);
    }

    // Update the score and number of questions answered in a quiz.
    public void updateQuiz(long quizId, int result, int questionsAnswered) {

        ContentValues values = new ContentValues();

        values.put(CapitalsDBHelper.QUIZZES_COLUMN_RESULT, result);

        values.put(CapitalsDBHelper.QUIZZES_COLUMN_QUESTIONS_ANSWERED,
                questionsAnswered);

        // Update the quiz with the matching ID
        db.update(CapitalsDBHelper.TABLE_QUIZZES, values,
                CapitalsDBHelper.QUIZZES_COLUMN_ID + " = ?",
                new String[] { String.valueOf(quizId) });

        Log.d(DEBUG_TAG, "Updated quiz with id: " + quizId);
    }
}
