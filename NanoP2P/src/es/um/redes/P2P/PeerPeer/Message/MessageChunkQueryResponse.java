package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;



public class MessageChunkQueryResponse extends Message{


	private static final Byte[] _conf_opCodes = {OP_GET_CHUNK_ACK, OP_CHUNK_ACK};
		
	private short numChunk;		// numero chunk
	private byte[] datos;
	private short chunkSize;			// tamaño chunk
	
	
	public MessageChunkQueryResponse(byte type, byte tid, short numChunk, byte[] datos,  short chunkSize) {
		this.chunkSize = chunkSize;
		this.numChunk = numChunk;
		setOpCode(type);
		setTransId(tid);
		this.datos = datos;
		valid = true;
	}
	
	public MessageChunkQueryResponse(DataInputStream dis, byte respOpcode) {
		if (fromDataInputStream(dis, respOpcode) == false) {
			throw new RuntimeException("Failed to parse message: format is not Query.");
		}
		else {
			assert(true);
		}
	}


	public short getNumChunk() {
		return numChunk;
	}


	public void setNumChunk(short numChunk) {
		this.numChunk = numChunk;
	}


	public int getChunk() {
		return chunkSize;
	}


//	private void setChunk(short chunk) {
//		this.chunkSize = chunk;
//	}
	
	public byte[] getDatos() {
		return datos;
	}
	
//	private void setDatos(byte[] datos) {
//		this.datos = datos;
//	}
	
	public String getDatosString() {
		String s = new String(datos);
		return s;
	}
	
	
	public byte[] toByteArray() {
		int sizeOfMessage = FIELD_OPCODE_BYTES + FIELD_TRANSID_BYTES + FIELD_CHUNKSIZE_BYTES + datos.length;
		ByteBuffer buf = ByteBuffer.allocate(sizeOfMessage);
		
		buf.put((byte) getOpCode());
		buf.put((byte) getTransId());
		buf.putShort((short) getNumChunk());
		buf.put((byte[]) this.datos);
		return buf.array();
	}
	
	
	public boolean fromDataInputStream(DataInputStream dis, byte respOpcode) {
		
		try {
		setOpCode(respOpcode);
		setTransId(dis.readByte());
		
		this.numChunk = dis.readShort();
		
		byte[] dat = new byte[chunkSize];
		dis.read(dat);
		datos = dat;
		
		
		
		valid = true;
		} catch(IOException e){
			e.printStackTrace();
			assert (valid == false);
		}
		return valid;
	}



	@Override
	protected boolean fromByteArray(byte[] buf) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override

		public String toString() {
		    assert (valid);
		    StringBuffer strBuf = new StringBuffer();
		    strBuf.append(" Type:" + this.getOpCodeString());
		    strBuf.append(" TransId:" + this.getTransId());
		    strBuf.append(" NumChunk:" + this.getNumChunk());
		    strBuf.append(" Datos:" + this.getDatos());
		    return strBuf.toString();
		  }

		  /**
		   * For checking opcode validity.
		   */
		  private static final Set<Byte> conf_opcodes = Collections
				  .unmodifiableSet(new HashSet<Byte>(Arrays.asList(_conf_opCodes)));

		  protected void _check_opcode(byte opcode) {
		    if (!conf_opcodes.contains(opcode))
		      throw new RuntimeException("Opcode " + opcode + " no es de tipo ChunkACK.");
		  
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
