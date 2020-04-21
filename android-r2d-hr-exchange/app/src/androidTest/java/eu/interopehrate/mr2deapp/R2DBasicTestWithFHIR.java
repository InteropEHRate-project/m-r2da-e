package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.interopehrate.mr2de.MobileR2DFactory;
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
    private static final String MARIO_ROSSI_SESSION = "f70e7d7e-ad8a-478d-9e02-2499e37fb7a8";
    private static final String CARLA_VERDI_SESSION = "7cde8fcd-dccd-47e1-ba25-e2fd96813649";

    private static MR2D marioRossiR2D;
    private static MR2D carlaVerdiR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Patient marioRossi = new Patient();
        marioRossi.addAddress().setCountry("ITA");
        marioRossiR2D = MobileR2DFactory.create(marioRossi, MARIO_ROSSI_SESSION);

        Patient carlaVerdi = new Patient();
        carlaVerdi.addAddress().setCountry("ITA");
        carlaVerdiR2D = MobileR2DFactory.create(carlaVerdi, CARLA_VERDI_SESSION);
    }

    @Test
    public void getLastRecordForPatientSummaryOfMarioRossi() {
        Bundle bundle = (Bundle)marioRossiR2D.getLastRecord(
                HealthRecordType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_CONVERTED);

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
    public void getLastRecordForPatientSummaryOfCarlaVerdi() {
        Bundle bundle = (Bundle)carlaVerdiR2D.getLastRecord(
                HealthRecordType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_CONVERTED);

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
        assertEquals("Patient Summary di: Carla Verdi", ps.getTitle());
    }

    @Test
    public void getRecordForPatientMarioRossi() {
        Resource res = marioRossiR2D.getRecord("Patient/33", ResponseFormat.STRUCTURED_CONVERTED);

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }

    @Test
    public void getRecordForPatientCarlaVerdi() {
        Resource res = carlaVerdiR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("Carla", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Verdi", p.getNameFirstRep().getFamily());
    }

    @Test
    public void getAllRecordsForForPatientSummaryOfMarioRossi() {
        HealthRecordBundle b = marioRossiR2D.getAllRecords(null, ResponseFormat.STRUCTURED_CONVERTED);

        int counter = 0;
        for (HealthRecordType t: b.getHealthRecordTypes()) {
            while (b.hasNext(t)) {
                b.next(t);
                counter++;
            }
        }

        assertEquals(15, counter);
    }

    @Test
    public void getAllRecordsForForPatientSummaryOfCarlaVerdi() {
        HealthRecordBundle b = carlaVerdiR2D.getAllRecords(null, ResponseFormat.STRUCTURED_CONVERTED);

        int counter = 0;
        for (HealthRecordType t: b.getHealthRecordTypes()) {
            while (b.hasNext(t)) {
                b.next(t);
                counter++;
            }
        }

        assertEquals(12, counter);
    }
}
