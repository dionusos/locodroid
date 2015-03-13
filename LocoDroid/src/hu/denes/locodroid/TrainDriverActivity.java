package hu.denes.locodroid;

import java.io.IOException;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

import com.esotericsoftware.kryonet.Client;

public class TrainDriverActivity extends Activity {

	String hostAddress;
	Client client;

	private int getLocoAddress() {
		final EditText et = (EditText) findViewById(R.id.locoAddresssEditText);
		return Integer.parseInt(et.getText().toString());
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_driver);
		final Intent intent = getIntent();
		hostAddress = intent.getStringExtra("hostAddress");
		client = new Client();
		client.start();
		final ToggleButton lightButton = (ToggleButton) findViewById(R.id.lightToggleButton);
		lightButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				try {
					client.connect(5000, InetAddress.getByName(hostAddress),
							54555, 54777);
				} catch (final IOException e) {
					e.printStackTrace();
				}

				if (isChecked) {
					final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ getLocoAddress()
							+ ",	\"type\": \"lights\", \"value\": \"on\"} }";
					client.sendTCP(request);
				} else {
					final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ getLocoAddress()
							+ ",	\"type\": \"lights\", \"value\": \"off\"}}";
					client.sendTCP(request);
				}

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
				try {
					client.connect(5000, InetAddress.getByName(hostAddress),
							54555, 54777);
				} catch (final IOException e) {
					e.printStackTrace();
				}

				final String request = "{\"target\": \"loco\",\"function\": {\"address\": "
						+ getLocoAddress()
						+ ",	\"type\": \"speed\", \"value\": \""
						+ progress
						+ "\"} }";
				client.sendTCP(request);

			}
		});
	}

	@Override
	protected void onStart() {

		client.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		client.stop();
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
}
