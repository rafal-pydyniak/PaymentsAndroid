package pl.pydyniak.payments;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rafal on 29.11.15.
 */
public class PaymentsAdapter extends BaseAdapter {
    private PaymentDatabase paymentDatabase;
    private Context context;

    public PaymentsAdapter(Context context) {
        this.context = context;
        paymentDatabase = new PaymentDatabase(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View paymentView = getPaymentView(convertView, parent, position);
        Payment payment = getItem(position);
        bindPaymentToView(payment, paymentView, position);
        return paymentView;
    }

    private View getPaymentView(View convertView, ViewGroup parent, int position) {
        View paymentView;
        if (convertView == null) {
            paymentView = LayoutInflater.from(context).inflate(R.layout.payment_row, parent, false);
        }
        else {
            paymentView = convertView;
        }
        return paymentView;
    }

    @Override
    public Payment getItem(int position) {
        Payment payment = paymentDatabase.getPaymentByPosition(position);
        return payment;
    }

    private void bindPaymentToView(Payment payment, View paymentView, int position) {
        TextView paymentName = (TextView) paymentView.findViewById(R.id.nameTextView);
        TextView paymentDate = (TextView) paymentView.findViewById(R.id.dateTextView);
        TextView paymentDescription = (TextView) paymentView.findViewById(R.id.descriptionTextView);

        paymentName.setText(payment.getName());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(payment.getDate());
        paymentDate.setText(date);
        paymentDescription.setText(payment.getDescription());

        setCorrectExtendedInfoVisibilityForPayment(paymentView, payment);
    }

    private void setCorrectExtendedInfoVisibilityForPayment(View paymentView, Payment payment) {
        if (payment.isOpen()) {
            setVisibilityVisibleForPayment(paymentView);
        } else {
            setVisibilityGoneForPayment(paymentView);
        }
    }

    private void setVisibilityGoneForPayment(View paymentView) {
        View extendedInfoView = paymentView.findViewById(R.id.paymentRowExtendedInfo);
        extendedInfoView.setVisibility(View.GONE);
    }

    private void setVisibilityVisibleForPayment(View paymentView) {
        View extendedInfoView = paymentView.findViewById(R.id.paymentRowExtendedInfo);
        extendedInfoView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return paymentDatabase.getPaymentsNumber();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
