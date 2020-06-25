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
    private Bundle currentBundle;
    private int currentBundleSize;
    private int currentBundleIndex = 0;

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
        return currentBundle.getEntry().size();
    }

    @Override
    public boolean hasNext(HealthRecordType type) {
        if (currentBundleIndex == currentBundleSize) {
            // executes next step of query
            try {
                currentBundle = executor.next(type);
                if (currentBundle == null)
                    return false;
                else {
                    currentBundleSize = currentBundle.getEntry().size();
                    currentBundleIndex = 0;
                }
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Exception in method executor.next()", e);
                throw new MR2DException(e);
            }
        }

        return (currentBundleIndex < currentBundleSize);
    }

    @WorkerThread
    @Override
    public Resource next(HealthRecordType type) {
        if (hasNext(type)) {
            Resource r = currentBundle.getEntry().get(currentBundleIndex++).getResource();
            r.setUserData(HealthRecordType.class.getName(), currentBundle.getUserData(HealthRecordType.class.getName()));
            return r;
        } else
            throw new IllegalStateException("No more objects to retrieve from LazyHealthRecordBundle.");
    }



}
