package com.example.scoreup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class QuizDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "toeic_quiz.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "questions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PART = "part";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_OPTION1 = "option1";
    public static final String COLUMN_OPTION2 = "option2";
    public static final String COLUMN_OPTION3 = "option3";
    public static final String COLUMN_OPTION4 = "option4";
    public static final String COLUMN_ANSWER = "answer";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PART + " TEXT,"
            + COLUMN_QUESTION + " TEXT,"
            + COLUMN_OPTION1 + " TEXT,"
            + COLUMN_OPTION2 + " TEXT,"
            + COLUMN_OPTION3 + " TEXT,"
            + COLUMN_OPTION4 + " TEXT,"
            + COLUMN_ANSWER + " TEXT"
            + ")";

    public QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PART, question.getPart());
        values.put(COLUMN_QUESTION, question.getQuestion());
        values.put(COLUMN_OPTION1, question.getOption1());
        values.put(COLUMN_OPTION2, question.getOption2());
        values.put(COLUMN_OPTION3, question.getOption3());
        values.put(COLUMN_OPTION4, question.getOption4());
        values.put(COLUMN_ANSWER, question.getAnswer());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getQuestionsForPart(String part) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PART + "=?", new String[]{part});
    }
}
