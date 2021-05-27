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
import eu.interopehrate.mr2dsm.MR2DSMFactory;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class MR2DATest {

    protected MR2DSM mr2dsm;
    protected MR2DA mr2da;

    public MR2DATest() {
        mr2dsm = MR2DSMFactory.create(Locale.ITALY);
        mr2dsm.login("mario.rossi","interopehrate");
        mr2da = MR2DAFactory.create("http://213.249.46.205:8080/R2D/fhir/", mr2dsm);
    }

    @Test
    public void testGetResourcesWithoutParameters() {
        Iterator<Resource> it = mr2da.getResources(null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        // assertEquals(96, counter);
    }

    @Test
    public void testGetResourcesByCategories() {
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
    public void testGetResourcesWithDate() {
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
    public void testGetPatientSummary() {
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

    @Test
    public void getRecordForPatientMarioRossi() {
        Resource res = mr2da.getResourceById("Patient/31");

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("RSSMRA60A01D663E", p.getIdentifierFirstRep().getValue());
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }
}
