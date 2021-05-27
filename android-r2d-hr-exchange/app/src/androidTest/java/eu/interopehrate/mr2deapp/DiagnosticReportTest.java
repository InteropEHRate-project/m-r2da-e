package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import eu.interopehrate.mr2da.MR2DAFactory;
import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2dsm.MR2DSMFactory;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DiagnosticReportTest {

    protected MR2DSM mr2dsm;
    protected MR2DA mr2da;

    public DiagnosticReportTest() {
        mr2dsm = MR2DSMFactory.create(Locale.ITALY);
        mr2dsm.login("mario.rossi","interopehrate");
        mr2da = MR2DAFactory.create("http://213.249.46.205:8080/R2D/fhir/", mr2dsm);
    }

    @Test
    public void testDiagnosticReportWithoutParameters() {
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
    public void testDiagnosticReportWithDate() {
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
    public void testDiagnosticReportWithCategory() {
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
    public void testDiagnosticReportWithType() {
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
    public void testDiagnosticReportWithTypeAndDate() {
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
    public void testDiagnosticReportWithCategoryAndTypeAndDate() {
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
