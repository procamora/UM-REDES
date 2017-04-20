package es.um.redes.P2P.PeerPeer.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

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
	public int getNumChunksDownloaded() {
		return numChunksDownloaded;
	}

	public Message receiveMessageFromPeer() {
		System.out.println("recibe downloader");
		Message msg = null;
		// byte[] buffer = new byte[1024];
		try {
			InputStream is = downloadSocket.getInputStream();
			dis = new DataInputStream(is);
			/// dis.read(buffer);

			msg = Message.parseResponse(dis);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	public void sendMessageToPeer(Message msg) {

		System.out.println("send downloader");
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
		// pide lista trozos o un trozo
		// es un bucle, mientras que no te bajes el fichero
		// si es un fichero se comprueba si nos ehemos bajado los byes del
		// fichero
		// si hay varios thread hay que coordinar con variable compartida, que
		// sera la instancia this
		// MASTER, servir trozos que te estas bajando, opcional
		String hash = downloader.getTargetFile().fileHash;

		// pido la lista de trozos del fichero
		Message msg = Message.makeGetChunkRequest(hash, (short) 0);
		// pido los datos del fichero correspondientes al num chunk 40
		Message msg2 = Message.makeChunkRequest(hash, (short) 40);

		sendMessageToPeer(msg2);
		Message msgRecibido = receiveMessageFromPeer();
		System.out.println(msgRecibido);
		if (msgRecibido.getOpCode() != Message.OP_GET_CHUNK_ACK && msgRecibido.getOpCode() != Message.OP_CHUNK_ACK)
			return;

		MessageCQueryACK response = (MessageCQueryACK) msgRecibido;
		switch (msgRecibido.getOpCode()) {
			case Message.OP_GET_CHUNK_ACK:
				System.out.println("num chunk: " + response.getNumChunk());
				System.out.println("chunk datos: ");
				response.getDatosChunk();
				break;
				
			case Message.OP_CHUNK_ACK:
				System.out.println("num chunk: " + response.getNumChunk());
				System.out.println("datos size: " + response.getDatos().length);
				Ficheros.escritura("/tmp/" + downloader.getTargetFile().fileName, response.getDatos(), 0);
				break;
			default:
				break;
		}
		System.out.println("final correcto");
	}

}
