package pl.pydyniak.payments;

/**
 * Created by rafal on 29.11.15.
 */
public interface PaymentsProvider {
    Payment getPayment(int position);
    int getPaymentsNumber();
}
