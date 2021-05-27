package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Resource;
import org.junit.BeforeClass;
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
import eu.interopehrate.protocols.common.FHIRResourceCategory;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ObservationTest {

    protected MR2DSM mr2dsm;
    protected MR2DA mr2da;

    public ObservationTest() {
        mr2dsm = MR2DSMFactory.create(Locale.ITALY);
        mr2dsm.login("mario.rossi","interopehrate");
        mr2da = MR2DAFactory.create("http://213.249.46.205:8080/R2D/fhir/", mr2dsm);
    }

    @Test
    public void testObservationWithoutParameters() {
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
    public void testObservationWithDate() {
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
    public void testObservationWithSubCategory() {
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
    public void testObservationWithType() {
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
    public void testObservationWithTypeAndDate() {
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
