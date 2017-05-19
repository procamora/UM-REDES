package es.um.redes.P2P.PeerPeer.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import es.um.redes.P2P.PeerPeer.Client.Downloader;
import es.um.redes.P2P.util.PeerDatabase;

/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link SeederThread} cada vez que se conecte un cliente.
 */
public class Seeder implements Runnable {
	public static final int SEEDER_FIRST_PORT = 10000;
	public static final int SEEDER_LAST_PORT = 10100;
	// son ficheros que nos estamos descargando, no estan en local aun
	private Downloader currentDownloader;
	private ServerSocket serverSocket;
	private short chunkSize;
	private boolean estado = true;

	/**
	 * Base de datos de ficheros locales compartidos por este peer.
	 */
	protected PeerDatabase database;

	public Seeder(short chunkSize, PeerDatabase database) {
		this.chunkSize = chunkSize;
		this.database = database;
		try {
			serverSocket = new ServerSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Pone al servidor a escuchar en un puerto libre del rango y devuelve cuál
	// es dicho puerto
	public int getAvailablePort() {
		int n = SEEDER_FIRST_PORT;
		boolean estado = true;
		while (estado) {
			try {
				serverSocket.bind(new InetSocketAddress(n));
				estado = false;
			} catch (IOException e) {
				if (n == SEEDER_LAST_PORT) {
					System.err.println("Estan todos los puertos ocupados");
					// si no conseguimos un puerto paramos el programa
					System.exit(-1);
				}
				n++;
			}
		}
		return n;
	}

	/**
	 * Función del hilo principal del servidor.
	 */
	public void run() {
		while (estado) {// hasta que pulsemos quit
			// estamos todo el rato aceptando conexiones
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				// En algún momento se llamará a
				new SeederThread(clientSocket, database, currentDownloader, chunkSize).start();
			} catch (SocketException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void quit() {
		estado = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inicio del hilo del servidor.
	 */
	public void start() {
		// Inicia esta clase como un hilo
		new Thread(this).start();
	}

	// es el fichero que nos estamos decargando
	public void setCurrentDownloader(Downloader downloader) {
		currentDownloader = downloader;
	}

	public int getSeederPort() {
		return serverSocket.getLocalPort();
	}

}
