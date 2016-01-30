package edu.rosehulman.photomessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class AddMessageDialogFragment extends DialogFragment {

    private MainActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a message");
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_add_message, null);
        builder.setView(view);
        final EditText messageEditText = (EditText) view.findViewById(R.id.dialog_add_message_edit_text);
        final ToggleButton colorToggleButton = (ToggleButton) view.findViewById(R.id.dialog_add_message_toggle);


        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.setMessage(messageEditText.getText().toString(), colorToggleButton.isSelected());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

}
