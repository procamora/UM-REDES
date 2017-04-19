package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

import es.um.redes.P2P.PeerTracker.Message.Message;

/**
 * @author rtitos
 * 
 * Peer-tracker protocol message, format "QueryChunk" 
 * 
    1b	  20 B		5B
 +--------------+------------+
 |Type  | Hash  |  Num_Chunk |
 +------+-------+------------+

 */
public class MessageChunkQuery extends es.um.redes.P2P.PeerPeer.Message.Message {
	
	public static final byte FILTERTYPE_INVALID = 0;	
	public static final int FILTERTYPE_LENGHT = 1;
	public static final byte OP_GET_CHUNK = 1;	  //  Tipo 1: Indica que es un GET_CHUNK
	public static final byte FILTERTYPE_CHUNK = 3;		  // Tipo 3: indica que que el formato de mensaje es un CHUNK
	private static final int FIELD_HASH_LENGHT = 20;  			  // Tamano de hash de queryChunk
	private static final int FIELD_NUM_CHUNK_BYTES_LENGHT = 5;   // Numero de bytes del numero de chunks
	private static final int FIELD_NUMERICAL_FILTER_BYTES = Long.SIZE / 8;

	

	private byte queryChunkType;			// el tipo de mensaje 
	private String hash;					// el Hash del mensaje
	private short numChunks;					// Numero de Chunks 

	public MessageChunkQuery(byte type,  String hash, short num_Chunk) {
		this.queryChunkType = type;
		this.hash = hash;
		this.numChunks = num_Chunk;
	}

	public MessageChunkQuery(byte[] buf) {
		if (fromByteArray(buf) == false) {
			throw new RuntimeException("Failed to parse message: format is not Query.");
		}
		else {
			assert(true);
		}
	}

	public byte getType(){
		return queryChunkType;
	}
	
	public String getHash() {
		return hash;
	}
	
	public short getNumChunks() {
		return numChunks;
	}
	
	
	private static boolean isNumericalChunkQueryType(byte type) {
		switch (type) {
			case FILTERTYPE_CHUNK:
			case OP_GET_CHUNK:
			case FILTERTYPE_INVALID:
				return true;
			default:
				return false;
		}		
	}

	/**
	 * Creates a byte array ready for sending a datagram packet, from a valid message of Query format 
	 */
	public byte[] toByteArray()
	{
		int byteBufferLength = FIELD_NUMERICAL_FILTER_BYTES + FIELD_HASH_LENGHT 
				 + FIELD_NUM_CHUNK_BYTES_LENGHT; // The actual filter of this message
		
		ByteBuffer buf = ByteBuffer.allocate(byteBufferLength);
		buf.put(queryChunkType);
		buf.put(hash.getBytes());
		buf.putInt(numChunks);
		return buf.array();
	}

	
	
/*	public static MessageChunkQuery fromInputStream(DataInputStream dis) throws IOException{
			
		byte auxType = dis.readByte();
		int auxNumChunks = dis.readInt();
		String auxHash = dis.readLine();
		return new MessageChunkQuery(auxType, auxHash, auxNumChunks);
	}
*/
	
	@Override
	public String toString() {
		assert(true);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: "+this.queryChunkType + "\n");
		strBuf.append(" Hash:  "+this.hash + "\n");
		strBuf.append(" NumChunks:  "+this.numChunks + "\n");
		return strBuf.toString();
	}

@Override
protected boolean fromByteArray(byte[] buf) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public int getTotalFragments() {
	// TODO Auto-generated method stub
	return 0;
}


@Override
public boolean fragmented() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void reassemble(Vector<es.um.redes.P2P.PeerPeer.Message.Message> fragments) {
	// TODO Auto-generated method stub
	
}

	

}
