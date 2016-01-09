package pl.pydyniak.payments;

import static pl.pydyniak.payments.DbConstants.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rafal on 29.11.15.
 */
public class PaymentsDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_CREATE =
            "CREATE TABLE " + PaymentsTable.TABLE_NAME + " (" +
                    PaymentsTable._ID + " INTEGER PRIMARY KEY,"+
                    PaymentsTable.COLUMN_NAME + " TEXT, " +
                    PaymentsTable.COLUMN_DATE + " INTEGER, " +
                    PaymentsTable.COLUMN_PRICE + " REAL, " +
                    PaymentsTable.COLUMN_DESCRIPTION + " TEXT);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PaymentsTable.TABLE_NAME;

    public PaymentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
