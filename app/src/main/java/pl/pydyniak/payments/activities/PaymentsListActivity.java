package pl.pydyniak.payments.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Date;

import pl.pydyniak.payments.Payment;
import pl.pydyniak.payments.PaymentDatabase;
import pl.pydyniak.payments.PaymentsAdapter;
import pl.pydyniak.payments.R;

public class PaymentsListActivity extends ActionBarActivity {
    private ListView listView;
    private PaymentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentDatabase db = new PaymentDatabase(this);
        System.out.println(db.getPaymentsNumber());
        setContentView(R.layout.payment_list);
        initializeList();
    }

    private void initializeList() {
        listView = (ListView) findViewById(R.id.paymentsList);
        adapter = new PaymentsAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(createAndReturnClickListener());

    }

    private AdapterView.OnItemClickListener createAndReturnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                togglePaymentItem(view, position);
            }
        };
    }

    private void togglePaymentItem(View view, final int position) {
        Payment payment = adapter.getItem(position);
        payment.setIsOpen(!payment.isOpen());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
