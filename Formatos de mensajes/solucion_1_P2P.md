

# 1. Completar y modificar el repertorio de mensajes para contemplar la información anterior y especificar los mensajes intercambiados entre peer y tracker en los siguientes casos:



#### Formato del mensaje: FILEINFO para el tipo ADD_SEED

- Type = 2 (ADD_SEED)
    - Formato del mensaje: FILEINFO
    - Un peer solicita al Tracker unirse a la red compartiendo una serie de ficheros.

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

- Type: Siempre sera 2 para indicar que es un ADD_SEED
- Port: Indica el puerto por el que escuchara el peer
- Files: Numero de ficheros que mandamos al Tracker
- Filename length: Longitud del nombre del fichero, se repetirá n veces, siendo n: Files
- Filename (filename length bytes): nombre del fichero, se repetirá n veces, siendo n: Files
- Size: Tamaño del fichero en bytes, se repetirá n veces, siendo n: Files
- Hash: Hash del fichero, se repetirá n veces, siendo n: Files


#### Formato del mensaje: SEEDINFO para el tipo GET_SEEDS

- Type = 3 (GET_SEEDS)
    - Formato del mensaje: SEEDINFO
    - Un peer solicita al trapacero la lista de semillas para un fichero.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Size (5 bytes)</td>
        <td>Seeds (2 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="3">Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="2">IP (5 bytes)</td>
        <td>Port (2 bytes)</td>
    </tr>
</table>

Información del paquete:

- Type: Siempre sera 3 para indicar que es un GET_SEEDS
- Size: Tamaño del fichero en bytes
- Seeds: Numero de peers que tienen trozos del fichero
- Hash: Hash del fichero
- IP: IP del peer que tiene trozos del fichero, se repetirá n veces, siendo n: Seeds
- Port: Puerto que tiene a la escucha el peer que tiene trozos del fichero, se repetirá n veces, siendo n: Seeds


<br/>
<br/>
<br/>



## 1.1. Usando mensajes multiformato un peer (155.54.2.3) se agrega como seed en el puerto 4533, y con los ficheros:

- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).

#### El peer envia al track un mensaje FILEINFO tipo ADD_SEED

<table>
    <tr align="center">
        <td>2</td>
        <td>4533</td>
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


#### El track responde mensaje de CONTROL tipo ADD_SEED_ACK al peer

<table>
    <tr align="center">
        <td>1</td>
    </tr>
</table>



## 1.2. Usando un lenguaje de marcas especificar la comunicación del apartado 1.1

```xml
<message>
	<operation>add_seed</operation>
	<port>4533</port>
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
