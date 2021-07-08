package de.skuzzle.springboot.test.wiremock;

import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;
import de.skuzzle.springboot.test.wiremock.stubs.Request;

@HttpStub(onRequest = @Request(toUrl = "/fromInterfaceCollection1"))
@HttpStub(onRequest = @Request(toUrl = "/fromInterfaceCollection2"))
public interface TestStubCollectionInterface {

}
