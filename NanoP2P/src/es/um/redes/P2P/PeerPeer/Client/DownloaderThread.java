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
 * @author procamora
 * 
 *         Threads of this class handle the download of file chunks from a given
 *         seed through a TCP socket established to the seed socket address
 *         provided to the constructor.
 */
public class DownloaderThread extends Thread {
	private static final int BYTES_TO_MB = 1024 * 1024;
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
		if (bytesRestantes < downloader.getChunkSize())
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
			try {
				return downloader.bookNextChunkNumber(response.desconcatenaArrayBytesDatos());
			} catch (Exception e) {
				System.err.println(
						"EL socket " + downloadSocket + "es cerrado por mandar mensaje con formato incorrecto");
				close();
			}
		}
		return Downloader.NUM_CHUNK_NO_DISPONIBLE;
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
			// excepcion causada por cierre del seederthread (porque no tiene el
			// fichero compartido)
			System.err.println("El seeder" + downloadSocket + " ya no dispone del fichero");
			close();
		}
	}

	private void calculaEstadisticas() {
		String info;
		try {
			double byteDescargados = (numChunksDownloaded * downloader.getChunkSize());
			String MbDescargados = String.format("%,.4f", byteDescargados / BYTES_TO_MB);
			double miliseconds = (tiempoFin - tiempoInicio);
			double seconds = miliseconds / 1000;
			String sizeFicheroCompleto = String.format("%,.2f",
					(double) downloader.getTargetFile().fileSize / BYTES_TO_MB);
			double megabytesPorSeg = ((byteDescargados / seconds) / BYTES_TO_MB);
			String speedMb = String.format("%,.4f", megabytesPorSeg);

			info = String.format(
					"\nHilo: %s\tPeer: %s:%s\tChunks: %d/%d\tDescargado: %sMb/%sMb\tVelocidad: %sMb/s\tTiempo: %s",
					getName(), downloadSocket.getInetAddress(), downloadSocket.getPort(), numChunksDownloaded,
					downloader.getTotalChunks(), MbDescargados, sizeFicheroCompleto, speedMb, calculaTiempoDescarga());
		} catch (ArithmeticException ae) {
			info = "\nHilo " + getName() + ": no se puieron obtener estadisticas :(";
		}
		Downloader.addResumenThread(info);
	}

	private String calculaTiempoDescarga() {
		long millis = tiempoFin - tiempoInicio;
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		return (second != 0) ? String.format("%02d:%02d:%02d", hour, minute, second)
				: String.format("%02d:%02d:%02d:%02d", hour, minute, second, millis);
	}

	private void close() {
		try {
			downloadSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Main code to request chunk lists and chunks
	public void run() {
		long chunkActual = 0;
		do {
			// if (numChunksDownloaded % FRECUENCIA_UPDATE_SEEDLIST == 0)
			// FIXME preguntamos por nuevos seeders
			// downloader.addThreads();

			if (!downloadSocket.isClosed()) {
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
						// System.err.println("Fallo chunk " + chunkActual + "s
						// continuamos con otro");
						Thread.sleep(1000);
						// downloader.addThreads(); //FIXME
					} catch (InterruptedException e) {
					}
				}
			}
		} while (!downloader.isDownloadComplete());
		close();

		tiempoFin = System.currentTimeMillis();
		calculaEstadisticas();
		downloader.joinDownloaderThreads(seed);
	}

}
