
# COMUNICACION UDP: PEER - TRACKER

#### Formato del mensaje: CONTROL

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Num Seq (2 bytes)</td>
    </tr>
</table>

Tipos que lo usan:
- Type = 1 (ADD_SEED_ACK)



#### Formato del mensaje: FILEINFO

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

Tipos que lo usan:
- Type = 2 (ADD_SEED)
- Type = 6 (QUERY_FILES_RESPONSE)



#### Formato del mensaje: SEEDINFO

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

Tipos que lo usan:
- Type = 3 (GET_SEEDS)
- Type = 4 (SEED_LIST)



#### Formato del mensaje: SEEDQUERY

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

Tipos que lo usan:
- Type = 5 (QUERY_FILES)



#### Formato del mensaje: CHUNKINFO

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Tamaño Chunk (2 bytes)</td>
    </tr>
</table>

Tipos que lo usan:
- Type = 7 (QUERY_CHUNK)
- Type = 8 (QUERY_CHUNK_RESPONSE)



# COMUNICACION TCP: PEER - PEER


#### Formato del mensaje: CHUNKQUERY

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td colspan="2">Num Chunks (longitud variable)</td>
    </tr>
</table>

Tipos que lo usan:
- Type = 1 (GET_CHUNK)
- Type = 3 (QUERY_CHUNK)



#### Formato del mensaje: CHUNKQUERYRESPONSE

<table>
    <tr align="center">
        <td>Type (1 byte)</td>
        <td>Hash (20 bytes)</td>
    </tr>
    <tr align="center">
        <td>Num Chunks (longitud variable)</td>
        <td>Chunk (X bytes)</td>
    </tr>
</table>

Tipos que lo usan:
- Type = 2 (GET_CHUNK_RESPONSE)
- Type = 4 (QUERY_CHUNK_RESPONSE)
