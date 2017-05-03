package es.um.redes.P2P.PeerPeer.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import es.um.redes.P2P.util.FileInfo;

enum Estado {
	DESCARGADO_GUARDADO, EN_DESCARGA, NO_DESCARGADO
}

public class Downloader implements DownloaderIface {

	private FileInfo targetFile;
	private InetSocketAddress[] seeds;
	private int totalChunks;
	private short chunkSize;

	private TreeMap<Long, HashSet<InetSocketAddress>> mapaPeers;
	private HashMap<Long, Estado> mapaEstados;

	public Downloader(short chunkSize, FileInfo targetFile) {
		this.chunkSize = chunkSize;
		this.targetFile = targetFile;

		mapaPeers = new TreeMap<>();
		mapaEstados = new HashMap<>();

		if (targetFile != null) {
			totalChunks = (int) targetFile.fileSize / chunkSize;
			if ((int) targetFile.fileSize % chunkSize != 0)
				totalChunks++;
		}
	}

	// IMPORTANTE
	// En esta interfaz no están definidos los métodos necesarios para gestionar
	// la lista de trozos
	// que se están disponibles o que se están descargando. Deberán ser
	// definidos en la clase que la instancia.

	public long bookNextChunkNumber(long[] listaNumChunk, InetSocketAddress ip) {
		for (long numChunk : listaNumChunk) {
			if (!mapaPeers.containsKey(ip)) {
				HashSet<InetSocketAddress> ips = new HashSet<>();
				ips.add(ip);
				mapaPeers.put(numChunk, ips);
			} else {
				HashSet<InetSocketAddress> ips = mapaPeers.get(numChunk);
				if (!ips.contains(ip)) {
					ips.add(ip);
					// copia para evitar problemas con hash modificados
					HashSet<InetSocketAddress> copia = new HashSet<>(ips);
					mapaPeers.remove(numChunk);
					mapaPeers.put(numChunk, copia);
				}
			}
			if (!mapaEstados.containsKey(ip))
				mapaEstados.put(numChunk, Estado.NO_DESCARGADO);
		}

		for (Long entry : mapaPeers.keySet()) {
			//retorna el primer nunChunk Qque contentga la ip del peer
		}

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
	public int getTotalChunks() {
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
