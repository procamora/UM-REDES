

# 1. Completar y modificar el repertorio de mensajes para contemplar la información anterior y especificar los mensajes intercambiados entre peer y tracker en los siguientes casos:

#### Formato del mensaje: CONTROL para el tipo  ADD_SEED_ACK

- Type = 1 (ADD_SEED_ACK)
	- Formato del mensaje: CONTROL
	- El tracker confirma que ha recibido la solicitud de un peer para unirse a la red p2p.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Num Seq (2 bytes)</td>
    </tr>
</table>

Información del paquete:

- Type: Siempre sera 1 para indicar que es un ADD_SEED_ACK.
- Num Seq: Numero de secuencia del paquete, lo usamos para saber que ACK recibimos, solo podremos tener 65535 paquetes a la espera de recibir un ACK, cuando llegamos al 65535 reiniciamos a 0 para continuar.




#### Formato del mensaje: FILEINFO para el tipo ADD_SEED

- Type = 2 (ADD_SEED)
    - Formato del mensaje: FILEINFO
    - Un peer solicita al Tracker unirse a la red compartiendo una serie de ficheros.

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Num Seq (2 bytes)</td>
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

Información del paquete:

- Type: Siempre sera 2 para indicar que es un ADD_SEED.
- Num Seq: Numero de secuencia del paquete, lo usamos para saber que ACK recibimos, solo podremos tener 65535 paquetes a la espera de recibir un ack, cuando llegamos al 65535 reiniciamos a 0 para continuar.
- Port: Indica el puerto por el que escuchara el peer.
- Files: Numero de ficheros que mandamos al tracker.
- Filename length: Longitud del nombre del fichero, se repetirá n veces, siendo n: Files.
- Filename (longitud variable): nombre del fichero, se repetirá n veces, siendo n: Files.
- Size: Tamaño del fichero en bytes, se repetirá n veces, siendo n: Files.
- Hash: Hash del fichero, se repetirá n veces, siendo n: Files.




#### Formato del mensaje: SEEDINFO para el tipo GET_SEEDS

- Type = 3 (GET_SEEDS)
    - Formato del mensaje: SEEDINFO
    - Un peer solicita al tracker la lista de semillas para un fichero.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td>Size (5 bytes)</td>
        <td>Seeds (2 bytes)</td>
    </tr>
    <tr align="center">
        <td>IP (4 bytes)</td>
        <td>Port (2 bytes)</td>
    </tr>
</table>

Información del paquete:

- Type: Siempre sera 3 para indicar que es un GET_SEEDS.
- Hash: Hash del fichero del que deseamos obtener los Seeds.
- Size: No se usa, todo a 0.
- Seeds: No se usa, todo a 0.
- IP: No se usa, todo a 0.
- Port: No se usa, todo a 0.




#### Formato del mensaje: SEEDINFO para el tipo SEED_LIST

- Type = 4 (SEED_LIST)
    - Formato del mensaje: SEEDINFO
    - Un tracker informa al peer de la lista de semillas de un fichero.


<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td>Size (5 bytes)</td>
        <td>Seeds (2 bytes)</td>
    </tr>
    <tr align="center">
        <td>IP (4 bytes)</td>
        <td>Port (2 bytes)</td>
    </tr>
</table>

Información del paquete:

- Type: Siempre sera 4 para indicar que es un SEED_LIST.
- Hash: Hash del fichero del que obtenemos los Seeds.
- Size: Tamaño del fichero en bytes.
- Seeds: Numero de peers que tienen trozos del fichero.
- IP: IP del peer que tiene trozos del fichero, se repetirá n veces, siendo n: Seeds.
- Port: Puerto que tiene a la escucha el peer que tiene trozos del fichero, se repetirá n veces, siendo n: Seeds.




## 1.1. Usando mensajes multiformato un peer (155.54.2.3) se agrega como seed en el puerto 4533, y con los ficheros:

- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).

#### El peer envía al tracker un mensaje FILEINFO tipo ADD_SEED

<table>
    <tr align="center">
        <td>2</td>
        <td>784</td>
        <td>4533</td>
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


#### El tracker responde con un mensaje de CONTROL tipo ADD_SEED_ACK al peer indicando el numero de secuencia 784

<table>
    <tr align="center">
        <td>1</td>
        <td>784</td>
    </tr>
</table>



## 1.2. Usando un lenguaje de marcas especificar la comunicación del apartado 1.1

#### peer -> tracker

```xml
<message>
	<operation>add_seed</operation>
	<num_seq>784</num_seq>
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


#### tracker -> peer

```xml
<message>
	<operation>add_seed_ack</operation>
	<num_seq>784</num_seq>
</message>
```