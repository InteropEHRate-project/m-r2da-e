package eu.interopehrate.mr2da.r2d.document;

import org.hl7.fhir.r4.model.Bundle;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.resources.QueryGeneratorFactory;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
class StructuredLaboratoryReportQueryGenerator extends AbstractQueryGenerator {
    private static final String DR_LAB_CATEGORY = "LAB";
    private AbstractQueryGenerator structuredLabRepGenerator;

    public StructuredLaboratoryReportQueryGenerator(IGenericClient fhirClient) {
        super(fhirClient);
        structuredLabRepGenerator = QueryGeneratorFactory.getQueryGenerator(
                FHIRResourceCategory.DIAGNOSTIC_REPORT, this.fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        // Creates new Arguments instance
        Arguments newArgs = new Arguments();
        newArgs.add(ArgumentName.CATEGORY, DR_LAB_CATEGORY);
        if (args.hasArgument(ArgumentName.FROM))
            newArgs.add(ArgumentName.FROM, args.getValueByName(ArgumentName.FROM));

        // Creates new Options instance
        Options newOpts = new Options();
        newOpts.add(OptionName.INCLUDE, Option.Include.INCLUDE_RESULTS);
        if (opts.hasOption(OptionName.SORT))
            newOpts.add(opts.getByName(OptionName.SORT));

        return structuredLabRepGenerator.generateQueryForSearch(newArgs, newOpts);
    }

}
