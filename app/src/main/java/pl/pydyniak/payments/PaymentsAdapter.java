package pl.pydyniak.payments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import pl.pydyniak.payments.activities.EditPaymentActivity;
import pl.pydyniak.payments.activities.PaymentsListActivity;
import pl.pydyniak.payments.database.PaymentDatabase;
import pl.pydyniak.payments.database.PaymentsProvider;
import pl.pydyniak.payments.domain.Payment;

/**
 * Created by rafal on 29.11.15.
 */
public class PaymentsAdapter extends BaseAdapter {
    private PaymentsProvider paymentDatabase;
    private Context context;

    public PaymentsAdapter(Context context) {
        this.context = context;
        paymentDatabase = new PaymentDatabase(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View paymentView = getPaymentView(convertView, parent);
        Payment payment = getItem(position);
        bindPaymentToView(payment, paymentView, position);
        return paymentView;
    }

    private View getPaymentView(View convertView, ViewGroup parent) {
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

    private void bindPaymentToView(final Payment payment, View paymentView, final int position) {
        TextView paymentName = (TextView) paymentView.findViewById(R.id.nameTextView);
        TextView paymentDate = (TextView) paymentView.findViewById(R.id.dateTextView);
        TextView paymentDescription = (TextView) paymentView.findViewById(R.id.descriptionTextView);
        TextView paymentPrice = (TextView) paymentView.findViewById(R.id.paymentPriceEditText);
        ImageButton deletePaymentButton = (ImageButton) paymentView.findViewById(R.id.deletePaymentButton);
        final ImageButton editPaymentButton = (ImageButton) paymentView.findViewById(R.id.editPaymentButton);

        paymentName.setText(payment.getName());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(payment.getDate());
        paymentDate.setText(date);
        paymentDescription.setText(payment.getDescription());
        paymentPrice.setText(payment.getPrice().toString());
        deletePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePayment(position);
            }
        });
        editPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPayment(position);
            }
        });

        setCorrectExtendedInfoVisibilityForPayment(paymentView, payment);
    }

    private void deletePayment(int position) {
        ((PaymentsListActivity) context).deletePayment(position);
    }

    public interface OnDeletePaymentClicked {
        void deletePayment(int position);
    }

    private void editPayment(int position) {
        Intent i = new Intent(context, EditPaymentActivity.class);
        i.putExtra("position", position);
        context.startActivity(i);
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

    public void updatePaymentInDatabase(Payment payment, int position) {
        paymentDatabase.updatePayment(payment, getItemId(position));
    }

    @Override
    public int getCount() {
        return paymentDatabase.getPaymentsNumber();
    }

    @Override
    public long getItemId(int position) {
        return paymentDatabase.getPaymentIdByPosition(position);
    }

    public void deletePaymentByPosition(int position) {
        paymentDatabase.deletePaymentByItsId(getItemId(position));
    }

}
