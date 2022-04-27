package eu.interopehrate.mr2da.r2d.document;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class DocumentQueryGeneratorFactory {
    private static Map<DocumentCategory, List<Class<?>>> generatorsMap;

    public static void initialize(InputStream resourceConfigFile) throws ClassNotFoundException, IOException {
        Properties config = new Properties();
        config.load(resourceConfigFile);

        generatorsMap = new HashMap<DocumentCategory, List<Class<?>>> ();

        Iterator<String> propertyNames = config.stringPropertyNames().iterator();
        String documentCategoryName;
        String generatorClassName;
        Class generatorClass;
        DocumentCategory documentCategory;
        List<Class <?>> generatorsList;
        while (propertyNames.hasNext()) {
            documentCategoryName = propertyNames.next();
            documentCategory = DocumentCategory.valueOf(documentCategoryName);
            if (documentCategory == null) {
                Log.w("MR2DA", "Unknown resource type configured in properties file. " +
                        "Property '" + documentCategoryName + "'is ignored.");
                continue;
            }

            StringTokenizer st = new StringTokenizer(config.getProperty(documentCategoryName), ",");
            generatorsList = new ArrayList<>();
            while (st.hasMoreElements()) {
                generatorClassName = st.nextToken();
                generatorClass = Class.forName(generatorClassName);
                if (!AbstractQueryGenerator.class.isAssignableFrom(generatorClass)) {
                    Log.w("MR2DA", "Invalid generator for resource type " + documentCategory +
                            ", " + generatorClassName + " does not extends AbstractQueryGenerator.");
                    continue;
                }
                generatorsList.add(generatorClass);
            }

            Log.d("MR2DA", "Adding generators for type: " + documentCategory);
            generatorsMap.put(documentCategory, generatorsList);
        }

        /*
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
         */

    }


    public static List<AbstractQueryGenerator> getQueryGenerators(DocumentCategory category,
                                                                  IGenericClient fhirClient) {
        List<Class<?>> qGenClss = generatorsMap.get(category);
        if (qGenClss == null)
            throw new IllegalArgumentException("DocumentQueryGeneratorFactory non configured for category: " + category);

        List<AbstractQueryGenerator> qGens = new ArrayList<>(5);
        Constructor<?> constructor;
        for (Class<?> qGen : qGenClss) {
            try {
                constructor = qGen.getConstructor(IGenericClient.class);
                qGens.add((AbstractQueryGenerator)constructor.newInstance(fhirClient));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        return qGens;
    }

}
