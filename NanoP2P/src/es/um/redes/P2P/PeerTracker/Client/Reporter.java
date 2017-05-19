package es.um.redes.P2P.PeerTracker.Client;

import java.io.IOException;
import java.net.*;

import es.um.redes.P2P.PeerTracker.Message.Message;

public class Reporter implements ReporterIface {
	private static final int TIMEOUT = 1000;

	private final int PORT = 4450;
	private final int MAX_MSG_SIZE_BYTES = 1024;

	private InetSocketAddress address;

	/**
	 * Tracker hostname, used for establishing connection
	 */
	private String trackerHostname;

	/**
	 * UDP socket for communication with tracker
	 */
	private DatagramSocket peerTrackerSocket;

	/***
	 * 
	 * @param tracker
	 *            Tracker hostname or IP
	 */
	public Reporter(String tracker) {
		trackerHostname = tracker;
		address = new InetSocketAddress(trackerHostname, PORT);

		try {
			peerTrackerSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Reporter cannot create datagram socket for communication with tracker");
			// e.printStackTrace();
		}
	}

	public void end() {
		// Close datagram socket with tracker
		peerTrackerSocket.close();
	}

	@Override
	public boolean sendMessageToTracker(DatagramSocket socket, Message request, InetSocketAddress trackerAddress) {
		// almacena los datos que el cliente envia
		byte[] enviarDatos = new byte[MAX_MSG_SIZE_BYTES];
		// conversion de string a array de bytes
		enviarDatos = request.toByteArray();
		// crea el paquete que enviamos
		DatagramPacket enviarPaquete = new DatagramPacket(enviarDatos, enviarDatos.length, trackerAddress);
		// enviamos el paquete
		try {
			socket.send(enviarPaquete);
			return true;
		} catch (IOException e) {
			return false;
			// e.printStackTrace();
		}
	}

	@Override
	public Message receiveMessageFromTracker(DatagramSocket socket) {
		byte[] recibirDatos = new byte[MAX_MSG_SIZE_BYTES];
		// creamos un contenedor para el paquete recibirPaquete
		DatagramPacket recibirPaquete = new DatagramPacket(recibirDatos, recibirDatos.length);
		// cliente inactivo hasta recibir un paquete, guarda en recibirPaquete
		Message m = null;

		try {
			socket.setSoTimeout(TIMEOUT);
			socket.receive(recibirPaquete);
			// parseamos la respuesta al mensaje correspondiente
			m = Message.parseResponse(recibirPaquete.getData());
		} catch (SocketTimeoutException e) {
			System.err.println("Timeout con el tracker, intentando otra vez...");
			// excepcion correcta de timeout
		} catch (IOException e) {
			e.printStackTrace(); // excepcion recive
		}

		return m;
	}

	@Override
	public Message conversationWithTracker(Message request) {
		// si hay fallos habra que retransmitir if null iteramos por timeout
		Message m = null;
		do {
			sendMessageToTracker(peerTrackerSocket, request, address);
			m = receiveMessageFromTracker(peerTrackerSocket);
		} while (m == null);
		return m;
	}

}
