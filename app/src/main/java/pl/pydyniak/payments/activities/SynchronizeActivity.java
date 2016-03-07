package pl.pydyniak.payments.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import pl.pydyniak.payments.R;
import pl.pydyniak.payments.exceptions.NetworkNotAvaliableException;
import pl.pydyniak.payments.exceptions.NotEnoughInformationException;
import pl.pydyniak.payments.exceptions.RegistrationException;
import pl.pydyniak.payments.exceptions.WrongCredentialsException;
import pl.pydyniak.payments.restManagement.ServerConnector;

/**
 * Created by rafal on 29.01.16.
 */
public class SynchronizeActivity extends ActionBarActivity implements View.OnClickListener {
    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronize);
        setupViews();
    }

    private void setupViews() {
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                new LoginTask(this).execute();
                break;
            case R.id.registerButton:
                new RegisterTask(this).execute();
                break;
        }
    }

    private class LoginTask extends AsyncTask {
        Context context;
        Handler handler;

        public LoginTask(Context context) {
            this.context = context;
            handler =  new Handler(context.getMainLooper());
        }

        @Override
        protected Object doInBackground(Object[] params) {
            tryToLogin();
            return null;
        }

        private void tryToLogin() {
            try {
                login();
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.login_success_text, Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(context, PaymentsListActivity.class);
                context.startActivity(intent);
            } catch (NotEnoughInformationException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.not_enough_login_information, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NetworkNotAvaliableException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.network_not_avaliable_text, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (WrongCredentialsException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.wrong_credentials_text, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException | JSONException ex) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.server_exception_text, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void login() throws NotEnoughInformationException, NetworkNotAvaliableException, WrongCredentialsException,
                IOException, JSONException {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (emailText.isEmpty() || passwordText.isEmpty() || !validateEmail(emailText) || !validatePassword(passwordText)) {
                throw new NotEnoughInformationException();
            }

            ServerConnector connector = new ServerConnector(context);

            if (!connector.isOnline()) {
                throw new NetworkNotAvaliableException();
            }

            connector.login(emailText, passwordText);
        }
    }

    private class RegisterTask extends AsyncTask {
        Context context;
        Handler handler;
        public RegisterTask(Context context) {
            this.context = context;
            handler =  new Handler(context.getMainLooper());
        }

        @Override
        protected Object doInBackground(Object[] params) {
            tryToRegister();
            return null;
        }

        private void tryToRegister() {
            try {
                register();
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.register_success_text, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NotEnoughInformationException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.not_enough_login_information, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NetworkNotAvaliableException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.network_not_avaliable_text, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException | RegistrationException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.server_exception_text, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void register() throws NotEnoughInformationException, NetworkNotAvaliableException,
                IOException, RegistrationException {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (emailText.isEmpty() || passwordText.isEmpty() || !validateEmail(emailText) || !validatePassword(passwordText)) {
                throw new NotEnoughInformationException();
            }

            ServerConnector connector = new ServerConnector(context);

            if (!connector.isOnline()) {
                throw new NetworkNotAvaliableException();
            }

            connector.registerUser(emailText, passwordText);
        }
    }

    private boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private boolean validatePassword(String password) {
        return password.length() >= 6;
    }
}
