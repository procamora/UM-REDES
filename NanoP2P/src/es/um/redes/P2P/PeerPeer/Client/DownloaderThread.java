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
	private Downloader downloader;
	private Socket downloadSocket;
	protected DataOutputStream dos; // FIXME USAR, ES UNA MEJORA DE STREAM
	protected DataInputStream dis;
	private int numChunksDownloaded;

	public DownloaderThread(Downloader dl, InetSocketAddress seed) {
		downloader = dl;
		try {
			downloadSocket = new Socket(seed.getAddress(), seed.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// It receives a message containing a chunk and it is stored in the file
	private void receiveAndWriteChunk() {
	}

	// It receives a message containing a chunk and it is stored in the file
	private void receiveAndProcessChunkList() {
	}

	// Number of chunks already downloaded by this thread
	private int getNumChunksDownloaded() {
		return numChunksDownloaded;
	}

	private MessageTCP receiveMessageFromPeer() {

		MessageTCP msg = null;
		// byte[] buffer = new byte[1024];
		try {
			InputStream is = downloadSocket.getInputStream();
			dis = new DataInputStream(is);
			/// dis.read(buffer);

			msg = MessageTCP.parseResponse(dis);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	private void sendMessageToPeer(MessageTCP msg) {

		try {
			OutputStream os = downloadSocket.getOutputStream();
			dos = new DataOutputStream(os);
			dos.write(msg.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	//FIXME cuando tenemos un trozo ya tenemos que hacer el addseed del fichero para compartirlo

	// Main code to request chunk lists and chunks
	public void run() {
		// pide lista trozos o un trozoes un bucle, mientras que no te bajes el
		// fichero si es un fichero se comprueba si nos ehemos bajado los byes
		// del fichero si hay varios thread hay que coordinar con variable
		// compartida, que sera la instancia this MASTER, servir trozos que te
		// estas bajando, opcional
		String hash = downloader.getTargetFile().fileHash;
		long totalChunks = downloader.getTotalChunks();
		long totalChunksDescargados = 0;
		long chunkActual = 0;

		// pido la lista de trozos del fichero
		sendMessageToPeer(MessageTCP.makeGetChunkRequest(hash, (short) 0));
		MessageTCP msgRecibido = receiveMessageFromPeer();

		if (msgRecibido.getOpCode() == MessageTCP.OP_GET_CHUNK_ACK) {
			MessageChunkQueryResponse response = (MessageChunkQueryResponse) msgRecibido;
			response.getDatosChunk();
		}

		do {
			// pido los datos del fichero correspondientes al num chunk 40
			sendMessageToPeer(MessageTCP.makeChunkRequest(hash, chunkActual));
			MessageTCP msgRecibido1 = receiveMessageFromPeer();

			if (msgRecibido1.getOpCode() == MessageTCP.OP_CHUNK_ACK) {
				MessageChunkQueryResponse response = (MessageChunkQueryResponse) msgRecibido1;

				Ficheros.escritura(Peer.db.getSharedFolderPath() + downloader.getTargetFile().fileName,
						response.getDatos(), (long) (chunkActual * downloader.getChunkSize()));

				chunkActual++;
				totalChunksDescargados++;
			}
		} while (totalChunks != totalChunksDescargados);
		System.out.println("final correcto");
	}

}
