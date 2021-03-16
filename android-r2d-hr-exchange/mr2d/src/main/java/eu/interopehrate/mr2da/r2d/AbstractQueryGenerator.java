package eu.interopehrate.mr2da.r2d;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Bundle;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public abstract class AbstractQueryGenerator {

    protected final static String ACCEPT_JSON = "application/fhir+json";
    protected final int MOST_RECENT_ITEMS_SIZE = 5;
    protected IGenericClient fhirClient;

    public AbstractQueryGenerator(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    public abstract IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts);

    /*
    public IQuery<Bundle> generateQueryForSearchMostRecent(Arguments args) {
        IQuery<Bundle> q = generateQueryForSearch(args);
        if (args.hasArgument(ArgumentName.COUNT))
            q = q.count((Integer)args.getValueByName(ArgumentName.COUNT));
        else
            q = q.count(MOST_RECENT_ITEMS_SIZE);

        return q;
    }
    */

    protected IQuery<Bundle> addSystemAndCodeArgument(IQuery<Bundle> query,
                                        @NonNull String systemAndCode, TokenClientParam param) {
        if (systemAndCode.indexOf('|') < 0)
            query = query.and(param.exactly().code(systemAndCode));
        else {
            String[] parts = StringUtils.split(systemAndCode, '|');
            query = query.and(param.exactly().systemAndCode(parts[0], parts[1]));
        }
        return query;
    }

}
