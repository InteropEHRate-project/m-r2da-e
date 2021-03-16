package eu.interopehrate.mr2da.r2d;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public final class QueryGeneratorFactory {
    private static final String[] generators = {
            "eu.interopehrate.mr2da.r2d.DiagnosticReportQueryGenerator",
            "eu.interopehrate.mr2da.r2d.ObservationQueryGenerator"
    };
    private static Map<FHIRResourceCategory, Class<? extends AbstractQueryGenerator>> generatorsMap;

    public static void initialize() throws ClassNotFoundException {
        generatorsMap = new HashMap<FHIRResourceCategory, Class<? extends AbstractQueryGenerator>>();
        generatorsMap.put(FHIRResourceCategory.DIAGNOSTIC_REPORT, DiagnosticReportQueryGenerator.class);
        generatorsMap.put(FHIRResourceCategory.OBSERVATION, ObservationQueryGenerator.class);
        generatorsMap.put(FHIRResourceCategory.DOCUMENT_REFERENCE, DocumentReferenceQueryGenerator.class);
    }

    public static AbstractQueryGenerator getQueryGenerator(FHIRResourceCategory category, IGenericClient fhirClient) {
        Class<? extends AbstractQueryGenerator> qg = generatorsMap.get(category);
        if (qg == null)
            throw new IllegalArgumentException("QueryGeneratorFactory non configured for category: " + category);

        final Constructor<? extends AbstractQueryGenerator> constructor;
        try {
            constructor = qg.getConstructor(IGenericClient.class);
            return constructor.newInstance(fhirClient);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
