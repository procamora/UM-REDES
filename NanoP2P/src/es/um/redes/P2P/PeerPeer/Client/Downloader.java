package es.um.redes.P2P.PeerPeer.Client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;

import es.um.redes.P2P.App.Peer;
import es.um.redes.P2P.App.PeerController;
import es.um.redes.P2P.PeerTracker.Message.Message;
import es.um.redes.P2P.PeerTracker.Message.MessageSeedInfo;
import es.um.redes.P2P.util.FileInfo;

enum Estado {
	DESCARGADO_GUARDADO, EN_DESCARGA, NO_DESCARGADO
}

public class Downloader implements DownloaderIface {

	public static final int CHUNKS_PROGRESSBAR = 100;
	public static final int NUM_CHUNK_NO_DISPONIBLE = -1;
	private static StringBuffer buffer = new StringBuffer();

	private FileInfo targetFile;
	private HashSet<InetSocketAddress> seeds;
	private long totalChunks;
	private short chunkSize;
	private PeerController peer;
	private long totalChunkDescargados;
	private HashSet<Long> chunksDownloadedFromSeeders;

	// private HashMap<Long, HashSet<Socket>> mapaPeers;
	private HashMap<Long, Estado> mapaEstados;
	private ProgressBar progressBar;

	private long tiempoInicio;
	private long tiempoFin;

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

		seeds = new HashSet<>();

		this.tiempoInicio = System.currentTimeMillis();
		this.tiempoFin = 0;
	}

	// IMPORTANTE
	// En esta interfaz no están definidos los métodos necesarios para gestionar
	// la lista de trozos
	// que se están disponibles o que se están descargando. Deberán ser
	// definidos en la clase que la instancia.

	public synchronized long bookNextChunkNumber(long[] listaNumChunk) {
		long chunkSeleccionado = NUM_CHUNK_NO_DISPONIBLE;
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
			System.err.println("RECIBO CHUNKS SUELTOS, BIEN!!!!");
			// comprobar listaNumChunk al inicio, si el chunk ya esta
			// descargado, si lo esta pasar al siguiete chunk
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

	public synchronized boolean setChunkDownloaded(long numChunk, boolean descargado) {
		/*
		 * Si el numero de trozo se ha descargado, se notifica al traker que ya
		 * puede servir ese trozo de fichero
		 */
		if (descargado) {
			totalChunkDescargados++;

			// cada X chunks imprimo
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

			} else
				mapaEstados.replace(numChunk, Estado.DESCARGADO_GUARDADO);
			return true;
		}
		// Si no lo hemos podido descargar
		mapaEstados.replace(numChunk, Estado.NO_DESCARGADO);
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
		return (InetSocketAddress[]) seeds.toArray();
	}

	// Devuelve el número total de Chunks en los que está compuesto el archivo
	@Override
	public Long getTotalChunks() {
		return totalChunks;
	}

	/**
	 * Compruebo que el seeder del que quiero descargarme archivos no soy yo
	 * mismo
	 */
	private boolean isLocalSeeder(InetSocketAddress seedList) {
		System.out.println("seed " + seedList);
		System.out.println(seedList.getAddress().getHostAddress());
		System.out.println(seedList.getAddress());

		System.out.println("peer");
		System.out.println(peer.getSeeder().getSeederAddress());
		System.out.println(peer.getSeeder().getSeederPort());

		System.out.println();
		System.out.println();
		System.out.println();
		if (peer.getSeeder().getSeederPort() == seedList.getPort())
			return true;
		return false;
	}

	// Inicia el proceso de descarga del archivo a partir de la lista de Seeds
	@Override
	public synchronized boolean downloadFile(InetSocketAddress[] seedList) {
		for (int i = 0; i < seedList.length; i++) {
			if (seedList[i] != null && !seeds
					.contains(seedList[i]) /* && !isLocalSeeder(seedList[i]) */) {
				seeds.add(seedList[i]);
				System.out.println("Thread " + seedList[i]);
				new DownloaderThread(this, seedList[i]).start();
			}
		}
		tiempoFin = System.currentTimeMillis();
		return isDownloadComplete();
	}

	// Devuelve el número de chunks que han sido descargados de cada uno de los
	// Seeders
	@Override
	public synchronized HashSet<Long> getChunksDownloadedFromSeeders() {
		return new HashSet<>(chunksDownloadedFromSeeders);
	}

	// Informa si la descarga del fichero ya se ha completado
	@Override
	public boolean isDownloadComplete() {
		return totalChunkDescargados == totalChunks;
	}

	// Método para recoger todos los threads de descarga (DownloaderThread) que
	// estaban activos
	@Override
	public synchronized void joinDownloaderThreads(InetSocketAddress seed) {
		seeds.remove(seed);

		if (seeds.isEmpty()) {
			// FIXME aqui hacer la comprobacion del hash del fichero
			peer.getMapaFicheros().remove(targetFile.fileHash);
			System.out.println(buffer);
		}

	}

	public synchronized void addThreads() {
		// FIXME no se si este metodo es el correcto

		Message request = Message.makeGetSeedsRequest(targetFile.fileHash);
		Message response = peer.getReporter().conversationWithTracker(request);
		if (response.getOpCode() == Message.OP_SEED_LIST) {
			InetSocketAddress[] seedList = ((MessageSeedInfo) response).getSeedList();
			downloadFile(seedList);
		}
	}

	// Devuelve el tamaño de trozo que se está utilizando
	@Override
	public short getChunkSize() {
		return chunkSize;
	}

	public static synchronized void addResumenThread(String texto) {
		buffer.append(texto);
	}

	public void velocidadDescarga(Double bytes) {
		double seconds = (tiempoFin - tiempoInicio) / 1000;
		double megabytes = (bytes / 1024) / 1024;
		double bytesSeconds = (megabytes / seconds);
		System.out.print("Velocidad de descarga: ");
		System.out.printf("%.3f", bytesSeconds);
		System.out.println(" MB/seg");
	}

	public void resumenDescarga() {

		System.out.println();
		System.out.println(
				"Fichero: " + targetFile.fileName + " anadido al repositorio: " + Peer.db.getSharedFolderPath());
		System.out.println("Resumen de Descarga:");
		System.out.println("Numbers of chunks: " + totalChunks);
		System.out.println();
		double totalBytes = 0;
		for (InetSocketAddress inet : seeds) {
			System.out.println("Seed IP:" + inet.getAddress() + " puerto: " + inet.getPort());
		}
		for (Long numchunk : chunksDownloadedFromSeeders) {
			totalBytes += numchunk;
		}
		velocidadDescarga(totalBytes);
		System.out.println();
	}

}
