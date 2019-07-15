package eu.interopehrate.mr2dexc;

import eu.interopehrate.mr2dexc.impl.fhir.fake.FHIRFakeMobileR2D;

/*
 *  Author: Engineering Ingegneria Informatica
 * Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: factory class for instances of IMobileR2D
 */
public class MobileR2DFactory {

    private MobileR2DFactory() {}

    /*
     * Parametri necessari: endpoint, session,
     */

    /*
     * TODO: define how to provide configuration to the library. Config should be
     * passed at the factory method while creating an istance of library. Each instance
     * should interact with a specific server.
     */

    public static IMobileR2D create() {
        return new FHIRFakeMobileR2D();
    }
}
