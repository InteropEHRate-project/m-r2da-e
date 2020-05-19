package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

    private static MR2D marioRossiR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Patient marioRossi = new Patient();
        marioRossi.addAddress().setCountry("ITA");
        marioRossiR2D = MR2DFactory.create(marioRossi);

        marioRossiR2D.login("mario.rossi", "interopehrate");
    }

    @AfterClass
    public static void close() throws Exception {
        if (marioRossiR2D != null)
            marioRossiR2D.logout();
    }

    @Test
    public void getLastPatientSummaryOfMarioRossi() {
        Bundle bundle = (Bundle)marioRossiR2D.getLastRecord(
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
        HealthRecordBundle b = marioRossiR2D.getRecords(null,
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
    public void getAllRecordsForForPatientSummaryOfMarioRossi() {
        HealthRecordBundle b = marioRossiR2D.getAllRecords(null, ResponseFormat.STRUCTURED_UNCONVERTED);

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
        Resource res = marioRossiR2D.getRecord("Patient/33");

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }

}
