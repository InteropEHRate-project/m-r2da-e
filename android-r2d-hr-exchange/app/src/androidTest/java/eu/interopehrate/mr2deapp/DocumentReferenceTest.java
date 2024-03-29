package eu.interopehrate.mr2deapp;

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
public class DocumentReferenceTest extends BasicMR2DATest {

    @Test
    public void testDocumentReference() throws Exception {
         Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DOCUMENT_REFERENCE,
                null, false);
        /*
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DOCUMENT_REFERENCE,
                "", "http://loinc.org|742-7", gc.getTime(), false);
         */

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(4, counter);
    }

    @Test
    public void testDocumentReferenceWithDateAndType() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2018, Calendar.JANUARY, 01);

        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DOCUMENT_REFERENCE,
                "", "http://loinc.org|57170-3", gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(1, counter);
    }
}
