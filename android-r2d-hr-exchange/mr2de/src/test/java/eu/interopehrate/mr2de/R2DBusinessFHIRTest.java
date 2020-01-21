package eu.interopehrate.mr2de;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Before;
import org.junit.Test;

import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

/**
 * Basic class for Integration Testing of R2D Library with an NCP supporting FHIR
 */
public class R2DBusinessFHIRTest {
    private static final String MARIO_ROSSI_SESSION = "f70e7d7e-ad8a-478d-9e02-2499e37fb7a8";
    private static final String CARLA_VERDI_SESSION = "7cde8fcd-dccd-47e1-ba25-e2fd96813649";

    private MR2D marioRossiR2D;
    private MR2D carlaVerdiR2D;


    @Before
    public void setUp() throws Exception {

        Patient marioRossi = new Patient();
        marioRossi.addAddress().setCountry("ITA");
        marioRossiR2D = MobileR2DFactory.create(marioRossi, MARIO_ROSSI_SESSION);

        Patient carlaVerdi = new Patient();
        carlaVerdi.addAddress().setCountry("ITA");
        carlaVerdiR2D = MobileR2DFactory.create(carlaVerdi, CARLA_VERDI_SESSION);
    }

    @Test
    public void getLastRecordForPatientSummaryOfMarioRossi() {
        Bundle bundle = (Bundle)marioRossiR2D.getLastRecord(
                HealthRecordType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_CONVERTED);

        Composition ps = (Composition)bundle.getEntryFirstRep().getResource();
        assert ("Patient Summary di: Mario Rossi".equals(ps.getTitle()));
     }

    @Test
    public void getLastRecordForPatientSummaryOfCarlaVerdi() {
        Bundle bundle = (Bundle)marioRossiR2D.getLastRecord(
                HealthRecordType.PATIENT_SUMMARY, ResponseFormat.STRUCTURED_CONVERTED);

        Composition ps = (Composition)bundle.getEntryFirstRep().getResource();
        assert ("Patient Summary di: Carla Verdi".equals(ps.getTitle()));

    }

    @Test
    public void getRecordForPatientMarioRossi() {

    }

    @Test
    public void getRecordForPatientCarlaVerdi() {

    }

    @Test
    public void getAllRecordsForForPatientSummaryOfMarioRossi() {

    }

    @Test
    public void getAllRecordsForForPatientSummaryOfCarlaVerdi() {

    }

}
