package eu.interopehrate.mr2dsm;

import android.util.Log;

import org.hl7.fhir.r4.model.Patient;

import java.util.Locale;

import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2d.ncp.NCPDescriptor;
import eu.interopehrate.mr2d.ncp.NCPRegistry;
import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2dsm.api.MR2DSM;

/*
 *		Author: UBITECH
 *		Project: InteropEHRate - www.interopehrate.eu
 *
 *	    Description: Factory class for creating instances of MR2DSM
 */
public final class MR2DSMFactory {

    private MR2DSMFactory() {}

    /**
     * Factory method for creating an instance of MR2D
     *
     * @param ncp: ncp
     * @return
     */
    public static MR2DSM create(NCPDescriptor ncp) {
        // preconditions checks
        if (ncp == null)
            throw new IllegalArgumentException("Precondition failed: Argument ncp cannot be null");

        // business logic
        return MR2DSMFactory.create(ncp.getCountry());
    }

    /**
     * Factory method for creating an instance of MR2DSM
     *
     * @param patient: patient
     * @return
     */
    public static MR2DSM create(Patient patient) {
        // preconditions checks
        if (patient == null)
            throw new IllegalArgumentException("Precondition failed: Argument patient cannot be null");

        // business logic
        return MR2DSMFactory.create(patient.getAddressFirstRep().getCountry());
    }

    /**
     * Factory method for creating an instance of MR2DSM
     *
     * @param locale: locale indicating the country of the patient
     * @return
     */
    public static MR2DSM create(Locale locale) {
        // preconditions checks
        if (locale == null)
            throw new IllegalArgumentException("Precondition failed: argument 'locale' cannot be null");

        // business logic
        return MR2DSMFactory.create(locale.getISO3Country());
    }


    /**
     * Factory method for creating an instance of MR2DSM
     *
     * @param country: ISO 3166 alpha 3 code identifying country
     * @return
     */
    private static MR2DSM create(String country) {
        // preconditions checks
        if (country == null || country.isEmpty())
            throw new IllegalArgumentException("Precondition failed: argument 'country' cannot be empty");

        Log.d(MR2DFactory.class.getName(), "Requested creation of an instance of MR2D for country: " +
                country);

        // business logic
        NCPDescriptor ncpDesc = NCPRegistry.getNCPDescriptor(country);
        if (ncpDesc == null)
            throw new MR2DException("No NCP descriptor found for country: " + country);

        if(ncpDesc.getIamEndpoint().endsWith("/"))
            return new MR2DSMOverEidas(ncpDesc.getIamEndpoint().substring(0, ncpDesc.getIamEndpoint().length() - 1));
        else
            return new MR2DSMOverKeycloak(ncpDesc.getIamEndpoint());
    }

}
