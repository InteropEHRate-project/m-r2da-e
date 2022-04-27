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

import android.content.res.XmlResourceParser;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public final class QueryGeneratorFactory {
    // Map used to bind a FHIR resource type to the corresponding Query Generator
    private static Map<FHIRResourceCategory, Class<?>> generatorsMap;


    public static void initialize(InputStream resourceConfigFile) throws ClassNotFoundException, IOException {
        Properties config = new Properties();
        config.load(resourceConfigFile);

        generatorsMap = new HashMap<FHIRResourceCategory, Class<?>>();

        Iterator<String> propertyNames = config.stringPropertyNames().iterator();
        String resourceCategoryName;
        String generatorClassName;
        Class generatorClass;
        FHIRResourceCategory resourceCategory;
        while (propertyNames.hasNext()) {
            resourceCategoryName = propertyNames.next();
            resourceCategory = FHIRResourceCategory.valueOf(resourceCategoryName);
            if (resourceCategory == null) {
                Log.w("MR2DA", "Unknown resource type configured in properties file. " +
                        "Property '" + resourceCategoryName + "'is ignored.");
                continue;
            }

            generatorClassName = config.getProperty(resourceCategoryName);
            generatorClass = Class.forName(generatorClassName);
            if (!AbstractQueryGenerator.class.isAssignableFrom(generatorClass)) {
                Log.w("MR2DA", "Invalid generator for resource type " + resourceCategory +
                        ", " + generatorClassName + " does not extends AbstractQueryGenerator.");
                continue;
            }

            Log.d("MR2DA", "Adding generator for type: " + resourceCategory);
            generatorsMap.put(resourceCategory, generatorClass);
        }
    }

    public static AbstractQueryGenerator getQueryGenerator(FHIRResourceCategory category, IGenericClient fhirClient) {
        Class<?> qg = generatorsMap.get(category);
        if (qg == null)
            throw new IllegalArgumentException("QueryGeneratorFactory non configured for category: " + category);

        final Constructor<?> constructor;
        try {
            constructor = qg.getConstructor(IGenericClient.class);
            return (AbstractQueryGenerator)constructor.newInstance(fhirClient);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
