package eu.interopehrate.mr2de.r2d.executor;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.util.Iterator;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: Implementation of a lazy Iterator<Resource> in order to execute progressive
 *               queries interacting several times with an NCP. Works with an istance of
 *               ProgressiveExecutor
 */
public class LazyIterator implements Iterator<Resource> {

    private final ProgressiveExecutor executor;
    private Bundle cache;
    private int idx;

    public LazyIterator(ProgressiveExecutor executor, Bundle bundle) {
        this.executor = executor;
        this.cache = bundle;
    }

    @Override
    public boolean hasNext() {
        if (idx == cache.getTotal() - 1) {
            cache = executor.next();
            if (cache == null)
                return false;
            else
                idx = 0;
        }

        return (idx < cache.getTotal() - 1);
    }

    @Override
    public Resource next() {
        if (hasNext())
            return cache.getEntry().get(idx++).getResource();
        else
            throw new IllegalStateException("No more objects to retrieve from Iterator.");

    }
}
