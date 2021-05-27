/**
 Copyright 2021 Engineering S.p.A. (www.eng.it) - InteropEHRate (www.interopehrate.eu)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package eu.interopehrate.mr2da.r2d.resources;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.DiagnosticReportQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.DocumentManifestQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.DocumentReferenceQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.MedicationRequestQueryGenerator;
import eu.interopehrate.mr2da.r2d.resources.ObservationQueryGenerator;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public final class QueryGeneratorFactory {
    private static Map<FHIRResourceCategory, Class<? extends AbstractQueryGenerator>> generatorsMap;

    public static void initialize() throws ClassNotFoundException {
        generatorsMap = new HashMap<FHIRResourceCategory, Class<? extends AbstractQueryGenerator>>();

        generatorsMap.put(FHIRResourceCategory.DIAGNOSTIC_REPORT, DiagnosticReportQueryGenerator.class);
        generatorsMap.put(FHIRResourceCategory.OBSERVATION, ObservationQueryGenerator.class);
        generatorsMap.put(FHIRResourceCategory.DOCUMENT_REFERENCE, DocumentReferenceQueryGenerator.class);
        generatorsMap.put(FHIRResourceCategory.DOCUMENT_MANIFEST, DocumentManifestQueryGenerator.class);
        generatorsMap.put(FHIRResourceCategory.MEDICATION_REQUEST, MedicationRequestQueryGenerator.class);

        // IMMUNIZATION, ALLERGY, CONDITION, ENCOUNTER
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
