package hu.denes.locodroid;

import java.net.InetAddress;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Client;

public class ControlCenterAdapter extends BaseAdapter {

	ArrayList<String> hosts;
	Client client;

	public void save(final Bundle out) {
		out.putStringArrayList("HOSTS", hosts);
	}

	public void restore(final Bundle in) {
		hosts = in.getStringArrayList("HOSTS");
	}

	public void refresh() {
		for (final InetAddress a : client.discoverHosts(54777, 5000)) {
			hosts.add(a.getHostAddress());
		}
	}

	public ControlCenterAdapter(final Client client) {
		this.client = client;
		hosts = new ArrayList<String>();
		// refresh();
	}

	@Override
	public int getCount() {
		return hosts.size();
	}

	@Override
	public Object getItem(final int position) {

		return hosts.get(position);
	}

	@Override
	public long getItemId(final int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {

		final String host = hosts.get(position);
		if (host == null) {
			return null;
		}
		final LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View foundCommandCenterView = inflater.inflate(
				(R.layout.found_command_center), null);
		((TextView) foundCommandCenterView
				.findViewById(R.id.hostAddressTextView)).setText(host);
		((Button) foundCommandCenterView.findViewById(R.id.connectCenterButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View v) {

			}
				});

		return foundCommandCenterView;
	}

}
