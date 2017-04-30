# UM-REDES


## Tareas pendientes

[ ] Creo que nunChunk tiene que ser un long, ya que segun nuestras estimaciones ocupa 6 bytes y un long son 8 bytes

### PeerController

[ ] Cambiar variable privada `TreeMap<String, FileInfoPeer> mapaFicheros` por un `HashSet<FileInfo>`


### MessageChunkQueryResponse

[ ] Econtrar la forma de que la funcion fromDataInputStream obtenga el chunkSize de forma dinamica


### Seeder

[ ] Obtener el numero de chunk que tiene el fichero
[ ] Hacer el bucle del run para ir respondiendo a mensajes mientras que la conexion tcp este abierta



### Downloader
Para cada fichero el procedimiento sera:

1. Crear un DownloaderThread por cada seeder que tenga el fichero, (habra una variable compartida por todos los thread con los chunks pendientes/descargados)
2. Pide la lista de chunks que tiene ese seeder y va actualizando la estructura de datos que guarde los chunks
3. Descarga los chunks por orden de rareza
4. Cuando se descarga el chunk vuelve a pedir la lista de chunks al seeder por si tiene trozos nuevos
5. Pone el chunk descargado en la bd para que otros se puedan descargar ese trozo
