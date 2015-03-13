package hu.denes.locodroid;

import java.io.IOException;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.esotericsoftware.kryonet.Client;

public class MainActivity extends Activity {

	Client client;
	InetAddress commandCenterAddress;
	int currentLocoAddress = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		client = new Client();
		client.start();

		final ToggleButton lightButton = (ToggleButton) findViewById(R.id.lightToggleButton);
		lightButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				try {
					client.connect(5000, commandCenterAddress.getHostAddress(),
							54555, 54777);
				} catch (final IOException e) {
					e.printStackTrace();
				}

				if (isChecked) {
					final String request = "{\"target\": \"loco\",\"function\": {\"address\": 1,	\"type\": \"lights\", \"value\": \"on\"} }";
					client.sendTCP(request);
				} else {
					final String request = "{\"target\": \"loco\",\"function\": {\"address\": 1,	\"type\": \"lights\", \"value\": \"off\"}}";
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
					client.connect(5000, commandCenterAddress.getHostAddress(),
							54555, 54777);
				} catch (final IOException e) {
					e.printStackTrace();
				}

				final String request = "{\"target\": \"loco\",\"function\": {\"address\": 1,	\"type\": \"speed\", \"value\": \""
						+ progress + "\"} }";
				client.sendTCP(request);

			}
		});
		final Spinner spinner = (Spinner) findViewById(R.id.locoAddressSpinner);
		// spinner.
	}

	@Override
	protected void onResume() {
		super.onResume();
		final InetAddress address = client.discoverHost(54777, 5000);
		if (address != null) {
			commandCenterAddress = address;
			((TextView) findViewById(R.id.textview)).setText(address
					.getHostAddress());
		}
	};

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	@Override
	protected void onPause() {
	}

	@Override
	protected void onStop() {
		client.stop();
		super.onStop();
	}
}
