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
import org.hl7.fhir.r4.model.Patient;

import java.util.Date;

import ca.uhn.fhir.rest.api.MethodOutcome;
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
public class PatientQueryGenerator extends AbstractQueryGenerator {

    public PatientQueryGenerator(IGenericClient fhirClient)  {
        super(fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        Log.d("MR2DA", "Generating query for Patient...");

        // Builds the basic query
        IQuery<Bundle> q = fhirClient
                .search()
                .forResource(Patient.class)
                .accept(ACCEPT_JSON)
                .returnBundle(Bundle.class);

        return q;
    }

    /**
     * Generate the operation to invoke Encounter/id/$everything
     * @return
     */
    public IOperationUntypedWithInput<Bundle> generatePatientSummaryOperation() {
        return fhirClient.operation()
                .onType(Patient.class)
                .named("$patient-summary")
                .withNoParameters(Parameters.class)
                .useHttpGet()
                .returnResourceType(Bundle.class);
    }

    /**
     * Generate the operation to invoke Encounter/id/$everything
     * @return
     */
    public IOperationUntypedWithInput<Bundle> generatePatientEverythingOperation() {
        return fhirClient.operation()
                .onType(Patient.class)
                .named("$everything")
                .withNoParameters(Parameters.class)
                .useHttpGet()
                .returnResourceType(Bundle.class);
    }

}
