package pl.pydyniak.payments.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

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
        values.put(DbConstants.PaymentsTable.COLUMN_DATE, payment.getDate().getTime());
        values.put(DbConstants.PaymentsTable.COLUMN_PRICE, payment.getPrice());
        values.put(DbConstants.PaymentsTable.COLUMN_DESCRIPTION, payment.getDescription());
        values.put(DbConstants.PaymentsTable.COLUMN_ISOPEN, payment.isOpen()==true ? 1 : 0);
        return values;
    }

    public Payment getPaymentById(long id) {
        Cursor paymentCursor = getDetailsCursor(id);
        Payment payment = createAndReturnPaymentFromCursor(paymentCursor);
        return payment;
    }

    private Cursor getDetailsCursor(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] tables = {DbConstants.PaymentsTable._ID, DbConstants.PaymentsTable.COLUMN_NAME, DbConstants.PaymentsTable.COLUMN_DATE, DbConstants.PaymentsTable.COLUMN_DESCRIPTION, DbConstants.PaymentsTable.COLUMN_PRICE, DbConstants.PaymentsTable.COLUMN_ISOPEN};
        String where = DbConstants.PaymentsTable._ID+" = ?";
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor = db.query(DbConstants.PaymentsTable.TABLE_NAME, tables, where, selectionArgs, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    private Payment createAndReturnPaymentFromCursor(Cursor cursor) {
        Payment payment = new Payment();
        payment.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_NAME)));
        payment.setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_DATE))));
        payment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_DESCRIPTION)));
        payment.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_PRICE)));
        boolean isOpen = cursor.getInt(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable.COLUMN_ISOPEN)) != 0 ? true : false;
        payment.setIsOpen(isOpen);
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
        String[] tables = {DbConstants.PaymentsTable._ID, DbConstants.PaymentsTable.COLUMN_NAME, DbConstants.PaymentsTable.COLUMN_DATE, DbConstants.PaymentsTable.COLUMN_DESCRIPTION, DbConstants.PaymentsTable.COLUMN_PRICE, DbConstants.PaymentsTable.COLUMN_ISOPEN};
        Cursor cursor = db.query(DbConstants.PaymentsTable.TABLE_NAME, tables, null, null, null, null, null);
        cursor.moveToPosition(position);
        return cursor;
    }

    public void deletePaymentByItsId(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String select = DbConstants.PaymentsTable._ID + "=?";
        String[] selectionArgs = { Long.toString(id) };
        db.delete(DbConstants.PaymentsTable.TABLE_NAME, select, selectionArgs);
    }

    public void deleteAllPayments() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbConstants.PaymentsTable.TABLE_NAME, null, null);
    }

    public long getPaymentIdByPosition(int position) {
        Cursor cursor = getDetailsCursorByPosition(position);
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndexOrThrow(DbConstants.PaymentsTable._ID));
    }

    public void updatePayment(Payment payment, long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = createAndReturnContentValuesForPayment(payment);
        String selection = DbConstants.PaymentsTable._ID + "=?";
        String[] selectionArgs = { Long.toString(id) };
        db.update(DbConstants.PaymentsTable.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public int getPaymentsNumber() {
        SQLiteDatabase db =  dbHelper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, DbConstants.PaymentsTable.TABLE_NAME, null, null);
    }
}
