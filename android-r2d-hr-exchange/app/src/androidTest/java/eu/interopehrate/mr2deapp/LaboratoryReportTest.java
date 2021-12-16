package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Iterator;

import eu.interopehrate.protocols.common.DocumentCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class LaboratoryReportTest extends BasicMR2DATest {

    @Test
    public void testLaboratoryReportWithoutParameters() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(DocumentCategory.LABORATORY_REPORT,
                null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        // 2 DiagnosticReports and 77 Observations
        assertEquals(79, counter);
    }

    /*

    @Test
    public void testLaboratoryReportWithDate() {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                null, gc.getTime());

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        assertEquals(77, counter);
    }

    @Test
    public void testLaboratoryReportWithType() {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                "http://loinc.org|742-7", null);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        assertEquals(2, counter);
    }

    @Test
    public void testDiagnosticReportWithTypeAndDate() {
        mr2da.getResourcesByCategory(FHIRResourceCategory.OBSERVATION,
                null, null);
    }
    */
}
