DDB Next
================

* Copyright (C) 2013 FIZ Karlsruhe
* Licensed under the Apache License

[DDB Next][DDB Next] is a beta-web interface for [Deutsche Digital Bibliothek][Deutsche Digital Bibliothek] build with the Grails web framework.
More information about current development issues can be found in the [DDB Jira][DDB Jira].

[Deutsche Digital Bibliothek]: http://ddb.de
[DDB Next]: https://github.com/Deutsche-Digitale-Bibliothek/ddb-next 
[DDB Jira]: https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT

-----

## Getting started

###Software requirements
The DDB Next project depends on the following software
* JDK 1.6
* Grails 2.2.3

### IDE support 
DDB Next is developed with [Groovy/Grails Tool Suite](http://www.grails.org/products/ggts) ([download tool link](http://www.springsource.org/groovy-grails-tool-suite-download)). 
The Grails Tool Suite is an Eclipse based application with built in support for Grails and [vFabric tc Server](http://www.vmware.com/products/application-platform/vfabric-tcserver/overview.html) a neat solution to help developers launch apps easily. 

### Download 
To obtain the latest version of the project please clone the github repository

	git clone https://github.com/Deutsche-Digitale-Bibliothek/ddb-next.git

### Build 
In order to run the app from Grails Tools Suite a developer can navigate to the project, right click and Select ''Run As'' > ''Grails Command (run-app)'' or ''Grails Command (test-app)''
This operation is the same as a direct execution from the command line on the

    grails run-app

To run Selenium tests use the command

    grails run-selenium


### Configuration
####Proxy
In development environment, proxies are read from $USER_HOME/.grails/ProxySettings.groovy by default.

####API key
For accessing the DDB backend via it's REST API you'll need a API key. If you are a registered user of the DDB you can apply for this key under the following link 
(https://www.deutsche-digitale-bibliothek.de/user/profile)

####Application configuration 
The components and the behaviour of the application can be configured via an external configuration file which is located here: 
* In development and test mode: $USER_HOME/.grails/$appname.properties 
* In Production Mode: /grails/app-config/$appname.properties

In case there is a need to change the default configurations, through external configuration files it is possible to override the default configurations. 
A list of some predefined variables is set below:

	#DDB services configuration
    ddb.binary.url="http://api.deutsche-digitale-bibliothek.de" (used from the DFG viewer)
    ddb.static.url="http://static-p1.deutsche-digitale-bibliothek.de"
    ddb.apis.url="http://localhost:8080"
    ddb.backend.url="http://backend-p1.deutsche-digitale-bibliothek.de:9998"
    ddb.backend.apikey= (put a valid API key here or leave it empty)
    ddb.aas.url=http://ddbaas1-t1.deutsche-digitale-bibliothek.de:8081
    ddb.culturegraph.url=http://hub.culturegraph.org
    ddb.bookmark.url=http://ddbelse1-t1.deutsche-digitale-bibliothek.de:9200
    ddb.newsletter.url=http://ddbelse1-t1.deutsche-digitale-bibliothek.de:9200
    ddb.elasticsearch.url=http://whvmescidev6.fiz-karlsruhe.de:9200

	#Favorites configuration
    ddb.favorites.sendmailfrom=noreply@deutsche-digitale-bibliothek.de
    ddb.favorites.reportMailTo=geschaeftsstelle@deutsche-digitale-bibliothek.de
	ddb.favorites.basedomain=http://dev.escidoc.org
	ddb.favorites.reportMailTo=noreply@deutsche-digitale-bibliothek.de

	#Filter configuration
    ddb.backend.facets.filter="[[facetName:language_fct, filter:term:unknown], [facetName:language_fct, filter:term:termunknown], [facetName:keywords_fct, filter:null], [facetName:provider_fct, filter:null], [facetName:affiliate_fct, filter:null], [facetName:type_fct, filter:null], [facetName:sector_fct, filter:null], [facetName:place_fct, filter:null], [facetName:time_fct, filter:null]]"

	#piwik configuration
    ddb.tracking.piwikfile=/opt/ddb/tracking.txt
    
    #content configuration
    grails.views.gsp.encoding=UTF-8
    grails.mime.types['html'][0]=text/html

	#Advanced Search configuration
    ddb.advancedSearch.searchGroupCount=3
    ddb.advancedSearch.searchFieldCount=10
    ddb.advancedSearch.defaultOffset=0
    ddb.advancedSearch.defaultRows=20  
    ddb.session.timeout=3600

	#Logging configuration
    ddb.logging.folder=/opt/ddb/logs

	#Loadbalance configuration
    ddb.loadbalancer.header.name="nid"
    ddb.loadbalancer.header.value="-1"
    
    #Mail configuration
    grails.mail.host=localhost
    grails.mail.port=1025
	
	
####Runtime configuration
The tomcat configuration in the server.xml must ensure, that the used Connector must contain a valid URIEncoding tag.

	<Connector [...] URIEncoding="UTF-8" /> 
 
 
####Piwik configuration
The Piwik integration is done by a separate file (tracking.txt) that must be available in the filesystem of the webserver and configured
in the ddb-next.properties file. For example if the file is accessible under "/opt/ddb/tracking.txt", the configuration in ddb-next.properties
would look like "ddb.tracking.piwikfile=/opt/ddb/tracking.txt".

The file tracking.txt must contain the full Piwik tracking code. It will be rendered in the web page in exactly this form.	
	

##DDB service infrastructure
The DDB infrastructure is build of a set of services providing digital items, metadata, user data and many more. Hence the operation of the DDB Next portal has several dependencies to external services. 
Almost all DDB services are web services and use the HTTP protocol for communication. Each DDB service provider is referenced via an entry in the configuration file containing an URL. 
Some services needs some extra configuration, like the API key for the DDB backend. The following gives s short overview about the services used by the frontend.


####Binary service
The binary service provides the digital content (Texts, Images, Videos etc.) for the DDB items and institutions. These binary data is referenced by the items metadata provided by the backend service.

Configuration parameter:
* 'ddb.binary.url' defines the url for the host which stores the binary content of the DDB


####Static  service
Provides the static content of the DDB frontend. It includes the content for the help pages, the terms of use page, the contact page and many more.

Configuration parameter:
* 'ddb.static.url' defines the url for the host which stores the static content for DDB


####API service 
This service is provided by the DDB frontend itself and used as a wrapper for all backend related requests. 
By default this url links to localhost to make an efficient loopback call to the frontend (without routing via firewalls etc).

Configuration parameter:
* 'ddb.apis.url' defines the url to reach the API service


###Backend service
The backend service provides metadata for all items and institutions served by the DDB. The service is based on the IAIS-Cortex (http://www.iais.fraunhofer.de/iais-cortex.html).
IAIS Cortex is developed by [Fraunhofer Institute for Intelligent Analysis and Information System] (http://www.iais.fraunhofer.de/) (IAIS).
The access to the DDB backend is limited and access is restricted via an API key.

Configuration parameter:
* 'ddb.backend.url' defines the url to reach the backend.
* 'ddb.backend.apikey' defines the API key to authenticate against the backend. 

More information about the backend API can be found here:
https://api.deutsche-digitale-bibliothek.de/doku/display/ADD/API+der+Deutschen+Digitalen+Bibliothek


####AAS
OpenSource service for handling registration, authentication and authorization functionality for DDB users. 
 
Configuration parameter:
* 'ddb.aas.url' defines the url to reach the aas service
 
More info's about this service can be found here:
https://github.com/Deutsche-Digitale-Bibliothek/aas-AASWebServices


####CultureGraph
CultureGraph is a service platform for Data Integration, Persistent Identifier and Linked Open Data for cultural entities. 
It's a project of the Deutsche Nationalbibliothek (http://www.dnb.de).

Configuration parameter:
* 'ddb.culturegraph.url' defines the url to reach the culturegraph service.

More information about this service is available under:
http://www.culturegraph.org


####Elastic search service
Elastic search is used to manage personal content like bookmarks and saved searches for registered users of the DDB.
The service is based on elastic search (http://www.elasticsearch.org/) and developed by the DDB-NEXT team

Configuration parameter:
* 'ddb.bookmark.url' defines the url where to retrieve documents of type bookmark
* 'ddb.newsletter.url' defines the url where to retrieve documents of type bookmark
* 'ddb.elasticsearch.url' defines the url where to retrieve documents of type bookmark

More infos to the service can be found here (https://github.com/Deutsche-Digitale-Bibliothek/ddb-bookmark-web-api)


## Dependencies
The DDB Next is a Web Application use the following Grails plugins 
* cache:1.0.0
* cache-headers:1.1.5
* rendering:0.4.3
* resources:1.1.6
* zipped-resources:1.0
* compress:0.4


Additionally the application makes use of the following Javascript frameworks
* Autocomplete 1.10.2
* Bootstrap 2.2.2
* History 1.7.1
* JQuery 1.8.2
* Jwplayer 6.2.3115
* Respond.js 1.1.0
* Underscore 1.3.1
