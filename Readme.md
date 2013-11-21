DDB Next
================

DDB Next is a beta-web interface for [Deutsche Digital Bibliothek](http://ddb.de).

[Git](https://github.com/Deutsche-Digitale-Bibliothek/ddb-next) | [Jira](https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT) | Demo Coming Soon

-----

# Information for developers 

The DDB Next is a Web Application build on Grails.


## Download 

The project is still under beta development. 
There are no ''war-s'' for download war-s released yet. 



### Configuration 
No special configurations right now

### Developer Info 
#### Development Environment 
Developed with [Groovy/Grails Tool Suite](http://www.grails.org/products/ggts) ([download tool link](http://www.springsource.org/groovy-grails-tool-suite-download)). 
The Grails Tool Suite is an Eclipse based application with built in support for Grails and [vFabric tc Server](http://www.vmware.com/products/application-platform/vfabric-tcserver/overview.html) a neat solution to help developers launch apps easily. 

#### Github 
You are probably on Github, or you received this file from there

#### Usages 
In order to run the app from Grails Tools Suite a developer can navigate to the project, right click and Select ''Run As'' > ''Grails Command (run-app)'' or ''Grails Command (test-app)''
This operation is the same as a direct execution from the command line on the

    grails run-app

To run Selenium tests use the command

    grails run-selenium

#### External Configurations
In case additional configurations are needed, then the following apply:
In development and test mode the external configuration is located on: $USER_HOME/.grails/$appname.properties 
In Production Mode: /grails/app-config/$appname.properties

####Proxy configuration 
In development environment, proxies are read from $USER_HOME/.grails/ProxySettings.groovy by default.

####Runtime configurations 
In case there is a need to change the default configurations, through external configuration files it is possible to overridde the default configurations. 
A list of some predefined variables is set below:

    # ddb.binary.url="http://www.binary-p1.deutsche-digitale-bibliothek.de/binary/" (Deprecated: the binaries are now accessed over the ddb.backend.url)
    ddb.static.url="http://static-p1.deutsche-digitale-bibliothek.de"
    ddb.apis.url="http://localhost:8080"
    ddb.backend.url="http://backend-p1.deutsche-digitale-bibliothek.de:9998"
    ddb.backend.apikey=                                                             (put a valid API key here or leave it empty)
    ddb.aas.url=http://ddbaas1-t1.deutsche-digitale-bibliothek.de:8081
    ddb.culturegraph.url=http://hub.culturegraph.org
    ddb.bookmark.url=http://ddbelse1-t1.deutsche-digitale-bibliothek.de:9200
    ddb.newsletter.url=http://ddbelse1-t1.deutsche-digitale-bibliothek.de:9200

    ddb.favorites.sendmailfrom=noreply@deutsche-digitale-bibliothek.de
    ddb.favorites.reportMailTo=geschaeftsstelle@deutsche-digitale-bibliothek.de

    ddb.backend.facets.filter="[[facetName:language_fct, filter:term:unknown], [facetName:language_fct, filter:term:termunknown], [facetName:keywords_fct, filter:null], [facetName:provider_fct, filter:null], [facetName:affiliate_fct, filter:null], [facetName:type_fct, filter:null], [facetName:sector_fct, filter:null], [facetName:place_fct, filter:null], [facetName:time_fct, filter:null]]"

    ddb.tracking.piwikfile=/opt/ddb/tracking.txt
    grails.views.gsp.encoding=UTF-8
    grails.mime.types['html'][0]=text/html

    ddb.advancedSearch.searchGroupCount=3
    ddb.advancedSearch.searchFieldCount=10
    ddb.advancedSearch.defaultOffset=0
    ddb.advancedSearch.defaultRows=20  
    ddb.session.timeout=3600

    ddb.logging.folder=/opt/ddb/logs

    ddb.loadbalancer.header.name="nid"
    ddb.loadbalancer.header.value="-1"
    
    grails.mail.host=localhost
    grails.mail.port=1025

####Tomcat configuration
The tomcat configuration in the server.xml must ensure, that the used Connector must contain a valid URIEncoding tag.

	<Connector [...] URIEncoding="UTF-8" /> 
 
###Piwik configuration
The Piwik integration is done by a separate file (tracking.txt) that must be available in the filesystem of the webserver and configured
in the ddb-next.properties file. For example if the file is accessible under "/opt/ddb/tracking.txt", the configuration in ddb-next.properties
would look like "ddb.tracking.piwikfile=/opt/ddb/tracking.txt".

The file tracking.txt must contain the full Piwik tracking code. It will be rendered in the web page in exactly this form.
