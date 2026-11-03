package edu.uga.cs.capitalsquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CapitalsDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "CapitalsDBHelper";

    private static final String DB_NAME = "capitals.db";
    private static final int DB_VERSION = 1;

    // Define all names (strings) for table and column names.
    public static final String TABLE_CAPITALS = "capitals";
    public static final String CAPITALS_COLUMN_STATE = "state";
    public static final String CAPITALS_COLUMN_CITIES = "cities";
    public static final String CAPITALS_COLUMN_STATEHOOD = "statehood";

    // reference to only instance for helper
    private static CapitalsDBHelper helperInstance;

    // creating the table
    private static final String CREATE_CAPITALS =
            "create table " + TABLE_CAPITALS + "("
            + CAPITALS_COLUMN_CITIES + "TEXT, "
            + CAPITALS_COLUMN_STATE + "TEXT PRIMARY KEY, "
            + CAPITALS_COLUMN_STATEHOOD + "INTEGER"
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
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_CAPITALS );
        Log.d( DEBUG_TAG, "Table " + TABLE_CAPITALS + " created");
    } // onCreate

    // will upgrade database if its version (DB_VERSION) has changed.
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "drop table if exists " + TABLE_CAPITALS );
        onCreate(db);
        Log.d( DEBUG_TAG, "Table " + TABLE_CAPITALS + " upgraded");
    } // onUpgrade

}
