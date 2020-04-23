package eu.interopehrate.mr2deapp;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class R2DSecurityTests {

    private static final String MARIO_ROSSI_SESSION = "f70e7d7e-ad8a-478d-9e02-2499e37fb7a8";

    private static MR2D marioRossiR2D;
    private static MR2D notAuthenticatedR2D;
    private static MR2D invalidSessionR2D;

    @BeforeClass
    public static void setUp() throws Exception {
        Patient marioRossi = new Patient();
        marioRossi.addAddress().setCountry("ITA");
        marioRossiR2D = MR2DFactory.create(marioRossi, MARIO_ROSSI_SESSION);
        notAuthenticatedR2D = MR2DFactory.create(marioRossi, "");
        invalidSessionR2D = MR2DFactory.create(marioRossi, "6538765492736452437");
    }

    @Test
    public void testNotAuthenticated() {
        try {
            notAuthenticatedR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);
        } catch (MR2DSecurityException e) {
            Exception cause = (Exception) e.getCause();
            System.out.println(cause.getClass().getName());

            assertTrue( cause instanceof AuthenticationException);
        }
    }

    @Test
    public void testNotAuthorized() {
        try{
            marioRossiR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);
        } catch (MR2DSecurityException e) {
            Exception cause = (Exception) e.getCause();
            System.out.println(cause.getClass().getName());

            assertTrue( cause instanceof ForbiddenOperationException);
        }
    }

    @Test
    public void testAuthorized() {
        Resource res = marioRossiR2D.getRecord("Patient/33", ResponseFormat.STRUCTURED_CONVERTED);

        assertEquals("Patient", res.getResourceType().name());

        Patient p = (Patient)res;
        assertEquals("Mario", p.getNameFirstRep().getGivenAsSingleString());
        assertEquals("Rossi", p.getNameFirstRep().getFamily());
    }

    @Test
    public void testInvalidSession() {
        try {
            invalidSessionR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);
        } catch (MR2DSecurityException e) {
            Exception cause = (Exception) e.getCause();
            System.out.println(cause.getClass().getName());

            assertTrue( cause instanceof AuthenticationException);
        }
    }
}
