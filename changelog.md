# ![RestyGWT](http://restygwt.fusesource.org/images/restygwt-logo.png)

## [resty-gwt 1.4](http://restygwt.fusesource.org/blog/release-1-4.html), 2013-12-17

* Support enum subtypes by serializing enums implementing an interface with a complex object containing the class name and the value. Issue #164 + Make sure we look up
* Added a test exposing a bug in the generated serializers for enums implementing interfaces with @JsonSubTypes. Issue #164
* Added test and solution for jsonsubtypes with interfaces, does not work with enums yet. Issue #164
* Added support for decoding long strings as proper long values in restygwt, added a testcase verifying the behaviour. Long numbers (not strings) will remain an issue 
* Fixed: Cannot compile generated JsonEncoderDecoder with certain type hierarchy
* Fixed regression where field type is more specific than constructor argument, issue #162.
* getField can throw assertion error if you ask for super types, use the field matched instead for fields from super types
* Set needNullHandling to false only in case of custom generator and true otherwise
* Added Custom Serializer Generators
* RestyGWT now supports normal inheritance, subtypes can be listed recursively.
* cherry picked commit 569b812 of PR #149 and adjusted mock-testcase
* fix #156
* fix naming for inner classes #158
* get more test working or clearly comment out tests in suite
* use rails3-maven-plugin which works with maven-3.1.x as well
* allow Attribute annotation expression to return null value
* getters and setters do not need a matching field. @JsonIgnore works on fields or the setter or the getter
* encode/decode nested arrays, lists, sets and maps as fields or property of a pojo
* move all GWT.lo to java.util.logging.Logger
* send enum null values as null when defined as pojo field #154
* adjusted contribution part and issue tracker to be in line with how we are doing it
* make the URLencode working with null values as well
* reduce noise during test run
* white spaces
* url encode PathParam -  issue #147
* Fix typos, improve consistency
* some white space changes
* Refactor the getPossibleType method in JsonEncoderDecoderClassCreator. I created a kind of JsonTypeInfo.Id visitor that can be reused for other matters. I extracted the code from the JsonEncoderDecoderClassCreator to put it in the visitor.
* Split the HUGE generate method into different methods.
* Fix Eclipse web.xml validation
* Support default value for @JsonTypeInfo property
* Revert "Default @JsonTypeInfo Id.CLASS property value"
* Revert "Add support for JsonDeserialize for interfaces"
* Add support for JsonDeserialize for interfaces
* Default @JsonTypeInfo Id.CLASS property value
* fixed mapping for @JsonTypeInfo - MINIMAL_CLASS (. as prefix); added support for discovering all subtypes of generic type during the generation process, thus no need to use the @JsonSubTypes annotation in combination with @JsonTypeInfo - CLASS or MINIMAL_CLASS
* added a note on hwo to parse jsonString and about JsonProperty to customize names
* added test case for decoding/encoding nested Maps
* tests for methods returning Request or JsonpRequest
* let RestService interface methods return either JsonpRequest or Request
* Fixed 204 not handled roperly for MSIE 8 and 9
* allow getters and setters corresponding field
* added generic list encoder - start to nest collection
* Reduced loglevel for type encoder information to debug
* Fixed a bug in serializing lists and arrays using @FormParams.
* some jsonp test
* make the resource creation lazy inside restService impl - #114
* Support FormParams. Fixes issue #92.
* fix encoding of BigDecimal keys of maps (#111)
* fixed issue with short decoding, cast requred (#106)
* parse strings of generic map keys correctly
* let jsonp handle list of POJOs correctly
* let rest-service use default dispatcher from Defaults when none is set on interface
* Take dispatcher from Defaults if local one for the Method is not set.
* get the generated ode compile
* added missing annotation
* discriminate Lists on JSONP results
* Revert "discriminate JSONArray on JSONP results"
* discriminate JSONArray on JSONP results
* fixed #108 - allow to ignore any fields in the classhierachy
* fixed map decode with complex and number keys
* Documented DirectRestService usage in the user documentation.
* added test for QueryParam
* add test case for @PathParams
* remove yellow flag from eclipse
* fix regression issue #102 : but expression insde brackets to allow further arithemitek to keep expression as is.

## [resty-gwt 1.3](http://restygwt.fusesource.org/blog/release-1-3.html), 2012-08-23

* Added additional tests to ensure ObjectEncoderDecoder is not left out again
* fix  Issue #84: allow null values in collections of pojo and primitives
* XSRFTokenCallbackFilter didn't set the XSRF token correctly in IE9
* allow jsonignore to omit fields for de/serialization - issue #30
* added test for arrays de/encoding
* added support for one-dimensional arrays
* go through ALL callbacks even in case of error
* restful caching shall not cache collection
* fix for #31
* redone reverted patch from StephanBeutel against latest master branch
* new invoker plugin version
* use gwt 2.4.0
* added testcase for passing on the headers when getting GET-method, etc
* remove debug System.out.println
* added testcase for issue #84
* move easymock tests to mokcing package
* remove obsolete files
* since implementation changed we need an extra call
* remove unwanted artifact prefix from previous commit
* obey failed status codes as without cache.
* use getter/setter for token to allow subclassing
* allow null values for primitives and fill use the default values in such cases
* remove yellow flags in eclipse, i.e. cleanup imports, deprectated method, suppress warnings
* remove error in eclipse indigo complaining about not supported goal ... from pullrequest #85
* Add support for numbers and booleans to be decoded to strings
* Removed restriction on @Attribute for sub resource locators
* Allow null to @QueryParam arguments
* pass on headers and query when resolving the path on a Resource
* Make ObjectEncoderDecoder an AbstractEncoderDecoder
* Revert "Merge branch 'casting'"
* Merge branch 'casting'
* added JsonIgnore annotation; rearranged the Object encoder/decoder to be properly registered
* slight change to accommodate situations where casting signature differs
* Added support for casting of RestService interfaces.
* some tests for #72
* closed #72 - initial implementation of Object encoding/decoding
* be less strict on wrapper objects

## [resty-gwt 1.2](http://restygwt.fusesource.org/blog/release-1-2.html), 2011-10-08

* JsonTypeIdResolver Support - Added support for Array wrapping
* Support for generic interfaces and DTO serialization
* Implementation of JAX-RS subresource locators with @PathParam annotations - closes #56
* Use abstract baseclasses for the subtype tests
* Allow a single subtype with WrapperObject declaration as well
* Fix issue#47 with BigInteger roundtrip encoding
* Add support for As.WRAPPER_OBJECT subtype declarations
* Bugfix for generic lists (see changes) adapted pull request #50 from pansen:bugfix-generic-lists
* Add support for @JsonCreator with polymorphic types
* Added accessors to RestServiceProxy
* Added integration test for rails: json and xsrf protection
* add wrapper to json when style == RAILS
* obey typeInfo of super class
* share the xsrf token with all dispatchers from the factory
* do not json-encode/decode transient fields
* fixed bug in XSRF protection and follow the header name to RubyOnRails default
* added file system support for isExpected
* added extra header when response get cached, i.e. callback filters can use it
* calculate uri in case it comes only the relative path as location header
* added dispatcher factory with lots of samples
* added XSS protection filters
* refactored CachingCallbackFilter so it could be extended by RestfulCachingCallbackFilter
* having a default callback without retrying and one with retrying
* having a default without retry and a non-filtering callback factory and finally a retrying factory
* with ignorance skip the factory and use "new" instead
* filter without caching - can be use as last in the filter chain
* refactored CachingDispatcherFilter so it could be extended to RestfulCachingDispatcherFilter
* changed UrlCacheKey and move the old one to ComplexCacheKey
* get default of INSTANCE of FilterawareRetryingDispatcher in place
* INSTANCE needs to be set so you can use it with @Options annotations from a RestService
* get async semantic back on response servered from cache
* allow part of the arg-object participate in as PathParam and the object goes over the wire
* use date format string for encoding if set
* allow jsonp call with custom value objects
* Do not set headers for JSONP methods.
* Allow collection interfaces as service argument and fix bug with List impl as argument
* Added parameter to pass a HTTP header map to a Resource
* Use parseStrict instead of just parse
* Bugfix for generic lists.
* Add support for Sending Lists and Sets as repeated parameters.
* Add support for byte data type
* Added JSON support for Unix timestamps and null values.
* create ``Domain`` annotation to be able to identify the information about what domain
  this service affects later on. this can be used to invalidate caching entries by domain. 
* after unsuccessful retrying we will not call ``window.alert`` if there is a callback
  available. instead use onError in this case. 
* no retrying behaviour on 301, 302 or 404, fallback to error handler in this case directly
* call onError in org.fusesource.restygwt.client.callback.FilterawareRetryingCallback
  when there is a !GET error request
* add scoped QueueableRuntimeCache to be able to invalidate a particular ident, e.g.
  a domain scope.
* add definition for org.fusesource.restygwt.annotationresolver
  @see http://code.google.com/p/google-web-toolkit/wiki/MultiValuedConfigProperties
* add ability to take part of the compilation process by registering 
  ``org.fusesource.restygwt.rebind.AnnotationResolver`` in 
  ``org.fusesource.restygwt.rebind.BindingDefaults``
* add some example for triggering ModelChangeEvent from a MethodCallback

## [resty-gwt 1.1](http://restygwt.fusesource.org/blog/release-1-1.html), released 2010-03-10

* have the integration tests use the same gwt version as the main project. (Hiram Chirino)
* Added a download profile to find recent scala artifacts. (Hiram Chirino)
* Update the scalate library and its build dependencies. (Jon Buffington)
* Support GWT release 2.2.0. (Jon Buffington)
* Remove execute bits from license text file. (Jon Buffington)
* in case the response gets served from the cache keep the async-nature of the call (kristian)
* allow MethodCallback<Void> in RestService (kristian)
* Convert tabs to spaces. (Hiram Chirino)
* restygwt-25 changed toString() to name() (jroyals)
* added some docu for the attribute annotation (kristian)
* Added: Jackson Annotations which are used by the polymorphic api. They are Apache 2.0 licensed so shouldn't cause a problem form that point of view. (Charlie Mason)
* Added: Polymorphic Sub Classes to the user guide. (Charlie Mason)
* Fixed: Merge conflict with recent upstream changes. (Charlie Mason)
* Added: Added Jackson style polymorphic serialisation and deserialisation. (Charlie Mason)
* Look for @Produces/@Consumes on method and if not found, then look on methods enclosing type. (Jason Dillon)
* Fix compile error. (Hiram Chirino)
* use htmlunit so the integration tests are actually running via invoker (kristian)
* annotate an attribute from a resource to used as PathParam (kristian)
* Added the ability for boolean setters to not only be formatted as isFoo(), but getFoo() and hasFoo(). (jlarsen)
* Fix typo. Also trying to fix. #19 (Hiram Chirino)
* Fix sp (Jason Dillon)
* Fixes issue #16 : RequestException does not handle server 500 responses (Hiram Chirino)
* rename doAction to send. (Hiram Chirino)
* Build the website by default. (Hiram Chirino)
* Fixed autogenned license header (jlarsen)
* Fixed a bug with the RestServicClassCreator where you couldn't have a chained hierarchy of services and added RestActions that will map to the various rest actions available and allow us to inject the RestActions into our application. (jlarsen)
* Better resource path resolution.. (Hiram Chirino)
* More doco. (Hiram Chirino)
* Simplify dispatcher interface slightly. (Hiram Chirino)
* tweaking deployment location. (Hiram Chirino)
* converted static website to be scalate based. (Hiram Chirino)
* Added a @JSONP annotation that can be used to mark methods as using the JSONP protocol. (Hiram Chirino)
* Allow configuring the timeout via @Options (Hiram Chirino)
* Allow configuring the expected status via the Option annotation. (Hiram Chirino)
* allowing the dispatcher to be configured on service interfaces. (Hiram Chirino)
* added missing license headers - Simplified the Dispatcher interface, eliminated the factory since they were easy to convert to stateless singletons. - Dispatcher can now be configured at a per method leve (Hiram Chirino)
* remove the fork notes. (Hiram Chirino)
* pom updates. (Hiram Chirino)
* Introduction of a dispatcher, generated by a DispatcherFactory - Dispatcher can be exchanged by 3rd party code easily => Custom caching rules become possible - The dispatcher also is responsible for the callback used => an implementation of a retrying Callback is supplied. - The Dispatcher is fetched and used in Method.send() - Automatic retrying of callbacks (often connections are flaky) (rbauer)
* factored out the common part of the integration-tests into a parent pom (kristian)
* moved internal maven properties which are recommended by maven3 (kristian)
* Use ${project.* instead of deprecated (Jason Dillon)
* Normalize Resource.path to *not* trail with a "/" so that when resolving relative resource, we can safely insert a "/" separator. This should handle @Path on type + @Path on method better w/o relying on the @Path to add "/". (Jason Dillon)
* Make sure service root always ends with a "/" (Jason Dillon)
* Make sure service root always ends with a "/" (Jason Dillon)
* added support for @Consumes @Produces annotations. (Hiram Chirino)
* basic setup for tests (rbauer)
* added ignores for usage with eclipse (rbauer)
* added - versioning for gwt and gwt-maven-plugin - 1.6 jdk (rbauer)
* back to original (Raphael Bauer)
* back to original name (rbauer)
* renamed servlet => to avoid naming conflicts with maven surfire plugin. The servlet is not "test" after all... (rbauer)
* typo (rbauer)
* formatting issue (rbauer)
* added igores for usage with eclipse (rbauer)
* added - versioning for gwt and gwt-maven-plugin - 1.6 jdk (rbauer)
* basic setup for tests (rbauer)
* Correctly Serialize JsArrays (Kyle Butt)
* Add support for more than just 200 as a successful return code. (Kyle Butt)
* Add support for receiving a Json Array as an Overlay Object (Kyle Butt)
* fixes issue 13 : Allow customization of the JSONP callback parameter (Hiram Chirino)
* fixes issue 13 : Allow customization of the JSONP callback parameter (Hiram Chirino)
* Fix JSONNull handling in decode (JRoyals)

## [resty-gwt 1.0](http://restygwt.fusesource.org/blog/release-1-0.html), released 2010-09-27

* Initial Release
