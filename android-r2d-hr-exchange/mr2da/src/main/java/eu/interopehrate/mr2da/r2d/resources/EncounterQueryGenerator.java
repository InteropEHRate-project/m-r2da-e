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

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;

import java.util.Date;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class EncounterQueryGenerator extends AbstractQueryGenerator {

    public EncounterQueryGenerator(IGenericClient fhirClient)  {
        super(fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        Log.d("MR2DA", "Generating query for Encounter...");

        // Builds the basic query
        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Encounter.class)
                .accept(ACCEPT_JSON)
                .returnBundle(Bundle.class)
                .where(Encounter.STATUS.exactly().code(Encounter.EncounterStatus.FINISHED.toCode()));

        // Checks how to sort results
        if (opts.hasOption(OptionName.SORT)) {
            Object sort = opts.getValueByName(OptionName.SORT);
            if (sort == Option.Sort.SORT_ASCENDING_DATE)
                q = q.sort().ascending(Encounter.DATE);
            else
                q = q.sort().descending(Encounter.DATE);
        }

        // Checks if has been provided a FROM argument
        if (args.hasArgument(ArgumentName.FROM)) {
            Date from = (Date)args.getValueByName(ArgumentName.FROM);
            q = q.and(Encounter.DATE.afterOrEquals().day(from));
        }

        return q;
    }

    /**
     * Generate the operation to invoke Encounter/id/$everything
     * @param encounterId
     * @return
     */
    public IOperationUntypedWithInput<Bundle> generateEncounterEverythingOperation(String encounterId) {
        return fhirClient.operation()
                .onInstance(new IdType(encounterId))
                .named("$everything")
                .withNoParameters(Parameters.class)
                .useHttpGet()
                .returnResourceType(Bundle.class);
    }

}
