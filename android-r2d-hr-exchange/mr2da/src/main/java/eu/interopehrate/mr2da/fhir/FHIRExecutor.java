package eu.interopehrate.mr2da.fhir;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.util.Iterator;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.protocols.common.ResourceCategory;

public interface FHIRExecutor {

    void setFhirClient(IGenericClient fhirClient);
    /**
     *
     * @param args
     * @param opts
     * @param categories
     * @return
     */
    Iterator<Resource> executeQueries(Arguments args, Options opts, ResourceCategory... categories);

    /**
     *
     * @param operation
     * @return
     */
    Bundle executeOperation(IOperationUntypedWithInput<Bundle> operation);

}
