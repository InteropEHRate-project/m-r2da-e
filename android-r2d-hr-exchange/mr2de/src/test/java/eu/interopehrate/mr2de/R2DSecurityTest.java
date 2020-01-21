package eu.interopehrate.mr2de;

import org.hl7.fhir.r4.model.Patient;
import org.junit.Before;
import org.junit.Test;

import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

public class R2DSecurityTest {
    private static final String MARIO_ROSSI_SESSION = "f70e7d7e-ad8a-478d-9e02-2499e37fb7a8";

    private MR2D marioRossiR2D;
    private MR2D notAuthenticatedR2D;
    private MR2D invalidSessionR2D;

    @Before
    public void setUp() throws Exception {
        Patient marioRossi = new Patient();
        marioRossi.addAddress().setCountry("ITA");
        marioRossiR2D = MobileR2DFactory.create(marioRossi, MARIO_ROSSI_SESSION);
        notAuthenticatedR2D = MobileR2DFactory.create(marioRossi, "");
        invalidSessionR2D = MobileR2DFactory.create(marioRossi, "6538765492736452437");
    }

    @Test
    public void testNotAuthenticated() {
        notAuthenticatedR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);
    }

    @Test
    public void testNotAuthorized() {
        marioRossiR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);
    }

    @Test
    public void testInvalidSession() {
        invalidSessionR2D.getRecord("Patient/50", ResponseFormat.STRUCTURED_CONVERTED);
   }

}
