package eu.interopehrate.mr2de;

import java.util.Locale;

import eu.interopehrate.mr2de.MobileR2DFactory;
import eu.interopehrate.mr2de.api.MR2D;

public class R2DTest {

    public static void main(String[] args) {
        MR2D mobileR2D = MobileR2DFactory.create(Locale.ITALY, "ytrer-ewwer-rwerw");

        /*
        Bundle patientSummaryBundle = (Bundle)mobileR2D.getLastResource(HealthRecordType.PATIENT_SUMMARY);

        Composition ps = (Composition)patientSummaryBundle.getEntryFirstRep().getResource();

        System.out.println("Header data of PS:");
        System.out.println(ps.getTitle());
        System.out.println(ps.getId());
        System.out.println(ps.getType().getCodingFirstRep().getCode());

        System.out.println();
        System.out.println("Sections of PS:");
        for (Composition.SectionComponent section : ps.getSection()) {
            System.out.println(section.getCode().getCodingFirstRep().getDisplay());
        }

        System.out.println();
        System.out.println("Entries of bundle:");
        // list all entries of Bundle
        for (Bundle.BundleEntryComponent entry : patientSummaryBundle.getEntry()) {
            System.out.println(entry.getResource().fhirType() + " with id: " + entry.getResource().getId());
        }
        */
    }

}
