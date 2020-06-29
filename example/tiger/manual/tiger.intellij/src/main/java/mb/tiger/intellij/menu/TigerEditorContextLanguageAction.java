package mb.tiger.intellij.menu;

import mb.resource.ResourceService;
import mb.spoofax.core.language.command.CommandRequest;
import mb.spoofax.core.language.menu.CommandAction;
import mb.spoofax.intellij.command.IntellijEnclosingCommandContextProvider;
import mb.spoofax.intellij.menu.EditorContextLanguageAction;
import mb.spoofax.intellij.pie.IntellijPieRunner;
import mb.spoofax.intellij.resource.IntellijResourceRegistry;
import mb.tiger.intellij.TigerPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import javax.swing.*;


public final class TigerEditorContextLanguageAction extends EditorContextLanguageAction {

    /**
     * Factory class for the {@link EditorContextLanguageAction} class.
     */
    public static final class Factory implements EditorContextLanguageAction.Factory {

        private final IntellijResourceRegistry resourceRegistry;
        private final IntellijPieRunner pieRunner;
        private final ResourceService resourceService;
        private final IntellijEnclosingCommandContextProvider enclosingCommandContextProvider;

        @Inject
        public Factory(
            IntellijResourceRegistry resourceRegistry,
           IntellijPieRunner pieRunner,
           ResourceService resourceService,
           IntellijEnclosingCommandContextProvider enclosingCommandContextProvider
        ) {
            this.resourceRegistry = resourceRegistry;
            this.pieRunner = pieRunner;
            this.resourceService = resourceService;
            this.enclosingCommandContextProvider = enclosingCommandContextProvider;
        }

        @Override
        public EditorContextLanguageAction create(
            String id,
            CommandRequest<?> commandRequest,
            CommandAction commandAction,
            @Nullable String text,
            @Nullable String description,
            @Nullable Icon icon
        ) {
            return new TigerEditorContextLanguageAction(id, commandRequest, commandAction, text, description, icon,
                this.resourceRegistry, this.resourceService, this.enclosingCommandContextProvider, this.pieRunner);
        }

    }

    public TigerEditorContextLanguageAction(
        String id,
        CommandRequest<?> commandRequest,
        CommandAction commandAction,
        @Nullable String text,
        @Nullable String description,
        @Nullable Icon icon,
        IntellijResourceRegistry resourceRegistry,
        ResourceService resourceService,
        IntellijEnclosingCommandContextProvider enclosingCommandContextProvider,
        IntellijPieRunner pieRunner
    ) {
        super(id, commandRequest, commandAction, text, description, icon, TigerPlugin.getComponent(), resourceRegistry, resourceService, enclosingCommandContextProvider, pieRunner);
    }

}
