package hu.denes.locodroid;

import java.net.InetAddress;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

import com.esotericsoftware.kryonet.Client;

public class MainActivity extends ListActivity {

	Client client;
	InetAddress commandCenterAddress;
	int currentLocoAddress = 0;
	ControlCenterAdapter adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.list_command_center);
		final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		client = new Client();
		adapter = new ControlCenterAdapter(client);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onStart() {
		client.start();
		super.onStart();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		adapter.save(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle state) {
		adapter.restore(state);
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onResume() {

		super.onResume();
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
		if (id == R.id.refreshCommandCenters) {
			adapter.refresh();
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onStop() {
		client.stop();
		super.onStop();
	}
}
