package hu.denes.locodroid;

import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.async.SaveLocosAsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewLocoActivity extends Activity {
	String hostAddress;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_loco);
		final Intent i = getIntent();
		hostAddress = i.getStringExtra("hostAddress");
		((Button) findViewById(R.id.saveNewLocoButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View v) {
						final Integer locoAddress = Integer
								.parseInt(((EditText) findViewById(R.id.locoAddressEditText))
										.getText().toString());
						final Loco l = new Loco(locoAddress);
						l.setName(((EditText) findViewById(R.id.locoNameEditText))
								.getText().toString());

						l.setMaxSpeed(Integer
								.parseInt(((EditText) findViewById(R.id.maxSpeedEditText))
										.getText().toString()));

						new SaveLocosAsyncTask(hostAddress).execute(l);
						/*
				 * final Intent intent = new Intent(null,
				 * LocoListActivity.class);
				 * intent.putExtra("hostAddress", hostAddress);
				 * startActivity(intent);
				 */
						Toast.makeText(getApplicationContext(), "Loco added!",
						Toast.LENGTH_SHORT).show();

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_new_loco, menu);
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