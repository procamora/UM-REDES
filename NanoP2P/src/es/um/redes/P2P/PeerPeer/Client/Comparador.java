package es.um.redes.P2P.PeerPeer.Client;

import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

//a comparator that compares Strings
class Comparador implements Comparator<Long> {
	// http://www.programcreek.com/2013/03/java-sort-map-by-value/
	private HashMap<Long, HashSet<Socket>> mapaPeers = new HashMap<>();
	private HashMap<Long, Estado> mapaEstados;

	public Comparador(HashMap<Long, HashSet<Socket>> mapaPeers, HashMap<Long, Estado> mapaEstados) {
		this.mapaPeers = new HashMap<>(mapaPeers);
		this.mapaEstados = new HashMap<>(mapaEstados);
	}

	@Override
	public int compare(Long l1, Long l2) {
		if (mapaPeers.get(l1).size() > mapaPeers.get(l2).size())
			return 1;
		else if (mapaPeers.get(l1).size() < mapaPeers.get(l2).size())
			return -1;
		// aqui son iguales, por lo que comparo por estado
		else {
			if (mapaEstados.get(l1) == Estado.NO_DESCARGADO && mapaEstados.get(l2) != Estado.NO_DESCARGADO)
				return -1;
			else
				return 1;
		}
	}
}