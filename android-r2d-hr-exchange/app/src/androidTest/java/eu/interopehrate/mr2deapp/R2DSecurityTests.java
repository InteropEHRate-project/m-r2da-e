package eu.interopehrate.mr2deapp;

import android.util.Log;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class R2DSecurityTests {

    private static MR2D itaR2D;
    private static MR2D itaNotAutheticatedR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Log.d(R2DBasicTestWithFHIR.class.getSimpleName(), "Executing setup()");
        itaR2D = MR2DFactory.create(Locale.ITALY);
        itaR2D.login("mario.rossi","interopehrate");

        itaNotAutheticatedR2D = MR2DFactory.create(Locale.ITALY);
    }

    @AfterClass
    public static void close() throws Exception {
        Log.d(R2DBasicTestWithFHIR.class.getSimpleName(), "Executing close()");
        if (itaR2D != null)
            itaR2D.logout();
    }

    @Test
    public void testNotAuthenticated() {
        try {
            itaNotAutheticatedR2D.getRecord("Patient/33");
        } catch (MR2DSecurityException e) {
            Exception cause = (Exception) e.getCause();
            System.out.println(cause.getClass().getName());

            assertTrue(cause instanceof IllegalStateException);
        }
    }

    @Test
    public void testNotAuthorized() {
        try{
            itaR2D.getRecord("Patient/50");
        } catch (MR2DSecurityException e) {
            Exception cause = (Exception) e.getCause();
            System.out.println(cause.getClass().getName());

            assertTrue( cause instanceof ForbiddenOperationException);
        }
    }

    @Test
    public void testAuthorized() {
        Resource res = itaR2D.getRecord("Patient/31");

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("RSSMRA60A01D663E", p.getIdentifierFirstRep().getValue());
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }

}
