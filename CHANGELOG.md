# Change Log

### Unreleased

* Upgrade the `org.realityforge.arez` artifacts to version `0.180`.
* Upgrade the `org.realityforge.arez.testng` artifact to version `0.12`.
* Simplify generated code for naming tasks to avoid triggering source analysis code warnings.

### [v0.04](https://github.com/arez/arez-testng/tree/v0.04) (2020-05-26) · [Full Changelog](https://github.com/arez/arez-testng/compare/v0.03...v0.04)

* Use the namespace of the generated sidecar to define the name of task to schedule an attach to avoid collisions with names in the peer component.
* Suffix the task name with a monotonically increasing integer value to ensure that the task name is unique even when multiple attach tasks are schedule within the same transaction.

### [v0.03](https://github.com/arez/arez-testng/tree/v0.03) (2020-05-22) · [Full Changelog](https://github.com/arez/arez-testng/compare/v0.02...v0.03)

* Allow access to nested scopes by making the `Scope.getNestedScopes()` method public.

### [v0.02](https://github.com/arez/arez-testng/tree/v0.02) (2020-05-22) · [Full Changelog](https://github.com/arez/arez-testng/compare/v0.01...v0.02)

* Add a `scheduleAttach` static method to the generated sidecar will schedule the `attach` invocation using an arez `Task`. This is required when the component is potentially created within a read-only Arez transaction or a transaction that does not allow nested actions. This is particularly common when the peer is constructed insider a `@Memoize` annotated method.

### [v0.01](https://github.com/arez/arez-testng/tree/v0.01) (2020-05-21) · [Full Changelog](https://github.com/arez/arez-testng/compare/v0.00...v0.01)

 ‎🎉 Initial release ‎🎉
