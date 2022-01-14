package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class OperationTest extends BasicMR2DATest {

    @Test
    public void testEncounterEverything() throws Exception {
        Bundle bundle = mr2da.getEncounterEverything("Encounter/35");
        assertEquals(42, bundle.getEntry().size());
    }

    @Test
    public void testPatientSummary() throws Exception {
        Bundle bundle = mr2da.getPatientSummary();
        assertEquals(33, bundle.getEntry().size());
    }
}
