package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

import es.um.redes.P2P.App.Tracker;

public class MessageChunkQueryResponse {

	public static byte FILTER_TYPE_GET_CHUNK_RESPONSE = 2;
	public static byte FILTER_TYPE_CHUNK_RESPONSE = 4;
	

	public static int NUM_CHUNK_LENGTH = 5;
	public static byte TYPE_LENGHT = 1;
	public static byte CHUNK_LENGHT = 4;
	
	public static final int sizeOfMessage = (TYPE_LENGHT + NUM_CHUNK_LENGTH + CHUNK_LENGHT);
	
	private byte type;
	private int numChunk;
	private int chunk;
	
	
	public MessageChunkQueryResponse(byte type, int numChunk) {
		this.chunk = Tracker.DEFAULT_P2P_CHUNK_SIZE;
		this.numChunk = numChunk;
		this.type = type;
	}


	public byte getType() {
		return type;
	}


	public void setType(byte type) {
		this.type = type;
	}


	public int getNumChunk() {
		return numChunk;
	}


	public void setNumChunk(int numChunk) {
		this.numChunk = numChunk;
	}


	public int getChunk() {
		return chunk;
	}


	public void setChunk(int chunk) {
		this.chunk = chunk;
	}
	
	
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(sizeOfMessage);
		buf.put(type);
		buf.putInt(numChunk);
		buf.putInt(chunk);
		return buf.array();
	}
	
	public static MessageChunkQueryResponse fromDataInputStream(DataInputStream dis) {
		return null;
	}
}
