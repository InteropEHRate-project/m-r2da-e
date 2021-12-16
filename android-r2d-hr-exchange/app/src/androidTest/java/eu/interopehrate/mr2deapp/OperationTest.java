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
        // Bundle bundle = mr2da.getEncounterEverything("http://213.249.46.205:8080/R2D/fhir/Encounter/36");
        Bundle bundle = mr2da.getEncounterEverything("Encounter/36");
        Log.d("MR2DA", "getId() " + bundle.getEntryFirstRep().getResource().getId());

        assertEquals(42, bundle.getTotal());
    }

}
