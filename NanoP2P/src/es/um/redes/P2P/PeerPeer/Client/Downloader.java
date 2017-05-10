package es.um.redes.P2P.PeerPeer.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import es.um.redes.P2P.App.PeerController;
import es.um.redes.P2P.PeerTracker.Message.Message;
import es.um.redes.P2P.util.FileInfo;

enum Estado {
	DESCARGADO_GUARDADO, EN_DESCARGA, NO_DESCARGADO
}

public class Downloader implements DownloaderIface {

	public static final int CHUNKS_PROGRESSBAR = 100;
	private FileInfo targetFile;
	private InetSocketAddress[] seeds; // posiblemente no haga falta
	private long totalChunks;
	private short chunkSize;
	private PeerController peer;
	private long totalChunkDescargados;
	private HashSet<Long> chunksDownloadedFromSeeders;

	// private HashMap<Long, HashSet<Socket>> mapaPeers;
	private HashMap<Long, Estado> mapaEstados;
	private ProgressBar progressBar;

	public Downloader(short chunkSize, FileInfo targetFile, PeerController peer) {
		if (targetFile == null)
			throw new IllegalArgumentException("tarjetfile no puede ser null");

		this.chunkSize = chunkSize;
		this.targetFile = targetFile;
		this.peer = peer; // para hacer el addseed

		mapaEstados = new HashMap<>();
		// mapaPeers = new HashMap<>();

		totalChunks = (long) targetFile.fileSize / chunkSize;
		if ((long) targetFile.fileSize % chunkSize != 0)
			totalChunks++;

		progressBar = new ProgressBar(totalChunks / CHUNKS_PROGRESSBAR);
		progressBar.start();
		chunksDownloadedFromSeeders = new HashSet<>();

		totalChunkDescargados = 0;
	}

	// IMPORTANTE
	// En esta interfaz no están definidos los métodos necesarios para gestionar
	// la lista de trozos
	// que se están disponibles o que se están descargando. Deberán ser
	// definidos en la clase que la instancia.

	public synchronized long bookNextChunkNumber(long[] listaNumChunk) {
		long chunkSeleccionado = -1;
		boolean getChunk = true;
		// Si es un fichero local del peer añado a mano a cada chunk el peer
		if (listaNumChunk.length == 0) {
			for (long numChunk = 0; numChunk < totalChunks; numChunk++) {
				if (!mapaEstados.containsKey(numChunk))
					mapaEstados.put(numChunk, Estado.NO_DESCARGADO);
				// retorna el primer nunChunk que contentga la ip del peer
				if (mapaEstados.get(numChunk) == Estado.NO_DESCARGADO && getChunk) {
					mapaEstados.replace(numChunk, Estado.EN_DESCARGA);
					chunkSeleccionado = numChunk;
					getChunk = false;
				}
			}

		} else {
			// comprobar listaNumChunk al inicio, si el chunk ya esta
			// descargado, si
			// lo esta pasar al siguiete chunk
			for (long numChunk : listaNumChunk) {
				if (!mapaEstados.containsKey(numChunk))
					mapaEstados.put(numChunk, Estado.NO_DESCARGADO);

				// retorna el primer nunChunk que contentga la ip del peer
				if (mapaEstados.get(numChunk) == Estado.NO_DESCARGADO && getChunk) {
					mapaEstados.replace(numChunk, Estado.EN_DESCARGA);
					chunkSeleccionado = numChunk;
					getChunk = false;
				}
			}
		}
		// en caso de que no haya ningun chunk disponible para ese peer
		return chunkSeleccionado;
	}

	public synchronized boolean setChunkDownloaded(long numChunk) {
		/*
		 * Si el numero de trozo se ha descargado, se notifica al traker que ya
		 * puede servir ese trozo de fichero
		 */
		totalChunkDescargados++;

		// cada 5 chunks imprimo
		if (totalChunkDescargados % CHUNKS_PROGRESSBAR == 0)
			progressBar.next();

		chunksDownloadedFromSeeders.add(numChunk);

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
	public Long[] getChunksDownloadedFromSeeders() {
		Long[] array = chunksDownloadedFromSeeders.toArray(new Long[chunksDownloadedFromSeeders.size()]);
		return array;
	}

	// Informa si la descarga del fichero ya se ha completado
	@Override
	public boolean isDownloadComplete() {
		// TODO Auto-generated method stub
		if (totalChunkDescargados == totalChunks) {
			System.out.println("igual");
			System.out.println(totalChunks);
			System.out.println(totalChunkDescargados);

		} else if (totalChunkDescargados > totalChunks) {
			System.out.println("mayor");
			System.out.println(totalChunks);
			System.out.println(totalChunkDescargados);
		}

		return totalChunkDescargados == totalChunks;
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
