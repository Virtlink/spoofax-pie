package mb.spoofax.intellij.command;

import com.intellij.openapi.project.Project;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.resource.ResourceRuntimeException;
import mb.resource.ResourceService;
import mb.resource.hierarchical.HierarchicalResource;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.language.command.CommandContext;
import mb.spoofax.core.language.command.EnclosingCommandContextType;
import mb.spoofax.core.language.command.ResourcePathWithKind;
import mb.spoofax.core.platform.Platform;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

/**
 * Provides the context enclosing a command, such as a project or directory.
 */
public final class IntellijEnclosingCommandContextProvider {

    private final Logger logger;
    private final ResourceService resourceService;

    @Inject
    public IntellijEnclosingCommandContextProvider(LoggerFactory loggerFactory, @Platform ResourceService resourceService) {
        this.logger = loggerFactory.create(IntellijEnclosingCommandContextProvider.class);
        this.resourceService = resourceService;
    }

    /**
     * Selects an enclosing context that matches one of the given set of required context enclosing types.
     *
     * @param context the context
     * @param requiredTypes the set of required context types, or an empty set
     * @return the context with its enclosing context set;
     * or {@code null} when no matching enclosing context could be found
     */
    public @Nullable CommandContext selectRequired(CommandContext context, Set<EnclosingCommandContextType> requiredTypes) {
        if(requiredTypes.isEmpty()) return context;
        for(EnclosingCommandContextType type : requiredTypes) {
            final @Nullable CommandContext enclosing = getEnclosing(context, type);
            if(enclosing != null) {
                context.setEnclosing(type, enclosing);
                return context;
            }
        }
        return null;
    }

    // TODO: Refactor this and (Eclipse's) EnclosingCommandContextProvider together
    public @Nullable CommandContext getEnclosing(CommandContext context, EnclosingCommandContextType type) {
        switch(type) {
            case Project: {
                final @Nullable ResourcePathWithKind resourcePath = context.getResourcePathWithKind().orElse(null);
                if(resourcePath == null) return null;
                final ResourcePath path = resourcePath.getPath();
                if(!(path instanceof EclipseResourcePath)) return null;
                final EclipseResourcePath eclipseResourcePath = (EclipseResourcePath)path;
                final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                final @Nullable IResource eclipseResource = root.findMember(eclipseResourcePath.getEclipsePath());
                if(eclipseResource == null) return null;
                final @Nullable IProject project = eclipseResource.getProject();
                if(project == null) return null;

                return CommandContext.ofProject(new EclipseResourcePath(project));
            }
            case Directory: {
                // TODO: Copied from (Eclipse's) EnclosingCommandContextProvider
                final @Nullable ResourcePathWithKind resourcePath = context.getResourcePathWithKind().orElse(null);
                if(resourcePath == null) return null;
                final ResourcePath path = resourcePath.getPath();
                final @Nullable ResourcePath parent = path.getParent();
                if(parent == null) return null;
                try {
                    final HierarchicalResource directory = resourceService.getHierarchicalResource(parent);
                    if(!directory.isDirectory()) {
                        return null;
                    }
                    return CommandContext.ofDirectory(parent);
                } catch(ResourceRuntimeException | IOException e) {
                    logger.error("Failed to get enclosing directory of '{}'", e, path);
                    return null;
                }
            }
            default: return null;
        }
    }

}
