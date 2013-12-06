# RestyGWT User Guide

RestyGWT is a GWT generator for REST services and JSON encoded data transfer objects.

{:toc:2-5}


## Features

* Generates Async Restful JSON based service proxies
* Java Object to JSON encoding/decoding
* Easy to use REST API

## REST Services

RestyGWT Rest Services allow you to define an asynchronous Service API which is then implemented via
GWT deferred binding by a RestyGWT generator. See the following listing for an example:

    import javax.ws.rs.POST;
    ...
    public interface PizzaService extends RestService {
        @POST
        public void order(PizzaOrder request,
                          MethodCallback<OrderConfirmation> callback);
    }

JAX-RS annotations are used to control how the methods interface map to HTTP requests. The
interface methods MUST return either `void`, `Request` or `JsonpRequest`. Each method must declare at least one callback argument.
Methods can optionally declare one method argument before the callback to pass via the request
body.

or if using `REST.withCallback(..).call(..)`, you can reuse the same interface on the server side as well:

    import javax.ws.rs.POST;
    ...
    public interface PizzaService extends DirectRestService {
        @POST
        public OrderConfirmation order(PizzaOrder request);
    }

### JAX/RS Subresource locators

RestyGWT supports JAX-RS subresource locator methods as synchronous methods on service interfaces. For instance:

    public interface LibraryService extends RestService {
        @Path("book/{isbn}")
        public BookService getBook(@PathParam("isbn") String isbn);
    }

    public interface BookService extends RestService {
        @GET
        @Path("title")
        public void getTitle(MethodCallback<String> callback);

        @PUT
        @Path("title")
        public void setTitle(String newTitle, MethodCallback<Void> callback);
    }

This allows developers to decompose service interfaces into logical chunks and even reuse interfaces for multiple sections of the virtual
REST URL hierarchy. Currently only `@PathParam` subresource locators are supported.

### JSON Encoding
Java beans can be sent and received via JSON encoding/decoding. Here what the classes declarations
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

The JSON encoding style is compatible with the default [Jackson](http://wiki.fasterxml.com/JacksonHome) Data Binding. For example,
`PizzaOrder`'s JSON representation would look like:

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

### Request Dispatchers

The request dispatcher intercepts all requests being made and can supply
additional features or behavior. For example, RestyGWT also supports a
`CachingRetryingDispatcher` which will automatically retry requests if
they fail.

To configure the `CachingRetryingDispatcher`, you can configure it on
your service interface at either the class or method level. Example:

        @POST
        @Options(dispatcher=CachingRetryingDispatcher.class)
        public void order(PizzaOrder request,
                          MethodCallback<OrderConfirmation> callback);

### Creating Custom Request Dispatchers

You can create a custom request dispatcher by implementing the following `Dispatcher`
interface:

    public interface Dispatcher {
        public Request send(Method method, RequestBuilder builder) throws RequestException;
    }

Your dispatcher implementation must also define a static singleton instance in a public
`INSTANCE` field. Example:

    public class SimpleDispatcher implements Dispatcher {
        public static final SimpleDispatcher INSTANCE = new SimpleDispatcher();

        public Request send(Method method, RequestBuilder builder) throws RequestException {
            return builder.send();
        }
    }

When the dispatcher's `send` method is called, the provided builder will already
be configured with all the options needed to do the request.

### Configuring the Request Timeout

You can use the `@Options` annotation to configure the timeout for requests
at either the class or method level. The timeout is specified in milliseconds,
For example, to set a 5 second timeout:

        @POST
        @Options(timeout=5000)
        public void order(PizzaOrder request,
                          MethodCallback<OrderConfirmation> callback);


### Configuring the Expected HTTP Status Code

By default results that have a 200, 201, or 204 HTTP status code are considered
to have succeeded. You can customize these defaults by setting the
`@Options` annotations at either the class or method level of the service interface.

Example:

        @POST
        @Options(expect={200,201})
        public void order(PizzaOrder request,
                          MethodCallback<OrderConfirmation> callback);

### Configuring the `Accept` and `Content-Type` and HTTP Headers

RestyGWT rest calls will automatically set the `Accept` and `Content-Type`
and HTTP Headers to match the type of data being sent and the type of
data expected by the callback. You can override these default values
by adding JAX-RS' `@Produces` and `@Consumes` annotations to the method
declaration.

### Keep the Java Interface clean

If you need an attribute of your DTO to be part of the URL you can do it by adding the `@Attribute` annotation alongside the `@PathParam`. The `@PathParam` references the placeholder in the `@Path` as usual and the `@Attribute` identifies the attribute/field of the DTO which shall be used for the replacement in the path.

Example:

        @PUT
        @Path("/{id}")
        public void updateOrder(@PathParam("id") @Attribute("order_id") OrderConfirmation order,
                                MethodCallback<OrderConfirmation> callback);

### Mapping to a JSONP request

If you need to access JSONP URl, then use the `@JSONP` annotation on the method.
For example:

    import org.fusesource.restygwt.client.JSONP;
    ...
    public interface FlickrService extends RestService {
        @Path("http://www.flickr.com/services/feeds/photos_public.gne?format=json")
        @JSONP(callbackParam="jsonFlickrFeed")
        public void photoFeed(JsonCallback callback);
    }

or if you have a `FlickrFeed` value object

    import org.fusesource.restygwt.client.JSONP;
    ...
    public interface FlickrService extends RestService {
        @Path("http://www.flickr.com/services/feeds/photos_public.gne?format=json")
        @JSONP(callbackParam="jsonFlickrFeed")
        public void photoFeed(MethodCallback<FlickrFeed> callback);
    }


## JSON Encoder/Decoders

If you want to manually access the JSON encoder/decoder for a given type just define
an interface that extends `JsonEncoderDecoder` and RestyGWT will implement it for you using
GWT deferred binding.

Example:

First you define the interface:

    import javax.ws.rs.POST;
    ...
    public interface PizzaOrderCodec extends JsonEncoderDecoder<PizzaOrder> {
    }

Then you use it as follows

    // GWT will implement the interface for you
    PizzaOrderCodec codec = GWT.create(PizzaOrderCodec.class);

    // Encoding an object to JSON
    PizzaOrder order = ...
    JSONValue json = codec.encode(order);

    // decoding an object to from JSON
    PizzaOrder other = codec.decode(json);

In case you need to parse the JSON from a `String` use

    JSONValue json = JSONParser.parseStrict( jsonString );

### Customizing the JSON Property Names

If you want to map a field name to a different JSON property name, you
can use the `@Json` annotation to configure the desired name. Example:

    public class Message {
        @Json(name="message-id")
        public String messageId;
    }

or with the Jackson annotation

    public class Message {
        @JSONProperty("message-id")
        public String messageId;
    }

### Using read-only objects

If you want to declare final fields and initialize an object through its constructor,
you can use the `@JsonCreator` and the `@JsonProperty` annotations. Example:

    public class Credentials {
        @JsonProperty
        private final String password;
        @JsonProperty
        private final String email;

        @JsonCreator
        public Credentials(@JsonProperty("email") String email, @JsonProperty("password") String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

Note that all the parameters of an annotated constructor should be annotated with the `@JsonProperty`.
As long as each property name matches the declared field names, the order can be freely chosen.

### Custom Serializer Generators

RestyGWT allows you to add another encode/decoder-generator which is used with certain classes. There are three simple steps for enabling fully customized serializer generation:
* Extend [`org.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator`](https://github.com/cponomaryov/resty-gwt/blob/master/restygwt/src/main/java/org/fusesource/restygwt/rebind/JsonEncoderDecoderClassCreator.java) to create an external serializer generator that can be registered to handle values of certain types. You should override the `generate()` method. Example: [`org.fusesource.restygwt.server.complex.OptionalSerializerGenerator`](https://github.com/cponomaryov/resty-gwt/blob/master/restygwt/src/test/java/org/fusesource/restygwt/server/complex/OptionalSerializerGenerator.java).
* Extend [`org.fusesource.restygwt.rebind.RestyJsonSerializerGenerator`](https://github.com/cponomaryov/resty-gwt/blob/master/restygwt/src/main/java/org/fusesource/restygwt/rebind/RestyJsonSerializerGenerator.java) and specify two things:
    * your custom serializer generator class created in the first step (`getGeneratorClass()` method)
    * target class or its superclass to use this generator for (`getType(TypeOracle)` method).
Example: [`org.fusesource.restygwt.server.complex.OptionalRestySerializerGenerator`](https://github.com/cponomaryov/resty-gwt/blob/master/restygwt/src/test/java/org/fusesource/restygwt/server/complex/OptionalRestySerializerGenerator.java).
* Specify your `RestyJsonSerializerGenerator` custom implementation fully qualified class name in the `org.fusesource.restygwt.restyjsonserializergenerator` configuration property in your GWT module XML file. This property is multi-valued so you can add multiple custom serializer generators. Example:

```xml
   <extend-configuration-property name="org.fusesource.restygwt.restyjsonserializergenerator"
               value="com.example.CustomRestySerializerGenerator1"/>
   <extend-configuration-property name="org.fusesource.restygwt.restyjsonserializergenerator"
               value="com.example.CustomRestySerializerGenerator2"/>
```

## Custom Annotation Handler

Due to the functionality of GWT generators, a generator needs to be responsible
for the whole generation processing of a particular type. To give RestyGWT users
the flexibility to extend this generation process, you can register custom
implementations of `org.fusesource.restygwt.rebind.AnnotationResolver`.


### Runtime Annotation Information

Whilst annotation-parsing and processing happens only in generation/compilation
process so far, `AnnotationResolver` are able to compile extra information into the
`org.fusesource.restygwt.client.RestService` implementation, which will be
available inside a `org.fusesource.restygwt.client.Dispatcher` and inside a
`com.google.gwt.http.client.RequestCallback`.

Transport of the Annotation information happens via
`org.fusesource.restygwt.client.Method#addData(String key, String value)`

The ability to use annotation information during runtime makes it really easy
to implement own annotations which can be applied to `RestService` definitions
and cause a customized runtime behaviour later on.

One use case of such an extension of the annotation parsing is the following:


### Model Change Events

Goal: the `Presenter` wants to be informed about changes in the model, so a
`reload` of some data can be automatically triggered, whenever a particular
`org.fusesource.restygwt.example.client.event.ModelChangeEvent` is caught.

To have a central location where such `ModelChangeEvents` are thrown, a
`RequestCallback` seems perfect. Usually -- without custom `AnnotationResolver`s
there would be no information about "what event for which domain type should be thrown".

Solution is to have a `org.fusesource.restygwt.rebind.ModelChangeAnnotationResolver`.
This class must be registered on RestyGWT's generation process in your `*.gwt.xml` via

        <set-configuration-property name="org.fusesource.restygwt.annotationresolver"
                value="org.fusesource.restygwt.rebind.ModelChangeAnnotationResolver"/>

Additionally assume the following `RestService` interface definition

    package org.fusesource.restygwt.client.event;

    import javax.ws.rs.GET;
    import javax.ws.rs.HeaderParam;
    import javax.ws.rs.PUT;
    import javax.ws.rs.Path;
    import javax.ws.rs.PathParam;

    import org.fusesource.restygwt.client.MethodCallback;
    import org.fusesource.restygwt.client.ModelChange;
    import org.fusesource.restygwt.client.RestService;

    import com.google.gwt.json.client.JSONValue;

    /**
     * @author andi
     */
    public interface ModelChangeAnnotatedService extends RestService {
        //...

        @PUT
        @Path("/foo/{fooId}")
        @ModelChange(domain=Foo.class)
        public void setItem(@HeaderParam("X-Echo-Code") int responseCode,
                @PathParam("fooId") int fooId, MethodCallback<Void> callback);
    }


Now we will have information about our custom annotation inside the
`Dispatcher`...

    public class CachingRetryingDispatcher implements Dispatcher {

        public Request send(Method method, RequestBuilder builder) throws RequestException {
            String[] DomainNameForUpdate = method.getData()
                    .get(ModelChange.MODEL_CHANGED_DOMAIN_KEY);
            // ...

... and inside the `RequestCallback`

        modelChangeAnnotatedService.setItem(Response.SC_CREATED, 1, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GwtEvent e = ModelChangeEvent.factory(method.getData()
                        .get(ModelChange.MODEL_CHANGED_DOMAIN_KEY));
                eventBus.fireEvent(e);
                // ...

A fully working example can be found in the integration-test:
`org.fusesource.restygwt.client.event.ModelChangeAnnotationTestGwt`.


## REST API

The RestyGWT REST API is handy when you don't want to go through the trouble of creating
service interfaces.

The following example, will post  a JSON request and receive a JSON response.
It will set the HTTP `Accept` and `Content-Type` and `X-HTTP-Method-Override` headers to the expected values.
It will also expect a HTTP 200 response code, otherwise it will
consider request the request is a failure.

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

Calling one of the above methods creates a `Method` object. You must create a new one
for each HTTP request. The `Method` object uses fluent API for easy building
of the request via method chaining. For example, to set the user id and password
used in basic authentication you would:

    method = resource.get().user("hiram").password("pass");

You can set the HTTP request body to a text, XML, or JSON value. Unless the content type
is explicitly set on the method, the following methods will set the `Content-Type` header
for you:

* `method.text(String data)`
* `method.xml(Document data)`
* `method.json(JSONValue data)`

The `Method` object also allows you to set headers, a request timeout, and the expected
'successful' status code for the request.

Finally, once you are ready to send the HTTP request, pick one of the following methods
based on the expected content type of the result. Unless explicitly set on the method,
the following methods will set the `Accept` header for you:

* `method.send(TextCallback callback)`
* `method.send(JsonCallback callback)`
* `method.send(XmlCallback callback)`

The response to the HTTP request is supplied to the callback passed in the `send` method.
Once the callback is invoked, the `method.getRespose()` method to get the GWT `Response`
if you are interested in things like the headers set on the response.

### Polymorphic Sub Types

A common feature used in Object Oriented languages is to use polymorphism to represent
several specialist versions of a parent object. This structure is simple to serialise
using GWT''s built-in RPC system as it represents the Java types via a specialist protocol.

Unfortunately the JSON spec doesn't directly support this behaviour. However it can be
added by including the type information in an extra JSON property. JSON parsers that are
not aware of this special behaviour will see it as a regular JSON property.

RestyGWT supports polymorphic sub-types via two of the Jackson features -- `@JsonSubTypes` and `@JsonTypeIdResolver`.
`@JsonSubTypes` is used when you have perfect knowledge of all possible subtypes at coding time. `@JsonTypeIdResolver`
can be used in situations where you want to classpath-scan or use some other dynamic mechanism to determine
the possible subtypes at application assembly (GWT compilation) time.

#### `@JsonSubTypes` Example

The Jackson library (a JSON parser) supports this behaviour by adding a series of annotations to the
POJO classes will be serialised to JSON. RestyGWT can also use these annotations
to provide the same Jackson compatible behaviour.


In the below example we have a zoo that contains various different types of animals, each
with their own specific properties. The super class that the other classes inherit must
be an abstract class and annotated like the example below:

    @JsonSubTypes({@Type(value=Dog.class, name="Dog"), @Type(Cat.class)})
    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@class")
    public abstract class Animal {
        protected Animal() { }
    }

The sub class uses the `@JsonTypeInfo` annotation to declare how the type information will
be included in the JSON. Only the `Name` and `Class` `use` methods are supported at present.
`Class` will use the full Java class name whereas `Name` will use a name provided by the user
to represent each type.

The `@JsonSubTypes` annotation provides a list of possible types which the JSON can be mapped
to. These must all be sub classes of this type or one of its sub classes. If the name method
is used the name can be optionally specified for each type. In the example above only the `Dog`
class name is specified.

    public class Dog extends Animal {
        public double barkVolume; // in decibels
        public Dog() { }
    }

    @JsonTypeName("Cat")
    public class Cat extends Animal {
        public boolean likesCream;
        public int lives;
        public Cat() { }
    }

The `@JsonTypeName` annotation can be used to specify the name the type will be mapped to. It must be
specified via this method or the `@JsonSubTypes` annotation on the super class. If both are
specified the one on the `@JsonSubTypes` is used.

Notice no additional information is needed to serialise the `Dog` class as all its information
is specified on the `Animal` super class.

    public class Zoo {
        public List<Animal> animals;
    }

Finally we can use the `Animal` class as we would if it was a single concrete class. When the
POJO is used in the GWT code you can use the animal list the same as you would with any
polymorphic list. Usually by using  the instanceof keyword and casting the object to the specific type.

#### `@JsonTypeIdResolver` Example

In this example, suppose we have an abstract class `Animal`, but we don't know until we have all of the
jars assembled for the final application just what kinds of animal the application will be supporting. Resty
can handle this situation like this:

    @JsonTypeIdResolver(my.project.AnimalResolver.class)
    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type")
    public abstract class Animal
    {
    }

You may have a proper implementation of `AnimalResolver` (which must implement `org.codehaus.jackson.map.jsontype.TypeIdResolver`)
for use by Jackson elsewhere in your system (e.g. on the server to write out these objects properly in response to
REST calls). However, because the `TypeIdResolver` interface does not offer an API that publishes all of the available subtypes, we
will need to implement an extension interface -- namely `RestyJsonTypeIdResolver`.

This is done as follows:

First, in your `*.gwt.xml`, instruct Resty to add your implementation to the available `RestyJsonTypeIdResolver`s available at compile time.

    <extend-configuration-property name="org.fusesource.restygwt.jsontypeidresolver" value="my.project.gwt.dev.AnimalRestyTypeIdResolver"/>

and then implement `AnimalRestyTypeIdResolver` as follows:

	public class AnimalRestyTypeIdResolver extends extends RestyJsonTypeIdResolver
    {
            @Override
	    public Class<? extends TypeIdResolver> getTypeIdResolverClass()
	    {
	        return AnimalResolver.class;
	    }

	    @Override
	    public Map<String, Class<?>> getIdClassMap()
	    {
		    // use classpath scanning or SPI or some other dynamic way to generate
		    // definitive map of subtypes and associated "tags"
	    }
    }

'AnimalRestyTypeIdResolver will be instantiated and interrogated at GWT compile time (deferred binding time, actually). This code will not be compiled itself into
JavaScript -- just used to generate JavaScript classes that will be used to serialize your Jackson annotated POJOs.

You can optionally implement `RestyJsonTypeIdResolver` on your `TypeIdResolver` class, in which case you do not need to include the configuration property setting.

### Reusing server interfaces.

If you're using JAX-RS on the server-side as well, you might notice that you're repeating yourself in the client and
server interfaces, with only a minor change for the client interface (since the callback must be asynchronous). So if
you would have a JAX-RS service that just lists files, implemented like this:

    public class ListFilesService
    {
        @Path("/files/list")
        @POST
        public List<FileVO> listFiles(@QueryParam("path") String path)
        {
            // actual logic to fetch the files.
        }
    }

Your corresponding RestyGWT interface would be:

    public interface ListFilesService extends RestService
    {
        @Path("/files/list")
        @POST
        public void listFiles(@QueryParam("path") String path, MethodCallback<List<FileVO>> callback);
    }

Now if on the server the interface would change, let's assume a new `filter` parameter would be added, your service call
will fail at runtime, without giving you a chance to know this API contract is now invalidated at compile time.

Using the new `REST.withCallback().call()` API this issue is addressed. How does it work?

First you need now an interface that will extend the marker interface `DirectRestService` (not simply `RestService`):

    public interface ListFilesService extends DirectRestService
    {
        @Path("/files/list")
        @POST
        public List<FileVO> listFiles(@QueryParam("path") String path);
    }

Secondly, your server will just implement it:

    public class ListFilesServiceImpl implements ListFilesService
    {
        @Override
        public List<FileVO> listFiles(String path)
        {
            // Logic to fetch the files. Annotations are being used from the interface, so there's no need to duplicate them here
        }
    }

And third, client side, you will just declare it:

        ListFilesService listFilesService = GWT.create(ListFilesService.class);

...and use it; since the calls are still async, you still need to provide a callback, and call it:

        List<FileVO> noop = REST.withCallback(new MethodCallback<List<FileVO>>() {
            // callback implementation.
        }).call(listFilesService).listFiles("/mypath");

Because the service is called asynchronously, the response returned by the `listFiles()` method -- from
`call(listFilesService).listFiles("/mypath")` -- will always be null, and in this example
is being assigned just to enforce the check by the compiler of the return type as well.

The parameters are the actual parameters sent to the service, and the response will be received by the callback.

Since the same interface is reused, this is extremely well fit to changes, including service path,
response type or parameters changes. In case the API changes, the compilation will break.

Thus you basically have now statically typed JSON services, with JAX-RS/Jackson on the server side, and RestyGWT on the
client side.
