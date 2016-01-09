package pl.pydyniak.payments;

import android.provider.BaseColumns;

import java.util.Date;

public class DbConstants {


    public static final String DATABASE_NAME = "payments_db";


//    public static final String DATABASE_NAME = "payments_db";

    public static abstract class PaymentsTable implements BaseColumns {
        public static final String TABLE_NAME = "payments";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DESCRIPTION = "description";
    }
}
