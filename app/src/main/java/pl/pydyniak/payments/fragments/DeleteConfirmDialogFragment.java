package pl.pydyniak.payments.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import pl.pydyniak.payments.activities.PaymentsListActivity;

public class DeleteConfirmDialogFragment extends DialogFragment {
    Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = getArguments().getInt("position");
                        OnPositiveDelete onPositiveDelete = (PaymentsListActivity)context;
                        onPositiveDelete.positiveDelete(position);
                    }
                })
                .setNegativeButton("No way", null).create();
    }

    public interface OnPositiveDelete {
        void positiveDelete(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }
}