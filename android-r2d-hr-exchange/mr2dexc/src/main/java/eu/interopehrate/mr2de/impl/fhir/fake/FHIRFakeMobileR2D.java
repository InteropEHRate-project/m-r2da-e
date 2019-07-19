package eu.interopehrate.mr2de.impl.fhir.fake;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.Timing;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import eu.interopehrate.mr2de.api.R2D;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.impl.fhir.utils.LoincCodes;
import eu.interopehrate.mr2de.impl.fhir.utils.SnomedCodes;

public class FHIRFakeMobileR2D implements R2D {


    @Override
    public Bundle getRecords(HealthRecordType[] hrTypes, Date from, ResponseFormat responseFormat) {
        Bundle results = new Bundle();

        if (Arrays.asList(hrTypes).contains(HealthRecordType.PATIENT_SUMMARY)) {
            Bundle patientSummaryBundle = buildPatientSummary();
            Bundle.BundleEntryComponent entry = (new Bundle.BundleEntryComponent()).setResource(patientSummaryBundle);
            results.addEntry(entry);
        }

        return results;
    }


    @Override
    public Bundle getAllRecords(Date from, ResponseFormat responseFormat) {
        HealthRecordType[] hrTypes = new HealthRecordType[]{HealthRecordType.PATIENT_SUMMARY};

        return getRecords(hrTypes, from, responseFormat);
    }


    @Override
    public Resource getLastResource(HealthRecordType rType) {
        if (rType == HealthRecordType.PATIENT_SUMMARY) {
            return buildPatientSummary();
        }

        return new Bundle();
    }


    private Bundle buildPatientSummary() {
        // Bundle contains all Resources of Patient Summary
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setId("IPS-Example-Bundle-for-InteropEHRate");
        bundle.setLanguage("it-IT");

        // Composition is used for Patient Summary
        Composition ps = new Composition();

        Date now = new Date();

        ps.setId("IPS-Example-Composition-for-InteropEHRate");
        // PS status
        ps.setStatus(Composition.CompositionStatus.FINAL);
        // PS Type
        ps.setType(LoincCodes.PATIENT_SUMMARY_CODE.getCodeableConcept());
        // PS Profile
        Meta ipsProfile = new Meta();
        ipsProfile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/composition-uv-ips");
        ps.setMeta(ipsProfile);

        // Create Practitioner / Attester
        Practitioner practitioner = buildPractitioner();

        // Author
        ps.addAuthor(new Reference(practitioner));

        // First Attester
        Composition.CompositionAttesterComponent firstAttester = new Composition.CompositionAttesterComponent();
        firstAttester.setMode(Composition.CompositionAttestationMode.LEGAL);
        firstAttester.setTime(now);
        firstAttester.setParty(new Reference(practitioner));
        ps.addAttester(firstAttester);

        // Second Attester
        Organization organization = buildOrganization();
        Composition.CompositionAttesterComponent secondAttester = new Composition.CompositionAttesterComponent();
        secondAttester.setMode(Composition.CompositionAttestationMode.LEGAL);
        secondAttester.setTime(now);
        secondAttester.setParty(new Reference(organization));
        ps.addAttester(secondAttester);

        // Custodian
        ps.setCustodian(new Reference(organization));

        // Create Patient
        Patient patient = buildPatient(practitioner);
        // Subject
        ps.setSubject(new Reference(patient));
        // Date
        ps.setDate(now);

        // Title
        ps.setTitle("Patient Summary of patient " + patient.getNameFirstRep().getGivenAsSingleString()
                + " " + patient.getNameFirstRep().getFamily());

        // Allergies Section (Required)
        Composition.SectionComponent allergiesSection = new Composition.SectionComponent();
        allergiesSection.setCode(LoincCodes.PATIENT_SUMMARY_ALLERGY_SECTION.getCodeableConcept());

        AllergyIntolerance allergy = buildAllergyIntoleranceForMedication(patient, SnomedCodes.PENICILLIN.getCodeableConcept());
        allergy.setOnset(new DateTimeType("2010"));
        allergiesSection.addEntry(new Reference(allergy));

        ps.addSection(allergiesSection);

        // Active Problems Section (Required)
        Composition.SectionComponent activeProblems = new Composition.SectionComponent();
        activeProblems.setCode(LoincCodes.PATIENT_SUMMARY_ACTIVE_PROBLEMS_SECTION.getCodeableConcept());
        ps.addSection(activeProblems);

        Condition cond1 = buildCondition(patient, SnomedCodes.MENO_PAUSAL.getCodeableConcept());
        cond1.setClinicalStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", null)));
        cond1.setVerificationStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-ver-status", "confirmed", null)));
        cond1.setSeverity(new CodeableConcept(new Coding(LoincCodes.SYSTEM, "LA6751-7", "Moderate")));
        cond1.setOnset(new DateTimeType("2014-01"));
        activeProblems.addEntry(new Reference(cond1));

        Condition cond2 = buildCondition(patient, SnomedCodes.HYPERTENSIVE_DISORDER.getCodeableConcept());
        cond2.setClinicalStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", null)));
        cond2.setVerificationStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-ver-status", "confirmed", null)));
        cond2.setSeverity(new CodeableConcept(new Coding(LoincCodes.SYSTEM, "LA6751-7", "Moderate")));
        cond2.setOnset(new DateTimeType("2010"));
        activeProblems.addEntry(new Reference(cond2));


        // Medication Section (Required)
        Composition.SectionComponent medicationSection = new Composition.SectionComponent();
        medicationSection.setCode(LoincCodes.PATIENT_SUMMARY_MEDICATION_SECTION.getCodeableConcept());

        // Medication #1
        MedicationStatement ms1 = buildMedicationStatement(patient);
        Dosage dosage = new Dosage();

        Timing timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent()).setCount(1).setPeriodUnit(Timing.UnitsOfTime.D));
        dosage.setTiming(timing);

        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());

        Dosage.DosageDoseAndRateComponent dosAndRate = dosage.addDoseAndRate();
        SimpleQuantity q = new SimpleQuantity();
        q.setValue(1);
        q.setUnit("tablet");
        q.setCode("1");
        q.setSystem("http://unitsofmeasure.org");
        dosAndRate.setDose(q);
        ms1.addDosage(dosage);

        CodeableConcept anostrozoleCode = new CodeableConcept(new Coding(SnomedCodes.SYSTEM, "108774000", "Anastrozole (product)"));
        Medication anastrozole = buildMedication(anostrozoleCode);
        ms1.setMedication(new Reference(anastrozole));
        medicationSection.addEntry(new Reference(ms1));

        // Medication #2
        MedicationStatement ms2 = buildMedicationStatement(patient);
        dosage = new Dosage();

        timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent()).setCount(1).setPeriodUnit(Timing.UnitsOfTime.D));
        dosage.setTiming(timing);

        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());

        dosAndRate = dosage.addDoseAndRate();
        q = new SimpleQuantity();
        q.setValue(1);
        q.setUnit("tablet");
        q.setCode("1");
        q.setSystem("http://unitsofmeasure.org");
        dosAndRate.setDose(q);
        ms2.addDosage(dosage);

        CodeableConcept frusemideCode = new CodeableConcept(new Coding(SnomedCodes.SYSTEM, "81609008", "Product containing furosemide (medicinal product)"));
        Medication frusemide = buildMedication(frusemideCode);
        ms2.setMedication(new Reference(frusemide));
        medicationSection.addEntry(new Reference(ms2));


        ps.addSection(medicationSection);

        // Immunizations Section (Suggested)

        // History of Procedures (Suggested)

        // Diagnostic Results (Suggested)

        // Add entries to bundle
        // Patient Summary
        Bundle.BundleEntryComponent entry = (new Bundle.BundleEntryComponent()).setResource(ps);
        bundle.addEntry(entry);
        // Practitioner
        entry = (new Bundle.BundleEntryComponent()).setResource(practitioner);
        bundle.addEntry(entry);
        // Patient
        entry = (new Bundle.BundleEntryComponent()).setResource(patient);
        bundle.addEntry(entry);
        // Organization
        entry = (new Bundle.BundleEntryComponent()).setResource(organization);
        bundle.addEntry(entry);
        // Allergy
        entry = (new Bundle.BundleEntryComponent()).setResource(allergy);
        bundle.addEntry(entry);
        // Medication Statement
        entry = (new Bundle.BundleEntryComponent()).setResource(ms1);
        bundle.addEntry(entry);
        entry = (new Bundle.BundleEntryComponent()).setResource(ms2);
        bundle.addEntry(entry);
        // Medication
        entry = (new Bundle.BundleEntryComponent()).setResource(anastrozole);
        bundle.addEntry(entry);
        entry = (new Bundle.BundleEntryComponent()).setResource(frusemide);
        bundle.addEntry(entry);
        // Condition
        entry = (new Bundle.BundleEntryComponent()).setResource(cond1);
        bundle.addEntry(entry);
        entry = (new Bundle.BundleEntryComponent()).setResource(cond2);
        bundle.addEntry(entry);

        return bundle;
    }


    private Patient buildPatient(Practitioner gp) {
        Patient p = new Patient();
        p.setId(UUID.randomUUID().toString());

        GregorianCalendar bd = new GregorianCalendar(1963, Calendar.MARCH, 24);
        p.setBirthDate(bd.getTime());

        HumanName name = new HumanName();
        name.setFamily("Rossi").addGiven("Maria");
        p.addName(name);

        p.setGender(Enumerations.AdministrativeGender.FEMALE);

        p.addAddress().addLine("Piazza di Spagna")
                .setCity("Roma")
                .setState("Italia")
                .setPostalCode("87654")
                .setUse(Address.AddressUse.HOME);

        p.addGeneralPractitioner(new Reference(gp));

        return p;
    }


    private Practitioner buildPractitioner() {
        Practitioner p = new Practitioner();
        p.setId(UUID.randomUUID().toString());

        GregorianCalendar bd = new GregorianCalendar(1970, Calendar.SEPTEMBER, 9);
        p.setBirthDate(bd.getTime());

        HumanName name = new HumanName();
        name.setFamily("Bianchi").addGiven("Antonio");
        p.addName(name);

        p.setGender(Enumerations.AdministrativeGender.MALE);

        p.addAddress().addLine("Piazza Navona")
                .setCity("Roma")
                .setState("Italia")
                .setPostalCode("13456")
                .setUse(Address.AddressUse.WORK);

        return p;
    }


    private Organization buildOrganization() {
        Organization org = new Organization();
        org.setId(UUID.randomUUID().toString());

        org.setActive(true);

        org.setName("Azienda ospedaliera San Camillo-Forlanini");

        org.addAddress().addLine("Circonvallazione Gianicolense, 87")
                .setCity("Roma")
                .setState("Italia")
                .setPostalCode("00152")
                .setUse(Address.AddressUse.WORK);


        org.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue("+39-6-58701")
                .setUse(ContactPoint.ContactPointUse.WORK);

        return org;
    }


    private AllergyIntolerance buildAllergyIntoleranceForMedication(Patient patient, CodeableConcept code) {
        AllergyIntolerance ai = new AllergyIntolerance();
        ai.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/allergyintolerance-uv-ips");
        ai.setMeta(profile);

        ai.setPatient(new Reference(patient));
        ai.setType(AllergyIntolerance.AllergyIntoleranceType.ALLERGY);
        ai.addCategory(AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION);
        ai.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.HIGH);

        ai.setCode(code);

        return ai;
    }


    private MedicationStatement buildMedicationStatement(Patient patient) {
        MedicationStatement ms = new MedicationStatement();
        ms.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/medicationstatement-uv-ips");
        ms.setMeta(profile);

        ms.setSubject(new Reference(patient));
        ms.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);

        return ms;
    }


    private Medication buildMedication(CodeableConcept code) {
        Medication m = new Medication();
        m.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/medication-uv-ips");
        m.setMeta(profile);

        m.setCode(code);

        return m;
    }

    private Condition buildCondition(Patient patient, CodeableConcept code) {
        Condition c = new Condition();
        c.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/condition-uv-ips");
        c.setMeta(profile);

        c.setCode(code);

        c.setSubject(new Reference(patient));

        return c;
    }

}
