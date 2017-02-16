

> Una vez el peer dispone de la información de las semillas para un fichero, puede solicitar a otros peers la descarga de trozos (de tamaño fijo) del fichero, a los que denominaremos normalmente como chunks, a través de un canal confiable. Utilizando multiformato o lenguaje de marcas especificar, al menos, los siguientes aspectos del protocolo peer to peer:


# 3. Puesto los peers pueden servir trozos de un fichero aunque no lo hayan descargado por completo, es necesario que un peer averigüe qué trozos pueden obtener de otro peer. Diseña los mensajes para dicha consulta y la correspondiente respuesta. Diseño los mensajes necesarios para solicitar y servir los trozos del fichero.



#### Formato del mensaje: CHUNKQUERY para el tipo GET_CHUNK

- Type = 1 (GET_CHUNK)
    - Formato del mensaje: CHUNKQUERY
    - Un peer solicita al otro peer la lista de chunks que tiene de un determinado fichero

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="2">Num Chunks (longitud variable)</td>
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 1 para indicar que es un GET_CHUNK.
- Hash: Hash del fichero del que deseamos saber los chunks disponibles, puede que en vez del nombre del fichero podamos poner el Hash del fichero ya que evitaría fallos por nombre duplicado.
- Num Chunks: No se usa, todo a 0.




#### Formato del mensaje: CHUNKQUERYRESPONSE para el tipo GET_CHUNK_RESPONSE

- Type = 2 (GET_CHUNK_RESPONSE)
    - Formato del mensaje: CHUNKQUERYRESPONSE
    - Un peer informa a otro peer de la lista de chunks que tiene de un determinado fichero listo para compartir.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
        <td>Num Chunks (longitud variable)</td>
        <td>Chunk (X bytes)</td>
    <tr align="center">
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 2 para indicar que es un GET_CHUNK_RESPONSE.
- Hash: Hash del fichero del que nos esta informando.
- Num Chunks: Numero de chunks que tiene para compartir, El tamaño es :[^1], SI SE TIENEN TODOS LOS CHUNKS PONER TODO A 1 PARA INDICARLO EN VEZ DE PONER EN CHUNK TODOS LOS PAQUETES
- Chunk: Chunks que tiene el peer, se repite n veces, siendo n: Num Chunks.


[^1]: $\log_2 \frac{Tamaño Maximo de un fichero (2^32)}{tamaño de chunks}$
