package eu.interopehrate.mr2da.async;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Properties;

import ca.uhn.fhir.rest.api.Constants;
import eu.interopehrate.mr2da.MR2DAContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: executes the HTTP polling to monitor the status of an async request.
 *  When the request has been executed, it forward the request to the RequestResultHandler.
 */
public class RequestPollingHandler extends Handler {
    public static final int REQUEST_TERMINATED_HTTP_STATUS = 200;
    public static final int REQUEST_RUNNING_HTTP_STATUS = 202;

    public static final int ASYNC_REQUEST_TO_BE_MONITORED = 1000;
    public static final int NEW_REQUEST = 100;
    public static final int OLD_REQUEST = 200;

    private OkHttpClient client = new OkHttpClient();
    private String eidasToken;
    private Gson gson;
    private ResultsRetrieverHandlerThread retrieverThread;
    private int pendingRequestsSize = 0;
    private int pollingInterval;
    private int initialInterval;
    private int numMaxRetries = 10;
    private int consecutiveErrors;

    public RequestPollingHandler(String eidasToken, ResultsRetrieverHandlerThread retrieverThread) {
        super();

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        this.eidasToken = eidasToken;
        this.retrieverThread = retrieverThread;
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();

        // reads polling properties from config file
        final Properties pollingProps = MR2DAContext.INSTANCE.getPollingProperties();

        initialInterval = Integer.valueOf(pollingProps.getProperty("INITIAL_INTERVAL"));

        pollingInterval = Integer.valueOf(pollingProps.getProperty("POLLING_INTERVAL"));

        if (pollingProps.getProperty("POLLING_MAX_NUM_RETRIES") != null) {
            numMaxRetries = Integer.valueOf(pollingProps.getProperty("POLLING_MAX_NUM_RETRIES"));
        }

    }

    public int getPendingRequestSize() {
        return pendingRequestsSize;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what != ASYNC_REQUEST_TO_BE_MONITORED) {
            Log.e("MR2DA.PollingHandler", "Error: received message with wrong message type.");
            return;
        }

        // Retrieves the URL to monitor from the msg
        String monitoringURL = (String)msg.obj;
        if (msg.arg1 == NEW_REQUEST) {
            pendingRequestsSize++;
            Message delayedMsg = this.obtainMessage(ASYNC_REQUEST_TO_BE_MONITORED);
            delayedMsg.arg1 = RequestPollingHandler.OLD_REQUEST;
            delayedMsg.obj = msg.obj;
            sendMessageDelayed(delayedMsg, initialInterval);
            return;
        }

        try {
            // Creates the OKHttp request to poll the URL
           Request request = new Request.Builder()
                .url(monitoringURL)
                .get()
                .addHeader(Constants.HEADER_AUTHORIZATION,
                           Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + eidasToken)
                .build();

            // Submit the request...
            Response response = client.newCall(request).execute();
            consecutiveErrors = 0;
            switch (response.code()) {
                case REQUEST_RUNNING_HTTP_STATUS:
                    // Request is not finished yet
                    Log.d("MR2DA.PollingHandler", "Request is still running...");
                    Message delayedMsg = this.obtainMessage(ASYNC_REQUEST_TO_BE_MONITORED);
                    delayedMsg.arg1 = RequestPollingHandler.OLD_REQUEST;
                    delayedMsg.obj = msg.obj;
                    sendMessageDelayed(delayedMsg, pollingInterval);
                    break;
                case REQUEST_TERMINATED_HTTP_STATUS:
                    // Request terminated
                    Log.d("MR2DA.PollingHandler", "Request has terminated");
                    // #1 decreases request size
                    pendingRequestsSize--;
                    // #2 parse response body to retrieve the URL for getting the results
                    String body = response.body().string();
                    // Log.d("MR2DA", "body:" + body);
                    RequestOutcome outcome = gson.fromJson(body, RequestOutcome.class);
                    // Log.d("MR2DA", "outcome:" + outcome);
                    // #3 notify handler responsible to retrieve results
                    Message outMsg = retrieverThread.getHandler().obtainMessage();
                    outMsg.what = RequestResultHandler.ASYNC_REQUEST_TO_GET_RESULT;
                    outMsg.obj = outcome;
                    retrieverThread.getHandler().sendMessage(outMsg);
                    break;
                default:
                    Log.e("MR2DA.PollingHandler", "Error " + response.code() + " while polling R2DAccess server.");
            }
        } catch (Error | Exception e) {
            consecutiveErrors++;
            if (consecutiveErrors <= numMaxRetries) {
                Log.e("MR2DA.PollingHandler", "Error while polling request status, polling will continue", e);
                Message retryMsg = this.obtainMessage(ASYNC_REQUEST_TO_BE_MONITORED);
                retryMsg.arg1 = RequestPollingHandler.OLD_REQUEST;
                retryMsg.obj = msg.obj;
                sendMessageDelayed(retryMsg, pollingInterval);
            } else {
                Log.e("MR2DA.PollingHandler", "Connection is lost, request result could not " +
                        "be retrieved after several tries.", e);
                Message outMsg = retrieverThread.getHandler().obtainMessage();
                outMsg.what = RequestResultHandler.ASYNC_REQUEST_ON_NETWORK_ERROR;
                retrieverThread.getHandler().sendMessage(outMsg);
            }
        }
    }

}
