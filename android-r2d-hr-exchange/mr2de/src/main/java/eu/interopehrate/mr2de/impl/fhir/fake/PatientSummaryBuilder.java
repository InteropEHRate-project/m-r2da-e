package eu.interopehrate.mr2de.impl.fhir.fake;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.Timing;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import eu.interopehrate.mr2de.impl.fhir.utils.LoincCodes;
import eu.interopehrate.mr2de.impl.fhir.utils.SnomedCodes;
import eu.interopehrate.mr2de.impl.fhir.utils.ICD9Codes;

class PatientSummaryBuilder {

    private static final String HTTP_UNITSOFMEASURE_ORG = "http://unitsofmeasure.org";

    static Bundle buildPatientSummary() {
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
        ps.setType(LoincCodes.PATIENT_SUMMARY.getCodeableConcept());
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

        // ALLERGIES SECTION (Required)
        Composition.SectionComponent allergiesSection = new Composition.SectionComponent();
        allergiesSection.setCode(LoincCodes.ALLERGIES_AND_INTOLERANCE.getCodeableConcept());

        AllergyIntolerance allergy = buildAllergyIntoleranceForMedication(patient, SnomedCodes.PENICILLIN.getCodeableConcept());
        allergy.setOnset(new DateTimeType("2010"));
        allergiesSection.addEntry(new Reference(allergy));

        ps.addSection(allergiesSection);

        // ACTIVE PROBLEMS Section (Required)
        Composition.SectionComponent activeProblems = new Composition.SectionComponent();
        activeProblems.setCode(LoincCodes.ACTIVE_PROBLEMS.getCodeableConcept());

        ps.addSection(activeProblems);

        Condition cond1 = buildProblemCondition(patient, ICD9Codes.ALTRE_CARDIOMIOPATIE_PRIMITIVE.getCodeableConcept());
        cond1.setClinicalStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", null)));
        cond1.setVerificationStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-ver-status", "confirmed", null)));
        cond1.setOnset(new DateTimeType("2017-03-30"));
        activeProblems.addEntry(new Reference(cond1));

        Condition cond2 = buildProblemCondition(patient, ICD9Codes.INSUFFICIENZA_CUORE_SINISTRO.getCodeableConcept());
        cond2.setClinicalStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", null)));
        cond2.setVerificationStatus(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/condition-ver-status", "confirmed", null)));
        cond2.setOnset(new DateTimeType("2017-03-30"));
        activeProblems.addEntry(new Reference(cond2));


        // Medication Section (Required)
        Composition.SectionComponent medicationSection = new Composition.SectionComponent();
        medicationSection.setCode(LoincCodes.MEDICATION.getCodeableConcept());

        /*
		1) Mexiletina (Mexiletina cloridrato)</td><td> 200 mg 1 cps x 2 die (ore 8-20)
		2) Carvedilolo (Caravel) </td><td> 25 mg 1 cpr x 2 die(ore 8-20)
		3) Fosinopril (Tensogard) </td><td>) 20 mg 1/2 cpr x 2/die (ore 8-20)
		4) Simvastatina (Sinvacor) </td><td>) 10 mg 1 cpr/die (ore 22)
		5) Spironolattone (Aldactone) </td><td> 20 mg 1 cpr/die (ore 14)
		6) Acetazolamide (Diamox) </td><td>  250 mg 1 cpr/die il lunedì, mercoledì, venerdì (ore 14).
        */

        // Medication #1
        /*
		Mexiletina (Mexiletina cloridrato)</td><td> 200 mg 1 cps x 2 die (ore 8-20)
        */
        MedicationStatement ms1 = buildMedicationStatement(patient);
        Dosage dosage = new Dosage();
        dosage.setPatientInstruction("1 cps, 200mg x 2 die (8:00 - 20:00)");

        Timing timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent())
                .setFrequency(2)
                .setPeriod(1)
                .setPeriodUnit(Timing.UnitsOfTime.D)
                .addTimeOfDay("08:00")
                .addTimeOfDay("20:00"));
        dosage.setTiming(timing);
        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());

        Dosage.DosageDoseAndRateComponent dosAndRate = dosage.addDoseAndRate();
        SimpleQuantity q = new SimpleQuantity();
        q.setValue(200);
        q.setUnit("mg");
        q.setCode("mg");
        q.setSystem(HTTP_UNITSOFMEASURE_ORG);
        dosAndRate.setDose(q);
        ms1.addDosage(dosage);

        CodeableConcept mexiletinaCode = new CodeableConcept(new Coding("WHO ATC", "R05DA04", "Mexiletina  (Mexiletina cloridrato)"));
        Medication mexiletina = buildMedication(mexiletinaCode);
        ms1.setMedication(new Reference(mexiletina));
        medicationSection.addEntry(new Reference(ms1));

        // Medication #2
        /*
         * Carvedilolo (Caravel) </td><td> 25 mg 1 cpr x 2 die(ore 8-20)
         */
        MedicationStatement ms2 = buildMedicationStatement(patient);
        dosage = new Dosage();
        dosage.setPatientInstruction("1 cps, 25mg x 2 die (8:00 - 20:00)");

        timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent())
                .setFrequency(2)
                .setPeriod(1)
                .setPeriodUnit(Timing.UnitsOfTime.D)
                .addTimeOfDay("08:00")
                .addTimeOfDay("20:00"));
        dosage.setTiming(timing);
        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());

        dosAndRate = dosage.addDoseAndRate();
        q = new SimpleQuantity();
        q.setValue(25);
        q.setUnit("mg");
        q.setCode("mg");
        q.setSystem(HTTP_UNITSOFMEASURE_ORG);
        dosAndRate.setDose(q);
        ms2.addDosage(dosage);

        CodeableConcept carvediloloCode = new CodeableConcept(new Coding("WHO ATC", "C07AG02", "Carvedilolo (Caravel)"));
        Medication carvedilolo = buildMedication(carvediloloCode);
        ms2.setMedication(new Reference(carvedilolo));
        medicationSection.addEntry(new Reference(ms2));

        // Medication #3
        /*
         * Fosinopril (Tensogard) </td><td>) 20 mg 1/2 cpr x 2/die (ore 8-20)
         */
        MedicationStatement ms3 = buildMedicationStatement(patient);
        dosage = new Dosage();
        dosage.setPatientInstruction("1/2 cps, 20mg x 2 die (8:00 - 20:00)");

        timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent())
                .setFrequency(2)
                .setPeriod(1)
                .setPeriodUnit(Timing.UnitsOfTime.D)
                .addTimeOfDay("08:00")
                .addTimeOfDay("20:00"));
        dosage.setTiming(timing);
        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());

        dosAndRate = dosage.addDoseAndRate();
        q = new SimpleQuantity();
        q.setValue(20);
        q.setUnit("mg");
        q.setCode("mg");
        q.setSystem(HTTP_UNITSOFMEASURE_ORG);
        dosAndRate.setDose(q);
        ms3.addDosage(dosage);

        CodeableConcept fosinoprilCode = new CodeableConcept(new Coding("WHO ATC", "C09AA09", "Fosinopril"));
        Medication fosinopril = buildMedication(fosinoprilCode);
        ms3.setMedication(new Reference(fosinopril));
        medicationSection.addEntry(new Reference(ms3));

        // Medication #4
        /*
         * Simvastatina (Sinvacor) </td><td>) 10 mg 1 cpr/die (ore 22)
         */
        MedicationStatement ms4 = buildMedicationStatement(patient);
        dosage = new Dosage();
        dosage.setPatientInstruction("1 cps 10mg die (22:00)");

        timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent())
                .setFrequency(1)
                .setPeriod(1)
                .setPeriodUnit(Timing.UnitsOfTime.D)
                .addTimeOfDay("22:00"));

        dosage.setTiming(timing);
        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());
        dosAndRate = dosage.addDoseAndRate();

        q = new SimpleQuantity();
        q.setValue(10);
        q.setUnit("mg");
        q.setCode("mg");
        q.setSystem(HTTP_UNITSOFMEASURE_ORG);
        dosAndRate.setDose(q);
        ms4.addDosage(dosage);

        CodeableConcept simvastatinaCode = new CodeableConcept(new Coding("WHO ATC", "C10AA01", "Simvastatina (Sinvacor)"));
        Medication simvastatina = buildMedication(simvastatinaCode);
        ms4.setMedication(new Reference(simvastatina));
        medicationSection.addEntry(new Reference(ms4));


        // Medication #5
        /*
         * Spironolattone (Aldactone) </td><td> 20 mg 1 cpr/die (ore 14)
         */
        MedicationStatement ms5 = buildMedicationStatement(patient);
        dosage = new Dosage();
        dosage.setPatientInstruction("1 cps 20mg die (14:00)");

        timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent())
                .setFrequency(1)
                .setPeriod(1)
                .setPeriodUnit(Timing.UnitsOfTime.D)
                .addTimeOfDay("14:00"));

        dosage.setTiming(timing);
        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());
        dosAndRate = dosage.addDoseAndRate();

        q = new SimpleQuantity();
        q.setValue(20);
        q.setUnit("mg");
        q.setCode("mg");
        q.setSystem(HTTP_UNITSOFMEASURE_ORG);
        dosAndRate.setDose(q);
        ms5.addDosage(dosage);

        CodeableConcept spironolattoneCode = new CodeableConcept(new Coding("WHO ATC", "C03DA01", "Spironolattone (Aldactone)"));
        Medication spironolattone = buildMedication(spironolattoneCode);
        ms5.setMedication(new Reference(spironolattone));
        medicationSection.addEntry(new Reference(ms5));

        // Medication #6
        /*
         * Acetazolamide (Diamox) </td><td>  250 mg 1 cpr/die il lunedì, mercoledì, venerdì (ore 14).
         */
        MedicationStatement ms6 = buildMedicationStatement(patient);
        dosage = new Dosage();
        dosage.setPatientInstruction("1 cps 250mg die lun, mer, ven (14:00)");

        timing = new Timing();
        timing.setRepeat((new Timing.TimingRepeatComponent())
                .setFrequency(3)
                .setPeriod(1)
                .setPeriodUnit(Timing.UnitsOfTime.WK)
                .addDayOfWeek(Timing.DayOfWeek.MON)
                .addDayOfWeek(Timing.DayOfWeek.WED)
                .addDayOfWeek(Timing.DayOfWeek.FRI)
                .addTimeOfDay("14:00"));

        dosage.setTiming(timing);
        dosage.setRoute(SnomedCodes.ORAL_USE.getCodeableConcept());
        dosAndRate = dosage.addDoseAndRate();

        q = new SimpleQuantity();
        q.setValue(20);
        q.setUnit("mg");
        q.setCode("mg");
        q.setSystem(HTTP_UNITSOFMEASURE_ORG);
        dosAndRate.setDose(q);
        ms6.addDosage(dosage);

        CodeableConcept acetazolamideCode = new CodeableConcept(new Coding("WHO ATC", "S01EC01", "Acetazolamide (Diamox)"));
        Medication acetazolamide = buildMedication(acetazolamideCode);
        ms6.setMedication(new Reference(acetazolamide));
        medicationSection.addEntry(new Reference(ms6));

        ps.addSection(medicationSection);

        // Relevant diagnostic tests &or laboratory data (Suggested)
        Composition.SectionComponent resultsSection = new Composition.SectionComponent();
        resultsSection.setCode(LoincCodes.RESULTS.getCodeableConcept());

        // Vital Signs Observations
        Observation vitalSigns = buildObservation(LoincCodes.VITAL_SIGNS.getCodeableConcept(), patient);
        DateTimeType eff = new DateTimeType("2017-03-30T08:45:00");
        vitalSigns.setEffective(eff);
        resultsSection.addEntry(new Reference(vitalSigns));

        Observation weight = buildObservation(LoincCodes.BODY_WEIGHT.getCodeableConcept(), patient);
        Quantity quantity = new Quantity();
        quantity.setValue(108).setUnit("kg").setSystem(HTTP_UNITSOFMEASURE_ORG);
        weight.setValue(quantity).setEffective(eff);
        vitalSigns.addHasMember(new Reference(weight));

        Observation height = buildObservation(LoincCodes.BODY_HEIGHT.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(185).setUnit("cm").setSystem(HTTP_UNITSOFMEASURE_ORG);
        height.setValue(quantity).setEffective(eff);
        vitalSigns.addHasMember(new Reference(height));

        Observation systolic = buildObservation(LoincCodes.SYSTOLIC.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(115).setUnit("mm[Hg]").setSystem(HTTP_UNITSOFMEASURE_ORG);
        systolic.setValue(quantity).setEffective(eff);
        vitalSigns.addHasMember(new Reference(systolic));

        Observation diastolic = buildObservation(LoincCodes.DIASTOLIC.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(60).setUnit("mm[Hg]").setSystem(HTTP_UNITSOFMEASURE_ORG);
        diastolic.setValue(quantity).setEffective(eff);
        vitalSigns.addHasMember(new Reference(diastolic));

        // Hematology Studies Observations
        Observation hematologyStudies = buildObservation(LoincCodes.HEMATOLOGY_STUDIES.getCodeableConcept(), patient);
        eff = new DateTimeType("2017-11-17T08:45:00");
        hematologyStudies.setEffective(eff);
        resultsSection.addEntry(new Reference(hematologyStudies));

        Observation glucose = buildObservation(LoincCodes.BLOOD_GLUCOSE.getCodeableConcept(), patient);
        quantity = new Quantity();
        // TODO: Il valore presente nel file inviatoci da FTGM presenta un valore probabilmente errato di 12345
        quantity.setValue(120).setUnit("mg/dL").setSystem(HTTP_UNITSOFMEASURE_ORG);
        glucose.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(glucose));

        Observation troponin = buildObservation(LoincCodes.TROPONIN.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(15.36).setUnit("ng/L").setSystem(HTTP_UNITSOFMEASURE_ORG);
        troponin.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(troponin));

        Observation bloodUrea = buildObservation(LoincCodes.BLOOD_UREA.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(55.6).setUnit("mg/dL").setSystem(HTTP_UNITSOFMEASURE_ORG);
        bloodUrea.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(bloodUrea));

        Observation creatinine = buildObservation(LoincCodes.CREATININA.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(1.10).setUnit("mg/dL").setSystem(HTTP_UNITSOFMEASURE_ORG);
        creatinine.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(creatinine));

        Observation glomFiltration = buildObservation(LoincCodes.GLOMERULAR_FILTRATION.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(69).setUnit("mL/min/1,73m2").setSystem(HTTP_UNITSOFMEASURE_ORG);
        glomFiltration.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(glomFiltration));

        Observation bloodSodium = buildObservation(LoincCodes.BLOOD_SODIUM.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(137.9).setUnit("mEq/L").setSystem(HTTP_UNITSOFMEASURE_ORG);
        bloodSodium.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(bloodSodium));

        Observation bloodPotassium = buildObservation(LoincCodes.BLOOD_POTASSIUM.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(3.76).setUnit("mEq/L").setSystem(HTTP_UNITSOFMEASURE_ORG);
        bloodPotassium.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(bloodPotassium));

        Observation bloodChloride = buildObservation(LoincCodes.BLOOD_CHLORIDE.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(109.1).setUnit("mEq/L").setSystem(HTTP_UNITSOFMEASURE_ORG);
        bloodChloride.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(bloodChloride));

        Observation peptide = buildObservation(LoincCodes.NATRIURETIC_PEPTIDE.getCodeableConcept(), patient);
        quantity = new Quantity();
        quantity.setValue(290).setUnit("ng/L").setSystem(HTTP_UNITSOFMEASURE_ORG);
        peptide.setValue(quantity).setEffective(eff);
        hematologyStudies.addHasMember(new Reference(peptide));

        ps.addSection(resultsSection);

        // Adding entries to bundle
        // Patient Summary
        Bundle.BundleEntryComponent entry = (new Bundle.BundleEntryComponent()).setResource(ps);
        bundle.addEntry(entry);

        // Practitioner
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(practitioner));

        // Patient
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(patient));

        // Organization
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(organization));

        // Allergy
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(allergy));

        // Medication Statement
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(ms1));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(ms2));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(ms3));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(ms4));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(ms5));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(ms6));

        // Medication
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(mexiletina));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(carvedilolo));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(fosinopril));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(simvastatina));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(spironolattone));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(acetazolamide));

        // Condition
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(cond1));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(cond2));

        // Observation
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(vitalSigns));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(hematologyStudies));

        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(weight));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(height));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(systolic));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(diastolic));

        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(glucose));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(troponin));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(bloodUrea));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(creatinine));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(glomFiltration));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(bloodSodium));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(bloodPotassium));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(bloodChloride));
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(peptide));

        return bundle;
    }

    private static Patient buildPatient(Practitioner gp) {
        Patient p = new Patient();
        p.setId(UUID.randomUUID().toString());

        Identifier id = new Identifier();
        id.setValue("RSSMRA58A01G702E");
        p.addIdentifier(id);

        GregorianCalendar bd = new GregorianCalendar(1958, Calendar.JANUARY, 1);
        p.setBirthDate(bd.getTime());

        HumanName name = new HumanName();
        name.setFamily("Rossi").addGiven("Mario");
        p.addName(name);

        p.setGender(Enumerations.AdministrativeGender.MALE);

        p.addAddress().addLine("Piazza del Duomo")
                .setCity("Pisa")
                .setState("Italia")
                .setPostalCode("56011")
                .setUse(Address.AddressUse.HOME);

        p.addTelecom()
                .setValue("+393391234567")
                .setSystem(ContactPointSystem.PHONE)
                .setUse(ContactPoint.ContactPointUse.MOBILE);

        p.addGeneralPractitioner(new Reference(gp));

        return p;
    }


    private static Practitioner buildPractitioner() {
        Practitioner p = new Practitioner();
        p.setId(UUID.randomUUID().toString());

        GregorianCalendar bd = new GregorianCalendar(1958, Calendar.JANUARY, 1);
        p.setBirthDate(bd.getTime());

        HumanName name = new HumanName();
        name.setFamily("Emdin").addGiven("Michele");
        p.addName(name);

        p.setGender(Enumerations.AdministrativeGender.MALE);

        p.addAddress().addLine("Via Moruzzi, 1")
                .setCity("Pisa")
                .setState("Italia")
                .setPostalCode("56126")
                .setUse(Address.AddressUse.WORK);

        p.addTelecom()
                .setValue("michele.emdin@ftgm.it")
                .setSystem(ContactPointSystem.EMAIL)
                .setUse(ContactPoint.ContactPointUse.MOBILE);

        return p;
    }


    private static Organization buildOrganization() {
        Organization org = new Organization();
        org.setId(UUID.randomUUID().toString());

        org.setActive(true);

        org.setName("Fondazione Gabriele Monasterio");

        org.addAddress().addLine("Via Moruzzi, 1")
                .setCity("Pisa")
                .setState("Italia")
                .setPostalCode("56126")
                .setUse(Address.AddressUse.WORK);

        return org;
    }


    private static AllergyIntolerance buildAllergyIntoleranceForMedication(Patient patient, CodeableConcept code) {
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


    private static MedicationStatement buildMedicationStatement(Patient patient) {
        MedicationStatement ms = new MedicationStatement();
        ms.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/medicationstatement-uv-ips");
        ms.setMeta(profile);

        ms.setSubject(new Reference(patient));
        ms.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);

        return ms;
    }


    private static Medication buildMedication(CodeableConcept code) {
        Medication m = new Medication();
        m.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/medication-uv-ips");
        m.setMeta(profile);
        m.setCode(code);

        return m;
    }


    private static Observation buildObservation(CodeableConcept code, Patient patient) {
        Observation o = new Observation();
        o.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/observation-laboratory-uv-ips");
        o.setMeta(profile);
        o.setCode(code);
        o.setStatus(ObservationStatus.FINAL);
        o.setSubject(new Reference(patient));

        return o;
    }

    private static Condition buildProblemCondition(Patient patient, CodeableConcept code) {
        Condition c = new Condition();
        c.setId(UUID.randomUUID().toString());
        Meta profile = new Meta();
        profile.addProfile("http://hl7.org/fhir/uv/ips/StructureDefinition/condition-uv-ips");
        c.setMeta(profile);

        c.addCategory(LoincCodes.PROBLEM.getCodeableConcept());

        c.setCode(code);

        c.setSubject(new Reference(patient));

        return c;
    }

}
