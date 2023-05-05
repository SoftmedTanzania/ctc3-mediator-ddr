package tz.go.moh.him.esrs.mediator.ctc3.orchestrator;

import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.PutPropertyInCoreResponse;

public class RejectionsOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link RejectionsOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public RejectionsOrchestrator(MediatorConfig config) {
        super(config);
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest) {
            workingRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + workingRequest.getHost() + " " + workingRequest.getMethod() + " " + workingRequest.getPath());
            String body = ((MediatorHTTPRequest) msg).getBody();

            try {
                JSONObject payload = new JSONObject(body);
                String requestId = payload.getString("RequestID");
                ((MediatorHTTPRequest) msg).getRequestHandler().tell(new PutPropertyInCoreResponse("request_code", requestId), getSelf());
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendDataToCtc3(body, REQUEST_TYPE.REJECTIONS);
        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from CTC3");
            (workingRequest).getRequestHandler().tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
