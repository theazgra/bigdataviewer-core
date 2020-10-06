# bigdataviewer-core
[![](https://api.travis-ci.org/bigdataviewer/bigdataviewer-core.svg?branch=master)](https://travis-ci.org/bigdataviewer/bigdataviewer-core)
[![Join the chat at https://gitter.im/bigdataviewer/bigdataviewer-core](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/bigdataviewer/bigdataviewer-core?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) 

ImgLib2-based viewer for registered SPIM stacks and more


## Usage:
```
java -ea -jar bigdataviewer-core.jar <INPUT-FILE>
```
Input file may be local XML file or dataset url on BigDataViwer Server, for example: http://127.0.0.1:8080/drosophila32.

## QCMP Compression Extension
This extension adds one command line option `-qcmp`. If this option is specified, then the client request compressed data from the BigDataViewer web server.

This extension doesn't limit functionality of original viewer application in any way.
