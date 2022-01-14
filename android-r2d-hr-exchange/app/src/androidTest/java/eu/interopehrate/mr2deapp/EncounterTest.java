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

import eu.interopehrate.mr2da.fhir.CategoryMapper;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class EncounterTest extends BasicMR2DATest {

    @Test
    public void testEncounterWithoutParameters() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.ENCOUNTER,
                null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        // Expecting 3 Encounter
        assertEquals(3, counter);
    }

    @Test
    public void testEncounterWithDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2015, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.ENCOUNTER,
                gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        assertEquals(3, counter);
    }

}
