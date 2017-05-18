package es.um.redes.P2P.PeerPeer.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import es.um.redes.P2P.App.Peer;
import es.um.redes.P2P.PeerPeer.Message.*;
import es.um.redes.P2P.util.Ficheros;

/**
 * @author rtitos
 * 
 *         Threads of this class handle the download of file chunks from a given
 *         seed through a TCP socket established to the seed socket address
 *         provided to the constructor.
 */
public class DownloaderThread extends Thread {
	private final static int FRECUENCIA_UPDATE_SEEDLIST = 500;
	private Downloader downloader;
	private Socket downloadSocket;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private long numChunksDownloaded;
	private long tiempoInicio;
	private long tiempoFin;
	private InetSocketAddress seed;

	public DownloaderThread(Downloader dl, InetSocketAddress seed) {
		if (dl == null)
			throw new IllegalArgumentException("dl no puede ser null en DownloaderThread");
		if (seed == null)
			throw new IllegalArgumentException("seed no puede ser null en DownloaderThread");

		downloader = dl;
		numChunksDownloaded = 0;
		tiempoInicio = System.currentTimeMillis();

		try {
			downloadSocket = new Socket(seed.getAddress(), seed.getPort());
			this.seed = seed;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Recibe un mensaje que contiene un fragmento y se almacena en el archivo
	private boolean receiveAndWriteChunk(long chunkActual) {
		Message requests = Message.makeChunkRequest(downloader.getTargetFile().fileHash, chunkActual);
		sendMessageToPeer(requests);

		Message msgRecibido = receiveMessageFromPeer(getSizeChunkRead(chunkActual));
		if (msgRecibido != null) {
			// if (msgRecibido1.getOpCode() == Message.OP_CHUNK_ACK)
			MessageChunkQueryResponse response = (MessageChunkQueryResponse) msgRecibido;

			Ficheros.escritura(Peer.db.getSharedFolderPath() + downloader.getTargetFile().fileName, response.getDatos(),
					(chunkActual * downloader.getChunkSize()));
			return true;
		}
		return false;
	}

	/**
	 * Metodo que retorna el tamaño del array que esperamos recibir
	 * 
	 * @param numChunk
	 * @return
	 */
	private short getSizeChunkRead(long numChunk) {
		long posicion = numChunk * downloader.getChunkSize();
		long bytesRestantes = downloader.getTargetFile().fileSize - posicion;
		if (bytesRestantes < 4096)
			return (short) bytesRestantes;
		else
			return downloader.getChunkSize();

	}

	// Recibe un mensaje que contiene un fragmento y se almacena en el archivo
	private long receiveAndProcessChunkList() {
		// pido la lista de trozos del fichero
		Message requests = Message.makeGetChunkRequest(downloader.getTargetFile().fileHash, (long) 0);
		sendMessageToPeer(requests);
		Message msgRecibido = receiveMessageFromPeer((short) 0);

		// creo que no hace falta comprobar que es el mensaje correcto
		if (msgRecibido != null) {
			MessageChunkQueryResponse response = (MessageChunkQueryResponse) msgRecibido;
			return downloader.bookNextChunkNumber(response.desconcatenaArrayBytesDatos());
		}
		return Downloader.NUM_CHUNK_NO_DISPONIBLE;
	}

	// Número de fragmentos ya descargados por este hilo
	private long getNumChunksDownloaded() {
		return numChunksDownloaded;
	}

	private Message receiveMessageFromPeer(short chunkSize) {
		Message msg = null;
		try {
			InputStream is = downloadSocket.getInputStream();
			dis = new DataInputStream(is);
			msg = Message.parseResponse(dis, chunkSize);
		} catch (IOException e) {
			// si hay un problema no retorno null, y se informara que el chunk
			// no se ha podido sdescargar
		}
		return msg;
	}

	private void sendMessageToPeer(Message msg) {
		try {
			OutputStream os = downloadSocket.getOutputStream();
			dos = new DataOutputStream(os);
			dos.write(msg.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void calculaEstadisticas() {
		String info;
		try {
			double byteDescargados = (numChunksDownloaded * downloader.getChunkSize());
			String MbDescargados = String.format("%,.2f", byteDescargados / 1024 / 1024);
			String speedMb = null;
			double seconds = (tiempoFin - tiempoInicio) / 1000;
			double megabytes = ((byteDescargados / seconds) / 1024) / 1024;
			speedMb = String.format("%,.2f", megabytes);
			info = "\n" + downloader.getTargetFile().fileName + "\tHilo " + getName() + "\tSeeder: " + downloadSocket
					+ "\tMegaBytes descargados: " + MbDescargados + "Mb\tVelocidad: " + speedMb + "Mb/s";
		} catch (ArithmeticException ae) {
			info = "\nHilo " + getName() + " no se puieron obtener estadisticas";
		}
		Downloader.addResumenThread(info);
	}

	// Main code to request chunk lists and chunks
	public void run() {
		long chunkActual = 0;
		System.out.println("Inicia hilo " + getName());

		do {
			// if (numChunksDownloaded % FRECUENCIA_UPDATE_SEEDLIST == 0)
			// FIXME preguntamos por nuevos seeders
			// downloader.addThreads();

			chunkActual = receiveAndProcessChunkList();
			// si hay un chunk valido lo proceso
			if (chunkActual >= 0) {
				if (receiveAndWriteChunk(chunkActual)) {
					numChunksDownloaded++;
					downloader.setChunkDownloaded(chunkActual, true);
				} else
					downloader.setChunkDownloaded(chunkActual, false);
			} else { // sino hay chunk valido espero 1s y volvere a probar
				try {
					Thread.sleep(1000);
					// downloader.addThreads(); FIXME
				} catch (InterruptedException e) {
				}
			}

		} while (!downloader.isDownloadComplete());

		try {
			downloadSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		tiempoFin = System.currentTimeMillis();
		calculaEstadisticas();
		downloader.joinDownloaderThreads(seed);
	}

}
