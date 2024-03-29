package es.um.redes.P2P.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * @author procamora
 * 
 *         Clase encargada de los metodos de leer y escribir en ficheros
 *         binarioss
 */
public class Ficheros {

	/**
	 * 
	 * @param fichero
	 *            nombre del fichero a leer
	 * @param CHUNK_SIZE
	 *            tamaño del trozo
	 * @param pos
	 *            posición dentro del fichero
	 * @return array byes leidos
	 * @throws IOException
	 */
	public static byte[] lectura(String fichero, int CHUNK_SIZE, long pos) throws IOException {
		if (pos < 0)
			throw new IllegalArgumentException("Posicion tiene que ser >= 0");

		byte chunk[] = new byte[CHUNK_SIZE];
		File file = new File(fichero);
		int leidos = 0; // variable de control del bucle
		int leidosTotal = 0; // bytes leidos del fichero
		if (!file.exists())
			throw new IOException("No existe el fichero: " + fichero);

		try {
			RandomAccessFile rfi = new RandomAccessFile(file, "r");
			do {
				// Nos situamos en la posición inical + los bytes leidos
				rfi.seek(pos + leidos);
				leidos = rfi.read(chunk); // Leemos el trozo
				if (leidos != -1)
					leidosTotal += leidos;
			} while (leidos != -1 && leidos != CHUNK_SIZE && (long) leidos != file.length());
			rfi.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// tiene el tamaño de los byte leidos
		byte[] buffer = Arrays.copyOfRange(chunk, 0, leidosTotal);

		return buffer;
	}

	/**
	 * 
	 * @param fichero
	 *            nombre del fichero
	 * @param datos
	 *            array de byes a escribir
	 * @param pos
	 *            posición dentro del fichero
	 */
	public static void escritura(String fichero, byte[] datos, long pos) {
		if (pos < 0)
			throw new IllegalArgumentException("Posicion tiene que ser >= 0");

		File file = new File(fichero);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			RandomAccessFile rfo = new RandomAccessFile(file, "rw");
			rfo.seek(pos);
			rfo.write(datos, 0, datos.length);// puede que lenght -1
			rfo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
