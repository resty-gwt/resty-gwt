![RestyGWT][5]
==============

Description
-----------

[RestyGWT][1] is a GWT generator for REST services and JSON encoded data transfer objects.

Project Resources
-----------------

* Source Code: [http://github.com/chirino/resty-gwt](http://github.com/chirino/resty-gwt)
* Discussions: [http://groups.google.com/group/restygwt](http://groups.google.com/group/restygwt)
* Issue Tracker: [http://github.com/chirino/resty-gwt/issues](http://github.com/chirino/resty-gwt/issues)

Features
--------

* Generates Async Restful JSON based service proxies
* Java Object to JSON encoding/decoding
* Easy to use REST API


REST Services
-------------

RestyGWT Rest Services allow you to define an asynchronous Service API which is then implemented via
GWT deferred binding by a RestyGWT generator.  See the following listing for an example:

    import javax.ws.rs.POST;
    ...
    public interface PizzaService extends RestService {
        @POST
        public void order(PizzaOrder request, MethodCallback<OrderConfirmation> callback);
    }

JAX-RS annotations are used to control how the methods interface map to HTTP requests.  The 
interface methods MUST return void.  Each method must declare at least one callback argument.  
Methods can optionally declare one method argument before the callback to pass via the request
body.

Java beans can be sent and received via JSON encoding/decoding.  Here what the classes declarations
look like for the `PizzaOrder` and `OrderConfirmation` in the previous example:

    public class PizzaOrder {
        public String phone_number;
        public boolean delivery;
        public List<String> delivery_address = new ArrayList<String>(4);
        public List<Pizza> pizzas = new ArrayList<Pizza>(10);
    }
    
    public class OrderConfirmation {
        public long order_id;
        public PizzaOrder order;
        public double price;
        public Long ready_time;
    }

The JSON encoding style is compatible with the default [Jackson][2] Data Binding.  En example,
`PizzaOrder` JSON representation would look like:

    {
      "phone_number":null,
      "delivery":true,
      "delivery_address":[
        "3434 Pinerun Ave.",
        "Wesley Chapel, FL 33734"
      ],
      "pizzas":[
        {"quantity":1,"size":16,"crust":"thin","toppings":["ham","pineapple"]},
        {"quantity":1,"size":16,"crust":"thin","toppings":["extra cheese"]}
      ]
    }

A GWT client creates an instance of the REST service and associate it with a HTTP
resource URL as follows:

    Resource resource = new Resource( GWT.getModuleBaseURL() + "pizza-service");

    PizzaService service = GWT.create(PizzaService.class);
    ((RestServiceProxy)service).setResource(resource);

    service.order(order, callback);
    

REST API
--------

The RestyGWT REST API is handy when you don't want to go through the trouble of creating 
service interfaces.

The following example, will post  a JSON request and receive a JSON response. 
It will set the HTTP `Accept` and `Content-Type` and `X-HTTP-Method-Override` header t
o the expected values.  It will also expect a HTTP 200 response code, otherwise it will 
consider request the request a failure.

    Resource resource = new Resource( GWT.getModuleBaseURL() + "pizza-service");

    JSONValue request = ...

    resource.post().json(request).send(new JsonCallback() {
        public void onSuccess(Method method, JSONValue response) {
            System.out.println(response);
        }
        public void onFailure(Method method, Throwable exception) {
            Window.alert("Error: "+exception);
        }
    });

All the standard HTTP methods are supported: 

* `resource.head()`
* `resource.get()`
* `resource.put()`
* `resource.post()`
* `resource.delete()`

Calling one of the above methods, creates a `Method` object.  You must create a new one 
for each HTTP request.  The `Method` object uses fluent API for easy building
of the request via method chaining.  For example, to set the user id and password
used in basic authentication you would:

    method = resource.get().user("hiram").password("pass");

You can set the HTTP request body to a text, xml, or json value.  Unless the content type
is explicitly set on the method, the following methods will set the `Content-Type` header 
for you:

* `method.text(String data)`
* `method.xml(Document data)`
* `method.json(JSONValue data)`

The `Method` object also allows you to set headers, a request timeout, and the expected 
'successful' status code for the request.

Finally, once you are ready to send the http request, pick one of the following methods
based on the expected content type of the result.  Unless explicitly set on the method, 
the following methods will set the `Accept` header for you:

* `method.send(TextCallback callback)`
* `method.send(JsonCallback callback)`
* `method.send(XmlCallback callback)`

The response to the HTTP request is supplied to the callback passed in the `send` method.
Once the callback is invoked, the `method.getRespose()` method to get the GWT `Response`
if your interested in things like the headers set on the response.

Using as a Maven Dependency
---------------------------

Just add the following dependency to your pom.xml

    <dependencies>
    ...
      <dependency>
        <groupId>org.fusesource.resty-gwt</groupId>
        <artifactId>resty-gwt</artifactId>
        <version>1.0-SNAPSHOT</version>
      </dependency>
    ...
    </dependencies>
    
And also the following repository:
  
    <repositories>
    ...
      <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>http://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
          <enabled>true</enabled>
        </snapshots>
        <releases>
          <enabled>false</enabled>
        </releases>
      </repository>
    ...
    </repositories>
    
Building from Source
--------------------
    
1. Download and install [Maven 2.10][3].  Set the `M2_HOME` environment variable to the installation directory.  Add the maven bin directory to your `PATH` environment variable and make sure your `JAVA_HOME` environment variable is set your your JDK install directory.
2. Download and install [GWT 2.0.0-rc1][4]. Set the `GWT_HOME` environment variable  to the installation directory.
3. Change to the resty-gwt source directory and run:

        $ mvn install
    
The above command will produce a `target/resty-gwt-*.jar` file.

To build and run the integration tests, use:    

    $ mvn install -P run-its

[1]: http://github.com/chirino/resty-gwt "resty-gwt"
[2]: http://wiki.fasterxml.com/JacksonHome "Jackson JSON Processor"
[3]: http://maven.apache.org/download.html#Maven_2.1.0 "Maven Download"
[4]: http://code.google.com/webtoolkit/download.html "GWT Download"
[5]: http://github.com/chirino/resty-gwt/raw/master/restygwt-website/src/images/restygwt-logo.png "resty-gwt logo"
