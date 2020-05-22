# Change Log

### Unreleased

* Add a `scheduleAttach` static method to the generated sidecar will schedule the `attach` invocation using an arez `Task`. This is required when the component is potentially created within a read-only Arez transaction or a transaction that does not allow nested actions. This is particularly common when the peer is constructed insider a `@Memoize` annotated method.

### [v0.01](https://github.com/arez/arez-testng/tree/v0.01) (2020-05-21) · [Full Changelog](https://github.com/arez/arez-testng/compare/v0.00...v0.01)

 ‎🎉 Initial release ‎🎉
