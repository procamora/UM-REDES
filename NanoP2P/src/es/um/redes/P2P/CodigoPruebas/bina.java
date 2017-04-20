package es.um.redes.P2P.CodigoPruebas;

import java.nio.ByteBuffer;

public class bina {
	public static void main(String[] args) {

		byte[] bytes = new byte[10];
		bytes[0] = (byte) 154;
		bytes[1] = (byte) 14;
		
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		bytes = new byte[buf.remaining()];
		buf.get(bytes, 0, bytes.length);
		buf.clear();

		bytes = new byte[buf.capacity()];

		buf.get(bytes, 0, bytes.length);
		
		
		System.out.println(bytes.length);
		System.out.println(buf);
		

	}

}
