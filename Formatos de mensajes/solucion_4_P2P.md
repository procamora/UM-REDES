
## 4.Diseño los mensajes necesarios para solicitar y servir los trozos del fichero.



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
        <td colspan="2">Num Chunk (longitud variable)</td>
    </tr>
</table>



```xml
<message>
    <operation>query_chunk</operation>
    <num_Chunk></num_Chunk>
</message>
```

Información del paquete:

- Type: Siempre sera 3 para indicar que es un QUERY_CHUNK
- Hash: Hash del fichero del que deseamos obtener un chunk.
- Num Chunks: numero del chunk que solicitas.



#### Formato del mensaje: CHUNKQUERYRESPONSE para el tipo QUERY_CHUNK_RESPONSE


- Type = 4 (QUERY_CHUNK_RESPONSE)
    - Formato del mensaje: CHUNKQUERYRESPONSE
    - Un peer manda a otro peer el chunk que le ha solicitado.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td>Num Chunk (longitud variable)</td>
        <td>Chunk (longitud indicada por el tracker)</td>
    </tr>
</table>


```xml
<message>
    <operation>chunk_response</operation>
    <hash></hash>
    <Num_Chunk></Num_Chunk>
    <Chunk_data></Chunk_data>
</message>
```

Información del paquete:

- Type: Siempre sera 4 para indicar que es un QUERY_CHUNK_RESPONSE.
- Hash: Hash del fichero del que procede el chunk.
- Num Chunk: Numero del chunk del que procede el dato.
- Chunk: Dato del chunk, el tamaño se establece acorde al tamaño que nos indica el tracker.




#### Un peer A solicita al otro peer B el chunk 30 del fichero ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)

<table>
    <tr align="center">
        <td>3</td>
        <td>b9153318862f0f7b5f82c913ecb2117f97c3153e</td>
    </tr>
    <tr align="center">
        <td colspan="2">30</td>
    </tr>
</table>


#### El peer B manda el dato del chunk 30 a el peer A del fichero ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)

<table>
    <tr align="center">
        <td>4</td>
        <td>b9153318862f0f7b5f82c913ecb2117f97c3153e</td>
    </tr>
    <tr align="center">
        <td>30</td>
        <td>DATOS</td>
    </tr>
</table>
