package mb.tiger.intellij;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.psi.SpoofaxFile;

public final class TigerFile extends SpoofaxFile {
    private final TigerFileType fileType;

    public TigerFile(TigerFileType fileType, FileViewProvider provider, IntellijLanguageComponent languageComponent) {
        super(/*TigerTokenTypes.TIGER_FILE,*/ provider, languageComponent);
        this.fileType = fileType;
    }

    @Override public FileType getFileType() {
        return fileType;
    }
}
