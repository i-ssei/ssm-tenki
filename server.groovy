@Grab('org.eclipse.jetty.aggregate:jetty-all:9.2.20.v20161216')
@Grab('com.sun.jersey:jersey-server:1.18.3')
@Grab('com.sun.jersey:jersey-servlet:1.18.3')
@Grab('com.sun.jersey:jersey-core:1.18.3')
@Grab('com.sun.jersey:jersey-json:1.18.3')

@Grab("net.gpedro.integrations.slack:slack-webhook:1.2.1")
@Grab('org.jsoup:jsoup:1.7.3')

import com.sun.jersey.spi.container.servlet.ServletContainer

import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.server.Server


/*----- jersey -----*/
//JAX-RS resource & provider
def ResourceClass = [
    WeatherResource.class,
]
def servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
def servletHolder = new ServletHolder(ServletContainer.class);

//Servlet class
servletHolder.setInitParameters([
    "com.sun.jersey.config.property.resourceConfigClass": "com.sun.jersey.api.core.ClassNamesResourceConfig",
    "com.sun.jersey.config.property.classnames": ResourceClass.collect{it.getName()}.join(";"),
    "com.sun.jersey.api.json.POJOMappingFeature": "true"
])

//URL
servletContext.setContextPath("/api")
servletContext.addServlet(servletHolder, "/*")

/*----- server -----*/
//port
def server = new Server(3389);

//context
def handlerList = new HandlerList()
handlerList.setHandlers(servletContext)
server.setHandler(handlerList)

println "start API server"
server.start()

/*----- terminate -----*/
addShutdownHook {
  println "stop API server"
  server.stop()
}
