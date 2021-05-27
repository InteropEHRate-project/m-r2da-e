package eu.interopehrate.mr2da.r2d.document;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.protocols.common.DocumentCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class DocumentQueryGeneratorFactory {
    private static Map<DocumentCategory, List<Class<? extends AbstractQueryGenerator>>> generatorsMap;

    public static void initialize() throws ClassNotFoundException {
        generatorsMap = new HashMap<DocumentCategory, List<Class<? extends AbstractQueryGenerator>>> ();

        List<Class<? extends AbstractQueryGenerator>> qGenClss = new ArrayList<>(5);
        qGenClss.add(StructuredLaboratoryReportQueryGenerator.class);
        qGenClss.add(UnstructuredLaboratoryReportQueryGenerator.class);
        generatorsMap.put(DocumentCategory.LABORATORY_REPORT, qGenClss);


        qGenClss = new ArrayList<>(5);
        qGenClss.add(StructuredImageReportQueryGenerator.class);
        qGenClss.add(UnstructuredImageReportQueryGenerator.class);
        generatorsMap.put(DocumentCategory.IMAGE_REPORT, qGenClss);

        qGenClss = new ArrayList<>(5);
        qGenClss.add(PatientSummaryQueryGenerator.class);
        generatorsMap.put(DocumentCategory.PATIENT_SUMMARY, qGenClss);
    }


    public static List<AbstractQueryGenerator> getQueryGenerators(DocumentCategory category,
                                                                  IGenericClient fhirClient) {
        List<Class<? extends AbstractQueryGenerator>> qGenClss = generatorsMap.get(category);
        if (qGenClss == null)
            throw new IllegalArgumentException("DocumentQueryGeneratorFactory non configured for category: " + category);

        List<AbstractQueryGenerator> qGens = new ArrayList<>(5);
        Constructor<? extends AbstractQueryGenerator> constructor;
        for (Class<? extends AbstractQueryGenerator> qGen : qGenClss) {
            try {
                constructor = qGen.getConstructor(IGenericClient.class);
                qGens.add(constructor.newInstance(fhirClient));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        return qGens;
    }

}
