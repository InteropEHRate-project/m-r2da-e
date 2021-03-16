package eu.interopehrate.mr2da;

import java.net.MalformedURLException;
import java.net.URL;

import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.protocols.client.ResourceReader;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public final class MR2DAFactory {

    public static MR2DA create(String r2dEndpoint, MR2DSM mr2dsm) {
        if (!mr2dsm.isAuthenticated())
            throw new IllegalArgumentException("Authentication has not been executes. ");

        try {
            URL url = new URL(r2dEndpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Provided endpoint '" + r2dEndpoint + "' is not valid. ");
        }

        return new MR2DAImpl(r2dEndpoint, mr2dsm);
    }

}
