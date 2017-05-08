package es.um.redes.P2P.PeerPeer.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import es.um.redes.P2P.PeerPeer.Client.Downloader;
import es.um.redes.P2P.PeerPeer.Message.*;
import es.um.redes.P2P.util.PeerDatabase;
import es.um.redes.P2P.util.Ficheros;

public class SeederThread extends Thread {
	private Socket socket = null;
	private Downloader downloader;
	/* Global buffer for performance reasons */
	private byte[] chunkDataBuf;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private PeerDatabase database;

	public SeederThread(Socket socket, PeerDatabase database, Downloader downloader) {
		this.socket = socket;
		this.database = database;
		this.downloader = downloader;
	}

	// Devuelve la lista de trozos que tiene del fichero solicitado
	public void sendChunkList(String fileHashStr) {
		// FIXME obtener numero de chunks de forma dinamica
		byte[] listaTrozos = MessageChunkQueryResponse.concatenateByteArrays((short) 1, (short) 451, (short) 66,
				(short) 667);
		// el tamaño de listaTrozos.length es el doble de elementos que contiene
		MessageTCP respuesta = MessageTCP.makeGetChunkResponseRequest((short) (listaTrozos.length / 2), listaTrozos,
				downloader.getChunkSize());
		sendMessageToPeer(respuesta);
	}

	// Envía por el socket el chunk solicitado por el DownloaderThread
	protected void sendChunk(long chunkNumber, String fileHashStr) {
		String rutaFichero = database.lookupFilePath(fileHashStr);
		if (rutaFichero == null)
			throw new IllegalStateException("No se ha encontrado el fichero: " + fileHashStr);

		byte[] datosEnviar = Ficheros.lectura(rutaFichero, (int) downloader.getChunkSize(),
				(long) chunkNumber * downloader.getChunkSize());
		MessageTCP respuesta = MessageTCP.makeChunkResponseRequest(chunkNumber, datosEnviar,
				downloader.getChunkSize());
		sendMessageToPeer(respuesta);
	}

	private MessageTCP receiveMessageFromPeer() {

		MessageTCP msg = null;
		try {
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			msg = MessageTCP.parseRequest(dis);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	private void sendMessageToPeer(MessageTCP msg) {
		try {
			OutputStream os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			dos.write(msg.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processMessageFromPeer(MessageTCP response) {
		if (response.getOpCode() != MessageTCP.OP_GET_CHUNK && response.getOpCode() != MessageTCP.OP_CHUNK)
			return;

		// tenemos la seguridad de que no hay problemas con el casting
		MessageChunkQuery mensaje = (MessageChunkQuery) response;
		switch (mensaje.getOpCode()) {
			case MessageTCP.OP_GET_CHUNK:
				sendChunkList(mensaje.getFileHash());
				break;

			case MessageTCP.OP_CHUNK:
				sendChunk(mensaje.getNumChunk(), mensaje.getFileHash());
				break;

			default:
				break;
		}
	}

	// Método principal que coordina la recepción y envío de mensajes
	public void run() {
		// while true hasta que el cliente tenga todos los trozos del ficheros
		// sabemos que se ha acabado de enviar el fichero cuando el otro cierra
		// el socket y al hacer el read
		// nos da una excepcion correcta que tenemos que capturar
		while (true) {
			MessageTCP msgRecibido = receiveMessageFromPeer();

			if (msgRecibido != null)
				processMessageFromPeer(msgRecibido);
		}
		// System.out.println("final correcto");

	}
}
