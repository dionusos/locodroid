package hu.denes.locodroid;

import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.adapter.LocoListAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class LocoListActivity extends ListActivity {

	String controlCenterAddress;
	LocoListAdapter adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_loco_list);
		final Intent intent = getIntent();
		controlCenterAddress = intent.getStringExtra("hostAddress");
		adapter = new LocoListAdapter(controlCenterAddress);

		setListAdapter(adapter);
		registerForContextMenu(getListView());

		adapter.refresh();

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loco_list, menu);
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
		if (id == R.id.refreshListMenuItem) {
			adapter.refresh();
			return true;
		}
		if (id == R.id.addNewLocoMenuItem) {
			final Intent intent = new Intent(this, AddNewLocoActivity.class);
			intent.putExtra("hostAddress", controlCenterAddress);
			intent.putExtra("loco-type", "new-loco");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		final Loco loco = ((Loco) ((LocoListAdapter) getListAdapter())
				.getItem(position));
		final Intent intent = new Intent(this, TrainDriverActivity.class);
		intent.putExtra("hostAddress", controlCenterAddress);
		intent.putExtra("locoAddress", loco.getAddress());
		intent.putExtra("locoName", loco.getName());
		startActivity(intent);
		// super.onListItemClick(l, v, position, id);
	}
}
