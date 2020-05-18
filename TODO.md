# TODO

This document is essentially a list of shorthand notes describing work yet to be completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

* Figure out a way for Sting/Dagger/other factory created components can integrate with the sidecar infrastructure.

* Determine if we want to support custom persistent ids divorced from arez component ids. This would be required
  to integrate the react4j and other frameworks that manage their own component model. If we wanted to support
  this we would likely need to add a `@PersistId` method annotation to allow the component to return the id under
  which state should be stored.

* Determine if we need an arez/other framework convention on how to copy type annotations to implementation
  classes. i.e. We could change the way we handle `@PersistType` so that we ignore it on `@ActAsComponent`
  types and react4j and other frameworks that generate `@ArezComponent` know to copy it down ... somehow.
  An annotation on the annotation seems likely the solution. This would make all sidecar solutions a lot
  easier to implement.
