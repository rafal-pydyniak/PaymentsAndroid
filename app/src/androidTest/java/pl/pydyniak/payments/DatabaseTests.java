package pl.pydyniak.payments;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import pl.pydyniak.payments.database.PaymentDatabase;
import pl.pydyniak.payments.database.PaymentsProvider;
import pl.pydyniak.payments.domain.Payment;

import static org.junit.Assert.assertEquals;

/**
 * Created by rafal on 02.01.16.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTests {
    private PaymentsProvider paymentDatabase;
    private Payment paymentInstance;

    @Before
    public void initialize() {
        Context context = InstrumentationRegistry.getTargetContext();
        paymentDatabase = new PaymentDatabase(context);
        paymentInstance = new Payment("testName", new Date(), "testDescription", 11.11);
        paymentInstance.setLastUpdated(new Date().getTime());
        paymentInstance.setTimestamp(new Date().getTime());
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
    public void getPaymentByIdTest() throws Exception{
        long id = paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
        Payment payment = paymentDatabase.getPaymentById(id);
        assertEquals(paymentInstance.getDate(), payment.getDate());
        assertEquals(paymentInstance.getDescription(), payment.getDescription());
        assertEquals(paymentInstance.getName(), payment.getName());
        assertEquals(paymentInstance.getPrice(), payment.getPrice());
    }

    @Test
    public void getPaymentByPositionTest() throws Exception{
        paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
        int position = paymentDatabase.getPaymentsNumber()-1;
        Payment payment = paymentDatabase.getPaymentByPosition(position);
        assertEquals(paymentInstance.getDate(), payment.getDate());
        assertEquals(paymentInstance.getDescription(), payment.getDescription());
        assertEquals(paymentInstance.getName(), payment.getName());
        assertEquals(paymentInstance.getPrice(), payment.getPrice());
        assertEquals(paymentInstance.isOpen(), payment.isOpen());
    }

    @Test
    public void getPaymentIdByPositionTest() throws Exception {
        Long id = paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
        int position = paymentDatabase.getPaymentsNumber()-1;
        Long paymentId = paymentDatabase.getPaymentIdByPosition(position);
        assertEquals(id, paymentId);
    }

    @Test
    public void updatePaymentTest() throws Exception{
        Payment paymentLocalInstance = new Payment("updateTest", new Date(), "updateTest", 12.222);
        assertEquals(false, paymentLocalInstance.isOpen());
        paymentDatabase.addPaymentAndReturnItsId(paymentLocalInstance);
        long id = paymentDatabase.getPaymentIdByPosition(paymentDatabase.getPaymentsNumber() - 1);
        paymentLocalInstance.setName("update test after update");
        paymentLocalInstance.setIsOpen(true);
        paymentDatabase.updatePayment(paymentLocalInstance, id);
        Payment payment = paymentDatabase.getPaymentById(id);
        assertEquals(paymentLocalInstance.getName(), payment.getName());
        assertEquals(paymentLocalInstance.isOpen(), payment.isOpen());
    }

//    @Test
//    public void testIfGettingAllWorks() throws Exception {
//        paymentDatabase.deleteAllPayments(); //Clear before testing
//        Payment paymentInstance2 = paymentInstance;
//        paymentInstance2.setName("SomeOtherName");
//        Double instance2Price = 5559.0;
//        paymentInstance2.setPrice(instance2Price);
//        paymentDatabase.addPaymentAndReturnItsId(paymentInstance);
//        paymentDatabase.addPaymentAndReturnItsId(paymentInstance2);
//
//        ArrayList<Payment> paymentsList = paymentDatabase.getAllPayments();
//        assertEquals(2, paymentsList.size());
//        assertEquals("SomeOtherName", paymentsList.get(1).getName());
//        assertEquals(instance2Price, paymentsList.get(1).getPrice());
//    }
}
