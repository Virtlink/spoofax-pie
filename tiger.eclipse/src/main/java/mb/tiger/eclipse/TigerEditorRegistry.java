package mb.tiger.eclipse;

import mb.spoofax.core.language.LanguageScope;
import mb.spoofax.eclipse.EclipseIdentifiers;
import mb.spoofax.eclipse.editor.EditorRegistry;

import javax.inject.Inject;

@LanguageScope
public class TigerEditorRegistry extends EditorRegistry {
    @Inject public TigerEditorRegistry(EclipseIdentifiers eclipseIdentifiers) {
        super(eclipseIdentifiers);
    }
}
