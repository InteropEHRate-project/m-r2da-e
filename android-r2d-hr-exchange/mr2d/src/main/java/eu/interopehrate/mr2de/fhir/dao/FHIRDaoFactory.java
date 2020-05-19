package eu.interopehrate.mr2de.fhir.dao;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Set;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.annotations.HealthRecordDAOConfig;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.ResponseFormat;
import eu.interopehrate.mr2de.r2d.dao.HealthRecordDAO;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Factory class for instantiating DAO related to a specific HealthRecordType.
 */
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
    public static GenericFHIRDAO create(IGenericClient fhirClient, HealthRecordType type) {

        if (type == HealthRecordType.PATIENT_SUMMARY)
            return new PatientSummaryDAO(fhirClient);
        else if (type == HealthRecordType.LABORATORY_REPORT)
            return new LaboratoryReportsDAO(fhirClient);
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
