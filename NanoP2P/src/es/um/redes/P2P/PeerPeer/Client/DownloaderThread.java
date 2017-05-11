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
	private final static int FRECUENCIA_UPDATE_SEEDLIST = 150;
	private Downloader downloader;
	private Socket downloadSocket;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private long numChunksDownloaded;

	public DownloaderThread(Downloader dl, InetSocketAddress seed) {
		if (dl == null)
			throw new IllegalArgumentException("dl no puede ser null en DownloaderThread");
		if (seed == null)
			throw new IllegalArgumentException("seed no puede ser null en DownloaderThread");

		downloader = dl;
		numChunksDownloaded = 0;
		try {
			downloadSocket = new Socket(seed.getAddress(), seed.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Recibe un mensaje que contiene un fragmento y se almacena en el archivo
	private boolean receiveAndWriteChunk(long chunkActual) {
		// pido los datos del fichero correspondientes al num chunk 40
		sendMessageToPeer(Message.makeChunkRequest(downloader.getTargetFile().fileHash, chunkActual));
		Message msgRecibido = receiveMessageFromPeer();

		if (msgRecibido != null) {
			// if (msgRecibido1.getOpCode() == Message.OP_CHUNK_ACK)
			MessageChunkQueryResponse response = (MessageChunkQueryResponse) msgRecibido;

			Ficheros.escritura(Peer.db.getSharedFolderPath() + downloader.getTargetFile().fileName, response.getDatos(),
					(chunkActual * downloader.getChunkSize()));
			return true;
		}
		return false;
	}

	// Recibe un mensaje que contiene un fragmento y se almacena en el archivo
	private long receiveAndProcessChunkList() {
		// pido la lista de trozos del fichero
		sendMessageToPeer(Message.makeGetChunkRequest(downloader.getTargetFile().fileHash, (long) 0));
		Message msgRecibido = receiveMessageFromPeer();

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

	private Message receiveMessageFromPeer() {
		Message msg = null;
		try {
			InputStream is = downloadSocket.getInputStream();
			dis = new DataInputStream(is);
			msg = Message.parseResponse(dis);
		} catch (IOException e) {
			// si hay un problema no retorno null, y se informara que el chunk
			// no se ha podido sdescargar
			// e.printStackTrace();
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

	// Main code to request chunk lists and chunks
	public void run() {
		long chunkActual = 0;

		do {
			if (numChunksDownloaded % FRECUENCIA_UPDATE_SEEDLIST == 0)
				// preguntamos por nuevos seeders
				downloader.joinDownloaderThreads();

			if (!downloadSocket.isClosed()) {
				chunkActual = receiveAndProcessChunkList();
				// System.out.println(getName() + " chunk " + chunkActual);
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
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else { // sino hay chunk valido espero 1s y volvere a probar
				try {
					Thread.sleep(1000);
					// preguntamos por nuevos seeders
					downloader.joinDownloaderThreads();
					// indicamos que no hemos podido descargarnos el fichero
					downloader.setChunkDownloaded(chunkActual, false);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} while (!downloader.isDownloadComplete());

		try {
			downloadSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("final correcto");
	}

}
