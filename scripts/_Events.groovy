import org.apache.log4j.Logger;

import groovy.util.logging.Log4j;
import groovy.xml.StreamingMarkupBuilder


eventCreateWarStart = { warName, stagingDir ->
    println "| Hook WAR creation"
    
    try{
        println "| "+warName    
        println "| "+stagingDir
        
        def buildNumber1 = System.getProperty("build.number", "NONE")
        def buildNumber2 = System.getProperty("BUILD_NUMBER", "NONE")
        //        def buildId = System.getProperty("BUILD_ID", "NONE")
        //        def buildTimeStamp= System.getProperty("build.timeStamp", "NONE")
        //        def buildUserName= System.getProperty("build.userName", "")
        //        def repositoryRepositoryUrl= System.getProperty("repository.repositoryUrl",  "")
        //        def repositoryRevisionNumber= System.getProperty("repository.revision.number", "")
        //        def repositoryBranch= System.getProperty("repository.branch", "")

        println "| buildNumber1 = "+buildNumber1
        println "| buildNumber2 = "+buildNumber2
        //        println "| buildId = "+buildId
        //        println "| buildTimeStamp = "+buildTimeStamp
    
    
  
        //        ant.propertyfile(file: "${stagingDir}/WEB-INF/classes/test.properties") {
        //            entry(key:"build.number.1", value:buildNumber1)
        //            entry(key:"build.number.2", value:buildNumber2)
        //            entry(key:"build.id", value:buildId)
        //            entry(key:"build.timestamp", value:buildTimeStamp)
        //        }
    }catch(Exception e){
        e.printStackTrace()
    }
    
    println "| Hook WAR creation finished"
}

eventWebXmlEnd = {String tmpfile ->
    def log = Logger.getLogger(this.getClass());
    
    log.info "Dynamically adjusting web.xml in /scripts/_Events.groovy"
    
    def root = new XmlSlurper().parse(webXmlFile)
    
    log.info "Adding security filter (de.ddb.next.filter.SecurityFilter) to web.xml"
    
    root.appendNode {
        'filter' {
            'filter-name' ('DdbSecurityFilter')
            'filter-class' ('de.ddb.next.filter.SecurityFilter')
        }
    }
    root.appendNode {
        'filter-mapping' {
            'filter-name' ('DdbSecurityFilter')
            'url-pattern' ('/*')
        }
    }

    log.info "Adding Loadbalancer response header filter (de.ddb.next.filter.LBHeaderFilter) to web.xml"
    
    root.appendNode {
        'filter' {
            'filter-name' ('LBHeaderFilter')
            'filter-class' ('de.ddb.next.filter.LBHeaderFilter')
        }
    }
    root.appendNode {
        'filter-mapping' {
            'filter-name' ('LBHeaderFilter')
            'url-pattern' ('/*')
        }
    }

    log.info "Adding session listener (de.ddb.next.listener.SessionListener) to web.xml"
    
    root.appendNode {
        'listener' {
            'listener-class' (
            'de.ddb.next.listener.SessionListener'
            )
        }
    }
    
    log.info "Adding mime-mapping (woff -> application/x-font-woff) to web.xml"
    
    root.appendNode {
        'mime-mapping' {
            'extension' (
                'woff'
            )
            'mime-type' (
                'application/x-font-woff'
            )
        }
    }
        
    webXmlFile.text = new StreamingMarkupBuilder().bind {
        mkp.declareNamespace("": "http://java.sun.com/xml/ns/javaee")
        mkp.yield(root)
    }
}
