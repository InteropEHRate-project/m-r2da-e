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
public class OperationTest extends BasicMR2DATest {

    @Test
    public void testObservationWithoutParameters() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(91, counter);
    }

    @Test
    public void testObservationWithDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        assertEquals(77, counter);
    }

    @Test
    public void testObservationWithSubCategory() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                "vital-signs", null, null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(4, counter);
    }

    @Test
    public void testObservationWithType() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                "", "http://loinc.org|742-7", null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(2, counter);
    }

    @Test
    public void testObservationWithTypeAndDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                "", "http://loinc.org|742-7", gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(2, counter);
    }

}
