package eu.interopehrate.mr2da.r2d;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Options {
    private Map<OptionName, Option> opts = new Hashtable<>();

    public Options add(@NonNull OptionName name, @NonNull Object value) {
        opts.put(name, new Option(name, value));
        return this;
    }

    public Options add(@NonNull Option opt) {
        opts.put(opt.getName(), opt);
        return this;
    }

    public boolean hasOption(@NonNull OptionName name) {
        return opts.containsKey(name);
    }

    @Nullable
    public Option getByName(@NonNull OptionName name) {
        return opts.get(name);
    }

    @Nullable
    public Object getValueByName(@NonNull OptionName name) {
        if (opts.containsKey(name))
            return opts.get(name).getValue();

        return null;
    }

    public Iterator<Option> getOptions() {
        return opts.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("opts=[");
        for(Option opt : opts.values())
            sb.append(opt.toString());
        sb.append("]");

        return sb.toString();
    }

}
