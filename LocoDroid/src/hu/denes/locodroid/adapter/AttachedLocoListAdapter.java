package hu.denes.locodroid.adapter;

import hu.denes.locodroid.R;
import hu.denes.locodroid.async.SendCommandAsyncTask;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AttachedLocoListAdapter extends BaseAdapter {
	ArrayList<Loco> locos;
	String hostAddress;
	int locoAddress;

	public void setLocos(final List<Loco> locos) {
		this.locos.clear();
		this.locos.addAll(locos);
	}

	public AttachedLocoListAdapter(final String hostAddress,
			final int locoAddress) {
		locos = new ArrayList<Loco>();
		this.hostAddress = hostAddress;
		this.locoAddress = locoAddress;
	}

	@Override
	public int getCount() {
		return locos.size();
	}

	@Override
	public Object getItem(final int position) {
		return locos.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return 0;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final Loco loco = locos.get(position);
		if (loco == null) {
			return null;
		}
		final LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View locoView = inflater.inflate(
				(R.layout.attach_loco_list_item), null);
		((TextView) locoView.findViewById(R.id.locoNameTextView)).setText(loco
				.getName());
		((TextView) locoView.findViewById(R.id.locoAddressTextView))
				.setText(loco.getAddress().toString());
		final CheckBox c = (CheckBox) locoView
				.findViewById(R.id.selectAttachedLococheckBox);
		c.setChecked(loco.getTicked());
		c.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				String request;
				if (isChecked) {
					request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ locoAddress
							+ ",	\"type\": \"add-loco-to-train\", \"value\": \""
							+ loco.getAddress() + "\" } }";
				} else {
					request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ locoAddress
							+ ",	\"type\": \"remove-loco-from-train\", \"value\": \""
							+ loco.getAddress() + "\"} }";
				}
				sendCommand(request);
			}
		});

		return locoView;
	}

	private void sendCommand(final String request) {

		new SendCommandAsyncTask().execute(hostAddress, request);

	}
}
