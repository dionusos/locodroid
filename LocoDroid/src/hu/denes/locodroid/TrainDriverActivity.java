package hu.denes.locodroid;

import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.async.SendCommandAsyncTask;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TrainDriverActivity extends Activity implements OnRefreshListener {

	private String hostAddress;
	private Integer locoAddress;
	private String locoName;
	private boolean emergencyStopped = false;
	private SeekBar speedSeekBar;
	private Switch sw;

	private ToggleButton lightButton;

	private SwipeRefreshLayout swipeRefreshLayout;
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (intent == null) {
				return;
			}

			if (intent.getIntExtra("locoAddress", 0) != locoAddress) {
				return;
			}

			final Loco l = Globals.GLOBAL_LOCO_MAP.get(locoAddress);
			if (l == null) {
				return;
			}

			speedSeekBar.setMax(l.getMaxSpeed());
			speedSeekBar.setProgress(l.getSpeed());
			sw.setChecked(l.getDirection() == 128);
			lightButton.setChecked(l.isLightsOn());
			swipeRefreshLayout.setRefreshing(false);
		}

	};

	private int getLocoAddress() {
		return locoAddress;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_driver);
		final Intent intent = getIntent();
		hostAddress = intent.getStringExtra("hostAddress");
		locoAddress = intent.getIntExtra("locoAddress", 0);
		locoName = intent.getStringExtra("locoName");
		final Context _this = this;

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshTrainDriver);
		swipeRefreshLayout.setDistanceToTriggerSync(500);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW,
				Color.GREEN, Color.BLUE);

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("LOCO_DETAILS_RECEIVED"));

		final TextView tv = (TextView) findViewById(R.id.locoAddressTextView);
		tv.setText(locoName + "@" + locoAddress);
		lightButton = (ToggleButton) findViewById(R.id.lightToggleButton);
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

		speedSeekBar = (SeekBar) findViewById(R.id.speedSeekBar);
		speedSeekBar.setMax(127);

		speedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"speed\", \"value\": \""
						+ seekBar.getProgress() + "\"} }";
				sendCommand(request);

			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(final SeekBar seekBar,
					final int progress, final boolean fromUser) {

			}
		});

		final Button addRemoteLoco = (Button) findViewById(R.id.addRemoteLocoButton);
		addRemoteLoco.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				/*
				 * final String request =
				 * "{\"target\": \"loco\",\"function\": {\"address\": " +
				 * getLocoAddress() +
				 * ",	\"type\": \"add-loco-to-train\", \"value\": \"" +
				 * getOtherLocoAddress() + "\"} }"; sendCommand(request);
				 */
				final Intent i = new Intent(_this, AttachLocoActivity.class);
				i.putExtra("hostAddress", hostAddress);
				i.putExtra("locoAddress", locoAddress);
				startActivity(i);

			}
		});

		sw = (Switch) findViewById(R.id.directionSwitch);
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
				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"speed\", \"value\": \""
						+ speedSeekBar.getProgress() + "\"} }";
				sendCommand(request);

			}
		});
		final Button increaseSpeedButton = (Button) findViewById(R.id.increaseSpeedButton);
		increaseSpeedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				speedSeekBar.setProgress(speedSeekBar.getProgress() + 1);
				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"speed\", \"value\": \""
						+ speedSeekBar.getProgress() + "\"} }";
				sendCommand(request);
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

		onRefresh();
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

	@Override
	public void onRefresh() {
		swipeRefreshLayout.setRefreshing(true);
		final Intent i = new Intent("GET_LOCO_DETAILS");
		i.putExtra("job", "getLocoDetails");
		i.putExtra("locoAddress", locoAddress);
		i.putExtra("hostAddress", hostAddress);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);
	}
}
