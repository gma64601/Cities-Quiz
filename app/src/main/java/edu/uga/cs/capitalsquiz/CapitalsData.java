package edu.uga.cs.capitalsquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Paint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    }; // questionColumns

    private static final String[] quizColumns = {
            CapitalsDBHelper.QUIZZES_COLUMN_ID,
            CapitalsDBHelper.QUIZZES_COLUMN_DATE,
            CapitalsDBHelper.QUIZZES_COLUMN_RESULT,
            CapitalsDBHelper.QUIZZES_COLUMN_QUESTIONS_ANSWERED
    }; // quizColumns

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

    // retrieve the quizzes
    public List<Quiz> retrieveAllQuizzes() {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;

        try {
            // execute select query and get Cursor to iterate over retrieved rows
            cursor = db.query(CapitalsDBHelper.TABLE_QUIZZES, quizColumns,
                    CapitalsDBHelper.QUIZZES_COLUMN_DATE + " > 0", null, null, null,
                    CapitalsDBHelper.QUIZZES_COLUMN_DATE + " DESC");

            // collect all quizzes into list
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getColumnCount() >= 4) {
                        // get all attributes
                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUIZZES_COLUMN_ID);
                        long id = cursor.getLong(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUIZZES_COLUMN_DATE);
                        long date = cursor.getLong(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUIZZES_COLUMN_RESULT);
                        int result = cursor.getInt(columnIndex);

                        columnIndex = cursor.getColumnIndex(CapitalsDBHelper.QUIZZES_COLUMN_QUESTIONS_ANSWERED);
                        int questionsAnswered = cursor.getInt(columnIndex);

                        Quiz quiz = new Quiz(date, result, questionsAnswered);
                        quiz.setId(id);

                        quizzes.add(quiz);

                        Log.d( DEBUG_TAG, "Retrieved Quiz: " + quiz);

                    } // if
                } // while
            } // if
        } catch (Exception e) {
            Log.d( DEBUG_TAG, "Exception caught: " + e);
        } finally {
            if (cursor != null)
                cursor.close();
        } // finally

        return quizzes;
    } // retrieveAllQuizzes

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

    // Retrieve all questions associated with a particular quiz.
    public List<Question> retrieveQuizQuestions(long quizId) {

        ArrayList<Question> questions = new ArrayList<>();
        Cursor cursor = null;

        try {

            // Join the questions and relationship tables.
            String query =
                    "SELECT q.* FROM " +
                            CapitalsDBHelper.TABLE_QUESTIONS + " q INNER JOIN " +
                            CapitalsDBHelper.TABLE_RELATIONSHIP + " r ON q." +
                            CapitalsDBHelper.QUESTIONS_COLUMN_ID + " = r." +
                            CapitalsDBHelper.RELATIONSHIP_COLUMN_QUESTION_ID +
                            " WHERE r." +
                            CapitalsDBHelper.RELATIONSHIP_COLUMN_QUIZ_ID + " = ?" +
                            " ORDER BY r." +
                            CapitalsDBHelper.RELATIONSHIP_COLUMN_ID;

            cursor = db.rawQuery(query,
                    new String[] { String.valueOf(quizId) });

            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    long id = cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_ID));

                    String state = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_STATE));

                    String capitalCity = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_CITY));

                    String secondCity = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_SECOND_CITY));

                    String thirdCity = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_THIRD_CITY));

                    int statehood = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_STATEHOOD));

                    int capitalSince = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_SINCE));

                    Question question = new Question(
                            state, capitalCity, secondCity, thirdCity,
                            statehood, capitalSince);

                    question.setId(id);

                    questions.add(question);
                }
            }

            Log.d(DEBUG_TAG, "Retrieved " + questions.size() +
                    " questions for quiz " + quizId);

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "Exception caught: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return questions;
    }
    public long storeQuiz() {
        ContentValues values = new ContentValues();

        values.put(CapitalsDBHelper.QUIZZES_COLUMN_DATE, 0);
        values.put(CapitalsDBHelper.QUIZZES_COLUMN_QUESTIONS_ANSWERED, 0);
        values.put(CapitalsDBHelper.QUIZZES_COLUMN_RESULT, 0);

        long id = db.insert(CapitalsDBHelper.TABLE_QUIZZES, null, values);

        Log.d( DEBUG_TAG, "Stored new quiz with id: " + id);

        return id;
    } // storeQuiz

    public Question storeQuestion(Question question) {
        ContentValues values = new ContentValues();

        values.put(CapitalsDBHelper.QUESTIONS_COLUMN_STATE, question.getState());
        values.put(CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_CITY, question.getCapitalCity());
        values.put(CapitalsDBHelper.QUESTIONS_COLUMN_SECOND_CITY, question.getSecondCity());
        values.put(CapitalsDBHelper.QUESTIONS_COLUMN_THIRD_CITY, question.getThirdCity());
        values.put(CapitalsDBHelper.QUESTIONS_COLUMN_STATEHOOD, question.getStatehood());
        values.put(CapitalsDBHelper.QUESTIONS_COLUMN_CAPITAL_SINCE, question.getCapitalSince());

        long id = db.insert(CapitalsDBHelper.TABLE_QUESTIONS, null, values);

        question.setId(id);

        Log.d( DEBUG_TAG, "Stored question with id: " + id);

        return question;
    } //storeQuestion

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

    // Mark a quiz as completed and store its completion date.
    public void completeQuiz(long quizId, int result, int questionsAnswered) {

        ContentValues values = new ContentValues();

        // Store the current date and time.
        values.put(CapitalsDBHelper.QUIZZES_COLUMN_DATE,
                System.currentTimeMillis());

        values.put(CapitalsDBHelper.QUIZZES_COLUMN_RESULT, result);

        values.put(CapitalsDBHelper.QUIZZES_COLUMN_QUESTIONS_ANSWERED,
                questionsAnswered);

        db.update(
                CapitalsDBHelper.TABLE_QUIZZES,
                values,
                CapitalsDBHelper.QUIZZES_COLUMN_ID + " = ?",
                new String[] { String.valueOf(quizId) }
        );

        Log.d(DEBUG_TAG, "Completed quiz with id: " + quizId);
    }

    public interface InitializationListener {
        void onInitializationComplete(boolean success);
    }

    // Initialize the questions table using the CSV file in assets.
    public void initializeQuestionsFromCSV(Context context, InitializationListener listener) {
        Log.d("CSV_INIT", "Starting CSV initialization");

        // Run CSV reading and database insertion in the background.
        new AsyncTask<Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                CapitalsData capitalsData = new CapitalsData(context);
                BufferedReader reader = null;

                try {

                    // Open the database.
                    capitalsData.open();

                    // Check whether questions already exist.
                    Cursor cursor = capitalsData.db.rawQuery(
                            "SELECT COUNT(*) FROM " +
                                    CapitalsDBHelper.TABLE_QUESTIONS,
                            null
                    );

                    int questionCount = 0;

                    if (cursor.moveToFirst()) {
                        questionCount = cursor.getInt(0);
                    }

                    cursor.close();

                    // If questions already exist, skip initialization.
                    if (questionCount > 0) {
                        Log.d(DEBUG_TAG, "Questions already exist. Skipping CSV initialization.");
                        return true;
                    }

                    // Open the CSV file from the assets folder.
                    InputStream inputStream =
                            context.getAssets().open("state_capitals.csv");

                    reader = new BufferedReader(
                            new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                    );

                    // Skip the header row.
                    String line = reader.readLine();

                    // Begin a database transaction.
                    capitalsData.db.beginTransaction();

                    try {

                        // Read each remaining CSV row.
                        while ((line = reader.readLine()) != null) {

                            // Skip empty lines.
                            if (line.trim().isEmpty()) {
                                continue;
                            }

                            // Separate the CSV columns.
                            String[] values = line.split(",");

                            // Make sure the row contains at least six columns.
                            if (values.length < 6) {
                                throw new IOException("Invalid CSV row: " + line);
                            }

                            // Extract the values from the CSV.
                            String state = values[0].trim();
                            String capitalCity = values[1].trim();
                            String secondCity = values[2].trim();
                            String thirdCity = values[3].trim();

                            int statehood = Integer.parseInt(values[4].trim());
                            int capitalSince = Integer.parseInt(values[5].trim());

                            // Create a Question object.
                            Question question = new Question(
                                    state,
                                    capitalCity,
                                    secondCity,
                                    thirdCity,
                                    statehood,
                                    capitalSince
                            );

                            // Store the question in the database.
                            capitalsData.storeQuestion(question);

                            Log.d(DEBUG_TAG, "Inserted state: " + state);
                        }

                        // Mark the transaction as successful.
                        capitalsData.db.setTransactionSuccessful();

                    } finally {

                        // End the transaction.
                        capitalsData.db.endTransaction();
                    }

                    Log.d(DEBUG_TAG, "CSV initialization completed.");

                    return true;

                } catch (Exception e) {

                    Log.e(DEBUG_TAG, "Error initializing questions from CSV", e);

                    return false;

                } finally {

                    // Close the CSV reader.
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Log.e(DEBUG_TAG, "Error closing CSV reader", e);
                        }
                    }

                    // Close the database.
                    capitalsData.close();
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {

                // This method runs on the main UI thread.
                if (listener != null) {
                    listener.onInitializationComplete(success);
                }

            }

        }.execute();
    }
}
