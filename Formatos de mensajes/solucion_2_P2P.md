
> Una de las operaciones más comunes de un peer con un tracker es solicitarle la lista de ficheros compartidos. Para ello le enviará un mensaje QUERY_FILES al que el tracker responderá con la lista de ficheros de su base de datos. Dicha consulta puede estar filtrada por ciertos valores, como se refleja en la especificación de la práctica de diseño.

# 2.Completar y modificar el repertorio de mensajes para considerar el mensaje QUERY_FILES y su respuesta. Imaginemos que la base de datos del tracker contiene los siguientes ficheros:


Type = 5 (QUERY_FILES)
- Formato del mensaje: SEED_QUERY_FILES
- Un peer solicita al tracker la lista de ficheros que coinciden con su patron de busqueda.


Type = 6 (QUERY_FILES_RESPONSE)
- Formato del mensaje: FILEINFO
- el tracker la lista de ficheros que coinciden con su patron de busqueda.


#### QUERY_FILES

Type (1 byte) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes)
------|------|------|-------
Siempre 5 | | |


#### QUERY_FILES_RESPONSE

Type (1 byte)| Port (2 bytes) | #Files (2 bytes) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes) | Hash (20 bytes)
------|------|------|------|------
siempre 6| todo a 0 | N | N veces | N veces | N veces | N veces

El puerto lo pongo a 0 ya que no necesito indicarlo


## 2.1. Usando mensajes multiformato el peer solicita un QUERY_FILES al tracker y éste responde con la lista de archivos correspondiente.

- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).


#### El peer envia al track un mensaje SEED_QUERY_FILES tipo QUERY_FILES

Type (1 byte) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes)
------|------|------|-------
5 | 1 | 000... | 100000

Repasar el campo Filename length (2 bytes), si solo quiero buscar por peso ver como indicarlo ya que tengo que indicar la longitud del nombre,  y creo que no podemos poner de longitud 0 para saltarnos el nombre y llegar directamente al campo size.


#### El tracker le responde con un mensaje FILEINFO tipo QUERY_FILES_RESPONSE

 Type (1 byte)| Port (2 bytes) | #Files (2 bytes) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes)| Hash (20 bytes)| Filename length (2 bytes)| Filename (filename length bytes)| Size (5 bytes)| Hash (20 bytes)
 ---|---------|------|------|------
6 | 00000... | 2 | 15 | ubuntu14.04.iso | 1.024.572.864 | b9153318862f0f7b5f82c913ecb2117f97c3153e | 18 | android-studio.zip| 380.943.097| af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d


## 2.2. Usando lenguaje de marcas especificar la comunicación del apartado 2.1.


```xml



```
