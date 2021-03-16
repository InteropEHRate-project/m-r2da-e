package eu.interopehrate.mr2da;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.document.DocumentQueryGeneratorFactory;
import eu.interopehrate.mr2da.r2d.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.QueryGeneratorFactory;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

/**
 *  Author: Engineering Ingegneria Informatica
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
class ProgressiveQueryExecutor {

    private final IGenericClient fhirClient;
    private ResourceCategory[] categories;
    private int queriesSize;
    private Stack<IQuery> queries = new Stack<>();
    private IQuery<Bundle> currentQuery;
    private Bundle currentBundle;

    public ProgressiveQueryExecutor(IGenericClient fhirClient, ResourceCategory... categories) {
        this.fhirClient = fhirClient;
        this.categories = categories;
    }

    /**
     *
     * @param args
     * @return
     */
    public Iterator<Resource> start(Arguments args, Options opts) {
        // Build the list of quey generators depending on the categories provided by caller
        List<AbstractQueryGenerator> qGenList = buildQueryGeneratorsList(fhirClient, categories);
        if (qGenList.size() == 0)
            throw new IllegalStateException("No QueryGenerators for " + Arrays.toString(categories));

        // Fills the stack of AbstractQueryGenerator
        for (AbstractQueryGenerator generator: qGenList)
            // TODO: come fare per capire quale metodo va invocato?
            queries.push(generator.generateQueryForSearch(args, opts));

        queriesSize = queries.size();
        // Executes the first query in the stack
        Log.d(ProgressiveQueryExecutor.class.getSimpleName(), "Starting execution of query 1 of " + queriesSize);
        currentBundle = (Bundle) queries.pop().execute();
        Log.d(getClass().getSimpleName(), currentBundle.getLink(Bundle.LINK_SELF).getUrl());
        Log.d(ProgressiveQueryExecutor.class.getSimpleName(), "Retrieved " +
                (currentBundle == null ? " 0 items " : currentBundle.getEntry().size() + " items."));

        return new LazyIterator<Resource>(this, currentBundle);
    }

    /**
     *
     * @return
     */
    protected Bundle next() {
        if (currentBundle.getLink(Bundle.LINK_NEXT) != null) {
            Log.d(ProgressiveQueryExecutor.class.getSimpleName(), "Loading next page of current query...");
            Log.d(ProgressiveQueryExecutor.class.getSimpleName(), currentBundle.getLink(Bundle.LINK_NEXT).getUrl());
            currentBundle = fhirClient.loadPage().next(currentBundle).execute();
            Log.d(ProgressiveQueryExecutor.class.getSimpleName(), "Retrieved " +
                    (currentBundle == null ? " 0 items " : currentBundle.getEntry().size() + " items."));
        } else {
            if (queries.empty()) {
                Log.d(getClass().getSimpleName(), "Execution terminated");
                return null;
            } else {
                IQuery<Bundle> nextQuery = queries.pop();
                Log.d(ProgressiveQueryExecutor.class.getSimpleName(), "Starting execution of query " +
                        (queriesSize - queries.size()) + " of " + queriesSize);
                currentBundle = nextQuery.execute();
                Log.d(getClass().getSimpleName(), currentBundle.getLink(Bundle.LINK_SELF).getUrl());
                Log.d(ProgressiveQueryExecutor.class.getSimpleName(), "Retrieved " +
                        (currentBundle == null ? " 0 items " : currentBundle.getEntry().size() + " items."));
            }
        }

        return currentBundle;
    }

    /**
     *
     * @param fhirClient
     * @param resourceCategories
     * @return
     * @throws ReflectiveOperationException
     */
    private List<AbstractQueryGenerator> buildQueryGeneratorsList(
            IGenericClient fhirClient,
            ResourceCategory... resourceCategories) {
        List<AbstractQueryGenerator> qGenList = new ArrayList<>();

        // TODO: remove duplicates
        for (ResourceCategory category : resourceCategories) {
            if (category instanceof FHIRResourceCategory) {
                qGenList.add(
                        QueryGeneratorFactory.getQueryGenerator((FHIRResourceCategory)category,
                                fhirClient)
                );
            } else if (category instanceof DocumentCategory) {
                qGenList.addAll(
                        DocumentQueryGeneratorFactory.getQueryGenerators((DocumentCategory) category,
                                fhirClient)
                );
            } else {
                throw new IllegalArgumentException("Unkown ResourceCategory: " + category);
            }
        }

        return qGenList;
    }

    /**
     *
     * @param <T>
     */
    class LazyIterator<T> implements Iterator<T> {

        private final ProgressiveQueryExecutor executor;
        private Bundle healthDataBundle;
        private Iterator<Bundle.BundleEntryComponent> healthDataIterator;
        private final Collection<String> ids = new HashSet<String>();

        public LazyIterator(ProgressiveQueryExecutor executor) {
            this.executor = executor;
        }

        public LazyIterator(ProgressiveQueryExecutor executor, Bundle healthData) {
            this.executor = executor;
            this.healthDataBundle = healthData;
            healthDataIterator = healthDataBundle.getEntry().iterator();
        }

        @Override
        public boolean hasNext() {
            if (!healthDataIterator.hasNext()) {
                healthDataBundle = executor.next();
                if (healthDataBundle == null)
                    return false;
                else
                    healthDataIterator = healthDataBundle.getEntry().iterator();
            }

            return (healthDataIterator.hasNext());
        }

        @Override
        public T next() {
            Resource r;
            String id;
            while (hasNext()) {
                r = healthDataIterator.next().getResource();
                id = r.getId();
                if (!ids.contains(id)) {
                    ids.add(id);
                    return (T) r;
                } else
                    Log.d(getClass().getSimpleName(), "Trovato duplicato: " + id);
            }

            return null;
        }
    }
}
