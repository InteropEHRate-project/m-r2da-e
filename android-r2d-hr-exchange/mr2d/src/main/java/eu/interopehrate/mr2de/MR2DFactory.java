package eu.interopehrate.mr2de;

import android.util.Log;
import android.view.View;

import org.hl7.fhir.r4.model.Patient;

import java.util.Locale;

import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2d.ncp.NCPDescriptor;
import eu.interopehrate.mr2d.ncp.NCPRegistry;
import eu.interopehrate.mr2de.api.MR2D;

/**
 *      Author: Engineering Ingegneria Informatica
 *     Project: InteropEHRate - www.interopehrate.eu
 *
 * Description: factory class for instances of MR2D
 */
public final class MR2DFactory {

    private MR2DFactory() {}

    /**
     * Factory method for creating an instance of MR2D
     *
     * @param ncp: ncp
     * @return
     */
    public static MR2D create(NCPDescriptor ncp) {
        // preconditions checks
        if (ncp == null)
            throw new IllegalArgumentException("Precondition failed: Argument ncp cannot be null");

        // business logic
        return MR2DFactory.create(ncp.getCountry());
    }

    /**
     * Factory method for creating an instance of MR2D
     *
     * @param patient: patient
     * @return
     */
    public static MR2D create(Patient patient) {
        // preconditions checks
        if (patient == null)
            throw new IllegalArgumentException("Precondition failed: Argument patient cannot be null");

        // business logic
        return MR2DFactory.create(patient.getAddressFirstRep().getCountry());
    }

    /**
     * Factory method for creating an instance of MR2D
     *
     * @param locale: locale indicating the country of the patient
     * @return
     */
    public static MR2D create(Locale locale) {
        // preconditions checks
        if (locale == null)
            throw new IllegalArgumentException("Precondition failed: argument 'locale' cannot be null");

        // business logic
        return MR2DFactory.create(locale.getISO3Country());
    }

    /**
     * Factory method for creating an instance of MR2D
     *
     * @param country: ISO 3166 alpha 3 code identifying country
     * @return
     */
    private static MR2D create(String country) {
        // preconditions checks
        if (country == null || country.isEmpty())
            throw new IllegalArgumentException("Precondition failed: argument 'country' cannot be empty");

        Log.d(MR2DFactory.class.getName(), "Requested creation of an instance of MR2D for country: " +
                country);

        // business logic
        NCPDescriptor ncpDesc = NCPRegistry.getNCPDescriptor(country);
        if (ncpDesc == null)
            throw new MR2DException("No NCP descriptor found for country: " + country);

        if (ncpDesc.isSupportsFHIR())
            return new MR2DOverFHIR(ncpDesc);

        if (ncpDesc.isSupportsEHDSI())
            throw new IllegalArgumentException("NCP adopting only eHDSI protocol are not supported yet.");

        // Used only for testing purposes or for demo
        return new MR2DOverLocal();
    }

}
