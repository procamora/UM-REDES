package es.um.redes.P2P.PeerPeer.Client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;

import es.um.redes.P2P.App.Peer;
import es.um.redes.P2P.App.PeerController;
import es.um.redes.P2P.PeerTracker.Message.Message;
import es.um.redes.P2P.PeerTracker.Message.MessageSeedInfo;
import es.um.redes.P2P.util.FileDigest;
import es.um.redes.P2P.util.FileInfo;

enum Estado {
	DESCARGADO_GUARDADO, EN_DESCARGA, NO_DESCARGADO
}

public class Downloader implements DownloaderIface {

	public static final int CHUNKS_PROGRESSBAR = 100;
	public static final int NUM_CHUNK_NO_DISPONIBLE = -1;
	private static final double PORCENTAJE_REFRESCO = 2; // 2%

	private static StringBuffer buffer = new StringBuffer();

	private FileInfo targetFile;
	private HashSet<InetSocketAddress> seeds;
	private long totalChunks;
	private short chunkSize;
	private PeerController peer;
	private long totalChunkDescargados;
	private HashSet<Long> chunksDownloadedFromSeeders;
	private double porcentajeRefresco;

	private HashMap<Long, Estado> mapaEstados;
	private ProgressBar progressBar;

	public Downloader(short chunkSize, FileInfo targetFile, PeerController peer) {
		if (targetFile == null)
			throw new IllegalArgumentException("tarjetfile no puede ser null");

		this.chunkSize = chunkSize;
		this.targetFile = targetFile;
		this.peer = peer; // para hacer el addseed

		mapaEstados = new HashMap<>();

		totalChunks = (long) targetFile.fileSize / chunkSize;
		if ((long) targetFile.fileSize % chunkSize != 0)
			totalChunks++;

		porcentajeRefresco = (totalChunks * PORCENTAJE_REFRESCO) / 100;
		if (porcentajeRefresco > 1) {
			progressBar = new ProgressBar(totalChunks);
			progressBar.start();
		}

		chunksDownloadedFromSeeders = new HashSet<>();

		totalChunkDescargados = 0;

		seeds = new HashSet<>();
	}

	// IMPORTANTE
	// En esta interfaz no están definidos los métodos necesarios para gestionar
	// la lista de trozos
	// que se están disponibles o que se están descargando. Deberán ser
	// definidos en la clase que la instancia.

	private void actualizaMapa() {
		// FIXME refactorizar codigo bookNextChunkNumber
	}

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

			// cada 2% descargado imprimo, para archivos pequeños no
			if (porcentajeRefresco > 1)
				if (totalChunkDescargados % (long) porcentajeRefresco == 0)
					progressBar.next(porcentajeRefresco);

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

	private boolean isFileCorrecto() {
		String fileRuta = Peer.db.getSharedFolderPath() + targetFile.fileName;
		byte[] byteHash = FileDigest.computeFileChecksum(fileRuta);

		if (FileDigest.getChecksumHexString(byteHash).equals(targetFile.fileHash))
			return true;

		return false;
	}

	// Método para recoger todos los threads de descarga (DownloaderThread) que
	// estaban activos
	@Override
	public synchronized void joinDownloaderThreads(InetSocketAddress seed) {
		seeds.remove(seed);

		if (seeds.isEmpty()) {

			if (isFileCorrecto()) {
				System.out.println("\nFICHERO DESCARGADO CON EXITO!! HASH COMPROBADO");
				peer.getMapaFicheros().remove(targetFile.fileHash);
				peer.getPeerDatabase().addDownloadedFile(targetFile);
			} else
				System.err.println("\nFichero descargado con errores, vuelva a descargarlo");

			System.out.println("\nEstadisticas:");
			System.out.println(buffer);
			buffer = new StringBuffer(); // vacio para siguientes descargas
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

}
