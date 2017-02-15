

> Una vez el peer dispone de la información de las semillas para un fichero, puede solicitar a otros peers la descarga de trozos (de tamaño fijo) del fichero, a los que denominaremos normalmente como chunks, a través de un canal confiable. Utilizando multiformato o lenguaje de marcas especificar, al menos, los siguientes aspectos del protocolo peer to peer:


# 4.Diseño los mensajes necesarios para solicitar y servir los trozos del fichero.



#### Formato del mensaje: SEEDQUERY para el tipo QUERY_CHUNKS

- Type = 9 (QUERY_CHUNKS)
    - Formato del mensaje: REQCHUNKS
    - Un peer solicita al otro peer un chunk de un fichero especifico


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
		<td>Num Chunks</td>
    </tr>
    <tr align="center">
        <td colspan="3">Hash (20 bytes)</td>
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 7 para indicar que es un QUERY_CHUNKS
- Num Chunks: numero de chunks que solicitas
- Hash: hash del fichero del que deseamos saber los chunks disponibles, puede que en vez del nombre del fichero podamos poner el Hash del fichero ya que evitaría fallos por nombre duplicado.


#### Formato del mensaje: CHUNKSRESPONSE para el tipo QUERY_CHUNKS_RESPONSE


- Type = 10 (QUERY_CHUNKS_RESPONSE)
    - Formato del mensaje: CHUNKSRESPONSE
    - Un peer informa a otro peer de la lista de chunks que tiene de un determinado fichero listo para compartir.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td colspan="2">Num Chunks (2 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="3">Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="3">Chunk (X bytes)</td>
    </tr>
</table>



Información del paquete:

- Type: Siempre sera 8 para indicar que es un QUERY_CHUNKS_RESPONSE
- Num Chunks: Todo a 0 ya que no se usa
- Hash: Hash del fichero del que nos esta informando
- Chunk: Datos del chunk, el tamaña se establece acorde al tamaño que nos indica el tracker
