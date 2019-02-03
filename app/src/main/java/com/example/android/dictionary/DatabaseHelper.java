package com.example.android.dictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    private static String DB_NAME = "eng_dictionary.db";
    private SQLiteDatabase myDataBase;
    private final Context mContext;
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.mContext = context;
        this.DB_PATH = "/data/data" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH );
    }
}
