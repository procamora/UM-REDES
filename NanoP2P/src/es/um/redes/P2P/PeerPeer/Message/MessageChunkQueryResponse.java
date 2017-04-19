package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

import es.um.redes.P2P.App.Tracker;

public class MessageChunkQueryResponse extends Message{

	public static byte FILTER_TYPE_GET_CHUNK_RESPONSE = 2;
	public static byte FILTER_TYPE_CHUNK_RESPONSE = 4;
	

	public static int NUM_CHUNK_LENGTH = 5;
	public static byte TYPE_LENGHT = 1;
	public static byte CHUNK_LENGHT = 4;
	
	public static final int sizeOfMessage = (TYPE_LENGHT + NUM_CHUNK_LENGTH + CHUNK_LENGHT);
	
	private byte type;			// tipo
	private short numChunk;		// numero chunk
	private int chunk;			// tamaño chunk
	
	
	public MessageChunkQueryResponse(byte type, short numChunk) {
		this.chunk = Tracker.DEFAULT_P2P_CHUNK_SIZE;
		this.numChunk = numChunk;
		this.type = type;
	}
	
	public MessageChunkQueryResponse(byte[] buf) {
		if (fromByteArray(buf) == false) {
			throw new RuntimeException("Failed to parse message: format is not Query.");
		}
		else {
			assert(true);
		}
	}


	public byte getType() {
		return type;
	}


	public void setType(byte type) {
		this.type = type;
	}


	public short getNumChunk() {
		return numChunk;
	}


	public void setNumChunk(short numChunk) {
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


	@Override
	protected boolean fromByteArray(byte[] buf) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getTotalFragments() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void reassemble(Vector<Message> fragments) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean fragmented() {
		// TODO Auto-generated method stub
		return false;
	}
}
