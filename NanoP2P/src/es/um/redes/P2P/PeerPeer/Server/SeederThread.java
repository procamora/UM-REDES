package es.um.redes.P2P.PeerPeer.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import es.um.redes.P2P.PeerPeer.Client.Downloader;
import es.um.redes.P2P.PeerPeer.Message.*;
import es.um.redes.P2P.util.PeerDatabase;

public class SeederThread extends Thread {
	private final int MAX_MSG_SIZE_BYTES = 1024;

	private Socket socket = null;
	private Downloader downloader;
	/* Global buffer for performance reasons */
	private byte[] chunkDataBuf;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private short chunkSize;

	public SeederThread(Socket socket, PeerDatabase database, Downloader downloader, short chunkSize) {
		// TODO
		this.socket = socket;
		this.downloader = downloader;
		this.chunkSize = chunkSize;
	}

	// Devuelve la lista de trozos que tiene del fichero solicitado
	public void sendChunkList(String fileHashStr) {

	}

	// Envía por el socket el chunk solicitado por el DownloaderThread
	protected void sendChunk(int chunkNumber, String fileHashStr) {
	}

	public Message receiveMessageFromPeer() {
		System.out.println("recibe seeder");
		Message msg = null;
		byte[] buffer = new byte[MAX_MSG_SIZE_BYTES];
		try {
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			dis.read(buffer);

			msg = Message.parseResponse(buffer);
			// System.out.println(msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	public void sendMessageToPeer(Message msg) {

		System.out.println("send seeder");
		// Message msg = (MessageCQueryACK)
		// Message.makeGetChunkResponseRequest((short)150, new byte[0]);
		try {
			OutputStream os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			dos.write(msg.toByteArray());
			// System.out.println("enviado: " + msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Message processMessageFromPeer(Message response) {
		Message respuesta = null;
		switch (response.getOpCode()) {
			case Message.OP_GET_CHUNK:
				System.out.println("OP_GET_CHUNK");
				String hash = ((MessageCQuery) response).getFileHash();
				System.out.println(hash);
				short numChunk = ((MessageCQuery) response).getNumChunk();
				System.out.println(numChunk);
				respuesta = Message.makeGetChunkResponseRequest(numChunk, new byte[0], chunkSize);
				break;

			case Message.OP_CHUNK:
				System.out.println("OP_CHUNK");
				String hash2 = ((MessageCQuery) response).getFileHash();
				System.out.println(hash2);
				short numChunk2 = ((MessageCQuery) response).getNumChunk();
				System.out.println(numChunk2);
				respuesta = Message.makeChunkResponseRequest(numChunk2, "DATOS++".getBytes(), chunkSize);
				break;
			default:
				break;
		}
		System.out.println(response);
		return respuesta;

	}

	// Método principal que coordina la recepción y envío de mensajes
	public void run() {
		// while true hasta que el cliente tenga todos los trozos del ficheros
		// sabemos que se ha acabado de enviar el fichero cuando el otro cierra
		// el socket y al hacer el read
		// nos da una excepcion correcta que tenemos que capturar

		Message msgRecibido = receiveMessageFromPeer();
		Message msgEnviado = processMessageFromPeer(msgRecibido);
		if (msgEnviado != null)
			sendMessageToPeer(msgEnviado);
		System.out.println("final correcto");

	}
}
