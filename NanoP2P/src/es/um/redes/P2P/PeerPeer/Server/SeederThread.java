package es.um.redes.P2P.PeerPeer.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;

import es.um.redes.P2P.PeerPeer.Client.Downloader;
import es.um.redes.P2P.PeerPeer.Message.*;
import es.um.redes.P2P.util.PeerDatabase;
import es.um.redes.P2P.util.Ficheros;
import es.um.redes.P2P.util.FileInfo;

public class SeederThread extends Thread {
	private Socket socket;
	private Downloader downloader;
	/* Global buffer for performance reasons */
	// private byte[] chunkDataBuf;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private PeerDatabase database;
	private Short chunkSize;
	private boolean bucle;

	public SeederThread(Socket socket, PeerDatabase database, Downloader downloader, Short chunkSize) {
		if (socket == null)
			throw new IllegalArgumentException("socket no puede ser null en SeederThread");
		if (database == null)
			throw new IllegalArgumentException("database no puede ser null en SeederThread");

		this.socket = socket;
		this.database = database;
		this.downloader = downloader;
		this.chunkSize = chunkSize;
		bucle = true;
	}

	// Devuelve la lista de trozos que tiene del fichero solicitado
	public void sendChunkList(String fileHashStr) {
		Message respuesta = null;

		if (downloader != null && downloader.getTargetFile().fileHash.equals(fileHashStr)) {
			HashSet<Long> chunkDisponibles = downloader.getChunksDownloadedFromSeeders();
			byte[] listaTrozos = MessageChunkQueryResponse.concatenateByteArrays(chunkDisponibles);
			// el tamaño de listaTrozos es el doble de elementos que contiene
			respuesta = Message.makeGetChunkResponseRequest((long) (listaTrozos.length / 8), listaTrozos);
			sendMessageToPeer(respuesta);
		} else {
			FileInfo[] ficherosLocales = database.getLocalSharedFiles();
			for (int i = 0; i < ficherosLocales.length; i++)
				if (ficherosLocales[i].fileHash.equals(fileHashStr)) {
					respuesta = Message.makeGetChunkResponseRequest(Long.MAX_VALUE, new byte[0]);
					sendMessageToPeer(respuesta);
					return; // terminamos de buscar
				}
		}
	}

	// Envía por el socket el chunk solicitado por el DownloaderThread
	protected void sendChunk(long chunkNumber, String fileHashStr) {
		if (downloader != null && downloader.getTargetFile().fileHash.equals(fileHashStr)) {
			String rutaFichero = database.getSharedFolderPath() + downloader.getTargetFile().fileName;

			byte[] datosEnviar;
			try {
				datosEnviar = Ficheros.lectura(rutaFichero, (int) chunkSize, (long) chunkNumber * chunkSize);
				Message respuesta = Message.makeChunkResponseRequest(chunkNumber, datosEnviar);
				sendMessageToPeer(respuesta);
			} catch (IOException e) {
				// Excepcion porque el fichero no existe (se ha borrado en
				// proceso de compartir)
				close();
			}
		} else {
			String rutaFichero = database.lookupFilePath(fileHashStr);
			if (rutaFichero == null)
				throw new IllegalStateException("No se ha encontrado el fichero: " + fileHashStr);
			byte[] datosEnviar;
			try {
				datosEnviar = Ficheros.lectura(rutaFichero, (int) chunkSize, (long) chunkNumber * chunkSize);
				Message respuesta = Message.makeChunkResponseRequest(chunkNumber, datosEnviar);
				sendMessageToPeer(respuesta);
			} catch (IOException e) {
				// Excepcion porque el fichero no existe (se ha borrado en
				// proceso de compartir)
				close();
			}
		}
	}

	private Message receiveMessageFromPeer() {

		Message msg = null;
		try {
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			msg = Message.parseRequest(dis, (short) 0);
		} catch (IOException e) {
			// capturamos la excepcion cuando cierran los downloaderthread y
			// cerramos tambien el socket
			close();
		}
		return msg;
	}

	private void close() {
		try {
			socket.close();
			bucle = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				sendChunkList(mensaje.getFileHash());
				break;

			case Message.OP_CHUNK:
				sendChunk(mensaje.getNumChunk(), mensaje.getFileHash());
				break;
		}
	}

	// Método principal que coordina la recepción y envío de mensajes
	public void run() {
		while (bucle) {
			Message msgRecibido = receiveMessageFromPeer();

			if (msgRecibido != null)
				processMessageFromPeer(msgRecibido);
		}
	}
}
