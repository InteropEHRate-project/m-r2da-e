package eu.interopehrate.mr2de;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BasicUnitTest {

    @Test
    public void testGetLastResourceForPatientSummary() {
        /*
        // Get an instance of MR2D
        MR2D mobileR2D = MobileR2DFactory.create("", "");

        // Inkove getLastResource() method to retrieve Patient Summary
        Bundle psBundle = (Bundle)mobileR2D.getLastResource(HealthRecordType.PATIENT_SUMMARY.PATIENT_SUMMARY);

        // Retrieve the Composition containing the Patient Summary
        Composition patientSummary = (Composition)psBundle.getEntryFirstRep().getResource();

        // Verify Patient Summary Title
        assertEquals("Patient Summary of patient Mario Rossi", patientSummary.getTitle());

        // Verify Patient Summary Code
        Coding coding = patientSummary.getType().getCodingFirstRep();
        String code = coding.getSystem() + "|" + coding.getCode();
        assertEquals("http://loinc.org|60591-5", code);

        // Verify Subject, Custodian, Attester
        assertNotNull(patientSummary.getSubject());
        assertNotNull(patientSummary.getCustodian());
        assertTrue(patientSummary.getAttester().size() > 0);

        // Verify Sections
        List<Composition.SectionComponent> sections = patientSummary.getSection();
        assertEquals(4, sections.size());

        // Sample code used only to show how to navigate Patient Summary sections
        List<Reference> sectionEntries;
        Coding sectionCode;
        IBaseResource entryRes;
        for (Composition.SectionComponent section : sections) {
            sectionCode = section.getCode().getCodingFirstRep();
        }
        */
    }

}