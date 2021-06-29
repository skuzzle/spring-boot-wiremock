package de.skuzzle.springboot.test.wiremock;

@HttpStub(onRequest = @Request(toUrl = "/fromInterfaceCollection1"))
@HttpStub(onRequest = @Request(toUrl = "/fromInterfaceCollection2"))
public interface TestStubCollectionInterface {

}
