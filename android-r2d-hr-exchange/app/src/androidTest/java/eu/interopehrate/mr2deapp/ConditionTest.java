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
public class ConditionTest extends BasicMR2DATest {

    @Test
    public void testConditionWithoutParameters() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.CONDITION,
                null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        // 2 DiagnosticReports with iamges containing 1 ImagingStudy
        assertEquals(1, counter);
    }

    @Test
    public void testConditionWithDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2015, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.CONDITION,
                gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        assertEquals(1, counter);
    }

}
