package es.um.redes.P2P.PeerPeer.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MessageChunkQueryResponse extends Message {

	/**
	 * @author procamora
	 * 
	 *         Peer-tracker protocol data message, format "ChunkACK"
	 * 
	 *         1b 20 B 5B +--------------+------------+ |Type | Hash | Num_Chunk
	 *         | +------+-------+------------+
	 */

	/**
	 * Message opcodes that use the Chunk format
	 */
	private static final Byte[] _conf_opcodes = { OP_GET_CHUNK_ACK, OP_CHUNK_ACK };

	private long numChunk;
	private byte[] datos;

	/**
	 * Constructor used by tracker
	 * 
	 * @param opCode
	 *            Message type
	 * @param tid
	 * @param fileHash
	 *            The chunk size
	 * @param numChunk
	 */
	public MessageChunkQueryResponse(byte opCode, long numChunk, byte[] datos) {
		setOpCode(opCode);
		this.numChunk = numChunk;
		this.datos = datos;
		valid = true;
	}

	/**
	 * Constructor used by client when creating message response after receiving
	 * 
	 * @param buf
	 */
	public MessageChunkQueryResponse(DataInputStream dis, byte respOpcode, short chunkSize) {
		if (fromDataInputStream(dis, respOpcode, chunkSize) == false)
			throw new RuntimeException("Failed to parse message: format is not ChunkACK.");
		else
			valid = true;

	}

	/**
	 * Creates a byte array ready for sending a datagram packet, from a valid
	 * message of ChunkACK format
	 */
	public byte[] toByteArray() {
		int byteBufferLength = FIELD_OPCODE_BYTES + FIELD_NUMCHUNKSIZE + datos.length;

		ByteBuffer buf = ByteBuffer.allocate(byteBufferLength);

		// Opcode
		buf.put((byte) this.getOpCode());

		// Num Chunk
		buf.putLong((long) this.getNumChunk());

		// File Datos
		buf.put((byte[]) this.getDatos());

		return buf.array();
	}

	/**
	 * Creates a valid message of ChunkACK format, from the byte array of the
	 * received packet
	 */
	@Override
	protected boolean fromDataInputStream(DataInputStream dis, byte respOpcode, short chunkSize) {

		try {
			// Opcode
			setOpCode(respOpcode);

			// Num Chunk
			this.numChunk = dis.readLong();

			// File Datos
			if (respOpcode == OP_CHUNK_ACK) {
				datos = new byte[chunkSize];
				dis.readFully(datos);
			} else {
				if (numChunk == Long.MAX_VALUE) // fichero local
					datos = new byte[chunkSize];
				else // fichero remoto con x trozoa
					datos = new byte[(int) (numChunk * FIELD_NUMCHUNKSIZE)];

				dis.readFully(datos);
			}
			valid = true;

		} catch (IOException e) {
			assert (valid == false);
		}
		return valid;
	}

	public long getNumChunk() {
		return numChunk;
	}

	public byte[] getDatos() {
		return datos;
	}

	public String getDatosString() {
		String s = new String(datos);
		return s;
	}

	/**
	 * Metodo publico usado por OP_GET_CHUNK_ACK, lee el array de bytes y lo
	 * convierte a un array de long
	 * 
	 * @throws Exception
	 *             para que el downloader cierre la conexion por formato
	 *             incorrecto
	 */
	public HashSet<Long> desconcatenaArrayBytesDatos() throws Exception {
		if (getOpCode() != OP_GET_CHUNK_ACK)
			throw new IllegalStateException("Esta funcion solo la puede hacer OP_GET_CHUNK_ACK");

		// Si no tengo todos los trozos retorno los trozos exactos
		if (numChunk != Long.MAX_VALUE) {
			ByteBuffer buf = ByteBuffer.wrap(datos);
			// long[] a = new long[(int) numChunk];
			HashSet<Long> a = new HashSet<>();
			for (int i = 0; i < numChunk; i++)
				// a[i] = buf.getLong();
				a.add(buf.getLong());
			return a;
		} else // si tengo todos los trozos retorno un array[0]
			return new HashSet<Long>();
	}

	public String toString() {
		assert (valid);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("Type:" + this.getOpCodeString());
		strBuf.append(" NumChunk:" + this.getNumChunk());
		strBuf.append(" Datos:" + this.getDatos());
		return strBuf.toString();
	}

	/**
	 * For checking opcode validity.
	 */
	private static final Set<Byte> conf_opcodes = Collections
			.unmodifiableSet(new HashSet<Byte>(Arrays.asList(_conf_opcodes)));

	protected void _check_opcode(byte opcode) {
		if (!conf_opcodes.contains(opcode))
			throw new RuntimeException("Opcode " + opcode + " no es de tipo ChunkACK.");
	}

	/**
	 * Metodo estatico que recibe arrays de long y los concatena retornando un
	 * array de bytes
	 */
	public static byte[] concatenateByteArrays(HashSet<Long> arraysChunks) {

		ByteBuffer buf = ByteBuffer.allocate(arraysChunks.size() * FIELD_NUMCHUNKSIZE);
		for (Long cs : arraysChunks)
			buf.putLong(cs);

		return buf.array();
	}

}