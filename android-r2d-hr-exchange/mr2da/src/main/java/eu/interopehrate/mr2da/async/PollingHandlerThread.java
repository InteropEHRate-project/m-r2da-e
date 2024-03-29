package eu.interopehrate.mr2da.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class PollingHandlerThread extends HandlerThread {
    private Handler handler;
    private String eidasToken;
    private ResultsRetrieverHandlerThread retrieverThread;

    public PollingHandlerThread(String eidasToken, ResultsRetrieverHandlerThread retrieverThread) {
        super("R2D Polling Thread", Process.THREAD_PRIORITY_BACKGROUND);

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        this.eidasToken = eidasToken;
        this.retrieverThread = retrieverThread;
    }

    @Override
    protected void onLooperPrepared() {
        Log.d("MR2DA.PollingThread", "Creating polling Handler!");
        this.handler = new RequestPollingHandler(eidasToken, retrieverThread);
    }

    public Handler getHandler() {
        return this.handler;
    }

    public String getEidasToken() {
        return this.eidasToken;
    }
}
