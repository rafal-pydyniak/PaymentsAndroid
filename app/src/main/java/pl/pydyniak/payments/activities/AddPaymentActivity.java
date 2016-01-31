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
public class AddPaymentActivity extends ActionBarActivity implements View.OnClickListener, DatePickerFragment.OnDateSelectedListener{
    private EditText paymentName;
    private EditText paymentDescription;
    private EditText paymentPrice;
    private TextView paymentDateLabel;

    private ImageButton dateButton;
    private Button addPaymentButton;
    private Date dateSelected = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_payment);
        setupViews();
    }


    private void setupViews() {
        paymentName = (EditText) findViewById(R.id.paymentNameEditText);
        paymentDescription = (EditText) findViewById(R.id.paymentDescriptionEditText);
        paymentPrice = (EditText) findViewById(R.id.paymentPriceEditText);
        paymentDateLabel = (TextView) findViewById(R.id.paymentDateLabel);
        dateButton = (ImageButton) findViewById(R.id.calendarDateButton);
        dateButton.setOnClickListener(this);
        addPaymentButton = (Button) findViewById(R.id.addButton);
        addPaymentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addButton:
                tryToAddPayment();
                break;
            case R.id.calendarDateButton:
                showDateFragment();
                break;
        }
    }

    private void tryToAddPayment() {
        try {
            addPayment();
        } catch (NotEnoughInformationException e) {
            Toast.makeText(this, R.string.error_not_enough_information, Toast.LENGTH_SHORT).show();
        }
    }

    private void addPayment() throws NotEnoughInformationException{
        Payment payment = createAndReturnPayment();
        PaymentsProvider db = new PaymentDatabase(this);
        db.addPaymentAndReturnItsId(payment);
        Toast.makeText(this, getString(R.string.payment_added_success), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, PaymentsListActivity.class);
        startActivity(i);
    }

    private Payment createAndReturnPayment() throws NotEnoughInformationException{
        Payment payment = new Payment();

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
        payment.setTimestamp(new Date().getTime());
        payment.setLastUpdated(new Date().getTime());
        return payment;
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
        String dateText = getString(R.string.payment_date_label) + " " + formatDate(date, "dd-MM-yyyy");
        paymentDateLabel.setText(dateText);
    }

    private String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

}
