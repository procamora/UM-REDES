package es.um.redes.P2P.CodigoPruebas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServidor {
	public final int PORT = 4450;
	public final int MAX_MSG_SIZE_BYTES = 1024;

	public void response() throws IOException {

		// get a datagram socket
		DatagramSocket socketServidor = new DatagramSocket(PORT);

		// allocate buffer and prepare message to be sent
		byte[] recibirDatos = new byte[MAX_MSG_SIZE_BYTES];
		byte[] enviarDatos = new byte[MAX_MSG_SIZE_BYTES];

		while (true) {
			// receive response
			DatagramPacket recibirPaquete = new DatagramPacket(recibirDatos, recibirDatos.length);
			socketServidor.receive(recibirPaquete);

			String frase = new String(recibirPaquete.getData());

			InetAddress DireccionIP = recibirPaquete.getAddress();
			int puerto = recibirPaquete.getPort();

			String fraseMayuscula = frase.toUpperCase();

			enviarDatos = fraseMayuscula.getBytes();

			// send request
			DatagramPacket enviarPaquete = new DatagramPacket(enviarDatos, enviarDatos.length, DireccionIP, puerto);
			socketServidor.send(enviarPaquete);
		}
	}

	public static void main(String[] args) {

		UDPServidor s = new UDPServidor();
		try {
			s.response();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
