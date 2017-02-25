
## 2.Completar y modificar el repertorio de mensajes para considerar el mensaje QUERY_FILES y su respuesta. Imaginemos que la base de datos del tracker contiene los siguientes ficheros:


#### Formato del mensaje: SEEDQUERY para el tipo QUERY_FILES

- Type = 5 (QUERY_FILES)
    - Formato del mensaje: SEEDQUERY
    - Un peer solicita al tracker la lista de ficheros que coinciden con su patrón de búsqueda.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Op (1 bytes)</td>
        <td>Size (5 bytes)</td>
    </tr>
    <tr align="center">
        <td>Filename length (2 bytes)</td>
        <td colspan="2">Filename (longitud variable)</td>
    </tr>
</table>

```xml
<message>
    <operation>query_files</operation>
    <op></op>
    <file>
        <size></size>
        <name></name>
    </file>
</message>
```

Información del paquete:

- Type: Siempre sera 5 para indicar que es un QUERY_FILES.
- Op: Operador de condición para tamaño:
    - 0: >
    - 1: >=
    - 2: <
    - 3: <=
    - 4: Aprox (10Mb) (EXTRA)
- Size: Tamaño del fichero que estamos buscando en bytes, si no lo usamos todo a 0.
- Filename length: Longitud del nombre del fichero, si no lo usamos todo a 0.
- Filename (filename length bytes): nombre del fichero que buscamos, si no lo usamos todo a 0.



#### Formato del mensaje: FILEINFO para el tipo QUERY_FILES_RESPONSE

- Type = 6 (QUERY_FILES_RESPONSE)
    - Formato del mensaje: FILEINFO
    - El tracker lista los ficheros que coinciden con su patrón de búsqueda. Si la lista es demasiado grande y no cabe en un unico paquete los calculara el numero de paquetes que necesite y lo indicara en el cammpo  **Port** poniendo el numero de mensajes que se va a enviar, si solo se envia un mensaje se pondra un 1.

añadir campo de numero de paquetes a enviar

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Port (2 bytes)</td>
        <td>Files (2 bytes)</td>
    </tr>
    <tr align="center">
        <td>Filename length (2 bytes)</td>
        <td colspan="3">Filename (longitud variable)</td>
    </tr>
    <tr align="center">
        <td>Size (5 bytes)</td>
        <td colspan="3">Hash (20 bytes)</td>
    </tr>
</table>

```xml
<message>
    <operation>query_files_response</operation>
    <port></port><!--inidca el numero de mensajes-->
    <file>
        <name></name>
        <size></size>
        <hash></hash>
    </file>
</message>
```

Información del paquete:

- Type: Siempre sera 6 para indicar que es un QUERY_FILES_RESPONSE.
- Port: COMPLETAR
- Files: Numero de ficheros que tiene el Tracker con el patrón que queremos.
- Filename length: Longitud del nombre del fichero, se repetirá n veces, siendo n: Files.
- Filename (filename length bytes): nombre del fichero, se repetirá n veces, siendo n: Files.
- Size: Tamaño del fichero en bytes, se repetirá n veces, siendo n: Files.
- Hash: Hash del fichero, se repetirá n veces, siendo n: Files.




#### Formato del mensaje: CHUNKINFO para el tipo QUERY_CHUNK

- Type = 7 (QUERY_CHUNK)
    - Formato del mensaje: CHUNKINFO
    - El peer pregunta al tracker cual es el tamaño de los chunks.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Tamaño Chunk (2 bytes)</td>
    </tr>
</table>

```xml
<message>
    <operation>query_chunk</operation>
    <size></size>
</message>
```

Información del paquete:

- Type: Siempre sera 7 para indicar que es un QUERY_CHUNK.
- Tamaño Chunk: No se usa, todo a 0.




#### Formato del mensaje: CHUNKINFO para el tipo QUERY_CHUNK_RESPONSE

- Type = 8 (QUERY_CHUNK_RESPONSE)
    - Formato del mensaje: CHUNKINFO
    - El tracker responde al peer cual es el tamaño de los chunks.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Tamaño Chunk (2 bytes)</td>
    </tr>
</table>

```xml
<message>
    <operation>query_chunk_response</operation>
    <size></size>
</message>
```

Información del paquete:

- Type: Siempre sera 8 para indicar que es un QUERY_CHUNK_RESPONSE.
- Tamaño Chunk: Indica el tamaño del chunk.


#### Formato del mensaje: REMOVE para el tipo REMOVE_SEED


- Type = 9 (REMOVE_SEED)
    - Formato del mensaje: REMOVE
    - El peer informa al tracler que quiero hacer unasolicitud de borrado, hay 2 opciones:
        - Darse de baja como peer: ``
        - Dar de baja un fichero: ``

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Port (2 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="3">Hash (20 bytes)</td>
    </tr>
</table>


```xml
<message>
    <operation>remove_seed_ack</operation>
    <port></port>
    <hash></hash>
</message>
```


Información del paquete:

- Type: Siempre sera 9 para indicar que es un REMOVE_SEED.
- Port: Indica el puerto del peer.
- Hash: Tiene 2 funciones, si va todo a 1 indicaremos que nos damos de baja como peer, en caso de que haya un hash valido daremos de baja ese fichero.


Sirve tanto para eliminar un fichero que ya no compartimos porque no lo tenemos como para darnos de baja en el tracker

type
puerto
hash

#### Formato del mensaje: CONTROL para el tipo REMOVE_SEED_ACK


- Type = 10 (REMOVE_SEED_ACK)
    - Formato del mensaje: CONTROL
    - El tracker confirma que ha recibido la solicitud de un peer para borrar.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
    </tr>
</table>


```xml
<message>
    <operation>remove_seed_ack</operation>
</message>
```


Información del paquete:

- Type: Siempre sera 10 para indicar que es un REMOVE_SEED_ACK.




### 2.1. Usando mensajes multiformato el peer solicita un QUERY_FILES al tracker y éste responde con la lista de archivos correspondiente.

- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).


#### El peer envía al track un mensaje SEED_QUERY_FILES tipo QUERY_FILES para buscar los ficheros de tamaño superior o igual a 250000 bytes

<table>
    <tr align="center">
        <td>5</td>
        <td>1</td>
        <td>250000</td>
    </tr>
    <tr align="center">
        <td>0000000000000000</td>
        <td colspan="2">000000000....</td>
    </tr>
</table>



#### El tracker le responde con un mensaje FILEINFO tipo QUERY_FILES_RESPONSE con los ficheros de tamaño superior a 250000 bytes


<table>
    <tr align="center">
        <td>6</td>
        <td>0000000000000000</td>
        <td>0000000000000000</td>
        <td>2</td>
    </tr>
    <tr align="center">
        <td>15</td>
        <td colspan="3">ubuntu14.04.iso</td>
    </tr>
    <tr align="center">
        <td>1024572864</td>
        <td colspan="3">b9153318862f0f7b5f82c913ecb2117f97c3153e</td>
    </tr>
    <tr align="center">
        <td>18</td>
        <td colspan="3">android-studio.zip</td>
    </tr>
    <tr align="center">
        <td>380943097</td>
        <td colspan="3">af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d</td>
    </tr>
</table>



### 2.2. Usando lenguaje de marcas especificar la comunicación del apartado 2.1.


#### Peer Informa a tracker con un mensaje SEEDQUERY:


```xml
<message>
    <operation>query_files</operation>
    <op>1</op> <!--podemos usar operadores bash: gt, lt, ge, etc-->
    <file>
        <size>250000</size>
        <name></name>
    </file>
</message>
```


#### El tracker responde con un mensaje FILEINFO:

```xml
<message>
    <operation>query_files_response</operation>
    <num_seq></num_seq>
    <port></port>
    <file>
        <name>ubuntu14.04.iso</name>
        <size>1024572864</size>
        <hash>b9153318862f0f7b5f82c913ecb2117f97c3153e</hash>
    </file>
    <file>
        <name>android-studio.zip</name>
        <size>380943097</size>
        <hash>af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d</hash>
    </file>
</message>
```
