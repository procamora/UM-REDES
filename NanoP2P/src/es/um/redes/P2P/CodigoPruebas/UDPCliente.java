package es.um.redes.P2P.CodigoPruebas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UDPCliente {

	private final int PORT = 4450;
	public final int MAX_MSG_SIZE_BYTES = 1024;

	public boolean send(DatagramSocket socketCliente, String frase) throws IOException {

		// almacena los datos que el cliente envia
		byte[] enviarDatos = new byte[MAX_MSG_SIZE_BYTES];
		// conversion de string a array de bytes
		enviarDatos = frase.getBytes();
		//
		InetSocketAddress addr = new InetSocketAddress("127.0.0.1", PORT);
		// crea el paquete que enviamos
		DatagramPacket enviarPaquete = new DatagramPacket(enviarDatos, enviarDatos.length, addr);
		// enviamos el paquete
		socketCliente.send(enviarPaquete);

		return true;
	}

	public void response(DatagramSocket socketCliente) throws IOException {
		byte[] recibirDatos = new byte[MAX_MSG_SIZE_BYTES];
		// creamos un contenedor para el paquete recibirPaquete
		DatagramPacket recibirPaquete = new DatagramPacket(recibirDatos, recibirDatos.length);
		// cliente inactivo hasta recibir un paquete, guarda en recibirPaquete
		socketCliente.receive(recibirPaquete);

		String fraseModificada = new String(recibirPaquete.getData());

		System.out.println("DEL SERVIDOR: " + fraseModificada);

		// cerramos el socket
		socketCliente.close();
	}

	public static void main(String[] args) {

		UDPCliente c = new UDPCliente();

		// get a datagram socket
		try {
			DatagramSocket socketCliente = new DatagramSocket();
			c.send(socketCliente, "hola mundo");
			c.response(socketCliente);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
