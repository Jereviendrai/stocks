# Roadmap for subcomponent server

## Version 0.4
* 00000 : DONE : Basic functionality

## Version 0.5
* 00012 : DONE : Provide unit tests and integrate them with maven
* 00014 : DONE : Add Ansible playbook for deployment
* 00002 : INVALID : First database access on new server causes exception

## Version 1.0 (current)
* 00018 : DONE : First database access on new server causes exception
* 00019 : DONE : Split ansible script into setup and installation part
* 00017 : DONE : Add integration test
* 00016 : DONE : Migrate from Jetty to Tomcat 8
* 00001 : DONE : Better logging, divide access from errors
* 00005 : CANCELLED  : Clear obsolete tickets
* 00004 : DONE : Add EAN-13 add and delete methods for food
* 00015 : DONE : Replace ansible systemd module by service calls
* 00020 : DONE : Update server admin doc: logging, symlinks tomcat
* 00021 : DONE : Clean up codebase
* 00022 : DONE : Close DB connections after usage or reuse it 

## Version 1.1
* 00023 : NEW  : Use injection framework for enpoint config
* 00024 : NEW  : Test all components in integration tests
* 00025 : DONE : Remove duplicate code in test/upgrade and test/system
* 00006 : NEW  : Image support for Food
* 00010 : NEW  : Edit eat-by date
* 00007 : NEW  : Remember maximum items per food to emit warnings on shortage
* 00026 : NEW  : Write SAD with sequence diagrams
* 00027 : NEW  : Add system tests for Updates Table
* 00028 : NEW  : Decouple server, timeouts and fail fast
* 00029 : NEW  : Perform locking on CA
* 00030 : NEW  : Database versioning and concurrency

## Version 1.2
* 00009 : NEW  : Add favourite food markers
* 00003 : NEW  : setup-ca script emits "unable to write random state"
* 00011 : NEW  : Race condition when adding Food_item and retrieving updates 
                 afterwards. Add command reaches server but client refresh does 
                 not get it. 

