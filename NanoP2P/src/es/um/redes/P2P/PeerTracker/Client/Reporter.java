package es.um.redes.P2P.PeerTracker.Client;

import java.net.*;

import es.um.redes.P2P.PeerTracker.Message.Message;

public class Reporter implements ReporterIface {

	private final int PORT = 4450;
	/**
	 * Tracker hostname, used for establishing connection
	 */
	private String trackerHostname;

	/**
	 * UDP socket for communication with tracker
	 */
	private DatagramSocket peerTrackerSocket;
	private InetSocketAddress address;

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
			System.err
					.println("Reporter cannot create datagram socket for communication with tracker");
			System.exit(-1);
		}
	}

	public void end() {
		// Close datagram socket with tracker
		peerTrackerSocket.close();
	}

	@Override
	public boolean sendMessageToTracker(DatagramSocket socket, Message request,
			InetSocketAddress trackerAddress) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Message receiveMessageFromTracker(DatagramSocket socket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message conversationWithTracker(Message request) {
		//si hay fallos habra que retransmitir
		sendMessageToTracker(peerTrackerSocket, request, address);
		receiveMessageFromTracker(peerTrackerSocket);
		return null;
	}


}
