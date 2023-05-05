package tz.go.moh.him.esrs.mediator.ctc3.orchestrator;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseOrchestrator extends UntypedActor {
    /**
     * The serializer
     */
    public static final JsonSerializer serializer = new JsonSerializer();
    /**
     * The logger instance.
     */
    public final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    /**
     * The mediator configuration.
     */
    protected final MediatorConfig config;
    /**
     * Represents a mediator request.
     */
    protected MediatorHTTPRequest workingRequest;
    protected JSONObject errorMessageResource;

    /**
     * Initializes a new instance of the {@link BaseOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public BaseOrchestrator(MediatorConfig config) {
        this.config = config;
        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        try {
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */

    /**
     * Handle sending of data to esrs
     *
     * @param msg to be sent
     */
    public void sendDataToCtc3(String msg, REQUEST_TYPE requestType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        String host;
        String path;
        int portNumber;

        JSONObject connectionProperties;
        if (requestType == REQUEST_TYPE.RESULTS) {
            connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("resultsDestinationConnectionProperties");
        } else {
            connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("rejectionsDestinationConnectionProperties");
        }


        if (!connectionProperties.getString("destinationUsername").isEmpty() && !connectionProperties.getString("destinationPassword").isEmpty()) {
            String auth = connectionProperties.getString("destinationUsername") + ":" + connectionProperties.getString("destinationPassword");
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.ISO_8859_1));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.put(HttpHeaders.AUTHORIZATION, authHeader);
        }

        host = connectionProperties.getString("destinationHost");
        portNumber = connectionProperties.getInt("destinationPort");
        scheme = connectionProperties.getString("destinationScheme");
        path = connectionProperties.getString("destinationRequestPath");

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToEsrs = new MediatorHTTPRequest(
                (workingRequest).getRequestHandler(), getSelf(), "Sending request data to esrs", "POST", scheme,
                host, portNumber, path, msg, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToEsrs, getSelf());
    }

    public enum REQUEST_TYPE {
        RESULTS,
        REJECTIONS
    }


}
