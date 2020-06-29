package mb.spoofax.intellij.resource;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import mb.resource.QualifiedResourceKeyString;
import mb.resource.ResourceKeyString;
import mb.resource.ResourceRegistry;
import mb.resource.ResourceRuntimeException;
import mb.spoofax.intellij.menu.ActionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;

/**
 * Resource registry for working with IntelliJ resources.
 */
public final class IntellijResourceRegistry implements ResourceRegistry {
    static final String qualifier = "intellij-resource";


    public IntellijResource getResource(String url) {
        // The URL of a VirtualFile is in the form "protocol://path",
        // where 'protocol' uniquely identifies a VirtualFileSystem
        // and 'path' is a path within that file system.
        final @Nullable VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if(file == null) {
            throw new ResourceRuntimeException("File not found while getting IntelliJ resource for URL: " + url);
        }
        return getResource(file);
    }

    /**
     * Gets a resource from a {@link VirtualFile}.
     *
     * @param virtualFile The virtual file.
     * @return The resource.
     */
    public IntellijResource getResource(VirtualFile virtualFile) {
        return new IntellijResource(virtualFile);
    }

    public IntellijResource getResource(Document document) {
        final @Nullable VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if(file == null) {
            throw new ResourceRuntimeException(
                "Cannot get IntelliJ resource for document '" + document + "'; it is not associated to a file");
        }
        return getResource(file);
    }

    public IntellijResource getResource(PsiFile psiFile) {
        final @Nullable VirtualFile file = psiFile.getOriginalFile().getVirtualFile();
        if(file == null) {
            throw new ResourceRuntimeException(
                "Cannot get IntelliJ resource for Psi file '" + psiFile.getName() + "'; the file exists only in memory");
        }
        return getResource(file);
    }

    /**
     * Gets a resource from an {@link AnActionEvent}.
     *
     * @param e The action event.
     * @return The resource; or null when it could not be determined.
     */
    @Nullable
    public IntellijResource getResource(AnActionEvent e) {
        @Nullable PsiFile psiFile = ActionUtils.getPsiFile(e);
        if (psiFile == null) return null;
        return getResource(psiFile);
    }


    @Override public String qualifier() {
        return qualifier;
    }


    @Override public IntellijResource getResource(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot get IntelliJ resource with ID '" + id + "'; the ID is not of type String");
        }
        final String url = (String)id;
        return getResource(url);
    }

    @Override public IntellijResourceKey getResourceKey(ResourceKeyString keyStr) {
        if(!keyStr.qualifierMatchesOrMissing(qualifier)) {
            throw new ResourceRuntimeException("Qualifier of '" + keyStr + "' does not match qualifier '" + qualifier + "' of this resource registry");
        }
        final String url = keyStr.getId();
        return new IntellijResourceKey(url);
    }

    @Override public IntellijResource getResource(ResourceKeyString keyStr) {
        if(!keyStr.qualifierMatchesOrMissing(qualifier)) {
            throw new ResourceRuntimeException("Qualifier of '" + keyStr + "' does not match qualifier '" + qualifier + "' of this resource registry");
        }
        final String url = keyStr.getId();
        return getResource(url);
    }

    @Override public QualifiedResourceKeyString toResourceKeyString(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot convert identifier '" + id + "' to its string representation; it is not of type String");
        }
        return QualifiedResourceKeyString.of(qualifier(), (String)id);
    }

    @Override public String toString(Serializable id) {
        if(!(id instanceof String)) {
            throw new ResourceRuntimeException(
                "Cannot convert identifier '" + id + "' to its string representation; it is not of type String");
        }
        return QualifiedResourceKeyString.toString(qualifier(), (String)id);
    }
}
