package eu.interopehrate.mr2da.async;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import ca.uhn.fhir.rest.api.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RequestPollingHandler extends Handler {
    public static final int REQUEST_TERMINATED = 200;
    public static final int REQUEST_RUNNING = 202;
    public static final int ASYNC_REQUEST_TO_BE_MONITORED = 1000;

    private OkHttpClient client = new OkHttpClient();
    private String eidasToken;
    private Gson gson;
    private ResultsRetrieverHandlerThread retrieverThread;

    // yyyy-MM-dd'T'HH:mm:ss.SSSZ
    public RequestPollingHandler(String eidasToken, ResultsRetrieverHandlerThread retrieverThread) {
        super();
        this.eidasToken = eidasToken;
        this.retrieverThread = retrieverThread;
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd@HH:mm:ss.SSSZ")
                .create();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what != ASYNC_REQUEST_TO_BE_MONITORED) {
            Log.e("MR2DA.PollingHandler", "Error: received message with wrong message type.");
            return;
        }
        // Retrieves the URL to monitor from the msg
        String monitoringURL = (String)msg.obj;

        // Creates the OKHttp request to poll the URL
        Request request = new Request.Builder()
                .url(monitoringURL)
                .get()
                .addHeader(Constants.HEADER_AUTHORIZATION,
                           Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + eidasToken)
                .build();

        try {
            // Submit the request...
            Response response = client.newCall(request).execute();
            switch (response.code()) {
                case REQUEST_RUNNING:
                    // Request is not finished yet
                    Log.d("MR2DA.PollingHandler", "Request is still running...");
                    Message delayedMsg = this.obtainMessage(ASYNC_REQUEST_TO_BE_MONITORED);
                    delayedMsg.obj = msg.obj;
                    sendMessageDelayed(delayedMsg, 8000);
                    break;
                case REQUEST_TERMINATED:
                    // Request terminated
                    Log.d("MR2DA.PollingHandler", "Request has terminated successfully!");
                    String body = response.body().string();
                    Log.d("MR2DA.PollingHandler", body);
                    RequestOutcome outcome = gson.fromJson(body, RequestOutcome.class);

                    // notify handler responsible to retrieve results
                    Message outMsg = retrieverThread.getHandler().obtainMessage();
                    outMsg.what = RequestResultHandler.ASYNC_REQUEST_TO_GET_RESULT;
                    outMsg.obj = outcome;
                    retrieverThread.getHandler().sendMessage(outMsg);
                    break;
                default:
                    Log.e("MR2DA.PollingHandler", "Error " + response.code() + " while polling R2DAccess server.");
            }
        } catch (IOException ioe) {
            Log.e("MR2DA.PollingHandler", "Error while polling request status!", ioe);
        }
    }

}
