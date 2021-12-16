package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DiagnosticReportTest extends BasicMR2DATest {

    @Test
    public void testDiagnosticReportWithoutParameters() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                null, false);

        int counter = 0;
        while (it.hasNext()) {
            // Log.d(getClass().getSimpleName(), it.next().toString());
            it.next();
            counter++;
        }
        // Specific checks
        assertEquals(4, counter);
    }

    @Test
    public void testDiagnosticReportWithDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            // Log.d(getClass().getSimpleName(), it.next().toString());
            it.next();
            counter++;
        }
        // Specific checks
        assertEquals(3, counter);
    }

    @Test
    public void testDiagnosticReportWithCategory() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                "LAB", null , null, false);

        int counter = 0;
        while (it.hasNext()) {
            // Log.d(getClass().getSimpleName(), it.next().toString());
            it.next();
            counter++;
        }
        // Specific checks
        assertEquals(2, counter);
    }

    @Test
    public void testDiagnosticReportWithType() throws Exception {
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                null, "http://loinc.org|30954-2" , null, false);

        int counter = 0;
        while (it.hasNext()) {
            // Log.d(getClass().getSimpleName(), it.next().toString());
            it.next();
            counter++;
        }
        // Specific checks
        assertEquals(2, counter);
    }

    @Test
    public void testDiagnosticReportWithTypeAndDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                null, "http://loinc.org|30954-2" , gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            // Log.d(getClass().getSimpleName(), it.next().toString());
            it.next();
            counter++;
        }
        // Specific checks
        assertEquals(2, counter);
    }

    @Test
    public void testDiagnosticReportWithCategoryAndTypeAndDate() throws Exception {
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                "LAB", "http://loinc.org|30954-2" , gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            // Log.d(getClass().getSimpleName(), it.next().toString());
            it.next();
            counter++;
        }
        // Specific checks
        assertEquals(2, counter);
    }
}
