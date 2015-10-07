import org.apache.tools.ant.taskdefs.Ant

includeTargets << grailsScript("_GrailsInit")

def classpath //= grailsSettings.getCompileDependencies().join(System.getProperty("path.separator"))
def separator = System.getProperty("path.separator")

classpath = grailsSettings.getRuntimeDependencies().join(separator) 
println "classpath: " + classpath

target('dependencyCheck': "OWASP dependency check.") {
  ant.taskdef(name: "dpc", classname: "org.owasp.dependencycheck.taskdefs.DependencyCheckTask", classpath: classpath)
  ant.dpc(applicationName: "ddb-next", reportoutputdirectory:"${basedir}/target", proxyUrl: "proxy.fiz-karlsruhe.de", proxyPort:"8888") {
    classpath.split(separator).each { path ->
      println "Check " + path
      fileset(file:path)
    }
  }
}

setDefaultTarget(dependencyCheck)
