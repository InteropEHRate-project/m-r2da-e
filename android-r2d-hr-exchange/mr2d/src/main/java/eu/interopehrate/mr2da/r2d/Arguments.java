package eu.interopehrate.mr2da.r2d;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: represents a collection of instances of Argument
 */
public class Arguments {
    private Map<ArgumentName, Argument> args = new Hashtable<>();

    public Arguments add(@NonNull ArgumentName name, @NonNull Object value) {
        args.put(name, new Argument(name, value));
        return this;
    }

    public Arguments add(@NonNull Argument arg) {
        args.put(arg.getName(), arg);
        return this;
    }

    public boolean hasArgument(@NonNull ArgumentName name) {
        return args.containsKey(name);
    }

    @Nullable
    public Argument getByName(@NonNull ArgumentName name) {
        return args.get(name);
    }

    @Nullable
    public Object getValueByName(@NonNull ArgumentName name) {
        if (args.containsKey(name))
            return args.get(name).getValue();

        return null;
    }

    public Iterator<Argument> getArguments() {
        return args.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("args=[");
        for(Argument arg : args.values())
            sb.append(arg.toString());
        sb.append("]");

        return sb.toString();
    }
}
