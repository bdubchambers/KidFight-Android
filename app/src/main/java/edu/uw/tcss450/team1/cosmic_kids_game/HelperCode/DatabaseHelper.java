package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import edu.uw.tcss450.team1.cosmic_kids_game.R;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    //LOG TAG
    public static final String TAG = "DatabaseHelper CLASS";

    // Database Version and Name
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "WORDS.DB";

    // Words table name
    public static final String TABLE_NAME = "tableofwords";

    // Words Table Column names
    public static final String KEY_ID = "_id";
    public static final String COL_GRADE = "grade";
    public static final String COL_WORD = "word";

    //Create table query String
   private static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_WORD + " TEXT NOT NULL UNIQUE, "
                    + COL_GRADE + " TEXT);";

    /**
     * Constructor
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Create Tables
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);

        /*========================================================================================
                Extract data from the file, 'words.xml, found in res/xml/
         */
        ContentValues cv = new ContentValues();
        Resources res = context.getResources();
        XmlResourceParser wordsXML = res.getXml(R.xml.words);
        try {
            int eventType = wordsXML.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if((eventType == XmlPullParser.START_TAG) && (wordsXML.getName().equals("record"))){
                    String word = wordsXML.getAttributeValue(null, DatabaseHelper.COL_WORD);
                    String grade = wordsXML.getAttributeValue(null, DatabaseHelper.COL_GRADE);
                    cv.put(DatabaseHelper.COL_WORD, word);
                    cv.put(DatabaseHelper.COL_GRADE, grade);
                    db.insert(DatabaseHelper.TABLE_NAME, null, cv);
                }
                eventType = wordsXML.next();
            }
        } catch(XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch(IOException e){
            Log.e(TAG, e.getMessage(), e);
        } finally {
            wordsXML.close();
        }
        //========================================================================================
    }

    /**
     * Make changes to DB and Tables, create new db
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}