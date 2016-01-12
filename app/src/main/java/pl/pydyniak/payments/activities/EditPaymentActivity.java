package pl.pydyniak.payments.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.pydyniak.payments.domain.Payment;
import pl.pydyniak.payments.database.PaymentDatabase;
import pl.pydyniak.payments.database.PaymentsProvider;
import pl.pydyniak.payments.R;
import pl.pydyniak.payments.fragments.DatePickerFragment;

/**
 * Created by rafal on 29.11.15.
 */
public class EditPaymentActivity extends ActionBarActivity implements View.OnClickListener, DatePickerFragment.OnDateSelectedListener{
    EditText paymentName;
    EditText paymentDescription;
    EditText paymentPrice;
    TextView paymentDateLabel;
    ImageButton dateButton;
    Button editPaymentButton;
    Date dateSelected = new Date();

    PaymentsProvider db;

    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_payment);
        db = new PaymentDatabase(this);
        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");
        setupViews();
    }

    private void setupViews() {
        paymentName = (EditText) findViewById(R.id.paymentNameEditText);
        paymentDescription = (EditText) findViewById(R.id.paymentDescriptionEditText);
        paymentPrice = (EditText) findViewById(R.id.paymentPriceEditText);
        paymentDateLabel = (TextView) findViewById(R.id.paymentDateLabel);

        Payment payment = db.getPaymentByPosition(position);
        setViewsForPayment(payment);

        dateButton = (ImageButton) findViewById(R.id.calendarDateButton);
        dateButton.setOnClickListener(this);
        editPaymentButton = (Button) findViewById(R.id.editButton);
        editPaymentButton.setOnClickListener(this);
    }

    private void setViewsForPayment(Payment payment) {
        paymentName.setText(payment.getName());
        paymentDescription.setText(payment.getDescription());
        paymentPrice.setText(payment.getPrice().toString());
        dateSelected = payment.getDate();
        paymentDateLabel.setText(createDateTextForDate(dateSelected));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.editButton:
                editPayment();
                break;
            case R.id.calendarDateButton:
                showDateFragment();
                break;
        }
    }

    private void editPayment() {
        Payment payment = createAndReturnPayment();
        db.updatePayment(payment, db.getPaymentIdByPosition(position));
        Toast.makeText(this, getString(R.string.payment_edited_success), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, PaymentsListActivity.class);
        startActivity(i);
    }

    private Payment createAndReturnPayment() {
        Payment payment = new Payment();
        payment.setName(paymentName.getText().toString());
        payment.setDescription(paymentDescription.getText().toString());
        payment.setPrice(Double.parseDouble(paymentPrice.getText().toString()));
        payment.setDate(dateSelected);
        return payment;
    }

    private void showDateFragment() {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setDate(dateSelected);
        picker.show(getFragmentManager(), "date");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);

        setupActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onDateSelected(Date date) {
        dateSelected = date;
        String dateText = createDateTextForDate(date);
        paymentDateLabel.setText(dateText);
    }

    private String createDateTextForDate(Date date) {
        return getString(R.string.payment_date_label) + " " + formatDate(date, "dd-MM-yyyy");
    }

    private String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

}
