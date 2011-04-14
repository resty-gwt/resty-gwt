RestyGWT
========

[resty-gwt 1.1]
---------------
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

[resty-gwt 1.0](http://restygwt.fusesource.org/blog/releases/2010/09/release-1-0.html), released 2010-09-27
----------------------------------

* Initial Release Pending

