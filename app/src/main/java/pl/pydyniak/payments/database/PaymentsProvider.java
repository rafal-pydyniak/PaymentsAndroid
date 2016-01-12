package pl.pydyniak.payments.database;

import pl.pydyniak.payments.domain.Payment;

/**
 * Created by rafal on 29.11.15.
 */
public interface PaymentsProvider {
    int getPaymentsNumber();
    long addPaymentAndReturnItsId(Payment payment);
    Payment getPaymentById(long id);
    Payment getPaymentByPosition(int position);
    void deletePaymentByItsId(long id);
    void deleteAllPayments();
    long getPaymentIdByPosition(int position);
    void updatePayment(Payment payment, long id);
    }
