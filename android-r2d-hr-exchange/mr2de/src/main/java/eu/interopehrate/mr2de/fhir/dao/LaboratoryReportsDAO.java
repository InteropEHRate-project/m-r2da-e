package eu.interopehrate.mr2de.fhir.dao;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import eu.interopehrate.mr2de.r2d.executor.Arguments;

public class LaboratoryReportsDAO extends GenericFHIRDAO {

    public LaboratoryReportsDAO(IGenericClient client) {
        super(client);
    }

    @Override
    public Bundle search(Arguments args) {
        return null;
    }

    @Override
    public Resource getLast() {
        return null;
    }
}
