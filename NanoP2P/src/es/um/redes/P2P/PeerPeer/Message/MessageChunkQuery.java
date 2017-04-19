package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import es.um.redes.P2P.util.FileDigest;

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
	

	
	private static final Byte[] _confOpCodes = {OP_GET_CHUNK, OP_CHUNK};
	private String hash;					// el Hash del mensaje
	private short numChunks;				// Numero de Chunks 
	
	public MessageChunkQuery(byte type, byte transId,  String hash, short num_Chunk) {
		setTransId(transId);
		setOpCode(type);
		this.hash = hash;
		this.numChunks = num_Chunk;
	}

	public MessageChunkQuery(DataInputStream dis, byte respOpcode) {
		if (fromInputStream(dis, respOpcode) == false) {
			throw new RuntimeException("Failed to parse message: format is not Query.");
		}
		else {
			assert(true);
		}
	}

	public String getHash() {
		return hash;
	}
	
	public short getNumChunks() {
		return numChunks;
	}
	



	/**
	 * Creates a byte array ready for sending a datagram packet, from a valid message of Query format 
	 */
	public byte[] toByteArray()
	{
		int byteBufferLength = FIELD_OPCODE_BYTES + FIELD_TRANSID_BYTES +  FIELD_FILEHASH_BYTES 
				 + FIELD_CHUNKSIZE_BYTES ; // The actual filter of this message
		
		ByteBuffer buf = ByteBuffer.allocate(byteBufferLength);
		buf.put(getOpCode());
		buf.put(getTransId());
		buf.put(hash.getBytes());
		buf.putInt(numChunks);
		return buf.array();
	}

	
	
	public boolean fromInputStream(DataInputStream dis, byte respOpcode) {
		
		try {
		setOpCode(respOpcode);
		setTransId(dis.readByte());
		byte[] b = new byte[FIELD_FILEHASH_BYTES];
		dis.read(b, 0, b.length);
		this.hash = new String(FileDigest.getChecksumHexString(b));
		this.numChunks = dis.readShort();
		valid = true;
		} catch(IOException e){
			e.printStackTrace();
			assert (valid == false);
		}
		return valid;
	}

	
	@Override
	public String toString() {
		assert(true);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(" Type: "+this.getOpCode());
		strBuf.append(" TransId :" + this.getTransId());
		strBuf.append(" Hash:  "+this.hash );
		strBuf.append(" NumChunks:  "+this.numChunks);
		return strBuf.toString();
	}

	private static final Set<Byte> conf_opCodes = Collections.unmodifiableSet(new HashSet<Byte>(Arrays.asList(_confOpCodes)));

		  // Protected to allow overriding in subclasses
	protected void _check_opcode(byte opcode){
		if (!conf_opCodes.contains(opcode))
		throw new RuntimeException("Opcode " + opcode + " no es del tipo Chunk.");
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
