package hu.denes.command_center.storage;

import hu.denes.command_center.client_connection.Loco;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Storage {

    public List<Loco> getLocoList(){
	return new ArrayList<Loco>();
    }

    public void saveLocos(Collection<Loco> locos){
	return;
    }

}
