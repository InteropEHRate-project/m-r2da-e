package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import eu.interopehrate.protocols.common.DocumentCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ImageReportTest extends BasicMR2DATest {

    @Test
    public void testImageReportWithoutParameters() {
        Iterator<Resource> it = mr2da.getResourcesByCategory(DocumentCategory.IMAGE_REPORT,
                null, false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }

        // 2 DiagnosticReports with iamges containing 1 ImagingStudy
        assertEquals(3, counter);
    }

    @Test
    public void testImageReportWithDate() {
        GregorianCalendar gc = new GregorianCalendar(2015, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(DocumentCategory.IMAGE_REPORT,
                gc.getTime(), false);

        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        assertEquals(3, counter);
    }

}
