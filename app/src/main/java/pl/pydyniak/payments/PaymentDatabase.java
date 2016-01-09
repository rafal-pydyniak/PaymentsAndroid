package pl.pydyniak.payments;

import static pl.pydyniak.payments.DbConstants.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

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
        long id = db.insert(PaymentsTable.TABLE_NAME, null, values);
        return id;
    }

    private ContentValues createAndReturnContentValuesForPayment(Payment payment) {
        ContentValues values = new ContentValues();
        values.put(PaymentsTable.COLUMN_NAME, payment.getName());
        values.put(PaymentsTable.COLUMN_DATE, payment.getDate().getTime());
        values.put(PaymentsTable.COLUMN_PRICE, payment.getPrice());
        values.put(PaymentsTable.COLUMN_DESCRIPTION, payment.getDescription());
        return values;
    }

    public Payment getPaymentById(long id) {
        Cursor paymentCursor = getDetailsCursor(id);
        Payment payment = createAndReturnPaymentFromCursor(paymentCursor);
        return payment;
    }

    private Cursor getDetailsCursor(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] tables = {PaymentsTable._ID, PaymentsTable.COLUMN_NAME, PaymentsTable.COLUMN_DATE, PaymentsTable.COLUMN_DESCRIPTION, PaymentsTable.COLUMN_PRICE};
        String where = PaymentsTable._ID+"=?";
        Cursor cursor = db.query(PaymentsTable.TABLE_NAME, tables, where, null, null, null, null, null);
        return cursor;
    }

    private Payment createAndReturnPaymentFromCursor(Cursor cursor) {
        Payment payment = new Payment();
        payment.setName(cursor.getString(cursor.getColumnIndexOrThrow(PaymentsTable.COLUMN_NAME)));
        payment.setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(PaymentsTable.COLUMN_DATE))));
        payment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(PaymentsTable.COLUMN_DESCRIPTION)));
        payment.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PaymentsTable.COLUMN_PRICE)));
        return payment;
    }

    public void deletePaymentByItsId(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String select = PaymentsTable._ID + "=?";
        String[] selectionArgs = { Long.toString(id) };
        db.delete(PaymentsTable.TABLE_NAME, select, selectionArgs);
    }

    public void deleteAllPayments() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(PaymentsTable.TABLE_NAME, null, null);
    }

    @Override
    public Payment getPayment(int position) {
        return null;
    }

    @Override
    public int getPaymentsNumber() {
        SQLiteDatabase db =  dbHelper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PaymentsTable.TABLE_NAME, null, null);
    }
}
