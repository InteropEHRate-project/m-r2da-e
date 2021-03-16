package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import eu.interopehrate.mr2da.MR2DAFactory;
import eu.interopehrate.mr2da.api.MR2DA;
import eu.interopehrate.mr2dsm.MR2DSMFactory;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MR2DAImageReportTest {

    protected MR2DSM mr2dsm;
    protected MR2DA mr2da;

    public MR2DAImageReportTest() {
        mr2dsm = MR2DSMFactory.create(Locale.ITALY);
        mr2dsm.login("mario.rossi","interopehrate");
        mr2da = MR2DAFactory.create("http://213.249.46.205:8080/R2D/fhir/", mr2dsm);
    }

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
