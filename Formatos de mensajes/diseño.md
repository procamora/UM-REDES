

# ADD_SEED

|Type (1 byte)| Port (2 bytes) | #Files (2 bytes) | Filename length (2 bytes) | Filename (filename length bytes) | Size (5 bytes) | Hash (20 bytes)
|------|------|------|------|------|
| siempre 2 | | N | N veces | N veces | N veces | N veces


# SEED_INFO

| Type (1 byte) | hash (20 bytes) | size (5 bytes)| seeds (2 bytes) | ip (5 bytes) | port (2 bytes)
|----|----|-----|---:--|---|---|
| siempre 3 o 4 | | | N | N veces | N veces

```xml
<message>
	<operation>add_seed</operation>
	<port>4533</port>
	<file>
		<name>Ubuntu_14.04.iso</name>
		<size>1024572864</size>
		<hash>..................</hash>
	</file>
	<file>
		.....
	</file>
</message>
```



query funciona por nombre y size
