package hu.denes.locodroid;

import hu.denes.locodroid.adapter.ControlCenterAdapter;
import hu.denes.locodroid.service.NetworkCommunicationService;

import java.net.InetAddress;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	InetAddress commandCenterAddress;
	int currentLocoAddress = 0;
	ControlCenterAdapter adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.list_command_center);
		// final StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder().permitAll().build();

		// StrictMode.setThreadPolicy(policy);

		ClientSingleton.getInstance().getClient().start();

		adapter = new ControlCenterAdapter();
		setListAdapter(adapter);
		registerForContextMenu(getListView());

	}

	@Override
	protected void onStart() {

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
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
			final String hostAddress = ((String) ((ControlCenterAdapter) getListAdapter())
					.getItem(info.position));

			final Intent intent = new Intent(this, LocoListActivity.class);
			intent.putExtra("hostAddress", hostAddress);
			startActivity(intent);

		}
		return true;
	}

	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		final String hostAddress = ((String) ((ControlCenterAdapter) getListAdapter())
				.getItem(position));
		// create service
		final Intent i = new Intent(this.getApplicationContext(),
				NetworkCommunicationService.class);
		i.putExtra("serverAddress", hostAddress);
		startService(i);

		final Intent intent = new Intent(this, LocoListActivity.class);
		intent.putExtra("hostAddress", hostAddress);
		startActivity(intent);
		// super.onListItemClick(l, v, position, id);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

}
