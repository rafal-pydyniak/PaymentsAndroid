package pl.pydyniak.payments.restManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.pydyniak.payments.R;
import pl.pydyniak.payments.database.PaymentDatabase;
import pl.pydyniak.payments.domain.Payment;
import pl.pydyniak.payments.exceptions.RegistrationException;
import pl.pydyniak.payments.exceptions.UnauthorizedException;
import pl.pydyniak.payments.exceptions.WrongCredentialsException;

/**
 * Created by rafal on 17.01.16.
 */
public class ServerConnector {
//    private static final String serverBaseUrl = "http://payments-pydyniak.herokuapp.com/api";
    private static final String serverBaseUrl = "http://payments-pydyniak.herokuapp.com/api";
    private Context context;

    public ServerConnector(Context context) {
        this.context = context;
    }

    public void registerUser(String username, String password) throws IOException, RegistrationException {
        HttpURLConnection conn = tryToOpenConnectionAndReturnIt(serverBaseUrl + "/users", "POST");
        JSONObject cred = createJsonObjectForUsernameAndPassword(username, password);
        sendRegistrationRequest(cred, conn);
    }

    private HttpURLConnection tryToOpenConnectionAndReturnIt(String serverUrl, String method)throws IOException{
        return tryToOpenConnectionAndReturnIt(serverUrl, method, true, true);
    }

    private HttpURLConnection tryToOpenConnectionAndReturnIt(String serverUrl, String method, boolean doInput, boolean doOutput)
            throws IOException {
        URL url = new URL(serverUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(method);
        conn.setDoInput(doInput);
        conn.setDoOutput(doOutput);
        conn.setRequestProperty("content-type", "application/json");
        return conn;
    }

    private JSONObject createJsonObjectForUsernameAndPassword(String username, String password) {
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("username", username);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    private void sendRegistrationRequest(JSONObject credentials, HttpURLConnection connection)
            throws IOException, RegistrationException {
        OutputStream os = connection.getOutputStream();
        os.write(credentials.toString().getBytes("UTF-8"));
        os.close();
        Log.d("Registration code", Integer.toString(connection.getResponseCode()));
        if (connection.getResponseCode() != 201) {
            throw new RegistrationException();
        }
    }

    public void login(String username, String password) throws IOException, WrongCredentialsException, JSONException {
        String key = getAuthKey(username, password);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE
        );
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(context.getString(R.string.key_name), key);
        edit.commit();
    }

    private String getAuthKey(String username, String password) throws IOException, WrongCredentialsException, JSONException {
        HttpURLConnection urlConnection = tryToOpenConnectionAndReturnIt(serverBaseUrl + "/login", "POST");
        JSONObject credentials = createJsonObjectForUsernameAndPassword(username, password);
        String key = sendLoginRequestAndReturnAuthKey(credentials, urlConnection);
        return key;
    }

    private String sendLoginRequestAndReturnAuthKey(JSONObject credentials, HttpURLConnection connection)
            throws IOException, WrongCredentialsException, JSONException {
        OutputStream os = connection.getOutputStream();
        os.write(credentials.toString().getBytes("UTF-8"));
        os.close();
        if (connection.getResponseCode() == 401) {
            throw new WrongCredentialsException();
        }
        String key = getAuthKeyFromConnection(connection);
        return key;
    }

    private String getAuthKeyFromConnection(HttpURLConnection connection) throws IOException, JSONException {
        String inputString = readStream(connection.getInputStream());
        JSONObject authorizationObject;
        authorizationObject = parseStringToJsonObject(inputString);
        return authorizationObject.getString("access_token");
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    private JSONObject parseStringToJsonObject(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject;
    }

    public void synchronizeWithServer() throws IOException, JSONException, UnauthorizedException {
        JSONArray tasks = downloadAllTasks();
        deleteTasksOnServer(tasks);
        tasks = downloadAllTasks();
        addTasksFromServer(tasks);
        tasks = downloadAllTasks();
        addTasksToServer(tasks);
    }

    private JSONArray downloadAllTasks() throws IOException, JSONException, UnauthorizedException {
        String key = getKeyFromSharedPreferences();
        HttpURLConnection urlConnection = tryToOpenConnectionAndReturnIt(serverBaseUrl + "/tasks", "GET", true, false);
        urlConnection.setRequestProperty("Authorization ", "Bearer " + key);
        Log.d("status", Integer.toString(urlConnection.getResponseCode()));
        Log.d("error", urlConnection.getResponseMessage());
        Log.d("url", serverBaseUrl + "/tasks");

        if (urlConnection.getResponseCode() == 401) {
            throw new UnauthorizedException();
        }

        String inputString = readStream(urlConnection.getInputStream());
        JSONArray tasks = parseStringToJsonArray(inputString);
        return tasks;
    }

    private String getKeyFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        String key = sharedPreferences.getString(context.getString(R.string.key_name), "");
        return key;
    }

    private JSONArray parseStringToJsonArray(String json) throws JSONException {
        return new JSONArray(json);
    }

    private void deleteTasksOnServer(JSONArray serverTasks) throws IOException{
        PaymentDatabase db = new PaymentDatabase(context);
        List<Payment> serverPayments = getTasksFromJsonArray(serverTasks);
        List<Payment> localPayments = db.getAllDeletedTasks();
        for (Payment payment : localPayments) {
            Payment serverPaymentToDelete = serverContainsPaymentWithTimestamp(serverPayments, payment.getTimestamp());
            if (serverPaymentToDelete!= null)
                deleteTaskFromServer(serverPaymentToDelete.getId());
        }
    }

    private void deleteTaskFromServer(long serverTaskId) throws IOException{
        HttpURLConnection connection = tryToOpenConnectionAndReturnIt(serverBaseUrl + "/tasks/" + serverTaskId, "DELETE", false, true);
        connection.connect();
    }

    private void addTasksFromServer(JSONArray tasks) throws JSONException, IOException, UnauthorizedException {
        PaymentDatabase db = new PaymentDatabase(context);
        for (int i=0; i<tasks.length();i++) {
            JSONObject task = tasks.getJSONObject(i);
            Payment serverPayment = parseJsonObjectToPayment(task);
            Payment localPayment = db.findPaymentByTimestamp(task.getLong("timestamp"));
            if (localPayment == null) {
                db.addPaymentAndReturnItsId(serverPayment);
            } else {
                if (localPayment.getLastUpdated() != serverPayment.getLastUpdated())
                    updatePayment(localPayment, serverPayment);
            }
        }
    }

    private Payment parseJsonObjectToPayment(JSONObject task) throws JSONException{
        Payment payment = new Payment();
        payment.setId(task.getLong("id"));
        payment.setName(task.getString("name"));
        payment.setDescription(task.getString("description"));
        payment.setDate(parseDate(task.getString("realisationDate")));
        payment.setPrice(task.getDouble("amount"));
        payment.setDeleted(task.getBoolean("deleted"));
        payment.setTimestamp(task.getLong("timestamp"));
        payment.setLastUpdated(task.getLong("lastUpdated"));
        return payment;
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private void updatePayment(Payment localPayment, Payment serverPayment) throws IOException, UnauthorizedException {
        PaymentDatabase db = new PaymentDatabase(context);
        if (localPayment.getLastUpdated() < serverPayment.getLastUpdated()) {
            db.updatePayment(serverPayment, localPayment.getId());
        }
        else {
            updatePaymentOnserver(localPayment, serverPayment);
        }
    }

    private void updatePaymentOnserver(Payment localPayment, Payment serverPayment) throws IOException, UnauthorizedException {
        HttpURLConnection connection = tryToOpenConnectionAndReturnIt(serverBaseUrl + "/tasks/"+serverPayment.getId(), "PUT", false, true);
        JSONObject jsonObjectForPayment = createJsonObjectForPayment(localPayment);
        sendJsonRequest(jsonObjectForPayment, connection);
    }

    private void addTasksToServer(JSONArray tasks) throws IOException, UnauthorizedException {
        PaymentDatabase db = new PaymentDatabase(context);
        List<Payment> payments = getTasksFromJsonArray(tasks);
        for (int i=0;i<db.getPaymentsNumber();i++) {
            Payment localPayment = db.getPaymentByPosition(i);

            if (serverContainsPaymentWithTimestamp(payments, localPayment.getTimestamp()) == null) {
                addPaymentToServer(localPayment);
            }
        }
    }

    private List<Payment> getTasksFromJsonArray(JSONArray tasks) {
        List<Payment> payments = new ArrayList<>();
        for (int i=0;i<tasks.length();i++) {
            try {
                payments.add(parseJsonObjectToPayment(tasks.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return payments;
    }

    private Payment serverContainsPaymentWithTimestamp(List<Payment> payments, long timestamp) {
        for (Payment payment : payments) {
            if (payment.getTimestamp() == timestamp)
                return payment;
        }
        return null;
    }

    private void addPaymentToServer(Payment payment) throws IOException, UnauthorizedException {
        HttpURLConnection connection = tryToOpenConnectionAndReturnIt(serverBaseUrl + "/tasks", "POST", true, true);
        JSONObject jsonObject = createJsonObjectForPayment(payment);
        sendJsonRequest(jsonObject, connection);
    }

    private JSONObject createJsonObjectForPayment(Payment payment) {
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("name", payment.getName());
            credentials.put("description", payment.getDescription());
            credentials.put("amount", payment.getPrice());
            credentials.put("realisationDate", new SimpleDateFormat("dd-MM-yyyy").format(payment.getDate()));
            credentials.put("deleted", payment.isDeleted());
            credentials.put("timestamp", payment.getTimestamp());
            credentials.put("lastUpdated", payment.getLastUpdated());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    private void sendJsonRequest(JSONObject jsonObject, HttpURLConnection connection) throws IOException, UnauthorizedException {
        connection.setRequestProperty("Authorization ", "Bearer " + getKeyFromSharedPreferences());
        OutputStream os = connection.getOutputStream();
        os.write(jsonObject.toString().getBytes("UTF-8"));
        os.close();
        if (connection.getResponseCode() == 401) {
            throw new UnauthorizedException();
        }
        if (connection.getResponseCode() != 201 && connection.getResponseCode() != 200) {
            throw new RuntimeException();
        }
    }

    public boolean isOnline() {
        ConnectivityManager connetion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connetion.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
