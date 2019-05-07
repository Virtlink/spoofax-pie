package mb.spoofax.eclipse.resource;

import mb.resource.ResourceKey;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import java.io.Serializable;

public class EclipseResourceKey implements ResourceKey {
    private final String id;

    public EclipseResourceKey(String portablePathString) {
        this.id = portablePathString;
    }

    public EclipseResourceKey(IPath path) {
        this(path.toPortableString());
    }

    public EclipseResourceKey(IResource resource) {
        this(resource.getFullPath().toPortableString());
    }

    @Override public Serializable qualifier() {
        return EclipseResourceRegistry.qualifier;
    }

    @Override public Serializable id() {
        return id;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final EclipseResourceKey that = (EclipseResourceKey) o;
        return id.equals(that.id);
    }

    @Override public int hashCode() {
        return id.hashCode();
    }

    @Override public String toString() {
        return "#eclipse:" + id;
    }
}