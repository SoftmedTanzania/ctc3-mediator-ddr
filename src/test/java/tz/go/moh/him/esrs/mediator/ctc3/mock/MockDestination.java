package tz.go.moh.him.esrs.mediator.ctc3.mock;


import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;

import java.util.Collections;
import java.util.Map;


/**
 * Represents a mock destination.
 */
public class MockDestination extends MockHTTPConnector {
    /**
     * serializer initialization
     */
    public JsonSerializer serializer = new JsonSerializer();


    public MockDestination() {

    }

    /**
     * Gets the response.
     *
     * @return Returns the response.
     */
    @Override
    public String getResponse() {
        return "";
    }

    /**
     * Gets the status code.
     *
     * @return Returns the status code.
     */
    @Override
    public Integer getStatus() {
        return 200;
    }

    /**
     * Gets the HTTP headers.
     *
     * @return Returns the HTTP headers.
     */
    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    /**
     * Handles the message.
     *
     * @param msg The message.
     */
    @Override
    public void executeOnReceive(MediatorHTTPRequest msg) {
        System.out.println("Received body : " + msg.getBody());
    }
}