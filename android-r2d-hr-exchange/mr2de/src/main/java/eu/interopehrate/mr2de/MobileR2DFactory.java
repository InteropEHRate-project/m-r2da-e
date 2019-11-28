package eu.interopehrate.mr2de;

import android.util.Log;

import org.hl7.fhir.r4.model.Patient;

import java.util.Locale;

import eu.interopehrate.mr2de.ncp.NCPDescriptor;
import eu.interopehrate.mr2de.ncp.NCPRegistry;
import eu.interopehrate.mr2de.ncp.fhir.fake.FHIRFakeMobileMR2D;
import eu.interopehrate.mr2de.api.MR2D;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: factory class for instances of MR2D
 */
public class MobileR2DFactory {

    private MobileR2DFactory() {}

    /**
     * Factory method for creating an instance of MR2D (linked to the specific session
     * provided as argument).
     *
     * @param patient: patient
     * @param ncpSessionToken: session token obtained by the login service to the NCP
     * @return
     */
    public static MR2D create(Patient patient, String ncpSessionToken) {
        // preconditions checks
        if (patient == null)
            throw new IllegalArgumentException("Precondition failed: Argument patient cannot be null");

        if (ncpSessionToken == null || ncpSessionToken.isEmpty())
            throw new IllegalArgumentException("Precondition failed: Argument ncpSessionToken cannot be empty");

        // business logic
        return MobileR2DFactory.create(patient.getAddressFirstRep().getCountry(), ncpSessionToken);
    }

    /**
     * Factory method for creating an instance of MR2D (linked to the specific session
     * provided as argument).
     *
     * @param locale: locale indicating the country of the patient
     * @param ncpSessionToken: session token obtained by the login service to the NCP
     * @return
     */
    public static MR2D create(Locale locale, String ncpSessionToken) {
        // preconditions checks
        if (locale == null)
            throw new IllegalArgumentException("Precondition failed: argument 'locale' cannot be null");

        if (ncpSessionToken == null || ncpSessionToken.isEmpty())
            throw new IllegalArgumentException("Precondition failed: argument 'ncpSessionToken' cannot be empty");

        // business logic
        return MobileR2DFactory.create(locale.getISO3Country(), ncpSessionToken);
    }


    private static MR2D create(String country, String ncpSessionToken) {
        // preconditions checks
        if (country == null || country.isEmpty())
            throw new IllegalArgumentException("Precondition failed: argument 'country' cannot be empty");

        Log.d(MobileR2DFactory.class.getName(), "Requested creation of an instance of MR2D for country: " +
                country);

        // business logic
        NCPDescriptor ncpDesc = NCPRegistry.getNCPDescriptor(country);
        if (ncpDesc == null)
            throw new MR2DException("No NCP descriptor found for country: " + country);

        if (ncpDesc.isSupportsFHIR())
            return new MR2DOverFHIR(ncpDesc, ncpSessionToken);

        if (ncpDesc.isSupportsEHDSI())
            throw new IllegalArgumentException("NCP adopting only eHDSI protocol are not supported yet.");

        // Used only for testing purposes or for demo
        return new FHIRFakeMobileMR2D();
    }
}
