package edu.rosehulman.photomessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class SetAlarmDialogFragment extends DialogFragment {

	private MainActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MainActivity) activity;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Set time for message");
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_set_time, null);
		builder.setView(view);
		final TimePicker picker = (TimePicker) view.findViewById(R.id.dialog_set_time_picker);
		final Button soonButton = (Button)view.findViewById(R.id.dialog_set_time_button);
		soonButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.setSoonAlarm();
				dismiss();
			}
		});
		
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int hour = picker.getCurrentHour();
				int minute = picker.getCurrentMinute();
				mActivity.setFixedAlarm(hour, minute);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

}
