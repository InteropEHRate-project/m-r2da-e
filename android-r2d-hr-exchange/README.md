MR2DA Android Library - The InteropEHRate project (www.interopEHRate.eu)

This Android Library, allows apps to use the R2DAccess protocol without requiring specific
knowledge about the protocol itself, hiding all the technical details regarding the underlying
HTTP/Rest communication protocol.

This library acts as an object oriented proxy to the REST services provided by an R2DAccess Server.
Moreover, it hides to the user all the technical details about the long running transactions
delivered by an R2D Access Server. The library constantly monitors the status of a request and
when it has completed it notifies the hosting app.

This version of the library provides several operations for allowing a client app to download the
following types of health data:
- Patient Summary: in FHIR format according to International Patient Summary specifications defined
  by HL7.
- Laboratory Report: represented with the FHIR resource named DiagnosticReport;
- Diagnostic Reports;
- Prescriptions: represented with the FHIR resource named MedicationRequest;
- Vital Signs: represented with the FHIR resource named Observation compliant to the specific
  profile defined by the InteropEHRate project.
- Encounters: represented with the FHIR resource named Encounter.
- Diagnosis and Symptoms: represented with the FHIR resource named Condition.
- Document: represented with the FHIR resource named Composition.
- Document: represented with the FHIR resource named DocumentReference.
- Allergies: represented with the FHIR resource named AllergyIntolerance.
- Immunization: represented with the FHIR resource named Immunization.
- Measured values: represented with the FHIR resource named Observation.
- Heterogeneous bundle of health data related to an Encounter.
- Heterogeneous bundle of health data related to the Patient.

To use the MR2DA library developers have to execute two stpes:
1) creating an implementation of the interface MR2DACallbackHandler;
2) getting an instance of MR2DA;

Once these two steps have been executed, it would be possibile to use the methods provided by the
MR2DA library to request health data of the authenticated citizen. The R2D Access transactions
are long running transactions, and are served asynchronously by and R2D Access server.
Due to the nature of these transactions, users of the MR2DA library, are requested to:
1) use methods provided by the MR2DA inteface to submit requests to a R2D Access server;
2) implements methods of the interface MR2DACallbackHandler to be notified about the
status of a previously submitted request.

MR2DACallbackHandler interface:

- onRequestCompleted: notifies the user that a request (of whatever type has finished).
- onDownloadStarted: notifies the user that server side processing of data has completed and that
                     download of data has started.
- onSearchCompleted: Invoked when a search method has completed successfully and data have
                     been downloaded. This method defines two arguments, the first one is of
                     type eu.interopehrate.protocols.common.ResourceCategory and states what
                     kind of search has completed, while the second one is of type
                     org.hl7.fhir.r4.model.Bundle and contains the received health data.
- onPatientSummaryCompleted: Invoked when a the request for the patient summary has completed
                     successfully and data have been downloaded. This method defines one argument
                     of type org.hl7.fhir.r4.model.Bundle that contains the received health data.
- onEncounterEverythingCompleted: Invoked when a the request for the encounter everything method
                     has completed successfully and data have been downloaded. This method defines
                     one argument of type org.hl7.fhir.r4.model.Bundle that contains the received
                     health data.
- onProvenanceValidationError: Invoked when heath data have been downloaded, but validation of the
                     provenance has failed. This method defines one argument of type
                     eu.interopehrate.mr2da.provenance.ProvenanceValidationResults containing
                     detailed information about the validation errors produced during the
                     validation process.
- onError: Invoked when a request for health data, has thrown an error. This method defines only
                     one argument of tye String, containing the description of the error.

Creating an instance of MR2DA

To obtain an instance of MR2DA, developers must use the static method create() of the class
MR2DFactory, as shown in the following example:

  MR2DA mr2da = MR2DAFactory.create(
                "https://hospitalOne/R2D",
                authToken,
                anInstanceofMR2DACallbackHandler,
                Locale.ITALIAN);

The create() method takes as input parameter the endpoint of the R2D server that it must connect to,
the authentication token retrieved with the eIDAS login, and the language of the citizen.
If the S-EHR app needs to connect to more than one R2D server, it must creates a MR2DA
instance for every R2D server:

  MR2DA mr2da_1 = MR2DAFactory.create(
                "https://hospitalOne/r2da",
                authToken,
                anInstanceofMR2DACallbackHandler,
                Locale.ITALIAN);

  MR2DA mr2da_2 = MR2DAFactory.create(
                "https://hospitalTWO/r2da",
                authToken,
                anotherInstanceofMR2DACallbackHandler,
                Locale.ITALIAN);

Sample code:
1) Requesting the Patient Summary

  Bundle psBundle = (Bundle)mr2da.getPatientSummary();
  Composition ps = (Composition)psBundle.getEntryFirstRep().getResource();

2) Requesting the Search of Encounters from a certain date

  GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
  mr2da.getResourcesByCategory(FHIRResourceCategory.ENCOUNTER, gc.getTime(), false);

3) Requesting health data produced during an Encounter

  mr2da.getEncounterEverything(anEncounter.getId());

4) Search of Diagnostic Reports from a certain date

  GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
  mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT, gc.getTime(), false);

5) Search of Diagnostic Reports and Observations from a certain date

  GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
  mr2da.getResourcesByCategories(gc.getTime(), false, FHIRResourceCategory.DIAGNOSTIC_REPORT,
   FHIRResourceCategory.OBSERVATION);

6) Search of Diagnostic Reports by code

  mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT, null,
  "http://loinc.org|30954-2", null, false);

7) Search of DocumentReference by code (Discharge Report code)

  mr2da.getResourcesByCategory(FHIRResourceCategory.DOCUMENT_REFERENCE, null,
   "http://loinc.org|18842-5", null, false);

8) Search of Diagnostic Reports by Sub-category

  mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT, "LAB", null, null, false);
