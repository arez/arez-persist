# Change Log

### [v0.12](https://github.com/arez/arez-persist/tree/v0.12) (2020-07-02) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.11...v0.12)

* Update the 'org.realityforge.arez' dependencies to version '0.184'

### [v0.11](https://github.com/arez/arez-persist/tree/v0.11) (2020-06-30) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.10...v0.11)

* Convert `Scope` into an arez component so that it supports the `arez.Disposable` interface rather than a custom mechanism for detecting disposal. The `Scope` also implements the `arez.component.DisposeNotifier` interface as a result of the conversion to an arez component. This makes it possible to annotated fields of type `Scope` with arezs `@CascadeDispsoe` and `@ComponentDependency` annotations.
* Upgrade the `org.realityforge.arez.testng` artifact to version `0.15`.

### [v0.10](https://github.com/arez/arez-persist/tree/v0.10) (2020-06-23) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.09...v0.10)

* Update the 'org.realityforge.arez' dependencies to version '0.183'
* Upgrade the `com.squareup` artifact to version `1.13.0`.

### [v0.09](https://github.com/arez/arez-persist/tree/v0.09) (2020-06-09) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.08...v0.09)

* Upgrade the `org.realityforge.arez.testng` artifact to version `0.14`.
* Update the implementation of `scheduleAttach` in the generated peers To skip creation of the sidecar if the peer or the scope has been disposed after the `scheduleAttach` was invoked but before the attach task runs.
* Add assertions to verify that neither a disposed peer nor a disposed scope is never passed to the `attach` or `scheduleAttach` methods on the generated peers.

### [v0.08](https://github.com/arez/arez-persist/tree/v0.08) (2020-06-07) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.07...v0.08)

* Update the 'org.realityforge.arez' dependencies to version '0.182'
* Upgrade the `org.realityforge.braincheck` artifact to version `1.29.0`.

### [v0.07](https://github.com/arez/arez-persist/tree/v0.07) (2020-06-03) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.06...v0.07)

* Upgrade the `org.realityforge.arez.testng` artifact to version `0.13`.
* Remove empty type objects when persisting to local and session storage.
* Update the `arez.persist.runtime.Scope` class to implement `arez.component.Identifiable` as an ugly hack to enable the `Scope` object to be used as an immutable prop in react4j components.
* Ensure that the sidecars do not attempt to persist or restore state if the associated scope is disposed.

### [v0.06](https://github.com/arez/arez-persist/tree/v0.06) (2020-05-29) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.05...v0.06)

* Update the 'org.realityforge.arez' dependencies to version '0.181'
* In the generated sidecar, stop explicitly setting the `@ArezComponent.requireEquals` parameter to `DISABLE` as that is the default value.
* In the generated sidecar, stop explicitly setting the `@ArezComponent.observable` parameter to `DISABLE` as that is the default value.

### [v0.05](https://github.com/arez/arez-persist/tree/v0.05) (2020-05-28) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.04...v0.05)

* Upgrade the `org.realityforge.proton` artifacts to version `0.51`.
* Upgrade the `org.realityforge.arez` artifacts to version `0.180`.
* Upgrade the `org.realityforge.arez.testng` artifact to version `0.12`.
* Simplify generated code for naming tasks to avoid triggering source analysis code warnings.
* Introduce a `@PersistId` annotation that can be used to identify an instance of a type.

### [v0.04](https://github.com/arez/arez-persist/tree/v0.04) (2020-05-26) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.03...v0.04)

* Use the namespace of the generated sidecar to define the name of task to schedule an attach to avoid collisions with names in the peer component.
* Suffix the task name with a monotonically increasing integer value to ensure that the task name is unique even when multiple attach tasks are schedule within the same transaction.

### [v0.03](https://github.com/arez/arez-persist/tree/v0.03) (2020-05-22) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.02...v0.03)

* Allow access to nested scopes by making the `Scope.getNestedScopes()` method public.

### [v0.02](https://github.com/arez/arez-persist/tree/v0.02) (2020-05-22) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.01...v0.02)

* Add a `scheduleAttach` static method to the generated sidecar will schedule the `attach` invocation using an arez `Task`. This is required when the component is potentially created within a read-only Arez transaction or a transaction that does not allow nested actions. This is particularly common when the peer is constructed insider a `@Memoize` annotated method.

### [v0.01](https://github.com/arez/arez-persist/tree/v0.01) (2020-05-21) · [Full Changelog](https://github.com/arez/arez-persist/compare/v0.00...v0.01)

 ‎🎉 Initial release ‎🎉
