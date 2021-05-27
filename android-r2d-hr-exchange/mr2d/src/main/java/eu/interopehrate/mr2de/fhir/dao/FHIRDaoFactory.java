package eu.interopehrate.mr2de.fhir.dao;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.api.HealthDataType;

/**
 *       Author: Engineering S.p.A. (www.eng.it)
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Factory class for instantiating DAO related to a specific HealthRecordType.
 */

@Deprecated
public class FHIRDaoFactory {

    /*
    private static Set<Class<?>> annotatedClasses;

    static {
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.setUrls(ClasspathHelper.forPackage("eu.interopehrate.mr2de.fhir.dao"));
        config.setScanners(new TypeAnnotationsScanner());
        Reflections reflections = new Reflections(config);
        annotatedClasses = reflections.getTypesAnnotatedWith(HealthRecordDAOConfig.class, true);
    }
    */

    /**
     * Creates instances of DAO based on HealthRecordType and ResponseFormat.
     *
     * @param fhirClient
     * @param type
     * @return
     */
    public static GenericFHIRDAO create(IGenericClient fhirClient, HealthDataType type) {

        if (type == HealthDataType.PATIENT_SUMMARY)
            return new PatientSummaryDAO(fhirClient);
        else if (type == HealthDataType.LABORATORY_RESULT)
            return new LaboratoryResultsDAO(fhirClient);
        else if (type == HealthDataType.MEDICAL_IMAGE)
            return new MedicalImagesDAO(fhirClient);
        else
            throw new IllegalArgumentException("DAO non trovato per type: " + type);
        /*
        Class<?> current;
        while (annotatedClasses.iterator().hasNext()) {
            current = annotatedClasses.iterator().next();
            if (current.isAssignableFrom(GenericFHIRDAO.class)) {
                HealthRecordDAOConfig a = current.getAnnotation(HealthRecordDAOConfig.class);
                if (a == null)
                    continue;

                if (a.type() == type && a.format() == format) {
                    try {
                        Constructor c = current.getConstructor(IGenericClient.class);
                        return (GenericFHIRDAO)c.newInstance(fhirClient);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        */

    }

}
