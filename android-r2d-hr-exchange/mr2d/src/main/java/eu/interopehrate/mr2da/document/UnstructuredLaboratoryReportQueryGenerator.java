package eu.interopehrate.mr2da.document;

import org.hl7.fhir.r4.model.Bundle;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.r2d.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.Option;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.QueryGeneratorFactory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class UnstructuredLaboratoryReportQueryGenerator extends AbstractQueryGenerator {
    private static final String[] LAB_REP_TYPES = {
            "http://loinc.org|26436-6"
    };
    private AbstractQueryGenerator documentReferenceQGen;

    public UnstructuredLaboratoryReportQueryGenerator(IGenericClient fhirClient) {
        super(fhirClient);
        documentReferenceQGen = QueryGeneratorFactory.getQueryGenerator(
                FHIRResourceCategory.DOCUMENT_REFERENCE, this.fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        // Creates new Arguments instance
        Arguments newArgs = new Arguments();
        newArgs.add(ArgumentName.CATEGORY, LAB_REP_TYPES);
        if (args.hasArgument(ArgumentName.FROM))
            newArgs.add(ArgumentName.FROM, args.getValueByName(ArgumentName.FROM));

        // Creates new Opions instance
        Options newOpts = new Options();
        newOpts.add(OptionName.INCLUDE, Option.Include.INCLUDE_RESULTS);
        if (opts.hasOption(OptionName.SORT))
            newOpts.add(opts.getByName(OptionName.SORT));

        return documentReferenceQGen.generateQueryForSearch(newArgs, newOpts);
    }

}
