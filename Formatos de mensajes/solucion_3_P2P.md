

> Una vez el peer dispone de la información de las semillas para un fichero, puede solicitar a otros peers la descarga de trozos (de tamaño fijo) del fichero, a los que denominaremos normalmente como chunks, a través de un canal confiable. Utilizando multiformato o lenguaje de marcas especificar, al menos, los siguientes aspectos del protocolo peer to peer:


# 3. Puesto los peers pueden servir trozos de un fichero aunque no lo hayan descargado por completo, es necesario que un peer averigüe qué trozos pueden obtener de otro peer. Diseña los mensajes para dicha consulta y la correspondiente respuesta. Diseño los mensajes necesarios para solicitar y servir los trozos del fichero.



#### Formato del mensaje: SEEDQUERY para el tipo QUERY_CHUNKS

- Type = 7 (QUERY_CHUNKS)
    - Formato del mensaje: REQCHUNKS
    - Un peer solicita al otro peer la lista de chunks que tiene de un determinado fichero


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
    </tr>
    <tr align="center">
        <td align="center">Hash (20 bytes)</td>
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 7 para indicar que es un QUERY_CHUNKS
- Hash: hash del fichero del que deseamos saber los chunks disponibles, puede que en vez del nombre del fichero podamos poner el Hash del fichero ya que evitaría fallos por nombre duplicado.


## HACER PAQUETE INFORMASE TAMAÑO DE CHUNKS


#### Formato del mensaje: CHUNKSRESPONSE para el tipo QUERY_CHUNKS_RESPONSE

- Type = 8 (QUERY_CHUNKS_RESPONSE)
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
- Num Chunks: Numero de chunks que tiene para compartir
- Hash: Hash del fichero del que nos esta informando
- Chunk: Chunks que tiene el peer, se repite n veces, siendo n: Num Chunks
