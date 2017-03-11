package es.um.redes.P2P.PeerTracker.Client;

import java.io.IOException;
import java.net.*;

import es.um.redes.P2P.PeerTracker.Message.Message;
import es.um.redes.P2P.PeerTracker.Message.MessageConf;

public class Reporter implements ReporterIface {

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
			e.printStackTrace();
			System.err.println("Reporter cannot create datagram socket for communication with tracker");
			System.exit(-1);
		}
	}

	public void end() {
		// Close datagram socket with tracker
		peerTrackerSocket.close();
	}

	@Override
	public boolean sendMessageToTracker(DatagramSocket socket, Message request, InetSocketAddress trackerAddress) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		byte[] recibirDatos = new byte[MAX_MSG_SIZE_BYTES];
		// creamos un contenedor para el paquete recibirPaquete
		DatagramPacket recibirPaquete = new DatagramPacket(recibirDatos, recibirDatos.length);
		// cliente inactivo hasta recibir un paquete, guarda en recibirPaquete
		try {
			socket.receive(recibirPaquete);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// parseamos la respuesta al mensaje correspondiente
		Message m = Message.parseResponse(recibirPaquete.getData());

		return m;
	}

	@Override
	public Message conversationWithTracker(Message request) {
		// TODO Auto-generated method stub
		// si hay fallos habra que retransmitir
		sendMessageToTracker(peerTrackerSocket, request, address);
		Message m = receiveMessageFromTracker(peerTrackerSocket);
		return m;
	}

}