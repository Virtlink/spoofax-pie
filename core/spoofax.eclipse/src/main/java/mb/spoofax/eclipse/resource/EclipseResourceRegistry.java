package mb.spoofax.eclipse.resource;

import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.resource.QualifiedResourceKeyString;
import mb.resource.Resource;
import mb.resource.ResourceKeyString;
import mb.resource.ResourceRegistry;
import mb.resource.ResourceRuntimeException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.jface.text.IDocument;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class EclipseResourceRegistry implements ResourceRegistry {
    static final String qualifier = "eclipse-resource";

    private final Logger logger;
    private final ConcurrentHashMap<EclipseResourcePath, IDocument> documentOverrides = new ConcurrentHashMap<>();


    @Inject public EclipseResourceRegistry(LoggerFactory loggerFactory) {
        this.logger = loggerFactory.create(EclipseResourceRegistry.class);
    }


    public void putDocumentOverride(EclipseResourcePath path, IDocument document) {
        logger.trace("Overriding path '{}' to document '{}'", path, document);
        documentOverrides.put(path, document);
    }

    public void removeDocumentOverride(EclipseResourcePath path) {
        logger.trace("Removing document override of path '{}'", path);
        documentOverrides.remove(path);
    }

    @Nullable IDocument getDocumentOverride(EclipseResourcePath path) {
        return documentOverrides.get(path);
    }


    @Override public String qualifier() {
        return qualifier;
    }

    @Override public EclipseResource getResource(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot get Eclipse resource with ID '" + id + "'; the ID is not of type String");
        }
        return new EclipseResource(this, new EclipseResourcePath((String)id));
    }

    @Override public EclipseResourcePath getResourceKey(ResourceKeyString keyStr) {
        if(!keyStr.qualifierMatchesOrMissing(qualifier)) {
            throw new ResourceRuntimeException("Qualifier of '" + keyStr + "' does not match qualifier '" + qualifier + "' of this resource registry");
        }
        return new EclipseResourcePath(keyStr.getId());
    }

    @Override public EclipseResource getResource(ResourceKeyString keyStr) {
        if(!keyStr.qualifierMatchesOrMissing(qualifier)) {
            throw new ResourceRuntimeException("Qualifier of '" + keyStr + "' does not match qualifier '" + qualifier + "' of this resource registry");
        }
        return new EclipseResource(this, new EclipseResourcePath(keyStr.getId()));
    }

    @Override public QualifiedResourceKeyString toResourceKeyString(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot convert ID '" + id + "' to its string representation; it is not of type String");
        }
        return QualifiedResourceKeyString.of(qualifier, (String)id);
    }

    @Override public String toString(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot convert ID '" + id + "' to its string representation; it is not of type String");
        }
        return QualifiedResourceKeyString.toString(qualifier, (String)id);
    }

    @Override public @Nullable File toLocalFile(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot attempt to convert identifier '" + id + "' to a local file; the ID is not of type String");
        }
        final EclipseResourcePath path = new EclipseResourcePath((String)id);
        return path.path.toFile();
    }

    @Override public @Nullable File toLocalFile(Resource resource) {
        if(!(resource instanceof EclipseResource)) {
            throw new ResourceRuntimeException(
                "Cannot attempt to convert resource '" + resource + "' to a local file; the resource is not of type EclipseResource");
        }
        final EclipseResource eclipseResource = (EclipseResource)resource;
        return eclipseResource.path.path.toFile();
    }


}
