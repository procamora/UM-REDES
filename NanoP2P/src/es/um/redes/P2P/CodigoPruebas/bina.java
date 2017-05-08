package es.um.redes.P2P.CodigoPruebas;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

enum Estado {
	DESCARGADO_GUARDADO, EN_DESCARGA, NO_DESCARGADO
}

// a comparator that compares Strings
class ValueComparator implements Comparator<Long> {
	// http://www.programcreek.com/2013/03/java-sort-map-by-value/
	HashMap<Long, HashSet<String>> map = new HashMap<>();

	public ValueComparator(HashMap<Long, HashSet<String>> map2) {
		this.map.putAll(map2);
	}

	@Override
	public int compare(Long l1, Long l2) {
		if (map.get(l1).size() >= map.get(l2).size())
			return 1;
		if (map.get(l1).size() < map.get(l2).size())
			return -1;
		else
			return 0;

	}
}

public class bina {
	// HashMap<Long, HashSet<String>> map = new HashMap<Long,
	// HashSet<String>>();
	// ValueComparator bvc = new ValueComparator(map);
	static HashMap<Long, HashSet<String>> mapaPeers = new HashMap<>();
	HashMap<Long, Estado> mapaEstados = new HashMap<>();

	public synchronized long bookNextChunkNumber(long[] listaNumChunk, String ip) {
		// FIXME si tengo todos los trozos calcular los trozos y añadirlos a los
		// diccionarios
		if (listaNumChunk.length == 0)
			return 0;

		// comprobar al iniio si el chunk ya esta descargado, si lo esta pasar
		// al siguiete chunk
		for (long numChunk : listaNumChunk) {
			// actualizo lista de estados
			if (!mapaEstados.containsKey(numChunk))
				mapaEstados.put(numChunk, Estado.NO_DESCARGADO);

			// actualizo lista de peers
			if (mapaPeers.containsKey(numChunk)) {
				HashSet<String> peerSet = mapaPeers.get(numChunk);
				if (!peerSet.contains(ip)) {
					HashSet<String> copia = new HashSet<>(peerSet);
					copia.add(ip);
					mapaPeers.remove(numChunk);
					mapaPeers.put(numChunk, copia);
				}
			} else {
				HashSet<String> peerSet = new HashSet<>();
				peerSet.add(ip);
				mapaPeers.put(numChunk, peerSet);
			}
		}

		// retorna el primer nunChunk Qque contentga la ip del peer
		for (Long entry : mapaPeers.keySet()) {
			if (mapaEstados.get(entry) == Estado.NO_DESCARGADO)
				return entry;
		}

		return -1;
	}

	public void descargadoChunk(long chunk) {
		for (Long chunks : mapaEstados.keySet()) {
			if (chunk == chunks)
				mapaEstados.replace(chunk, Estado.DESCARGADO_GUARDADO);
		}

	}

	public static TreeMap<Long, HashSet<String>> sortMapByValue(HashMap<Long, HashSet<String>> map) {
		// TreeMap is a map sorted by its keys.
		// The comparator is used to sort the TreeMap by keys.
		TreeMap<Long, HashSet<String>> result = new TreeMap<>(new ValueComparator(map));
		result.putAll(map);
		return result;
	}

	public void imprime() {

		for (Long chunks : mapaEstados.keySet()) {
			System.out.println(chunks + " => " + mapaEstados.get(chunks));
		}
		System.out.println("NORMAL");
		System.out.println(mapaPeers);

		System.out.println("ORDENADOS RAREZA");
		TreeMap<Long, HashSet<String>> sortedMap = sortMapByValue(mapaPeers);
		System.out.println(sortedMap);

	}

	public static void main(String[] args) {

		bina a = new bina();

		long[] uno = { 1, 2, 3, 4, 5 };
		long[] dos = { 4, 6 };
		long[] tres = { 1, 2, 3, 4 };

		long des;
		des = a.bookNextChunkNumber(uno, "192.168.1.1");
		System.out.println(des);
		a.descargadoChunk(des);
		a.imprime();
		System.out.println("INICIO DESCARGA");
		des = a.bookNextChunkNumber(dos, "192.168.1.2");
		System.out.println(des);
		a.descargadoChunk(des);
		des = a.bookNextChunkNumber(tres, "192.168.1.3");
		System.out.println(des);
		a.descargadoChunk(des);

		a.imprime();

		System.out.println("FIN");
	}

}
