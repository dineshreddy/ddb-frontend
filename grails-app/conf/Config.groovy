/*
 * Copyright (C) 2013 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          [
        'text/html',
        'application/xhtml+xml'
    ],
    js:            'text/javascript',
    json:          [
        'application/json',
        'text/json'
    ],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           [
        'text/xml',
        'application/xml'
    ]
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000
grails.resources.uri.prefix = "appStatic"

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = [
    '/images/*',
    '/css/*',
    '/js/*',
    '/plugins/*'
]

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false


grails.resources.mappers.zip.excludes = [
    '**/*.png',
    '**/*.gif',
    '**/*.ico',
    '**/*.jpg',
    '**/*.jpeg',
    '**/*.swf',
    '**/*.gz',
    '**/*.zip'
]

ddb {
    backend {
        facets {
            filter = [
                [facetName:'language_fct', filter:'term:unknown' ],
                [facetName:'language_fct', filter:'term:termunknown'],
                [facetName:'keywords_fct', filter:'null'],
                [facetName:'provider_fct', filter:'null'],
                [facetName:'affiliate_fct', filter:'null'],
                [facetName:'type_fct', filter:'null'],
                [facetName:'sector_fct', filter:'null'],
                [facetName:'place_fct', filter:'null'],
                [facetName:'time_fct', filter:'null']
            ]
        }
    }
}

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.config.locations = [
            "file:${userHome}/.grails/${appName}.properties"
        ]

    }
    production {
        grails.logging.jul.usebridge = false
        grails.config.locations = [
            "file:"+ System.getProperty('catalina.base')+ "/grails/app-config/${appName}.properties"
        ]
    }
}

//DDB SPECIFIC Configuration variables
//The variables have to be overwritten by defining local configurations, see below environments
ddb.self.base.url="http://localhost:8080"
ddb.binary.url="http://localhost/binary/"
ddb.static.url="http://localhost/static/"
ddb.apis.url="http://localhost:8080/"
ddb.backend.url="http://localhost/backend:9998/"
ddb.backend.apikey=""
ddb.aas.url="http://localhost/aas:8081/"
ddb.culturegraph.url="http://hub.culturegraph.org"
ddb.dnb.url="http://d-nb.info"
ddb.bookmark.url="http://localhost:9200"
ddb.newsletter.url="http://localhost:9200"
ddb.logging.folder="target/logs"
ddb.tracking.piwikfile="${userHome}/.grails/tracking.txt"
ddb.advancedSearch.searchGroupCount=3
ddb.advancedSearch.searchFieldCount=10
ddb.advancedSearch.defaultOffset=0
ddb.advancedSearch.defaultRows=20
ddb.session.timeout=3600 // in sec -> 60min
ddb.loadbalancer.header.name="nid"
ddb.loadbalancer.header.value="-1"
ddb.favorites.sendmailfrom = "noreply@deutsche-digitale-bibliothek.de"
ddb.favorites.basedomain="http://dev.escidoc.org"

// The grails.serverURL is required for the PDF rendering plugin.
//grails.serverURL=ddb.apis.url // hla: Temporarily removed due to side effects on link generation


// log4j configuration
log4j = {

    // The appenders define the output method of the loggings
    appenders {
        console name: "console", threshold: org.apache.log4j.Level.INFO, layout:pattern(conversionPattern: "%-5p: %d{dd:MM:yyyy HH:mm:ss,SSS} %c: %m%n")
        rollingFile name: "ddbnext-info", threshold: org.apache.log4j.Level.INFO, file: config.ddb.logging.folder+"/ddbnext-info.log", maxFileSize: "50MB", layout:pattern(conversionPattern: "%-5p: %d{dd:MM:yyyy HH:mm:ss,SSS} %c: %m%n")
        rollingFile name: "ddbnext-warn", threshold: org.apache.log4j.Level.WARN, file: config.ddb.logging.folder+"/ddbnext-warn.log", maxFileSize: "50MB", layout:pattern(conversionPattern: "%-5p: %d{dd:MM:yyyy HH:mm:ss,SSS} %c: %m%n")
        rollingFile name: "ddbnext-error", threshold: org.apache.log4j.Level.ERROR, file: config.ddb.logging.folder+"/ddbnext-error.log", maxFileSize: "50MB", layout:pattern(conversionPattern: "%-5p: %d{dd:MM:yyyy HH:mm:ss,SSS} %c: %m%n")
        rollingFile name: "stacktrace", threshold: org.apache.log4j.Level.ERROR, file: config.ddb.logging.folder+"/ddbnext-stacktrace.log", maxFileSize: "50MB", layout:pattern(conversionPattern: "%-5p: %d{dd:MM:yyyy HH:mm:ss,SSS} %c: %m%n")
    }

    // The root logger defines the basic log level and to which appenders the logging is going
    environments {
        development {
            root {  info "console", "ddbnext-info", "ddbnext-warn", "ddbnext-error", "stacktrace"  }
        }
        production {
            root { info "ddbnext-info", "ddbnext-warn", "ddbnext-error", "stacktrace" }
        }
        test {
            root {  info "console", "ddbnext-info", "ddbnext-warn", "ddbnext-error", "stacktrace"  }
        }
    }

    // This part can be used to filter out all loggings that are not interesting
    environments {
        development {
            warn    "org.codehaus.groovy.grails",               // only warnings or errors from grails
                    "grails.plugin",                            // only warnings or errors from grails.plugins
                    "org.grails.plugin",                        // only warnings or errors from plugins
                    "org.springframework",                      // only warnings or errors from spring
                    "net.jawr",                                 // only warnings or errors from jawr
                    "org.apache.catalina.core",                 // only warnings or errors from catalina core
                    "org.apache.coyote.http11.Http11Protocol",  // only warnings or errors from Http11Protocol
                    "org.apache.catalina.startup.ContextConfig" // only warnings or errors from ContextConfig

            error   "grails.util.GrailsUtil"                    // hide deprecated warnings on startup
        }
        production {
            //Don't filter messages in production
        }
        test {
            warn    "org.codehaus.groovy.grails",               // only warnings or errors from grails
                    "grails.plugin",                            // only warnings or errors from grails.plugins
                    "org.grails.plugin",                        // only warnings or errors from plugins
                    "org.springframework",                      // only warnings or errors from spring
                    "net.jawr",                                 // only warnings or errors from jawr
                    "org.apache.catalina.core",                 // only warnings or errors from catalina core
                    "org.apache.coyote.http11.Http11Protocol",  // only warnings or errors from Http11Protocol
                    "org.apache.catalina.startup.ContextConfig" // only warnings or errors from ContextConfig

            error   "grails.util.GrailsUtil"                    // hide deprecated warnings on startup
        }
    }

}

jawr {
    js {
        // Specific mapping to disable resource handling by plugin.
        mapping = '/jawr/'
        bundle {
            lib {
                // Bundle id is used in views.
                id = '/i18n/messages.js'

                // Tell which messages need to localized in Javascript.
                mappings = 'messages:grails-app.i18n.messages'
            }
        }
    }
    locale { // Define resolver so ?lang= Grails functionality works with controllers.
        resolver = 'net.jawr.web.resource.bundle.locale.SpringLocaleResolver' }
}


compress {
    enabled = true

    debug = false
    statsEnabled = true
    compressionThreshold = 1024
    // filter's url-patterns
    //urlPatterns = ["/*"]
    urlPatterns = [
        //"/static/*", No /static!
        //"/binary/*", No /binary!
        "/apis/*",
        "/searchresults/*",
        "/facets/*",
        "/content/*",
        "/advancedsearch/*",
        "/item/*",
        "/about-us/*",
        "/entity/*"
    ]
    includePathPatterns = []
    // Important! CSS and JS must be handled by the ressource plugin
    excludePathPatterns = [
        ".*\\.png",
        ".*\\.gif",
        ".*\\.ico",
        ".*\\.jpg",
        ".*\\.jpeg",
        ".*\\.swf",
        '.*\\.gz',
        '.*\\.zip',
        '.*\\.css',
        '.*\\.js'
    ]
    includeContentTypes = ["application/json"]
    excludeContentTypes = [".*"]
    includeUserAgentPatterns = []
    excludeUserAgentPatterns = []
    development { debug = true }

    production {  statsEnabled = false  }
}

grails {
    mail {
        host = "relay.fiz-karlsruhe.de"
        port = 25
    }
}

// Often needed for testing the staticpages on localhost,
// because these use absolute linking to the server root "/abc"
//grails.app.context = "/"
