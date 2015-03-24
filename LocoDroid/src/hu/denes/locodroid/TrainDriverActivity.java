package hu.denes.locodroid;

import hu.denes.locodroid.async.SendCommandAsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TrainDriverActivity extends Activity {

	private String hostAddress;
	private Integer locoAddress;
	private String locoName;
	private boolean emergencyStopped = false;

	private int getLocoAddress() {
		return locoAddress;
	}

	private int getOtherLocoAddress() {
		final EditText et = (EditText) findViewById(R.id.otherLocoAddressEditText);
		if ("".equals(et.getText().toString())) {
			return 0;
		}
		return Integer.parseInt(et.getText().toString());
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_driver);
		final Intent intent = getIntent();
		hostAddress = intent.getStringExtra("hostAddress");
		locoAddress = intent.getIntExtra("locoAddress", 0);
		locoName = intent.getStringExtra("locoName");

		final TextView tv = (TextView) findViewById(R.id.locoAddressTextView);
		tv.setText(locoName + "@" + locoAddress);
		final ToggleButton lightButton = (ToggleButton) findViewById(R.id.lightToggleButton);
		lightButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {

				String request = "";
				if (isChecked) {
					request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ getLocoAddress()
							+ ",	\"type\": \"lights\", \"value\": \"on\"} }";
				} else {
					request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ getLocoAddress()
							+ ",	\"type\": \"lights\", \"value\": \"off\"}}";

				}
				sendCommand(request);

			}

		});

		final SeekBar speedSeekBar = (SeekBar) findViewById(R.id.speedSeekBar);
		speedSeekBar.setMax(127);

		speedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(final SeekBar seekBar,
					final int progress, final boolean fromUser) {

				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"speed\", \"value\": \""
						+ progress
						+ "\"} }";
				sendCommand(request);

			}
		});

		final Button addRemoteLoco = (Button) findViewById(R.id.addRemoteLocoButton);
		addRemoteLoco.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"add-loco-to-train\", \"value\": \""
						+ getOtherLocoAddress() + "\"} }";
				sendCommand(request);

			}
		});

		final Button removeRemoteLoco = (Button) findViewById(R.id.removeRemoteLocoButton);
		removeRemoteLoco.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"remove-loco-from-train\", \"value\": \""
						+ getOtherLocoAddress() + "\"} }";
				sendCommand(request);

			}
		});

		final Switch sw = (Switch) findViewById(R.id.directionSwitch);
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"direction\", \"value\": \""
						+ (isChecked ? "forward" : "backward") + "\"} }";
				sendCommand(request);

			}
		});

		final Button decreaseSpeedButton = (Button) findViewById(R.id.decreaseSpeedButton);
		decreaseSpeedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				speedSeekBar.setProgress(speedSeekBar.getProgress() - 1);
			}
		});
		final Button increaseSpeedButton = (Button) findViewById(R.id.increaseSpeedButton);
		increaseSpeedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				speedSeekBar.setProgress(speedSeekBar.getProgress() + 1);
			}
		});
		final Button stopButton = (Button) findViewById(R.id.stopButton);
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (!emergencyStopped) {
					sendCommand("STOP");
					stopButton.setText("RESUME");
					emergencyStopped = true;
				} else {
					sendCommand("RESUME");
					stopButton.setText("STOP");
					emergencyStopped = false;
				}
			}
		});
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.train_driver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sendCommand(final String request) {

		new SendCommandAsyncTask().execute(hostAddress, request);

	}
}
