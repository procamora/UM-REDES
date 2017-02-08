

# 1. Completar y modificar el repertorio de mensajes para contemplar la información anterior y especificar los mensajes intercambiados entre peer y tracker en los siguientes casos:

#### FILEINFO

Type (1 byte) | Port (2 bytes) | #Files (2 bytes) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes) | Hash (20 bytes)
------|------|------|------|------|------|------
siempre 2 | | N | N veces | N veces | N veces | N veces


#### SEEDINFO

Type (1 byte)  | hash (20 bytes) | size (5 bytes)| seeds (2 bytes) | ip (5 bytes) | port (2 bytes)
----|----|-----|-----|---|---
siempre 3 o 4| | | N | N veces | N veces



## 1.1. Usando mensajes multiformato un peer (155.54.2.3) se agrega como seed en el puerto 4533, y con los ficheros:

- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).

#### El peer envia al track un mensaje FILEINFO tipo ADD_SEED

Type (1 byte)| Port (2 bytes) | #Files (2 bytes) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes)| Hash (20 bytes)| Filename length (2 bytes)| Filename (filename length bytes)| Size (5 bytes)| Hash (20 bytes)
---|-----|----|------|------|------|-----|-----|----|------|----
 2| 4533| 2| 15| ubuntu14.04.iso | 1.024.572.864 | b9153318862f0f7b5f82c913ecb2117f97c3153e | 18 | android-studio.zip| 380.943.097| af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d



#### El track responde mensaje de CONTROL tipo ADD_SEED_ACK al peer


|Type (1 byte) |
|-----|
|1 |



## 1.2. Usando un lenguaje de marcas especificar la comunicación del apartado 1.1

```xml
<message>
	<operation>add_seed</operation>
	<port>4533</port>
	<file>
		<name>ubuntu14.04.iso</name>
		<size>1024572864</size>
		<hash>b9153318862f0f7b5f82c913ecb2117f97c3153e<hash>
	</file>
	<file>
		<name>android-studio.zip</name>
		<size>380943097</size>
		<hash>af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d<hash>
	</file>
</message>
```
