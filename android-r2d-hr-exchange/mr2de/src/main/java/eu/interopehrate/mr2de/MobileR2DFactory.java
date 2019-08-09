package eu.interopehrate.mr2de;

import eu.interopehrate.mr2de.api.R2D;
import eu.interopehrate.mr2de.impl.fhir.fake.FHIRFakeMobileR2D;

/*
 *  Author: Engineering Ingegneria Informatica
 * Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: factory class for instances of R2D
 */
public class MobileR2DFactory {

    private MobileR2DFactory() {}

    /**
     * Factory method for creating an instance of R2D (linked to the specific session
     * provided as argument).
     *
     * @param ncpEndPoint: endpoint of the remote NCP
     * @param ncpSessionToken: session token obtained by the login service to the NCP
     * @return
     */
    public static R2D create(String ncpEndPoint, String ncpSessionToken) {
        return new FHIRFakeMobileR2D(ncpSessionToken);
    }
}
