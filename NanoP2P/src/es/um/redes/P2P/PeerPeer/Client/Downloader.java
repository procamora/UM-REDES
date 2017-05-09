package es.um.redes.P2P.PeerPeer.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import es.um.redes.P2P.App.PeerController;
import es.um.redes.P2P.PeerTracker.Message.Message;
import es.um.redes.P2P.util.FileInfo;

enum Estado {
	DESCARGADO_GUARDADO, EN_DESCARGA, NO_DESCARGADO
}

public class Downloader implements DownloaderIface {

	private FileInfo targetFile;
	private InetSocketAddress[] seeds; // posiblemente no haga falta
	private long totalChunks;
	private short chunkSize;
	private PeerController peer;
	private long totalChunkDescargados;

	private HashMap<Long, HashSet<Socket>> mapaPeers;
	private HashMap<Long, Estado> mapaEstados;

	public Downloader(short chunkSize, FileInfo targetFile, PeerController peer) {
		this.chunkSize = chunkSize;
		this.targetFile = targetFile;
		this.peer = peer; // para hacer el addseed

		mapaPeers = new HashMap<>();
		mapaEstados = new HashMap<>();

		if (targetFile != null) {
			totalChunks = (long) targetFile.fileSize / chunkSize;
			if ((long) targetFile.fileSize % chunkSize != 0)
				totalChunks++;
		}
		totalChunkDescargados = 0;
	}

	private TreeMap<Long, HashSet<Socket>> sortMapByValue(HashMap<Long, HashSet<Socket>> map) {
		// TreeMap is a map sorted by its keys. The comparator is used to sort
		// the TreeMap by keys.
		TreeMap<Long, HashSet<Socket>> result = new TreeMap<>(new Comparador(map, mapaEstados));
		result.putAll(map);
		return result;
	}

	// IMPORTANTE
	// En esta interfaz no están definidos los métodos necesarios para gestionar
	// la lista de trozos
	// que se están disponibles o que se están descargando. Deberán ser
	// definidos en la clase que la instancia.

	public synchronized long bookNextChunkNumber(long[] listaNumChunk, Socket ip) {

		// Si es un fichero local del peer añado a mano a cada chunk el peer
		if (listaNumChunk.length == 0) {
			for (long numChunk = 0; numChunk < totalChunks; numChunk++) {
				if (!mapaEstados.containsKey(numChunk))
					mapaEstados.put(numChunk, Estado.NO_DESCARGADO);

				// actualizo lista de peers
				if (mapaPeers.containsKey(numChunk)) {
					HashSet<Socket> peerSet = mapaPeers.get(numChunk);
					if (!peerSet.contains(ip)) {
						HashSet<Socket> copia = new HashSet<>(peerSet);
						copia.add(ip);
						mapaPeers.remove(numChunk);
						mapaPeers.put(numChunk, copia);
					}
				} else {
					HashSet<Socket> peerSet = new HashSet<>();
					peerSet.add(ip);
					mapaPeers.put(numChunk, peerSet);
				}
			}
		}

		// comprobar listaNumChunk al inicio, si el chunk ya esta descargado, si
		// lo esta pasar al siguiete chunk
		for (long numChunk : listaNumChunk) {
			// actualizo lista de estados
			if (!mapaEstados.containsKey(numChunk))
				mapaEstados.put(numChunk, Estado.NO_DESCARGADO);

			// actualizo lista de peers
			if (mapaPeers.containsKey(numChunk)) {
				HashSet<Socket> peerSet = mapaPeers.get(numChunk);
				if (!peerSet.contains(ip)) {
					HashSet<Socket> copia = new HashSet<>(peerSet);
					copia.add(ip);
					mapaPeers.remove(numChunk);
					mapaPeers.put(numChunk, copia);
				}
			} else {
				HashSet<Socket> peerSet = new HashSet<>();
				peerSet.add(ip);
				mapaPeers.put(numChunk, peerSet);
			}
		}

		// System.out.println(mapaPeers);
		// System.out.println(mapaEstados);

		// retorna el primer nunChunk que contentga la ip del peer
		TreeMap<Long, HashSet<Socket>> sortedMap = sortMapByValue(mapaPeers);
		for (Long entry : sortedMap.keySet()) {
			if ((mapaEstados.get(entry) == Estado.NO_DESCARGADO) && (mapaPeers.get(entry).contains(ip))) {
				mapaEstados.replace(entry, Estado.EN_DESCARGA);
				return entry;
			}
		}
		// en caso de que no haya ningun chunk disponible para ese peer
		return -1;
	}

	public Map<Long, HashSet<Socket>> getMapaPeers() {
		return sortMapByValue(mapaPeers);
	}

	public Map<Long, Estado> getMapaEstados() {
		return Collections.unmodifiableMap(mapaEstados);
	}

	public synchronized boolean setChunkDownloaded(long numChunk) {
		/*
		 * Si el numero de trozo se ha descargado, se notifica al traker que ya
		 * puede servir ese trozo de fichero
		 */
		totalChunkDescargados++;

		// al descargar el primer chunk
		if (totalChunkDescargados == 1) {
			mapaEstados.replace(numChunk, Estado.DESCARGADO_GUARDADO);
			// mandamos aadseed
			FileInfo[] lista = { targetFile };
			Message request = Message.makeAddSeedRequest(peer.getSeeder().getSeederPort(), lista);
			peer.getReporter().conversationWithTracker(request);
			// eliminamos fichero de lista de descarga
			peer.getMapaFicheros().remove(targetFile.fileHash);
		} else
			mapaEstados.replace(numChunk, Estado.DESCARGADO_GUARDADO);
		return false;
	}

	// Devuelve la información sobre el archivo que se está descargando
	@Override
	public FileInfo getTargetFile() {
		return targetFile;
	}

	// Obtiene la lista de Seeds desde los cuales se está descargando el archivo
	@Override
	public InetSocketAddress[] getSeeds() {
		// TODO Auto-generated method stub
		return seeds;
	}

	// Devuelve el número total de Chunks en los que está compuesto el archivo
	@Override
	public Long getTotalChunks() {
		return totalChunks;
	}

	// Inicia el proceso de descarga del archivo a partir de la lista de Seeds
	@Override
	public boolean downloadFile(InetSocketAddress[] seedList) {
		// TODO Auto-generated method stub
		for (int i = 0; i < seedList.length; i++) {
			if (seedList[i] != null)
				new DownloaderThread(this, seedList[i]).start();
		}
		return isDownloadComplete();
	}

	// Devuelve el número de chunks que han sido descargados de cada uno de los
	// Seeders
	@Override
	public int[] getChunksDownloadedFromSeeders() {
		// TODO Auto-generated method stub
		return null;
	}

	// Informa si la descarga del fichero ya se ha completado
	@Override
	public boolean isDownloadComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	// Método para recoger todos los threads de descarga (DownloaderThread) que
	// estaban activos
	@Override
	public void joinDownloaderThreads() {
		// TODO Auto-generated method stub

	}

	// Devuelve el tamaño de trozo que se está utilizando
	@Override
	public short getChunkSize() {
		return chunkSize;
	}

}
