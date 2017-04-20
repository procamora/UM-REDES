package es.um.redes.P2P.CodigoPruebas;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import es.um.redes.P2P.PeerPeer.Server.SeederThread;
import es.um.redes.P2P.util.PeerDatabase;

public class TCPServidor {
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(20000));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			Socket clientSocket = null;

			try {
				clientSocket = serverSocket.accept();
				// En algún momento se llamará a
				System.out.println(clientSocket.getPort());
				new SeederThread(clientSocket,
						new PeerDatabase(
								"/mnt/WD_BLACK/pablojoserocamora@gmail.com/Universidad/REDES/UM-REDES/comp/peer2/"),
						null, (short) 4096).start();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
