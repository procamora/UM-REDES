
> Una de las operaciones más comunes de un peer con un tracker es solicitarle la lista de ficheros compartidos. Para ello le enviará un mensaje QUERY_FILES al que el tracker responderá con la lista de ficheros de su base de datos. Dicha consulta puede estar filtrada por ciertos valores, como se refleja en la especificación de la práctica de diseño.

# 2.Completar y modificar el repertorio de mensajes para considerar el mensaje QUERY_FILES y su respuesta. Imaginemos que la base de datos del tracker contiene los siguientes ficheros:


Type = 5 (GET_QUERY)
- Formato del mensaje: SEED_QUERY
- Un peer solicita al tracker la lista de ficheros que coinciden con su patron de busqueda.


Type = 6 (SEED_QUERY_RESPONSE)
- Formato del mensaje: FILEINFO
- el tracker la lista de ficheros que coinciden con su patron de busqueda.


#### SEED_QUERY

Type (1 byte) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes)
------|------|------|-------
Siempre 5 | | |


#### SEED_QUERY_RESPONSE



- ubuntu14.04.iso (hash b9153318862f0f7b5f82c913ecb2117f97c3153e, tamaño 1.024.572.864 bytes)
- android-studio.zip (hash af09cc0a33340d8daccdf3cbfefdc9ab45b97b5d , tamaño 380.943.097 bytes).

## 2.1. Usando mensajes multiformato el peer solicita un QUERY_FILES al tracker y éste responde con la lista de archivos correspondiente.


## 2.2. Usando lenguaje de marcas especificar la comunicación del apartado 2.1.
