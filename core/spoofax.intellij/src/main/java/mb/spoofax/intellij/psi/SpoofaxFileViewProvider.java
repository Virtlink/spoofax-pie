package mb.spoofax.intellij.psi;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import org.jetbrains.annotations.Nullable;

public abstract class SpoofaxFileViewProvider extends SingleRootFileViewProvider {
    protected SpoofaxFileViewProvider(PsiManager manager, VirtualFile virtualFile, boolean eventSystemEnabled, Language language) {
        super(manager, virtualFile, eventSystemEnabled, language);
    }

    @Override public boolean supportsIncrementalReparse(Language rootLanguage) {
        return false;
    }
}
