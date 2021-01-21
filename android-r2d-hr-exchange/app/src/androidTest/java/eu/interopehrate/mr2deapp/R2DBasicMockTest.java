package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.HealthDataBundle;
import eu.interopehrate.mr2de.api.HealthDataType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class R2DBasicMockTest {

    private static MR2D marioRossiR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Patient marioRossi = new Patient();
        marioRossi.addAddress().setCountry("FKE");
        marioRossiR2D = MR2DFactory.create(marioRossi);
    }

    @Test
    public void getLastRecordForPatientSummaryOfMarioRossi() {
        Bundle bundle = (Bundle)marioRossiR2D.getLastRecord(
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
    public void getAllRecordsForForPatientSummaryOfMarioRossi() {
        HealthDataBundle b = marioRossiR2D.getAllRecords(null, ResponseFormat.ALL);

        int counter = 0;
        for (HealthDataType t: b.getHealthRecordTypes()) {
            while (b.hasNext(t)) {
                b.next(t);
                counter++;
            }
        }

        assertEquals(15, counter);
    }

}
