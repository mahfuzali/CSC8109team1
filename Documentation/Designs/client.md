Requirements
============

* A user Alice or Bob needs to use a client application to communicate with TDS
* Each user has their own copy of the client application
* A user uses the same client application for all their exchanges, i.e. to send messages to every receipient via TDS

Notes
=====

The client application needs to communicate with Amazon Key Storage so that a user can access the public keys of other users

The client application will be a long-running application

The client will keep a record in memory of all current exchange labels (in a HashMap or HashSet?).  It must be able to persist this to stable storage (a simple file will do) in case the client program crashes
