package eu.interopehrate.mr2da.r2d.document;

import org.hl7.fhir.r4.model.Bundle;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.ArgumentName;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.mr2da.r2d.OptionName;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.resources.QueryGeneratorFactory;
import eu.interopehrate.mr2da.utils.codes.LoincCodes;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
class PatientSummaryQueryGenerator extends AbstractQueryGenerator {

    private AbstractQueryGenerator documentReferenceQGen;

    public PatientSummaryQueryGenerator(IGenericClient fhirClient) {
        super(fhirClient);
        documentReferenceQGen = QueryGeneratorFactory.getQueryGenerator(
                FHIRResourceCategory.DOCUMENT_REFERENCE, this.fhirClient);
    }

    @Override
    public IQuery<Bundle> generateQueryForSearch(Arguments args, Options opts) {
        // Creates new Arguments instance
        Arguments newArgs = new Arguments();

        String psLoincCode = LoincCodes.PATIENT_SUMMARY.getSystem() +
                "|" + LoincCodes.PATIENT_SUMMARY.getCode();
        newArgs.add(ArgumentName.TYPE, psLoincCode);
        if (args.hasArgument(ArgumentName.FROM))
            newArgs.add(ArgumentName.FROM, args.getByName(ArgumentName.FROM));

        // Creates new Opions instance
        Options newOpts = new Options();
        if (opts.hasOption(OptionName.SORT))
            newOpts.add(opts.getByName(OptionName.SORT));

        return documentReferenceQGen.generateQueryForSearch(newArgs, newOpts);
    }

}
