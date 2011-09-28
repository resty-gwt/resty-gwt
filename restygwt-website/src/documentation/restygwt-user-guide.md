# RestyGWT User Guide

RestyGWT is a GWT generator for REST services and JSON encoded data transfer objects.

{:toc:2-5}


## Features

* Generates Async Restful JSON based service proxies
* Java Object to JSON encoding/decoding
* Easy to use REST API

## REST Services

RestyGWT Rest Services allow you to define an asynchronous Service API which is then implemented via
GWT deferred binding by a RestyGWT generator.  See the following listing for an example:

    import javax.ws.rs.POST;
    ...
    public interface PizzaService extends RestService {
        @POST
        public void order(PizzaOrder request, 
                          MethodCallback<OrderConfirmation> callback);
    }

JAX-RS annotations are used to control how the methods interface map to HTTP requests.  The 
interface methods MUST return void.  Each method must declare at least one callback argument.  
Methods can optionally declare one method argument before the callback to pass via the request
body.

### JAX/RS Subresource locators

RestyGWT supports JAX/RS subresource locator methods as synchronous methods on service interfaces.  For instance:

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
REST URL hierarchy.  Currently only @PathParam subresource locators are supported.

### JSON Encoding
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

The JSON encoding style is compatible with the default [Jackson](http://wiki.fasterxml.com/JacksonHome) Data Binding.  En example,
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

### Request Dispatchers

The request dispatcher intercepts all requests being made and can supply
additional features or behavior.  For example, RestyGWT also supports a 
`CachingRetryingDispatcher` which will automatically retry requests if 
they fail.

To configure the `CachingRetryingDispatcher`, you can configure it on
your service interface at either the class or method level.  Example:

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
`INSTANCE` field.  Example:

    public class SimpleDispatcher implements Dispatcher {
        public static final SimpleDispatcher INSTANCE = new SimpleDispatcher();

        public Request send(Method method, RequestBuilder builder) throws RequestException {
            return builder.send();
        }
    }

When the dispatcher´s ``send´´ method is called, the provided builder will already
be configured with all the options needed to do the request.

### Configuring the Request Timeout

You can use the @Options annotation to configure the timeout for requests
at either the class or method level.  The timeout is specified in milliseconds,
For example, to set a 5 second timeout:

        @POST
        @Options(timeout=5000)
        public void order(PizzaOrder request, 
                          MethodCallback<OrderConfirmation> callback);


### Configuring the Expected HTTP Status Code

By default results that have a 200, 201, or 204 HTTP status code are considered
to have succeeded. You can customize these defaults by setting the 
@Options annotations at either the class or method level of the service interface.

Example:

        @POST
        @Options(expect={200,201})
        public void order(PizzaOrder request, 
                          MethodCallback<OrderConfirmation> callback);

### Configuring the `Accept` and `Content-Type` and HTTP Headers

RestyGWT rest calls will automatically set the `Accept` and `Content-Type` 
and HTTP Headers to match the type of data being sent and the type of
data expected by the callback, you you can override these default values
by adding JAXRS `@Produces` and `@Consumes` annotations to the method 
declaration.

### Keep the Java Interface clean

If you need an attribute of your DTO to be part of the url you can do it by adding @Attribute annotation along side the @PathParam. the @PathParam references the placeholder in the @Path as usual and the @Attribute identifies the attribute/field of the DTO which shall be used for the replacement in the path.

Example:

        @PUT
        @Path("/{id}")
        public void updateOrder(@PathParam("id") @Attribute("order_id") OrderConfirmation order, 
                                MethodCallback<OrderConfirmation> callback);

### Mapping to a JSONP request

If you need to access JSONP URl, then use the @JSONP annotation on the method
for example:

    import org.fusesource.restygwt.client.JSONP;
    ...
    public interface FlickrService extends RestService {  
        @Path("http://www.flickr.com/services/feeds/photos_public.gne?format=json")
        @JSONP(callbackParam="jsonFlickrFeed")
        public void photoFeed(JsonCallback callback);    
    }

or if you have a **FlickrFeed** value object

    import org.fusesource.restygwt.client.JSONP;
    ...
    public interface FlickrService extends RestService {  
        @Path("http://www.flickr.com/services/feeds/photos_public.gne?format=json")
        @JSONP(callbackParam="jsonFlickrFeed")
        public void photoFeed(MethodCallback<FlickrFeed> callback);    
    }


## JSON Encoder/Decoders
    
If you want to manually access the JSON encoder/decoder for a given type just define
an interface that extends JsonEncoderDecoder and RestyGWT will implement it for you using
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

    // Encoding an object to json
    PizzaOrder order = ... 
    JSONValue json = codec.encode(order);

    // decoding an object to from json
    PizzaOrder other = codec.decode(json);

### Customizing the JSON Property Names 

If you want to map a field name to a different json property name, you
can use the `@Json` annotation to configure the desired name.  Example:

    public class Message {
        @Json(name="message-id")
        public String messageId;
    }

### Using readonly objects

If you want to declare final fields and initialize an object through its constructor, 
you can use the @JsonCreator and the @JsonProperty annotations. Example:

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

Note that all the paramaters of an annotated constructor should be annotated with the @JsonProperty. 
As long as each property name matches the declared field names, the order can be freely chosen.

## Custom Annotation Handler

Due to the functionality of GWTs generators, a generator needs to be responsible
for the whole generation processing of a particular type. To give RestyGWT users
the felxibility to extend this generation process, you can register custom
implementations of ``org.fusesource.restygwt.rebind.AnnotationResolver``.


### Runtime Annotation Information

Whilst annotation-parsing and processing happens only in generation/compilation 
process so far,``AnnotationResolver`` are able to compile extra information into the 
``org.fusesource.restygwt.client.RestService`` implementation, which will be 
available inside a ``org.fusesource.restygwt.client.Dispatcher`` and inside a
``com.google.gwt.http.client.RequestCallback``.

Transport of the Annotation information happens via 
``org.fusesource.restygwt.client.Method#addData(String key, String value)``

The ability to use annotation information during runtime makes it really easy
to implement own annotations which can be applied to RestService definitions
and cause a customized runtime behaviour later on.

One usecase of such an extension of the annotation parsing is the following:


### Model Change Events

Goal: the Presenter wants to be informed about changes in the model, so a
``reload`` of some data can be automatically triggered, whenever a particular
``org.fusesource.restygwt.example.client.event.ModelChangeEvent`` is catched.

To have a central location where such ``ModelChangeEvents`` are thrown, a 
``RequestCallback`` seems perfect. Usually - without custom ``AnnotationResolver``s
there would be no information about "what event for which domain type should be thrown".

Solution is to have a ``org.fusesource.restygwt.rebind.ModelChangeAnnotationResolver``.
This class must be registered on RestyGWTs generation process in your ``*.gwt.xml`` via 

        <set-configuration-property name="org.fusesource.restygwt.annotationresolver"
                value="org.fusesource.restygwt.rebind.ModelChangeAnnotationResolver"/>

Additionally assume the following RestService interface definition

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
``Dispatcher`` ...

    public class CachingRetryingDispatcher implements Dispatcher {

        public Request send(Method method, RequestBuilder builder) throws RequestException {
            String[] DomainNameForUpdate = method.getData()
                    .get(ModelChange.MODEL_CHANGED_DOMAIN_KEY);
            // ...

... and inside the ``RequestCallback``

        modelChangeAnnotatedService.setItem(Response.SC_CREATED, 1, new MethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void response) {
                GwtEvent e = ModelChangeEvent.factory(method.getData()
                        .get(ModelChange.MODEL_CHANGED_DOMAIN_KEY));
                eventBus.fireEvent(e);
                // ...

A fully working example can be found in the integration-test: 
``org.fusesource.restygwt.client.event.ModelChangeAnnotationTestGwt``.


## REST API

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

### Polymorphic Sub Types

A common feature used in Object Orientated languages is to use Polymorphism to represent
several specialist version of a parent object. This is structure is simple to serialise 
using GWT''s built-in RPC system as it represents the Java types via a specialist protocol.

Unfortunately the JSON spec doesn't directly support this behaviour. However it can be
added by including the type information in an extra JSON property. JSON parsers that are
not aware of this special behaviour will see it as a regular JSON property.

RestyGWT supports polymorphic sub-types via two of the Jackson features - `@JsonSubTypes` and `@JsonTypeIdResolver`.  
`@JsonSubTypes` is used when you have perfect knowledge of all possible subtypes at coding time.  `@JsonTypeIdResolver` 
can be used in situations where you want to classpath-scan or use some other dynamic mechanism to determine
the possible subtypes at application assembly (GWT compilation) time.

#### `@JsonSubTypes` Example

The Jackson (a JSON parser) supports this behaviour by adding a series of annotations to the
POJO classes will be serialised to JSON. Resty-GWT supports can also use these annotations 
to provide the same behaviour Jackson compatible behaviour. 


In the below example have a zoo that contains various different types of animals, each
with their own specific properties. The super class that the other classes inherit must 
be an abstract class and annotated like the example below:

    @JsonSubTypes({@Type(value=Dog.class, name="Dog"), @Type(Cat.class)})
    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@class")
    public abstract class Animal { 
        protected Animal() { }
    }

The sub class uses the JsonTypeInfo annotation to declare how the type information will
be included in the JSON. Only the Name and Class "use" methods are supported at present.
Class will use the full Java class name where as Name will use a name provided by the user
to represent each type.

The JsonSubTypes annotation provides a list of possible types which the JSON can be mapped
to. These must all be sub classes of this type or one of its subclasses. If the name method
is used the name can be optionally specified for each type. In this example only the Dog 
classes name is specified.

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

The JsonTypeName can be used to specify the name the type will be mapped to. It must be
specified via this method or the JsonSubTypes annotation on the super class. If both are
specified the one on the JsonSubTypes is used.

Notice no additional information is needed to serialise the Dog class as all its information
is specified on the Animal supper class.

    public class Zoo {
        public List<Animal> animals;
    }

Finally we can use the animal class as we would if it was a single concrete class. When the
POJO is used in the GWT code you can use the animals list the same as you would with any
polymorphic list. Usually by using  the instanceof keyword and casting the to the specific type.

#### `@JsonTypeIdResolver` Example

In this example, suppose we have an abstract class `Animal`, but we don't know until we have all of the
jars assembled for the final application just what kinds of animal the application will be supporting.  Resty
can handle this situation like this:

    @JsonTypeIdResolver(my.project.AnimalResolver.class)
    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type")
    public abstract class Animal
    {
    }

You may have a proper implementation of `AnimalResolver` (which must implement `org.codehaus.jackson.map.jsontype.TypeIdResolver`)
for use by Jackson elsewhere in your system (e.g. on the server to write out these objects properly in response to 
REST calls).  However, because the `TypeIdResolver` interface does not offer api that publishes all of the available subtypes, we
will need to implement an extension interface - namely RestyJsonTypeIdResolver.

This is done as follows:

First, in your *.gwt.xml, instruct Resty to add your implementation to the available RestyJsonTypeIdResolvers available at compile time.

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

'AnimalRestyTypeIdResolver will be instantiated and interrogated at GWT compile time (deferred binding time, actually).  This code will not be compiled itself into
javascript - just used to generate javascript classes that will be used to serialize your Jackson annotated pojos.

You can optionally implement RestyJsonTypeIdResolver on your TypeIdResolver class, in which case you do not need to include the configuration property setting.