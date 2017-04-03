package es.um.redes.P2P;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class TCPServidor {

	public static final int SEEDER_FIRST_PORT = 10000;
	public static final int SEEDER_LAST_PORT = 10100;

	private ServerSocket serverSocket;

	public TCPServidor() {
		try {
			serverSocket = new ServerSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getAvailablePort(int n) {
		try {
			serverSocket.bind(new InetSocketAddress(n));
			return n;
		} catch (IOException e) {
			return getAvailablePort(n + 1);
		}
	}

	public int getAvailablePort2(int n) {
		boolean estado = true;
		while (estado) {
			try {
				serverSocket.bind(new InetSocketAddress(n));
				estado = false;
			} catch (IOException e) {
				n++;
			}
		}
		return n;
	}

	public static void main(String[] args) {

		Integer[] a = new Integer[];

		a[0] = 1;
		a[1] = 3;
		a[2] = 4;
		a[3] = 5;

		for (int i = 0; i < a.length; i++)
			System.out.println(a[i]);

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * TCPServidor a = new TCPServidor(); a.getAvailablePort(SEEDER_FIRST_PORT);
	 * System.out.println(a.serverSocket); while (true) { try {
	 * Thread.sleep(100000); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } // 1 second }
	 */

}
