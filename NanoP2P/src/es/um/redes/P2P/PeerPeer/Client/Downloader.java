package es.um.redes.P2P.PeerPeer.Client;

import java.net.InetSocketAddress;

import es.um.redes.P2P.util.FileInfo;

public class Downloader implements DownloaderIface {
	
	private FileInfo targetFile;
	private InetSocketAddress[] seeds;
	private int totalChunks;
	
	private short chunkSize;

	public Downloader(short chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	
	// IMPORTANTE
	// En esta interfaz no están definidos los métodos necesarios para gestionar
	// la lista de trozos
	// que se están disponibles o que se están descargando. Deberán ser
	// definidos en la clase que la instancia.

	// Devuelve la información sobre el archivo que se está descargando
	@Override
	public FileInfo getTargetFile() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return totalChunks;
	}

	// Inicia el proceso de descarga del archivo a partir de la lista de Seeds
	@Override
	public boolean downloadFile(InetSocketAddress[] seedList) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return chunkSize;
	}

}
