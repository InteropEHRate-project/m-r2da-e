package eu.interopehrate.mr2da.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import eu.interopehrate.mr2da.api.MR2DACallbackHandler;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class ResultsRetrieverHandlerThread extends HandlerThread {
    private Handler handler;
    private String eidasToken;
    private MR2DACallbackHandler callbackHandler;

    public ResultsRetrieverHandlerThread(String eidasToken, MR2DACallbackHandler callbackHandler) {
        super("R2D Result Retriever Thread", Process.THREAD_PRIORITY_BACKGROUND);

        if (eidasToken == null || eidasToken.trim().isEmpty())
            throw new IllegalArgumentException("Provided auth token is empty.");

        this.eidasToken = eidasToken;
        this.callbackHandler = callbackHandler;
    }

    @Override
    protected void onLooperPrepared() {
        this.handler = new RequestResultHandler(eidasToken, callbackHandler);
    }

    public Handler getHandler() {
        return this.handler;
    }

    public String getEidasToken() {
        return this.eidasToken;
    }
}
