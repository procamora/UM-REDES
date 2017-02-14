
> Una de las operaciones más comunes de un peer con un Tracker es solicitarle la lista de ficheros compartidos. Para ello le enviará un mensaje QUERY_FILES al que el Tracker responderá con la lista de ficheros de su base de datos. Dicha consulta puede estar filtrada por ciertos valores, como se refleja en la especificación de la práctica de diseño.

# 2.Completar y modificar el repertorio de mensajes para considerar el mensaje QUERY_FILES y su respuesta. Imaginemos que la base de datos del tracker contiene los siguientes ficheros:


#### Formato del mensaje: SEEDQUERY para el tipo QUERY_FILES

- Type = 5 (QUERY_FILES)
    - Formato del mensaje: SEEDQUERY
    - Un peer solicita al Tracker la lista de ficheros que coinciden con su patrón de búsqueda.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Size (5 bytes)</td>
        <td>Filename length (2 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="3" align="center">Filename (filename length bytes)</td>
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 5 para indicar que es un QUERY_FILES
- Size: Tamaño del fichero que estamos buscando en bytes
- Filename length: Longitud del nombre del fichero
- Filename (filename length bytes): nombre del fichero que buscamos



#### Formato del mensaje: FILEINFO para el tipo QUERY_FILES_RESPONSE


- Type = 6 (QUERY_FILES_RESPONSE)
    - Formato del mensaje: FILEINFO
    - el Tracker lista los ficheros que coinciden con su patrón de búsqueda.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Port (2 bytes)</td>
        <td>Files (2 bytes)</td>
    </tr>
    <tr align="center">
        <td>Filename length (2 bytes)</td>
        <td colspan="2">Filename (filename length bytes)</td>
    </tr>
    <tr align="center">
        <td>Size (5 bytes)</td>
        <td colspan="2">Hash (20 bytes)</td>
    </tr>
</table>


Información del paquete:

- Type: Siempre sera 6 para indicar que es un QUERY_FILES_RESPONSE
- Port: Todo a 0, ya que no se usa
- Files: Numero de ficheros que tiene el Tracker con el patrón que queremos
- Filename length: Longitud del nombre del fichero, se repetirá n veces, siendo n: Files
- Filename (filename length bytes): nombre del fichero, se repetirá n veces, siendo n: Files
- Size: Tamaño del fichero en bytes, se repetirá n veces, siendo n: Files
- Hash: Hash del fichero, se repetirá n veces, siendo n: Files



## 2.1. Usando mensajes multiformato el peer solicita un QUERY_FILES al tracker y éste responde con la lista de archivos correspondiente.

- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).


#### El peer envia al track un mensaje SEED_QUERY_FILES tipo QUERY_FILES para buscar los ficheros de tamaño superior a 250000 bytes


<table>
    <tr align="center">
        <td>5</td>
        <td>250000</td>
        <td>0000000000000000</td>
    </tr>
    <tr align="center">
        <td colspan="3" align="center">000000000....</td>
    </tr>
</table>


#### El tracker le responde con un mensaje FILEINFO tipo QUERY_FILES_RESPONSE con los ficheros de tamaño superior a 250000 bytes


<table>
    <tr align="center">
        <td>6</td>
        <td>00000</td>
        <td>2</td>
    </tr>
    <tr align="center">
        <td>15</td>
        <td colspan="2">ubuntu14.04.iso</td>
    </tr>
    <tr align="center">
        <td>1024572864</td>
        <td colspan="2">b9153318862f0f7b5f82c913ecb2117f97c3153e</td>
    </tr>
    <tr align="center">
        <td>18</td>
        <td colspan="2">android-studio.zip</td>
    </tr>
    <tr align="center">
        <td>380943097</td>
        <td colspan="2">af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d</td>
    </tr>
</table>



## 2.2. Usando lenguaje de marcas especificar la comunicación del apartado 2.1.


Peer Informa a tracker con un mensaje SEEDQUERY:


```xml
<message>
	<operation>query_files</operation>
	<file>
        <size>250000</size>
		<name></name>
    </file>
</message>
```


El tracker responde con un mensaje FILEINFO:

```xml
<message>
	<operation>query_files_response</operation>
	<port>00000</port>
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
