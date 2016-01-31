package pl.pydyniak.payments.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rafal on 29.11.15.
 */
public class PaymentsDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 14;

    private static final String TABLE_CREATE =
            "CREATE TABLE " + DbConstants.PaymentsTable.TABLE_NAME + " (" +
                    DbConstants.PaymentsTable._ID + " INTEGER PRIMARY KEY,"+
                    DbConstants.PaymentsTable.COLUMN_NAME + " TEXT, " +
                    DbConstants.PaymentsTable.COLUMN_DATE + " INTEGER, " +
                    DbConstants.PaymentsTable.COLUMN_PRICE + " REAL, " +
                    DbConstants.PaymentsTable.COLUMN_DESCRIPTION + " TEXT," +
                    DbConstants.PaymentsTable.COLUMN_TIMESTAMP + " INTEGER," +
                    DbConstants.PaymentsTable.COLUMN_LAST_UPDATED + " INTEGER,"+
                    DbConstants.PaymentsTable.COLUMN_ISOPEN + " INTEGER, " +
                    DbConstants.PaymentsTable.COLUMN_DELETED+ " INTEGER);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DbConstants.PaymentsTable.TABLE_NAME;

    public PaymentsDbHelper(Context context) {
        super(context, DbConstants.DATABASE_NAME, null, DATABASE_VERSION);
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
