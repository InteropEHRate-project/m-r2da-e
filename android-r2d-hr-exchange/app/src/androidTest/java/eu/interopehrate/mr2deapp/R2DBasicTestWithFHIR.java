package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class R2DBasicTestWithFHIR {

    private static MR2D itaR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Log.d(R2DBasicTestWithFHIR.class.getSimpleName(), "Executing setup()");
        itaR2D = MR2DFactory.create(Locale.ITALY);
        itaR2D.login("mario.rossi","interopehrate");
    }

    @AfterClass
    public static void close() throws Exception {
        Log.d(R2DBasicTestWithFHIR.class.getSimpleName(), "Executing close()");
        if (itaR2D != null)
            itaR2D.logout();
    }

    @Test
    public void getLastPatientSummaryOfMarioRossi() {
        Log.d(getClass().getSimpleName(), "Executing getLastPatientSummaryOfMarioRossi()");
        Bundle bundle = (Bundle) itaR2D.getLastRecord(
                HealthRecordType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_UNCONVERTED);

        Resource res = bundle.getEntryFirstRep().getResource();
        assertEquals("Composition", res.getResourceType().name());

        Composition ps = (Composition)res;

        // Generic checks
        Coding psType = ps.getType().getCodingFirstRep();
        assertEquals("60591-5", psType.getCode());
        assertEquals("http://loinc.org", psType.getSystem());
        assertNotNull(ps.getAuthorFirstRep());
        assertNotNull(ps.getAttesterFirstRep());
        assertNotNull(ps.getCustodian());

        // Specific checks
        assertEquals("Patient Summary di: Mario Rossi", ps.getTitle());
    }

    @Test
    public void getLaboratoryResultsForForPatientSummaryOfMarioRossi() {
        HealthRecordBundle b = itaR2D.getRecords(null,
                ResponseFormat.STRUCTURED_UNCONVERTED,
                HealthRecordType.LABORATORY_REPORT);

        int counter = 0;
        for (HealthRecordType t: b.getHealthRecordTypes()) {
            while (b.hasNext(t)) {
                b.next(t);
                counter++;
            }
        }

        assertTrue(counter > 1);
    }

    @Test
    public void getRecordForPatientMarioRossi() {
        Resource res = itaR2D.getRecord("Patient/31");

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("RSSMRA60A01D663E", p.getIdentifierFirstRep().getValue());
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }

}
