package pl.pydyniak.payments.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import pl.pydyniak.payments.domain.Payment;

/**
 * Created by rafal on 29.11.15.
 */
public class PaymentDatabase implements PaymentsProvider {
    private PaymentsDbHelper dbHelper;

    public PaymentDatabase(Context context) {
        this.dbHelper = new PaymentsDbHelper(context);
    }

    public long addPaymentAndReturnItsId(Payment payment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = createAndReturnContentValuesForPayment(payment);
        long id = db.insert(DbConstants.PaymentsTable.TABLE_NAME, null, values);
        return id;
    }

    private ContentValues createAndReturnContentValuesForPayment(Payment payment) {
        ContentValues values = new ContentValues();
        values.put(DbConstants.PaymentsTable.COLUMN_NAME, payment.getName());
        values.put(DbConstants.PaymentsTable.COLUMN_DATE, payment.getDate() != null? payment.getDate().getTime():0);
        values.put(DbConstants.PaymentsTable.COLUMN_PRICE, payment.getPrice());
        values.put(DbConstants.PaymentsTable.COLUMN_DESCRIPTION, payment.getDescription() != null ? payment.getDescription() : "");
        values.put(DbConstants.PaymentsTable.COLUMN_TIMESTAMP, payment.getTimestamp());
        values.put(DbConstants.PaymentsTable.COLUMN_LAST_UPDATED, payment.getLastUpdated());
        values.put(DbConstants.PaymentsTable.COLUMN_ISOPEN, payment.isOpen() ? 1 : 0);
        values.put(DbConstants.PaymentsTable.COLUMN_DELETED, payment.isDeleted());
        return values;
    }

    public Payment getPaymentById(long id) {
        Cursor paymentCursor = getDetailsCursor(id);
        Payment payment = createAndReturnPaymentFromCursor(paymentCursor);
        return payment;
    }

    private Cursor getDetailsCursor(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] tables = DbConstants.PaymentsTable.allTables;
        String where = DbConstants.PaymentsTable._ID+" = ?";
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor = db.query(DbConstants.PaymentsTable.TABLE_NAME, tables, where, selectionArgs, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    private Payment createAndReturnPaymentFromCursor(Cursor cursor) {
        Payment payment = new Payment();
        payment.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable._ID)));
        payment.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_NAME)));
        payment.setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_DATE))));
        payment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_DESCRIPTION)));
        payment.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_PRICE)));
        boolean isOpen = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_ISOPEN)) != 0 ? true : false;
        payment.setIsOpen(isOpen);
        payment.setLastUpdated(cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_LAST_UPDATED)));
        payment.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_TIMESTAMP)));
        payment.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_DELETED)) != 0);
        return payment;
    }

    public Payment getPaymentByPosition(int position) {
        Payment payment;
        Cursor cursor = getDetailsCursorByPosition(position);
        payment = createAndReturnPaymentFromCursor(cursor);
        return payment;
    }

    private Cursor getDetailsCursorByPosition(int position) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] tables = DbConstants.PaymentsTable.allTables;
        Cursor cursor = db.query(DbConstants.PaymentsTable.TABLE_NAME, tables, null, null, null, null, null);
        cursor = moveToPositionWorkingWithDeleted(position, cursor);
        return cursor;
    }

    private Cursor moveToPositionWorkingWithDeleted(int position, Cursor cursor) {
        cursor.moveToFirst();
        //We don't know if first element is deleted or not
        int currentPosition = -1;

        while (true) {
            if (cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_DELETED)) == 0) {
                currentPosition++;
            }
            if (currentPosition == position) {
                return cursor;
            }
            cursor.moveToNext();
        }
    }

    public void deletePaymentByItsId(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbConstants.PaymentsTable.COLUMN_DELETED, 1);
        values.put(DbConstants.PaymentsTable.COLUMN_LAST_UPDATED, new Date().getTime());
        String select = DbConstants.PaymentsTable._ID + "=?";
        String[] selectionArgs = { Long.toString(id) };
        db.update(DbConstants.PaymentsTable.TABLE_NAME, values, select, selectionArgs);
    }

    public void deleteAllPayments() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbConstants.PaymentsTable.COLUMN_DELETED, 1);
        db.update(DbConstants.PaymentsTable.TABLE_NAME, values, null, null);
    }

    public long getPaymentIdByPosition(int position) {
        Cursor cursor = getDetailsCursorByPosition(position);
        return cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable._ID));
    }

    public void updatePayment(Payment payment, long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = createAndReturnContentValuesForPayment(payment);
        String selection = DbConstants.PaymentsTable._ID + "=?";
        String[] selectionArgs = { Long.toString(id) };
        db.update(DbConstants.PaymentsTable.TABLE_NAME, values, selection, selectionArgs);
    }

    public Payment findPaymentByTimestamp(long timestamp) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DbConstants.PaymentsTable.COLUMN_TIMESTAMP + "=?";
        String[] selectionArgs = { Long.toString(timestamp)};
        Cursor query = db.query(DbConstants.PaymentsTable.TABLE_NAME, DbConstants.PaymentsTable.allTables,
                selection, selectionArgs, null, null, null);
        if (!query.moveToFirst()) {
            return null;
        }

        Payment payment = createAndReturnPaymentFromCursor(query);
        return payment;
    }

    @Override
    public int getPaymentsNumber() {
        SQLiteDatabase db =  dbHelper.getReadableDatabase();
        String selection = DbConstants.PaymentsTable.COLUMN_DELETED + "=?";
        String[] selectionArgs = { Integer.toString(0) };
        return (int) DatabaseUtils.queryNumEntries(db, DbConstants.PaymentsTable.TABLE_NAME, selection, selectionArgs);
//        return (int) DatabaseUtils.queryNumEntries(db, DbConstants.PaymentsTable.TABLE_NAME, null, null);
    }

    public ArrayList<Payment> getAllDeletedTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Payment> tasks = new ArrayList<>();
        String selection = DbConstants.PaymentsTable.COLUMN_DELETED + "=?";
        String[] selectionArgs = { Long.toString(1)};
        Cursor query = db.query(DbConstants.PaymentsTable.TABLE_NAME, DbConstants.PaymentsTable.allTables,
                selection, selectionArgs, null, null, null);

        for (int i=0; i<query.getCount();i++) {
            query.moveToPosition(i);
            tasks.add(createAndReturnPaymentFromCursor(query));
        }
        return tasks;
    }

    private Cursor getDetailsCursorByPositionFromAllPayments(int position) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] tables = DbConstants.PaymentsTable.allTables;
        Cursor cursor = db.query(DbConstants.PaymentsTable.TABLE_NAME, tables, null, null, null, null, null);
        cursor.moveToPosition(position);
        return cursor;
    }
}
