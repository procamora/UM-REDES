package es.um.redes.P2P.PeerPeer.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

import es.um.redes.P2P.App.Tracker;
import es.um.redes.P2P.PeerTracker.Message.*;

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
	private FileInfo[] ficherosLocales;

	public Downloader(short chunkSize, FileInfo targetFile, FileInfo[] ficherosLocales) {
		this.chunkSize = chunkSize;
		this.targetFile = targetFile;
		mapaPeers = new TreeMap<>();
		mapaEstados = new HashMap<>();
		this.ficherosLocales = ficherosLocales;
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

	public synchronized long bookNextChunkNumber(long[] listaNumChunk, InetSocketAddress ip) {
		/*for (long numChunk : listaNumChunk) {
			HashSet<InetSocketAddress> peerSet = mapaPeers.get(numChunk);
			if (!peerSet.contains(ip)) { // tenemos un fallo,
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
		}*/

		for (long numChunk: listaNumChunk) {
			Estado estado = mapaEstados.get(numChunk);
			if (estado == null) System.err.println("El numero de Chunk no esta en el mapa");
			else {
				if (estado == Estado.NO_DESCARGADO) {
				//	long chunkForDownload = numChunk;
					HashSet<InetSocketAddress> peerSet = mapaPeers.get(numChunk);
					if (peerSet.contains(ip)) {
						return  numChunk;
					}

				}
			}
		}
			return -1;
		}

		public synchronized boolean setChunkDownloaded(long numChunk, boolean descargado) {
			/*
				Si el numero de trozo se ha descargado satisfactoriamente,
				tendriamos que mandar un mensaje addSeed al tracker con la nueva lista de ficheros que tenemos
				
			 */
		if (descargado) {
				boolean primerChunkDescargado = true; // Indica si es el primer trozo descargado del fichero
			    HashSet<Estado> estados = (HashSet<Estado>) mapaEstados.values();
			    if (!estados.isEmpty()) {
					for (Estado estado : estados) {
						if (estado == Estado.DESCARGADO_GUARDADO) {
							primerChunkDescargado = false;
						}
					}
				}
				mapaEstados.replace(numChunk, Estado.DESCARGADO_GUARDADO);
				if (primerChunkDescargado) {
					//Message mensajeAddSeed = Message.makeAddSeedRequest(Tracker.TRACKER_PORT, )
				}
			}
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
		for(Long numchunk: mapaEstados.keySet()) {
			Estado descargado = mapaEstados.get(numchunk);
			if (descargado == Estado.NO_DESCARGADO || descargado == Estado.EN_DESCARGA){
				return false;
			}
		}
		return false;
	}

	public Set<Long> getListDownloadedChunks(){
		HashSet<Long> lista = new HashSet<>();
		for (long numChunk : mapaEstados.keySet()) {
			Estado estado = mapaEstados.get(numChunk);
			if (estado == Estado.DESCARGADO_GUARDADO) {
				lista.add(numChunk);
			}
		}
		return lista;
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
