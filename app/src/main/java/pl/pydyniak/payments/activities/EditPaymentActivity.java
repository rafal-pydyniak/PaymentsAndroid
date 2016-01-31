package pl.pydyniak.payments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.pydyniak.payments.R;
import pl.pydyniak.payments.database.PaymentDatabase;
import pl.pydyniak.payments.database.PaymentsProvider;
import pl.pydyniak.payments.domain.Payment;
import pl.pydyniak.payments.exceptions.NotEnoughInformationException;
import pl.pydyniak.payments.fragments.DatePickerFragment;

/**
 * Created by rafal on 29.11.15.
 */
public class EditPaymentActivity extends ActionBarActivity implements View.OnClickListener, DatePickerFragment.OnDateSelectedListener{
    private EditText paymentName;
    private EditText paymentDescription;
    private EditText paymentPrice;
    private TextView paymentDateLabel;
    private ImageButton dateButton;
    private Button editPaymentButton;
    private Date dateSelected = new Date();
    private Payment payment;

    private PaymentsProvider db;

    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_payment);
        db = new PaymentDatabase(this);
        Bundle extras = getIntent().getExtras();
        position = extras.getInt(getString(R.string.positionExtra));
        setupViews();
    }

    private void setupViews() {
        paymentName = (EditText) findViewById(R.id.paymentNameEditText);
        paymentDescription = (EditText) findViewById(R.id.paymentDescriptionEditText);
        paymentPrice = (EditText) findViewById(R.id.paymentPriceEditText);
        paymentDateLabel = (TextView) findViewById(R.id.paymentDateLabel);

        payment = db.getPaymentByPosition(position);
        setViewsForPayment();

        dateButton = (ImageButton) findViewById(R.id.calendarDateButton);
        dateButton.setOnClickListener(this);
        editPaymentButton = (Button) findViewById(R.id.editButton);
        editPaymentButton.setOnClickListener(this);
    }

    private void setViewsForPayment() {
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
                tryToEditPayment();
                break;
            case R.id.calendarDateButton:
                showDateFragment();
                break;
        }
    }

    private void tryToEditPayment() {
        try {
            editPayment();
        } catch (NotEnoughInformationException e) {
            Toast.makeText(this, R.string.error_not_enough_information, Toast.LENGTH_SHORT).show();
        }
    }

    private void editPayment() throws NotEnoughInformationException{
        setPaymentVariables();
        db.updatePayment(payment, payment.getId());
        Toast.makeText(this, getString(R.string.payment_edited_success), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, PaymentsListActivity.class);
        startActivity(i);
    }

    private void setPaymentVariables() throws NotEnoughInformationException{
        String name = paymentName.getText().toString();
        String description = paymentDescription.getText().toString();
        String price = paymentPrice.getText().toString();

        if (name.isEmpty() || price.isEmpty()) {
            throw new NotEnoughInformationException();
        }

        payment.setName(name);
        payment.setDescription(description);
        payment.setPrice(Double.parseDouble(price));
        payment.setDate(dateSelected);
        payment.setLastUpdated(new Date().getTime());
    }

    private void showDateFragment() {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setDate(dateSelected);
        picker.show(getFragmentManager(), getString(R.string.date_fragment_label));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBackToMainIntent();
                return true;
            default:
                return false;
        }
    }

    private void goBackToMainIntent() {
        Intent intent = new Intent(this, PaymentsListActivity.class);
        startActivity(intent);
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
