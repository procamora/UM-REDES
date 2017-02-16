

> Una vez el peer dispone de la información de las semillas para un fichero, puede solicitar a otros peers la descarga de trozos (de tamaño fijo) del fichero, a los que denominaremos normalmente como chunks, a través de un canal confiable. Utilizando multiformato o lenguaje de marcas especificar, al menos, los siguientes aspectos del protocolo peer to peer:


# 4.Diseño los mensajes necesarios para solicitar y servir los trozos del fichero.



#### Formato del mensaje: CHUNKQUERY para el tipo QUERY_CHUNK

- Type = 3 (QUERY_CHUNK)
    - Formato del mensaje: CHUNKQUERY
    - Un peer solicita al otro peer un chunk de un fichero especifico


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

- Type: Siempre sera 3 para indicar que es un QUERY_CHUNK
- Hash: Hash del fichero del que deseamos obtener un chunk.
- Num Chunks: numero de chunks que solicitas.



#### Formato del mensaje: CHUNKQUERYRESPONSE para el tipo QUERY_CHUNK_RESPONSE


- Type = 4 (QUERY_CHUNK_RESPONSE)
    - Formato del mensaje: CHUNKQUERYRESPONSE
    - Un peer manda a otro peer el chunk que le ha solicitado.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
        <td>Num Chunks (longitud variable)</td>
        <td>Chunk (longitud indicada por el tracker)</td>
    <tr align="center">
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 4 para indicar que es un QUERY_CHUNK_RESPONSE.
- Num Chunks: Numero del chunk del que procede el dato.
- Hash: Hash del fichero del que procede el chunk.
- Chunk: Dato del chunk, el tamaño se establece acorde al tamaño que nos indica el tracker.
