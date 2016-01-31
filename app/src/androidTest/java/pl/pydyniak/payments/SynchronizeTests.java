package pl.pydyniak.payments;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import pl.pydyniak.payments.exceptions.RegistrationException;
import pl.pydyniak.payments.exceptions.WrongCredentialsException;
import pl.pydyniak.payments.restManagement.ServerConnector;

/**
 * Created by rafal on 17.01.16.
 */
@RunWith(AndroidJUnit4.class)
public class SynchronizeTests {
    private ServerConnector connector;

    @Before
    public void initialize() {
        Context context = InstrumentationRegistry.getTargetContext();
        connector = new ServerConnector(context);
    }

    @Test(expected = RegistrationException.class)
    public void wrongUsernameRegistrationExceptionTest() throws Exception{
        connector.registerUser("wrongEmail", "password");
    }

    @Test(expected = RegistrationException.class)
    public void shortPasswordRegistrationExceptionTest() throws Exception{
        connector.registerUser("user@gmail.com", "sh");
    }

    @Test
    public void registerUserTest() throws Exception {
        connector.registerUser("test@gmail.com", "password");
    }

    @Test(expected = WrongCredentialsException.class)
    public void getAuthKeyTestThrowWrongAccountExceptionTest() throws Exception{
        connector.login("somenotexistinguser@somemail.com", "somedummypassword");
    }

    @Test
    public void loginSuccessTest() throws Exception{
        connector.login("test@gmail.com", "password");
    }

    @Test
    public void synchronizeWithServer() throws Exception{
        connector.login("test@gmail.com", "password");
        connector.synchronizeWithServer();
    }

    @Test
    public void timeTest() throws Exception {
        System.out.println(new Date().getTime());
    }
}
