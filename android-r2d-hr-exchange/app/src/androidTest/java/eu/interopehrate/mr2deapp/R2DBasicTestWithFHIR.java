package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.HealthDataBundle;
import eu.interopehrate.mr2de.api.HealthDataType;
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
    private static MR2D carlaVerdiR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Log.d(R2DBasicTestWithFHIR.class.getSimpleName(), "Executing setup()");
        marioRossiR2D = MR2DFactory.create(Locale.ITALY);
        marioRossiR2D.login("mario.rossi","interopehrate");
    }

    @AfterClass
    public static void close() throws Exception {
        Log.d(R2DBasicTestWithFHIR.class.getSimpleName(), "Executing close()");
        if (marioRossiR2D != null)
            marioRossiR2D.logout();
    }

    @Test
    public void getLastPatientSummaryOfMarioRossi() {
        Log.d(getClass().getSimpleName(), "Executing getLastPatientSummaryOfMarioRossi()");
        Bundle bundle = (Bundle) marioRossiR2D.getLastRecord(
                HealthDataType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_UNCONVERTED);

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
    public void getLastPatientSummaryForPatientMarioRossi() {
        Log.d(getClass().getSimpleName(), "Executing getLastPatientSummaryOfMarioRossi()");
        Bundle ps = (Bundle) marioRossiR2D.getLastRecord(
                HealthDataType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_UNCONVERTED);

        assertEquals("Bundle", ps.getResourceType().name());
        assertEquals("DOCUMENT", ps.getType().toString());

        assertTrue(ps.getEntry().size() == 33);
    }

    @Test
    public void getLastLaboratoryResultForPatientMarioRossi() {
        Log.d(getClass().getSimpleName(), "Executing getLastPatientSummaryOfMarioRossi()");
        Bundle bundle = (Bundle) marioRossiR2D.getLastRecord(
                HealthDataType.LABORATORY_RESULT, ResponseFormat.STRUCTURED_UNCONVERTED);

        Resource res = bundle.getEntryFirstRep().getResource();
        assertEquals("DiagnosticReport", res.getResourceType().name());

        DiagnosticReport dr = (DiagnosticReport)res;
        assertTrue(dr.getResult().size() == 40);
    }

    @Test
    public void getLaboratoryResultsForPatientMarioRossi() {
        HealthDataBundle b = marioRossiR2D.getRecords(null,
                ResponseFormat.ALL,
                HealthDataType.LABORATORY_RESULT);

        int counter = 0;
        int drCounter = 0;
        int obsCounter = 0;
        Resource r;

        for (HealthDataType t: b.getHealthRecordTypes()) {
            while (b.hasNext(t)) {
                r = b.next(t);
                if (r instanceof DiagnosticReport)
                    drCounter++;
                else if (r instanceof Observation)
                    obsCounter++;

                counter++;
            }
        }

        assertTrue(counter == 79);
        assertTrue(drCounter == 2);
        assertTrue(obsCounter == 77);
    }

    @Test
    public void getMedicalImagesForPatientMarioRossi() {
        HealthDataBundle b = marioRossiR2D.getRecords(null,
                ResponseFormat.ALL,
                HealthDataType.MEDICAL_IMAGE);

        int counter = 0;
        int drCounter = 0;
        int mediaCounter = 0;
        Resource r;

        for (HealthDataType t: b.getHealthRecordTypes()) {
            while (b.hasNext(t)) {
                r = b.next(t);
                if (r instanceof DiagnosticReport)
                    drCounter++;
                else if (r instanceof Media)
                    mediaCounter++;

                counter++;
            }
        }

        assertTrue(counter == 3);
        assertTrue(drCounter == 2);
        assertTrue(mediaCounter == 1);
    }

    @Test
    public void getRecordForPatientMarioRossi() {
        Resource res = marioRossiR2D.getRecord("Patient/31");

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("RSSMRA60A01D663E", p.getIdentifierFirstRep().getValue());
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }

}
