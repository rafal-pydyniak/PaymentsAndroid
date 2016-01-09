package pl.pydyniak.payments;

import static org.junit.Assert.*;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Created by rafal on 02.01.16.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTests {
    PaymentDatabase paymentDatabase;
    Payment paymentInstance;

    @Before
    public void initialize() {
        Context context = InstrumentationRegistry.getTargetContext();
        paymentDatabase = new PaymentDatabase(context);
        paymentInstance = new Payment("testName", new Date(), "testDescription", 11.11);
    }

    @Test
    public void testIfAddingPaymentWorks() throws Exception{
        int numberOfRows = paymentDatabase.getPaymentsNumber();
        paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
        assertEquals(numberOfRows + 1, paymentDatabase.getPaymentsNumber());
    }

    @Test
    public void testIfDeletingWorks() throws Exception{
        long id = paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
        int numberOfRows = paymentDatabase.getPaymentsNumber();
        paymentDatabase.deletePaymentByItsId(id);
        assertEquals(numberOfRows - 1, paymentDatabase.getPaymentsNumber());
    }

    @Test
    public void testIfDeletingAllWorks() throws Exception{
        paymentDatabase.deleteAllPayments();
        assertEquals(0, paymentDatabase.getPaymentsNumber());
    }

    @Test
    public void getPaymentById() throws Exception{
        long id = paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
        Payment payment = paymentDatabase.getPaymentById(id);
        assertEquals(paymentInstance.getDate(), payment.getDate());
        assertEquals(paymentInstance.getDescription(), payment.getDescription());
        assertEquals(paymentInstance.getName(), payment.getName());
        assertEquals(paymentInstance.getPrice(), payment.getPrice());
    }
}
