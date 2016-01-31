package pl.pydyniak.payments.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import pl.pydyniak.payments.PaymentsAdapter;
import pl.pydyniak.payments.R;
import pl.pydyniak.payments.domain.Payment;
import pl.pydyniak.payments.exceptions.UnauthorizedException;
import pl.pydyniak.payments.fragments.DeleteConfirmDialogFragment;
import pl.pydyniak.payments.restManagement.ServerConnector;

public class PaymentsListActivity extends ActionBarActivity implements PaymentsAdapter.OnDeletePaymentClicked, DeleteConfirmDialogFragment.OnPositiveDelete {
    private ListView listView;
    private PaymentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_list);
        initializeList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payments_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_payment: {
                startAddPaymentActivity();
                return true;
            }
            case R.id.synchronize: {
                synchronize();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void startAddPaymentActivity() {
        Intent i = new Intent(this, AddPaymentActivity.class);
        startActivity(i);
    }

    private void synchronize() {
        Connection connection = new Connection(this);
        connection.execute();
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
                togglePaymentItem(position);
            }
        };
    }

    private void togglePaymentItem(final int position) {
        Payment payment = adapter.getItem(position);
        payment.setIsOpen(!payment.isOpen());
        adapter.updatePaymentInDatabase(payment, position);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deletePayment(int position) {
        showConfirmDialog(position);
    }

    private void showConfirmDialog(int position) {
        DeleteConfirmDialogFragment confirmDialogFragment = new DeleteConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.positionExtra), position);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getFragmentManager(), getString(R.string.delete_payment_tag));
    }

    @Override
    public void positiveDelete(int position) {
        adapter.deletePaymentByPosition(position);
        Toast.makeText(this, getString(R.string.payment_deleted_success), Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    private class Connection extends AsyncTask {
        Context context;
        Handler handler;

        public Connection(Context context) {
            this.context = context;
            handler =  new Handler(context.getMainLooper());
        }

        @Override
        protected Object doInBackground(Object[] params) {
            ServerConnector connector = new ServerConnector(context);
            try {
                connector.synchronizeWithServer();
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.synchronize_success, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException | JSONException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.server_exception_text, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (UnauthorizedException e) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, R.string.unauthorized_exception_text, Toast.LENGTH_SHORT).show();
                    }
                });
                startSynchronizeActivity();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            ((PaymentsListActivity) context).notifyDataSetChanged();
        }

        private void startSynchronizeActivity() {
            Intent i = new Intent(context, SynchronizeActivity.class);
            context.startActivity(i);
        }
    }
}
