# UM-REDES




## Tareas pendientes
+ [ ] DownloaderThread comprobar cada x tiempo si hay nuevos seeders, en caso afirmativo crear nuevo downloaderThread para el
+ [x] Creo que nunChunk tiene que ser un long, ya que segun nuestras estimaciones ocupa 6 bytes y un long son 8 bytes
+ [x] Revisar toda la automata de UDP
+ [ ] UDP query files no funciona para recibir varios mensajes fragmentados
+ [x] Revisar toda la automata de TCP
+ [x] La descarga de un fichero por un seeder que no tiene todos los trozos creo que no funciona correctamente
+ [x] Tratar los 2 ultimos casos de error
+ [x] En las  estadisticas repasar la velocidad, aveces sale infinity
+ [ ] Progressbar no funciona para ficheros muy pequeños por division por 0
+ [x] Tratar error en el caso de que se borre archivo que se comparte


## Dudas

+ [x] Que excepcion hay que capturar en seederthread para cuando downloaderthread cierra la conexion
+ [x] currentDownloader de seederthread no se usarlo


### PeerController

+ [x] Cambiar variable privada `TreeMap<String, FileInfoPeer> mapaFicheros` por un `HashSet<FileInfo>`
+ [x] Despues de descargar un fichero en PeerControler, debemos eliminarlo. :)


### MessageChunkQueryResponse

+ [x] Econtrar la forma de que la funcion fromDataInputStream obtenga el chunkSize de forma dinamica (usando dis.available())


### Seeder

+ [x] Obtener el numero de chunk que tiene el fichero
+ [x] Hacer el bucle del run para ir respondiendo a mensajes mientras que la conexion tcp este abierta



### Downloader
Para cada fichero el procedimiento sera:

1. Crear un DownloaderThread por cada seeder que tenga el fichero, (habra una variable compartida por todos los thread con los chunks pendientes/descargados)
2. Pide la lista de chunks que tiene ese seeder y va actualizando la estructura de datos que guarde los chunks
3. Descarga los chunks por orden de rareza
4. Cuando se descarga el chunk vuelve a pedir la lista de chunks al seeder por si tiene trozos nuevos
5. Pone el chunk descargado en la bd para que otros se puedan descargar ese trozo

+ [x] No olvidemos el criterio de comparacion del mapa de IP y chunks, con el algoritmo de rareza
+ [x] Nada mas tener un trozo del fichero, el peer debe darse de alta como Seed. Imprescindible para el punto 3: SetChunkDownloaded()

## Mensajes TCP

+ [x] El atributo TransactionId debe ser eliminado, solo se usa para UDP.
