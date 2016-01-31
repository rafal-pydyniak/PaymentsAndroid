package pl.pydyniak.payments.database;

import android.provider.BaseColumns;

public class DbConstants {


    public static final String DATABASE_NAME = "payments_db";


//    public static final String DATABASE_NAME = "payments_db";

    public static abstract class PaymentsTable implements BaseColumns {
        public static final String TABLE_NAME = "payments";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_ISOPEN = "is_open";
        public static final String COLUMN_DELETED = "deleted";


        public static final String[] allTables = {DbConstants.PaymentsTable._ID, DbConstants.PaymentsTable.COLUMN_NAME,
                DbConstants.PaymentsTable.COLUMN_DATE, DbConstants.PaymentsTable.COLUMN_DESCRIPTION,
                DbConstants.PaymentsTable.COLUMN_PRICE, DbConstants.PaymentsTable.COLUMN_ISOPEN,
                DbConstants.PaymentsTable.COLUMN_DELETED, PaymentsTable.COLUMN_LAST_UPDATED, PaymentsTable.COLUMN_TIMESTAMP};
    }
}
