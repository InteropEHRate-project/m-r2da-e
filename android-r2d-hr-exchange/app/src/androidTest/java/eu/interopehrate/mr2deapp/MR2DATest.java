package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import eu.interopehrate.mr2da.MR2DAFactory;
import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class MR2DATest extends BasicMR2DATest {

    @Test
    public void testGetResourcesWithoutParameters() throws Exception {
        Iterator<Resource> it = mr2da.getResources(null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        // assertEquals(96, counter);
    }

    @Test
    public void testGetResourcesByCategories() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategories(null, false,
                FHIRResourceCategory.MEDICATION_REQUEST,
                FHIRResourceCategory.DIAGNOSTIC_REPORT);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
    }

        @Test
    public void testGetResourcesWithDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResources(gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(80, counter);
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
