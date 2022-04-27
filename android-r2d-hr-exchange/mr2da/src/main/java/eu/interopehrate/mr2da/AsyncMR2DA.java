package eu.interopehrate.mr2da;

import org.hl7.fhir.r4.model.Bundle;

import java.net.URL;

import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import eu.interopehrate.mr2da.api.MR2DACallbackHandler;
import eu.interopehrate.mr2da.async.PollingHandlerThread;
import eu.interopehrate.mr2da.async.ResultsRetrieverHandlerThread;
import eu.interopehrate.mr2da.fhir.SingleQueryExecutor;
import eu.interopehrate.mr2da.async.AsyncHTTPClientInterceptor;
import eu.interopehrate.mr2da.fhir.FHIRExecutor;
import eu.interopehrate.mr2da.r2d.resources.PatientQueryGenerator;

class AsyncMR2DA extends DefaultMR2DAImpl {

    private MR2DACallbackHandler callbackHandler;
    private PollingHandlerThread pollingThread;
    private ResultsRetrieverHandlerThread resultsThread;

    AsyncMR2DA(URL r2dServerURL, String eidasToken, MR2DACallbackHandler callbackHandler) {
        super(r2dServerURL, eidasToken);

        // store the callback handler
        this.setCallbackHandler(callbackHandler);

        // instantiates the result retriever Thread
        resultsThread = new ResultsRetrieverHandlerThread(eidasToken, callbackHandler);
        // instantiates the polling Thread
        pollingThread = new PollingHandlerThread(eidasToken, resultsThread);

        // register the AsyncResponseClientInterceptor to the fhirClient assuring it is only one
        this.fhirClient.unregisterInterceptor(AsyncHTTPClientInterceptor.getInstance());
        this.fhirClient.registerInterceptor(AsyncHTTPClientInterceptor.getInstance());

        // starts the polling thread
        pollingThread.start();

        // starts the results retriever thread
        resultsThread.start();

        // provides handler to client interceptor
        AsyncHTTPClientInterceptor.getInstance().setHandlerThread(pollingThread);
    }


    public void setCallbackHandler(MR2DACallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @Override
    protected FHIRExecutor createFHIRExecutorInstance() {
        if (this.callbackHandler == null)
            throw new IllegalStateException("Cannot use AsynchronousMR2DA with listener set to null!");

        if (this.callbackHandler == null)
            throw new IllegalStateException("Cannot use AsynchronousMR2DA with listener set to null!");

        FHIRExecutor executor = new SingleQueryExecutor(this.fhirClient);
        return executor;
    }

}
