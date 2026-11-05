package edu.uga.cs.capitalsquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CapitalsDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "CapitalsDBHelper";

    private static final String DB_NAME = "capitals.db";
    private static final int DB_VERSION = 1;

    // Questions table
    public static final String TABLE_QUESTIONS = "capitals";
    public static final String QUESTIONS_COLUMN_ID = "_id";
    public static final String QUESTIONS_COLUMN_STATE = "state";
    public static final String QUESTIONS_COLUMN_CAPITAL_CITY = "capital_city";
    public static final String QUESTIONS_COLUMN_SECOND_CITY = "second_city";
    public static final String QUESTIONS_COLUMN_THIRD_CITY = "third_city";
    public static final String QUESTIONS_COLUMN_STATEHOOD = "statehood";
    public static final String QUESTIONS_COLUMN_CAPITAL_SINCE = "capital_since";


    // quizzes table
    public static final String TABLE_QUIZZES = "quizzes";
    public static final String QUIZZES_COLUMN_ID = "_id";
    public static final String QUIZZES_COLUMN_DATE = "date";
    public static final String QUIZZES_COLUMN_RESULT = "result";
    public static final String QUIZZES_COLUMN_QUESTIONS_ANSWERED = "questions_answered";


    // relationship database
    public static final String TABLE_RELATIONSHIP = "relationship";
    public static final String RELATIONSHIP_COLUMN_ID = "_id";
    public static final String RELATIONSHIP_COLUMN_QUESTION_ID = "question_id";
    public static final String RELATIONSHIP_COLUMN_QUIZ_ID = "quiz_id";
    public static final String RELATIONSHIP_COLUMN_USER_ANSWER = "user_answer";

    // reference to only instance for helper
    private static CapitalsDBHelper helperInstance;

    // creating questions table
    private static final String CREATE_QUESTIONS =
            "create table " + TABLE_QUESTIONS + "("
                    + QUESTIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUESTIONS_COLUMN_STATE + " TEXT, "
                    + QUESTIONS_COLUMN_CAPITAL_CITY + " TEXT, "
                    + QUESTIONS_COLUMN_SECOND_CITY + " TEXT, "
                    + QUESTIONS_COLUMN_THIRD_CITY + " TEXT, "
                    + QUESTIONS_COLUMN_STATEHOOD + " INTEGER, "
                    + QUESTIONS_COLUMN_CAPITAL_SINCE + " INTEGER"
            + ")";

    // creating quizzes table
    private static final String CREATE_QUIZZES =
            "create table " + TABLE_QUIZZES + "("
                    + QUIZZES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZZES_COLUMN_DATE + " INTEGER, "
                    + QUIZZES_COLUMN_RESULT + " INTEGER, "
                    + QUIZZES_COLUMN_QUESTIONS_ANSWERED + " INTEGER"
            + ")";

    // creating relationship table
    private static final String CREATE_RELATIONSHIP =
            "create table " + TABLE_RELATIONSHIP + "("
                    + RELATIONSHIP_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + RELATIONSHIP_COLUMN_QUESTION_ID + " INTEGER, "
                    + RELATIONSHIP_COLUMN_QUIZ_ID + " INTEGER, "
                    + RELATIONSHIP_COLUMN_USER_ANSWER + " TEXT "
                    + "FOREIGN KEY (" + RELATIONSHIP_COLUMN_QUESTION_ID + ") REFERENCES " + TABLE_QUESTIONS
                        + "(" + QUESTIONS_COLUMN_ID + ") ON DELETE CASCADE, "
                    + "FOREIGN KEY (" + RELATIONSHIP_COLUMN_QUIZ_ID + ") REFERENCES " + TABLE_QUIZZES
                        + "(" + QUIZZES_COLUMN_ID + ") ON DELETE CASCADE"
            + ")";

    // constructor
    private CapitalsDBHelper( Context context ) {
        super ( context, DB_NAME, null, DB_VERSION);
    } // CapitalsDBHelper

    // access method to single instance of the class
    public synchronized static CapitalsDBHelper getInstance(Context context ) {
        // check if instance already exists. if not, create the instance
        if (helperInstance == null ) {
            helperInstance = new CapitalsDBHelper(context.getApplicationContext());
        } // if
        return helperInstance;
    } // getInstance

    @Override
    public void onConfigure( SQLiteDatabase db ) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    } // onConfigure

    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_QUESTIONS );
        db.execSQL( CREATE_QUIZZES );
        db.execSQL( CREATE_RELATIONSHIP );
        Log.d( DEBUG_TAG, "Tables created");
    } // onCreate

    // will upgrade database if its version (DB_VERSION) has changed.
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "drop table if exists " + TABLE_QUIZZES );
        db.execSQL( "drop table if exists " + TABLE_QUESTIONS );
        db.execSQL( "drop table if exists " + TABLE_RELATIONSHIP );
        onCreate(db);
        Log.d( DEBUG_TAG, "Tables upgraded");
    } // onUpgrade

}
