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
import es.um.redes.P2P.util.Ficheros;
import es.um.redes.P2P.util.FileInfo;

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
		// FIXME Si tiene todos los ficheros poner todo a 1
		byte[] listaTrozos = MessageChunkQueryResponse.concatenateByteArrays((long) 1, (long) 451, (long) 66,
				(long) 667);

		FileInfo[] ficherosLocales = database.getLocalSharedFiles();
		boolean ficheroLocal = false;
		for (int i = 0; i < ficherosLocales.length; i++)
			if (ficherosLocales[i].fileHash.equals(fileHashStr))
				ficheroLocal = true;

		// el tamaño de listaTrozos.length es el doble de elementos que contiene
		Message respuesta = null;
		// respuesta = Message.makeGetChunkResponseRequest(Long.MAX_VALUE, new
		// byte[0], downloader.getChunkSize());
		System.out.println("IMP " + listaTrozos.length/8);
		if (ficheroLocal)
			respuesta = Message.makeGetChunkResponseRequest(Long.MAX_VALUE, new byte[0], downloader.getChunkSize());
		else
			respuesta = Message.makeGetChunkResponseRequest((long) (listaTrozos.length / 8), listaTrozos,
					downloader.getChunkSize());
		sendMessageToPeer(respuesta);
		System.out.println("enviado");
	}

	// Envía por el socket el chunk solicitado por el DownloaderThread
	protected void sendChunk(long chunkNumber, String fileHashStr) {
		String rutaFichero = database.lookupFilePath(fileHashStr);
		if (rutaFichero == null)
			throw new IllegalStateException("No se ha encontrado el fichero: " + fileHashStr);

		byte[] datosEnviar = Ficheros.lectura(rutaFichero, (int) downloader.getChunkSize(),
				(long) chunkNumber * downloader.getChunkSize());
		Message respuesta = Message.makeChunkResponseRequest(chunkNumber, datosEnviar, downloader.getChunkSize());
		sendMessageToPeer(respuesta);
	}

	private Message receiveMessageFromPeer() {

		Message msg = null;
		try {
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			msg = Message.parseRequest(dis);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	private void sendMessageToPeer(Message msg) {
		try {
			OutputStream os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			dos.write(msg.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processMessageFromPeer(Message response) {
		if (response.getOpCode() != Message.OP_GET_CHUNK && response.getOpCode() != Message.OP_CHUNK)
			throw new IllegalStateException("Fallo en cod Mensaje SeederThread.processMessageFromPeer()");

		// tenemos la seguridad de que no hay problemas con el casting
		MessageChunkQuery mensaje = (MessageChunkQuery) response;
		switch (mensaje.getOpCode()) {
			case Message.OP_GET_CHUNK:
				System.out.println("proceso OP_GET_CHUNK");
				sendChunkList(mensaje.getFileHash());
				break;

			case Message.OP_CHUNK:
				System.out.println("proceso OP_CHUNK");
				sendChunk(mensaje.getNumChunk(), mensaje.getFileHash());
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
			Message msgRecibido = receiveMessageFromPeer();

			if (msgRecibido != null)
				processMessageFromPeer(msgRecibido);
		}
		// System.out.println("final correcto");

	}
}
