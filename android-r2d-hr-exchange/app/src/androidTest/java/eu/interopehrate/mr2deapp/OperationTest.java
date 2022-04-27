package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class OperationTest extends BasicMR2DATest {

    @Test
    public void testEncounterEverything() throws Exception {
        Bundle bundle = mr2da.getEncounterEverything("Encounter/35");
        assertEquals(42, bundle.getEntry().size());
    }

    @Test
    public void testGetPatientSummary() throws Exception {
        Resource res = mr2da.getPatientSummary();

        assertTrue(res instanceof Bundle);

        Bundle psBundle = (Bundle) res;

        Composition ps = (Composition)psBundle.getEntryFirstRep().getResource();

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
}
