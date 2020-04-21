package eu.interopehrate.mr2de.r2d.executor;

import android.util.Log;

import androidx.annotation.WorkerThread;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Implementation of HealthRecordBundle interface. LazyHealthRecordBundle allows
 *  clients to iterate over query result adopting a lazy loading policy.
 *  Health data must will be downloaded incrementally only when needed.
 *
 *  Lazy Loading is implemented by interaction with class ProgressiveExecutor.
 *
 */
public class LazyHealthRecordBundle implements HealthRecordBundle {

    private HealthRecordType current;
    private int total;

    private final ProgressiveExecutor executor;
    private Bundle cache;
    private int cacheSize;
    private int cacheIndex = 0;

    public LazyHealthRecordBundle(ProgressiveExecutor executor) {
        this.executor = executor;
    }

    @Override
    public HealthRecordType[] getHealthRecordTypes() {
        return executor.getHealthRecordTypes();
    }

    @Override
    public int getTotal(HealthRecordType type) {
        // TODO: controllare che sia giusto, perche questo corrrisponde solo al corrente
        return cache.getTotal();
    }

    @Override
    public boolean hasNext(HealthRecordType type) {
        if (cacheIndex == cacheSize) {
            // executes next step of query
            try {
                cache = executor.next(type);
                if (cache == null)
                    return false;
                else {
                    cacheSize = cache.getEntry().size();
                    cacheIndex = 0;
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), "Exception in method executor.next()", e);
                throw new MR2DException(e);
            }
        }

        return (cacheIndex < cacheSize);
    }

    @WorkerThread
    @Override
    public Resource next(HealthRecordType type) {
        if (hasNext(type)) {
            Resource r = cache.getEntry().get(cacheIndex++).getResource();
            r.setUserData(HealthRecordType.class.getName(), cache.getUserData(HealthRecordType.class.getName()));
            return r;
        } else
            throw new IllegalStateException("No more objects to retrieve from LazyHealthRecordBundle.");
    }



}
