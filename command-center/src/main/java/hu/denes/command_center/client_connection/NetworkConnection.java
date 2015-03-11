package hu.denes.command_center.client_connection;

import hu.denes.command_center.storage.Storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkConnection {
    private final Map<Integer, Loco> locoMap;
    private final Storage storage;

    public NetworkConnection(Storage storage){
	this.storage = storage;
	locoMap = new HashMap<Integer, Loco>();
    }

    public void setLocos(List<Loco> locoList){
	for(final Loco l : locoList){
	    locoMap.put(l.getAddress(), l);
	}
    }

    public void loadLocos(){
	storage.getLocoList();
    }

    public void saveLocos(){
	storage.saveLocos(locoMap.values());
    }
}
