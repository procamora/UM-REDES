package es.um.redes.P2P.CodigoPruebas;

import java.nio.ByteBuffer;
import es.um.redes.P2P.PeerPeer.Message.*;

public class bina {
	public static void main(String[] args) {
		int chunSize = 4096;
		int tam = 4880;

		System.out.println(tam % chunSize);

		System.out.println(Long.MAX_VALUE);

		Message m = Message.makeChunkResponseRequest(Long.MAX_VALUE - 1, new byte[5], (short) 4096);
		Message m2 = Message.makeChunkRequest("87C43258EB02FD2D077B6AC9B4D520D28B49EB04", Long.MAX_VALUE - 1);
		System.out.println(m.toByteArray());
		System.out.println(m2.toByteArray());
		
	}

}
