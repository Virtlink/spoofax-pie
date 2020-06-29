package mb.tiger.intellij;

import com.intellij.openapi.util.IconLoader;
import dagger.Module;
import dagger.Provides;
import mb.spoofax.core.language.LanguageScope;
import mb.spoofax.intellij.IntellijLanguage;
import mb.spoofax.intellij.menu.EditorContextLanguageAction;
import mb.spoofax.intellij.menu.LanguageActionGroup;
import mb.spoofax.intellij.resource.IntellijResourceRegistry;
import mb.tiger.intellij.menu.TigerEditorContextLanguageAction;
import mb.tiger.intellij.menu.TigerLanguageActionGroup;

import javax.swing.*;

@Module
public class TigerIntellijModule {
    @Provides @LanguageScope
    IntellijLanguage provideSpoofaxLanguage(TigerLanguage language) {
        // Downcast because injections in spoofax.intellij require an IntellijLanguage, and dagger does not implicitly downcast.
        return language;
    }

    @Provides @LanguageScope
    Icon provideFileIcon() {
        return IconLoader.getIcon("/META-INF/fileIcon.svg");
    }

    @Provides
    LanguageActionGroup provideLanguageActionGroup() { return new TigerLanguageActionGroup(); }

    @Provides @LanguageScope
    EditorContextLanguageAction.Factory providesTigerEditorContextLanguageAction_Factory(IntellijResourceRegistry resourceRegistry, PieRunner pieRunner) {
        return new TigerEditorContextLanguageAction.Factory(resourceRegistry, pieRunner, resourceService, enclosingCommandContextProvider);
    }
}
