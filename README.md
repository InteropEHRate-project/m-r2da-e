
MR2DA (Mobile R2D Access) Library is a Java library for Android Apps using R2D protocol, it acts as a proxy for a R2D compliant server.

Example usage: 

        MR2DSM mr2dsm = MR2DSMFactory.create(Locale.ITALY);
        mr2dsm.login("mario.rossi","interopehrate");

        MR2DA mr2da = MR2DAFactory.create("http://213.249.46.205:8080/R2D/fhir/", mr2dsm);

        // retrieving Patient Summary
        Bundle psBundle = (Bundle)mr2da.getPatientSummary();
        Composition ps = (Composition)psBundle.getEntryFirstRep().getResource();

        // retrieving Diagnostic Reports from a Date
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                gc.getTime(), false);

        // retrieving Diagnostic Reports and Observation from a Date
        GregorianCalendar gc = new GregorianCalendar(2019, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategories(gc.getTime(), false,
                                FHIRResourceCategory.DIAGNOSTIC_REPORT,
                                FHIRResourceCategory.OBSERVATION);

        // retrieving Diagnostic Reports with a certain type
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                null, "http://loinc.org|30954-2", null, false);

        // retrieving Diagnostic Reports with a certain category
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DIAGNOSTIC_REPORT,
                "LAB", null, null, false);

        // retrieving Image Reports from a certain date
        GregorianCalendar gc = new GregorianCalendar(2015, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(DocumentCategory.IMAGE_REPORT,
                gc.getTime(), false);

        // retrieving Laboratory Reports from a certain date
        GregorianCalendar gc = new GregorianCalendar(2015, Calendar.JANUARY, 01);
        Iterator<Resource> it = mr2da.getResourcesByCategory(DocumentCategory.LABORATORY_REPORT,
                gc.getTime(), false);

        // retrieving Discharge Reports
        Iterator<Resource> it = mr2da.getResourcesByCategory(FHIRResourceCategory.DOCUMENT_REFERENCE,
                null, LoincCodes.DISCHARGE_REPORT.getSystemAndCode(), null, false);
