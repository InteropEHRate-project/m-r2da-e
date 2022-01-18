package eu.interopehrate.mr2da.async;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;


public class AsyncHTTPClientInterceptor implements IClientInterceptor {
    private static final AsyncHTTPClientInterceptor INSTANCE = new AsyncHTTPClientInterceptor();

    public static AsyncHTTPClientInterceptor getInstance() {
        return INSTANCE;
    }

    //private AsyncRequest request;
    private PollingHandlerThread pollingThread;
    private AsyncHTTPClientInterceptor() {};

    public void setHandlerThread(PollingHandlerThread pollingThread) {
        this.pollingThread = pollingThread;
    }

    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        RequestPollingHandler handler = (RequestPollingHandler) pollingThread.getHandler();
        if (handler.getPendingRequestSize() < 3) {
            // Sets the Prefer header param for indicating asynchronous trx
            theRequest.addHeader(Constants.HEADER_PREFER, "respond-async");
            // sends a request to the R2DServer
            // request = new AsyncRequest(theRequest.getUri());
        } else
            throw new IllegalStateException("MR2DA.ClientInterceptor: cannot have more than 3 " +
                    "requests pending! Request can't be submitted");

    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        // if the return code is 200 the request has been served synchronously
        // and nothing has to be done
        if (theResponse.getStatus() == RequestPollingHandler.REQUEST_RUNNING_HTTP_STATUS) {
            Log.d("MR2DA.ClientInterceptor", "The previous request will be served asynchronously from the R2D Access Server...");
            List<String> contentLocations = theResponse.getHeaders("Content-Location");
            if (contentLocations == null || contentLocations.size() < 1)
                Log.e("MR2DA.ClientInterceptor", "Error: No Content-Location header found in response!");
            else {
                Log.d("MR2DA.ClientInterceptor", "Starting request monitoring...");
                // request.setMonitoringURL(contentLocations.get(0));
                Message msg = new Message();
                msg.what = RequestPollingHandler.ASYNC_REQUEST_TO_BE_MONITORED;
                msg.arg1 = RequestPollingHandler.NEW_REQUEST;
                msg.obj = contentLocations.get(0);
                pollingThread.getHandler().sendMessage(msg);
            }
        }
    }
}
