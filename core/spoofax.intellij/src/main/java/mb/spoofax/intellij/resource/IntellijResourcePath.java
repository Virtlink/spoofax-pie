package mb.spoofax.intellij.resource;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import mb.resource.hierarchical.ResourcePath;
import mb.resource.hierarchical.ResourcePathDefaults;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;

/**
 * An IntelliJ resource path.
 */
public final class IntellijResourcePath extends ResourcePathDefaults<IntellijResourcePath> implements ResourcePath {

    /**
     * The IntelliJ resource URL, which can be serialized and deserialized.
     * The URL uniquely identifies a file in all file systems.
     */
    private final String url;

    public IntellijResourcePath(String url) {
        this.url = url;
    }

    public IntellijResourcePath(VirtualFile virtualFile) {
        this(virtualFile.getUrl());
    }

    public String getIntellijUrl() {
        return url;
    }

    @Override public String getQualifier() {
        return IntellijResourceRegistry.qualifier;
    }

    @Override public Serializable getId() {
        return url;
    }

    @Override protected IntellijResourcePath self() {
        return this;
    }

    @Override public boolean isAbsolute() {
        return false;
    }

    @Override public int getSegmentCount() {
        return 0;
    }

    @Override public Iterable<String> getSegments() {
        return null;
    }

    @Override public @Nullable ResourcePath getParent() {
        return null;
    }

    @Override public @Nullable ResourcePath getRoot() {
        return null;
    }

    @Override public @Nullable String getLeaf() {
        return null;
    }

    @Override public ResourcePath getNormalized() {
        return null;
    }

    @Override public ResourcePath relativize(ResourcePath other) {
        return null;
    }

    @Override public String relativizeToString(ResourcePath other) {
        return null;
    }

    @Override public ResourcePath appendSegment(String segment) {
        return null;
    }

    @Override public IntellijResourcePath appendSegments(Iterable<String> segments) {
        return null;
    }

    @Override public ResourcePath appendRelativePath(String relativePath) {
        return null;
    }

    @Override public ResourcePath appendOrReplaceWithPath(String other) {
        return null;
    }

    @Override public ResourcePath appendString(String other) {
        return null;
    }

    @Override public IntellijResourcePath appendRelativePath(ResourcePath relativePath) {
        return null;
    }

    @Override public IntellijResourcePath replaceLeaf(String segment) {
        return null;
    }


    @Override public boolean equals(@Nullable Object other) {
        return false;
    }

    @Override public int hashCode() {
        return 0;
    }

    @Override public String toString() {
        return null;
    }
}
