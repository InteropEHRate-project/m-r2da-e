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
package eu.interopehrate.mr2da.fhir;

import android.util.Log;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInput;
import ca.uhn.fhir.rest.gclient.IQuery;
import eu.interopehrate.mr2da.r2d.document.DocumentQueryGeneratorFactory;
import eu.interopehrate.mr2da.r2d.resources.AbstractQueryGenerator;
import eu.interopehrate.mr2da.r2d.Options;
import eu.interopehrate.mr2da.r2d.resources.QueryGeneratorFactory;
import eu.interopehrate.mr2da.r2d.Arguments;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: This class is able to execute a set of R2D queries to retrieve the requested
 *               data from an R2D Server. Every query may requested to interact several times
 *               with the R2D Server to download each page composing the overall result (results
 *               are paged).
 *
 *               Lazy download of data is hidden to the client, results are provided to the client
 *               inside an instance of Iterator&lt;Resource&gt; that handles the lazy loading
 *               mechanism. The client only need to iterate over the Iterator.
 *
 */
public class ProgressiveQueryExecutor implements FHIRExecutor{

    private IGenericClient fhirClient;
    private ResourceCategory[] categories;
    private int queriesSize;

    private Queue<IQuery> queries = new LinkedList<IQuery>();
    private IQuery<Bundle> currentQuery;
    private Bundle currentBundle;

    public ProgressiveQueryExecutor(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    @Override
    public void setFhirClient(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    @Override
    public Iterator<Resource> executeQueries(Arguments args, Options opts, ResourceCategory... categories) {
        // Build the list of quey generators depending on the categories provided by caller
        List<AbstractQueryGenerator> qGenList = buildQueryGeneratorsList(fhirClient, categories);
        if (qGenList.size() == 0)
            throw new IllegalStateException("No QueryGenerators for " + Arrays.toString(categories));

        // Fills the stack of AbstractQueryGenerator
        for (AbstractQueryGenerator generator: qGenList)
            queries.add(generator.generateQueryForSearch(args, opts));

        queriesSize = queries.size();
        // Executes the first query in the stack
        Log.d("MR2DA", "Starting execution of query 1 of " + queriesSize);
        currentBundle = (Bundle) queries.poll().execute();
        Log.d("MR2DA", "Retrieved " +
                (currentBundle == null ? " 0 items " : currentBundle.getEntry().size() + " items."));

        return new LazyIterator<Resource>(this, currentBundle);
    }

    @Override
    public Bundle executeOperation(IOperationUntypedWithInput<Bundle> operation) {
        Bundle operationOutcome = operation.execute();
        BundleFetcher.fetchRestOfBundle(fhirClient, operationOutcome);

        return operationOutcome;
    }

    /**
     *
     * @param args
     * @return
     */
    @Deprecated
    public Iterator<Resource> start(Arguments args, Options opts) {
        // Build the list of quey generators depending on the categories provided by caller
        List<AbstractQueryGenerator> qGenList = buildQueryGeneratorsList(fhirClient, categories);
        if (qGenList.size() == 0)
            throw new IllegalStateException("No QueryGenerators for " + Arrays.toString(categories));

        // Fills the stack of AbstractQueryGenerator
        for (AbstractQueryGenerator generator: qGenList)
            queries.add(generator.generateQueryForSearch(args, opts));

        queriesSize = queries.size();
        // Executes the first query in the stack
        Log.d("MR2DA", "Starting execution of query 1 of " + queriesSize);
        currentBundle = (Bundle) queries.poll().execute();
        Log.d("MR2DA", "Retrieved " +
                (currentBundle == null ? " 0 items " : currentBundle.getEntry().size() + " items."));

        return new LazyIterator<Resource>(this, currentBundle);
    }

    /**
     *
     * @return
     */
    protected Bundle next() {
//        Log.d("MR2D", String.format("invoked method next(). Queries %d currentBundle.next = %s ",
//                queries.size(), currentBundle.getLink(Bundle.LINK_NEXT)));
        if (currentBundle.getLink(Bundle.LINK_NEXT) != null) {
            Log.d("MR2DA", "Loading next page of current query...");
            currentBundle = fhirClient.loadPage().next(currentBundle).execute();
            Log.d("MR2DA", "Retrieved " +
                    (currentBundle == null ? " null Bundle " : currentBundle.getEntry().size() + " items."));
        } else {
            if (queries.isEmpty()) {
                Log.d("MR2DA", "Execution terminated");
                return null;
            } else {
                IQuery<Bundle> nextQuery = queries.poll();
                Log.d("MR2DA", "Starting execution of query " +
                        (queriesSize - queries.size()) + " of " + queriesSize);
                currentBundle = nextQuery.execute();
                Log.d("MR2DA", "Retrieved " +
                        (currentBundle == null ? " null Bundle " : currentBundle.getEntry().size() + " items."));

                if (currentBundle.getEntry().size() == 0)
                    return this.next();
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
                    Log.d("MR2DA", "Remove duplicated element: " + id);
            }

            return null;
        }
    }
}
