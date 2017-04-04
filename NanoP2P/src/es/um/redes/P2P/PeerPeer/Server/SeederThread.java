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
//import es.um.redes.P2P.PeerPeer.Message.PeerMessage;
import es.um.redes.P2P.util.PeerDatabase;

public class SeederThread extends Thread {
	private Socket socket = null;
	private Downloader downloader;
	/* Global buffer for performance reasons */
	private byte[] chunkDataBuf;

	public SeederThread(Socket socket, PeerDatabase database, Downloader downloader, short chunkSize) {
		// TODO
	}

	// Devuelve la lista de trozos que tiene del fichero solicitado
	public void sendChunkList(String fileHashStr) {

	}

	// Envía por el socket el chunk solicitado por el DownloaderThread
	protected void sendChunk(int chunkNumber, String fileHashStr) {
	}

	// Método principal que coordina la recepción y envío de mensajes
	public void run() {
		DataOutputStream dos;
		DataInputStream dis;
		String s = null;
		System.out.println("recibe seeder");
		byte[] buffer = new byte[40];
		try {
			InputStream is = socket.getInputStream();
			dis = new DataInputStream(is);
			dis.read(buffer);
			s = new String(buffer, 0, 40);
			System.out.println(s);
			System.out.println(buffer.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("send seeder");
		String str = s.toUpperCase();
		try {
			socket.getOutputStream().write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
