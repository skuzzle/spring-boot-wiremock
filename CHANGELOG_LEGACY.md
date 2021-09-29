
This changelog is no longer maintained. Follow the release notes at the GitHub releases for latest changes

## Changelog

### Version 0.0.14
* [Dependency] Update to WireMock 2.27.2

### Version 0.0.13
* Improve documentation
* [Change] Move stubbing annotations into their own package: `de.skuzzle.wiremock.test.stubs` (**breaking**)
* [Change] Deprecated `HttpStub.wrapAround` and introduced `HttpStub.onLastResponse` with new enum `WrapAround`
* [Add] New properties that will always be injected: `wiremock.server.http(s)Host`, `wiremock.server.http(s)Port`
* [Add] `WrapAround.REPEAT` which will repeat the last response on every subsequent request
* [Add] Allow to globally define required authentication via `WithWiremock.withGlobalAuthentication`

 
### Version 0.0.12
* Just some improvements to the build/release process

### Version 0.0.11
* Just some improvements to the build/release process

### Version 0.0.10
* [Fix] Readme
* [Change] Use latest WireMock version (`2.27.1`)

### Version 0.0.9
* [Add] Possibility to set a stub's priority
* [Add] Allow to define annotation stubs on inherited super classes and interfaces of the test class
* [Add] Allow to define annotation stubs using meta-annotated custom annotations
* [Fix] Possibility to place multiple stubs on the test class (missing `target = { ..., ElementType.TYPE }` on `HttpStubs`) 

### Version 0.0.8
* Allow to configure consecutive responses for the same request

### Version 0.0.7
* Compatibility to older Spring-Boot versions
* Remove note about Junit 5 being required. This library actually isn't tied to a specific testing framework 

### Version 0.0.6
* Improve JavaDoc
* Add automatic module name to jar manifest

### Version 0.0.5
* Improve JavaDoc
* Improve configuration consistency checks
* Allow `@HttpStub` on test class itself (instead of only on test method)
* Allow to set _status message_ on mock response
* Allow to configure WireMock _scenarios_ for stateful request matching using annotations

### Version 0.0.4
* Skipped by accident 🤡

### Version 0.0.3
* Renamed `SimpleStub` to `HttpStub` and split into multiple annotations
* `HttpStatus` enum is now used for defining the stubbed response status
* Match _any_ HTTP method by default (instead of _GET_)
* Allow to define different matchers for params, cookies, headers and body using prefixes like `eq:` or `containing:`

### Version 0.0.2
* Support multiple `@SimpleStub` instances per test method
* Allow to stub authentication and response headers via `@SimpleStub`
* Fix bug with unresolvable test keystore locations

### Version 0.0.1
* Initial prototype
