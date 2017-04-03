package es.um.redes.P2P.PeerPeer.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import es.um.redes.P2P.PeerPeer.Client.Downloader;
import es.um.redes.P2P.util.PeerDatabase;

/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link SeederThread} cada vez que se conecte un cliente.
 */
public class Seeder implements Runnable {
	public static final int SEEDER_FIRST_PORT = 10000;
	public static final int SEEDER_LAST_PORT = 10100;
	public Downloader currentDownloader;
	private ServerSocket serverSocket;

	/**
	 * Base de datos de ficheros locales compartidos por este peer.
	 */
	protected PeerDatabase database;

	public Seeder(short chunkSize) {
		// TODO
		try {
			serverSocket = new ServerSocket();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1); // si falla paramos el programa
		}
	}

	// Pone al servidor a escuchar en un puerto libre del rango y devuelve cuál
	// es dicho puerto
	public int getAvailablePort() {
		// TODO
		int n = SEEDER_FIRST_PORT;
		boolean estado = true;
		while (estado) {
			try {
				serverSocket.bind(new InetSocketAddress(n));
				estado = false;
			} catch (IOException e) {
				if (n == SEEDER_LAST_PORT) {
					System.err.println("Estan todos los puertos ocupados");
					System.exit(-1); // si no conseguimos un puerto paramos el programa
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
		// TODO
		// while(true) hasta que pulsemos quit
		// estamos todo el rato aceptando conexiones

		// En algún momento se llamará a
		//new SeederThread(socket, database, currentDownloader, chunkSize).start();

	}

	/**
	 * Inicio del hilo del servidor.
	 */
	public void start() {
		// Inicia esta clase como un hilo
		new Thread(this).start();
	}

	public void setCurrentDownloader(Downloader downloader) {
		// TODO
	}

	public int getSeederPort() {
		// TODO
		return 0;
	}
}
