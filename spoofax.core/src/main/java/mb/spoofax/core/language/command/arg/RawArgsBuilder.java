package mb.spoofax.core.language.command.arg;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.language.command.CommandContext;
import mb.spoofax.core.language.command.CommandContexts;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RawArgsBuilder {
    private final ParamDef paramDef;
    private final HashMap<Class<? extends Serializable>, ArgConverter<?>> converters = new HashMap<>();
    private final HashMap<String, Serializable> optionArgs = new HashMap<>();
    private final ArrayList<Serializable> positionalArgs = new ArrayList<>();


    public RawArgsBuilder(ParamDef paramDef) {
        this.paramDef = paramDef;
        // TODO: add default argument converters.
    }


    public void setConverter(Class<? extends Serializable> type, ArgConverter<?> converter) {
        converters.put(type, converter);
    }

    public void setOptionArg(String name, Serializable arg) {
        optionArgs.put(name, arg);
    }

    public void addPositionalArg(Serializable arg) {
        positionalArgs.add(arg);
    }

    public void setAndAddArgsFrom(RawArgsCollection collection) {
        for(Map.Entry<? extends String, ? extends Serializable> optionArg : collection.optionArgs.entrySet()) {
            setOptionArg(optionArg.getKey(), optionArg.getValue());
        }
        for(Serializable positionalArg : collection.positionalArgs) {
            addPositionalArg(positionalArg);
        }
    }


    public RawArgs build(CommandContext context) {
        final HashMap<String, Serializable> finalOptionArgs = new HashMap<>();
        final HashMap<Integer, Serializable> finalPositionalArgs = new HashMap<>();
        // TODO: use a list for positional arguments instead, but that requires looping over positionalArgs instead, and
        // then requires finding the matching positional parameters.

        for(Param param : paramDef.params) {
            Params.caseOf(param)
                .option((name, type, required, providers) -> {
                    @Nullable Serializable arg = optionArgs.get(name);
                    if(arg == null && !providers.isEmpty()) {
                        arg = argFromProviders(type, providers, context);
                    }
                    final boolean argSet = arg != null;
                    if(!argSet && required) {
                        throw new RuntimeException("Option parameter '" + name + "' of type '" + type + "' is required, but no argument was set, and no argument could be retrieved from providers '" + providers + "'");
                    }
                    if(argSet) {
                        if(String.class.equals(arg.getClass()) && !String.class.isAssignableFrom(type)) {
                            arg = convert((String) arg, type);
                        }
                        finalOptionArgs.put(name, arg);
                    }
                    return Optional.empty();
                })
                .positional((index, type, required, providers) -> {
                    @Nullable Serializable arg;
                    if(index < positionalArgs.size()) {
                        arg = positionalArgs.get(index);
                    } else {
                        arg = null;
                    }
                    if(arg == null && !providers.isEmpty()) {
                        arg = argFromProviders(type, providers, context);
                    }
                    final boolean argSet = arg != null;
                    if(!argSet && required) {
                        throw new RuntimeException("Positional parameter at index '" + index + "' of type '" + type + "' is required, but no argument was set, and no argument could be retrieved from providers '" + providers + "'");
                    }
                    if(argSet) {
                        if(String.class.equals(arg.getClass()) && !String.class.isAssignableFrom(type)) {
                            arg = convert((String) arg, type);
                        }
                        finalPositionalArgs.put(index, arg);
                    }
                    return Optional.empty();
                });
        }
        return new DefaultRawArgs(finalOptionArgs, finalPositionalArgs);
    }


    private @Nullable Serializable argFromProviders(Class<? extends Serializable> type, ListView<ArgProvider> providers, CommandContext context) {
        for(ArgProvider provider : providers) {
            @SuppressWarnings("ConstantConditions") final @Nullable Serializable arg = ArgProviders.caseOf(provider)
                .value((o) -> o)
                .context_(argFromContext(type, context));
            // noinspection ConstantConditions (arg can really be null)
            if(arg != null) return arg;
        }
        return null;
    }

    private @Nullable Serializable argFromContext(Class<? extends Serializable> type, CommandContext context) {
        if(CommandContext.class.isAssignableFrom(type)) {
            return context;
        } else if(Region.class.isAssignableFrom(type)) {
            return CommandContexts.getRegion(context).orElse(null);
        } else if(Integer.class.isAssignableFrom(type)) {
            return CommandContexts.getOffset(context).orElse(null);
        } else {
            if(ResourcePath.class.isAssignableFrom(type)) {
                final Optional<ResourcePath> path = CommandContexts.caseOf(context)
                    .project((p) -> p)
                    .directory((p) -> p)
                    .file((p) -> p)
                    .fileWithRegion((p, r) -> p)
                    .fileWithOffset((p, o) -> p)
                    .otherwiseEmpty();
                if(path.isPresent()) {
                    return path.get();
                }
            }
            if(ResourceKey.class.isAssignableFrom(type)) {
                return CommandContexts.getReadable(context).orElse(null);
            }
            return null;
        }
    }


    private Serializable convert(String argStr, Class<? extends Serializable> type) {
        final @Nullable ArgConverter<?> converter = converters.get(type);
        if(converter == null) {
            throw new RuntimeException("Cannot convert argument '" + argStr + "' to an object of type '" + type + "', no type converter was found for that type");
        }
        try {
            return converter.convert(argStr);
        } catch(RuntimeException e) {
            throw e; // Just rethrow runtime exceptions.
        } catch(Exception e) {
            throw new RuntimeException("Cannot convert argument '" + argStr + "' to an object of type '" + type + "', conversion failed unexpectedly", e);
        }
    }
}