package eu.interopehrate.mr2de;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;

import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.R2D;

public class R2DTest {

    public static void main(String[] args) {

        R2D mobileR2D = MobileR2DFactory.create();

        Bundle patientSummaryBundle = (Bundle)mobileR2D.getLastResource(HealthRecordType.PATIENT_SUMMARY);

        for (Bundle.BundleEntryComponent entry : patientSummaryBundle.getEntry()) {
            System.out.println(entry.getResource().fhirType() + " with id: " + entry.getResource().getId());
        }

        Composition ps = (Composition)patientSummaryBundle.getEntryFirstRep().getResource();

        System.out.println(ps.getId());
        System.out.println(ps.getTitle());
        System.out.println(ps.getType().getCodingFirstRep().getCode());
        System.out.println(ps.getSubject().getReference());


        for (Bundle.BundleEntryComponent entry : patientSummaryBundle.getEntry()) {
            System.out.println(entry.getResource().fhirType() + " with id: " + entry.getResource().getId());
        }

        // Patient Summary Title
        System.out.println(ps.getTitle()=="Patient Summary of patient Maria Rossi");
        // Patient Summary Code = http://loinc.org|60591-5
        System.out.println(ps.getType().getCodingFirstRep().getCode());

        System.out.println(ps.getSubject().getResource().getIdElement().getValueAsString());
        System.out.println(ps.getAuthorFirstRep().getResource().getIdElement().getValueAsString());
        System.out.println(ps.getCustodian().getResource().getIdElement().getValueAsString());

        // ps.getSection().size() == 3

        for (Composition.SectionComponent section : ps.getSection()) {
            System.out.println(section.getCode().getCodingFirstRep().getDisplay());
        }

        //FhirContext fc = FhirContext.forR4();
        //System.out.println(fc.newJsonParser().setPrettyPrint(true).encodeResourceToString(patientSummaryBundle));

    }

}
