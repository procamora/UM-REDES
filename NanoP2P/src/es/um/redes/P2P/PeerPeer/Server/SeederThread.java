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

public class SeederThread extends Thread {
	private Socket socket = null;
	private Downloader downloader;
	/* Global buffer for performance reasons */
	private byte[] chunkDataBuf;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	private PeerDatabase database;

	public SeederThread(Socket socket, PeerDatabase database, Downloader downloader) {
		// TODO
		this.socket = socket;
		this.database = database;
		this.downloader = downloader;
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
		// byte[] buffer = new byte[MAX_MSG_SIZE_BYTES];
		try {
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			// dis.read(buffer);

			msg = Message.parseResponse(dis);
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
		if (response.getOpCode() != Message.OP_GET_CHUNK && response.getOpCode() != Message.OP_CHUNK)
			return respuesta;
		
		// tenemos la seguridad de que no hay problemas con el casting
		MessageCQuery mensaje = (MessageCQuery) response;
		switch (mensaje.getOpCode()) {
			case Message.OP_GET_CHUNK:
				System.out.println("OP_GET_CHUNK");
				System.out.println(mensaje.getFileHash());
				System.out.println(mensaje.getNumChunk());
				byte[] listaTrozos = MessageCQueryACK.concatenateByteArrays((short) 1, (short) 451, (short) 66);
				respuesta = Message.makeGetChunkResponseRequest((short) 3, listaTrozos, downloader.getChunkSize());
				break;

			case Message.OP_CHUNK:
				System.out.println("OP_CHUNK");
				System.out.println(mensaje.getFileHash());
				System.out.println(mensaje.getNumChunk());
				String rutaFichero = database.lookupFilePath(mensaje.getFileHash());
				System.out.println(rutaFichero);
				if (rutaFichero == null)
					throw new IllegalStateException("No se ha encontrado el fichero: " + mensaje.getFileHash());
				
				byte[] datosEnviar = Ficheros.lectura(rutaFichero, (int) downloader.getChunkSize(), 0);
				System.out.println("tamaño datos enviados " + datosEnviar.length);
				respuesta = Message.makeChunkResponseRequest(mensaje.getNumChunk(), datosEnviar, downloader.getChunkSize());
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
