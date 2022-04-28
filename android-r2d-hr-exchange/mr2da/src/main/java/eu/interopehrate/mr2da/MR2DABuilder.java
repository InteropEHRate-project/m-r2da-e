package eu.interopehrate.mr2da;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2da.api.MR2DACallbackHandler;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: builder class to create instances of MR2DA in a fluent style
 */
public class MR2DABuilder {
    private Locale language;
    private URL endpoint;
    private String eIDASToken;
    private MR2DACallbackHandler callbackHandler;

    public MR2DABuilder endpoint(String endpoint) {
        try {
            this.endpoint = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided endpoint '" + endpoint + "' is not valid. ");
        }
        return this;
    }

    public MR2DABuilder endpoint(URL endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public MR2DABuilder eIDASToken(String eIDASToken) {
        this.eIDASToken = eIDASToken;
        return this;
    }

    public MR2DABuilder callbackHandler(MR2DACallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
        return this;
    }

    public MR2DABuilder language(Locale language) {
        this.language = language;
        return this;
    }

    public MR2DA build() {
        if (endpoint == null)
            throw new IllegalArgumentException();

        if (eIDASToken == null)
            throw new IllegalArgumentException();

        DefaultMR2DAImpl mr2da;
        if (callbackHandler == null)
            mr2da = new DefaultMR2DAImpl(endpoint, eIDASToken);
        else
            mr2da = new AsyncMR2DA(endpoint, eIDASToken, callbackHandler);

        if (language !=  null)
            mr2da.setLanguage(language);

        return mr2da;
    }

}
