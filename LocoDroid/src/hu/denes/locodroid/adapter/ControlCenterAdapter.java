package hu.denes.locodroid.adapter;

import hu.denes.locodroid.ClientSingleton;
import hu.denes.locodroid.R;
import hu.denes.locodroid.async.DiscoverControlCentersAsyncTask;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ControlCenterAdapter extends BaseAdapter {

	private ArrayList<String> hosts;

	public void setHosts(final List<String> l) {
		hosts.clear();
		hosts.addAll(l);
	}

	public void save(final Bundle out) {
		out.putStringArrayList("HOSTS", hosts);
	}

	public void restore(final Bundle in) {
		hosts = in.getStringArrayList("HOSTS");
	}

	public void refresh() {
		hosts.clear();

		final DiscoverControlCentersAsyncTask t = new DiscoverControlCentersAsyncTask(
				this);
		t.execute((Object) ClientSingleton.getInstance().getClient());
	}

	public ControlCenterAdapter() {
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

		return foundCommandCenterView;
	}

}
