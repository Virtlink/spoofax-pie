package mb.spoofax.intellij.menu;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import mb.common.region.Selection;
import mb.common.util.ListView;
import mb.pie.api.ExecException;
import mb.pie.api.MixedSession;
import mb.pie.api.exec.NullCancelableToken;
import mb.resource.ResourceService;
import mb.spoofax.core.language.LanguageInstance;
import mb.spoofax.core.language.command.CommandContext;
import mb.spoofax.core.language.command.CommandExecutionType;
import mb.spoofax.core.language.command.CommandRequest;
import mb.spoofax.core.language.menu.CommandAction;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.command.IntellijEnclosingCommandContextProvider;
import mb.spoofax.intellij.editor.EditorUtils;
import mb.spoofax.intellij.pie.IntellijPieRunner;
import mb.spoofax.intellij.resource.IntellijResource;
import mb.spoofax.intellij.resource.IntellijResourceRegistry;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;

/**
 * A language action executed in the context of an editor.
 */
public abstract class EditorContextLanguageAction extends LanguageAction {

    private final CommandRequest<?> commandRequest;

    private CommandAction commandAction;
    private final IntellijLanguageComponent languageComponent;
    private final IntellijResourceRegistry resourceRegistry;
    private final ResourceService resourceService;
    private final IntellijEnclosingCommandContextProvider enclosingCommandContextProvider;
    private final IntellijPieRunner pieRunner;


    /**
     * Factory interface for the {@link EditorContextLanguageAction} class.
     */
    public interface Factory {
        EditorContextLanguageAction create(
        String id,
        CommandRequest<?> commandRequest,
        CommandAction commandAction,
        @Nullable String text,
        @Nullable String description,
        @Nullable Icon icon);
    }
//
//    /**
//     * Factory class for the {@link EditorContextLanguageAction} class.
//     */
//    public static final class Factory {
//        private final IntellijLanguageComponent languageComponent;
//        private final IntellijResourceRegistry resourceRegistry;
//        private final PieRunner pieRunner;
//
//        @Inject
//        public Factory(IntellijLanguageComponent languageComponent,
//                       IntellijResourceRegistry resourceRegistry,
//                       PieRunner pieRunner) {
//            this.languageComponent = SpoofaxPlugin.getComponent();
//            this.resourceRegistry = resourceRegistry;
//            this.pieRunner = pieRunner;
//        }
//
//        EditorContextLanguageAction create(
//                String id,
//                CommandRequest commandRequest,
//                @Nullable String text,
//                @Nullable String description,
//                @Nullable Icon icon) {
//            return new EditorContextLanguageAction(id, commandRequest, text, description, icon,
//                    this.languageComponent, this.resourceRegistry, this.pieRunner);
//        }
//    }

    /**
     * Initializes a new instance of the {@link EditorContextLanguageAction} class.
     *
     * @param id                the ID of the action.
     * @param commandRequest    the command request
     * @param commandAction     the command action
     * @param text              the text of the action; or {@code null}.
     * @param description       the description of the action; or {@code null}.
     * @param icon              the icon of the action; or {@code null}.
     */
    public EditorContextLanguageAction(
        String id,
        CommandRequest<?> commandRequest,
        CommandAction commandAction,
        @Nullable String text,
        @Nullable String description,
        @Nullable Icon icon,
        IntellijLanguageComponent languageComponent,
        IntellijResourceRegistry resourceRegistry,
        ResourceService resourceService,
        IntellijEnclosingCommandContextProvider enclosingCommandContextProvider,
        IntellijPieRunner pieRunner
    ) {
        super(id, text, description, icon);
        this.commandRequest = commandRequest;
        this.commandAction = commandAction;
        this.languageComponent = languageComponent;
        this.resourceRegistry = resourceRegistry;
        this.resourceService = resourceService;
        this.enclosingCommandContextProvider = enclosingCommandContextProvider;
        this.pieRunner = pieRunner;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if(commandRequest.executionType() == CommandExecutionType.AutomaticContinuous) {
            return; // Automatic continuous execution is not supported when manually invoking commands.
        }

        @Nullable Project project = e.getProject();
        if(project == null) {
            // The action needs a project.
            return;
        }

        @Nullable IntellijResource resource = this.resourceRegistry.getResource(e);
        @Nullable Editor editor = ActionUtils.getEditor(e);
        if(resource == null || editor == null) return;

        final Selection selection = EditorUtils.getPrimarySelection(editor);
        final CommandContext context = new CommandContext(resource.getKey(), selection);

        if(!context.supportsAnyEditorFileType(commandAction.requiredEditorFileTypes())) {
            return; // Command requires a certain type of file, but the context does not have one.
        }
        if(!context.supportsAnyEditorSelectionType(commandAction.requiredEditorSelectionTypes())) {
            return; // Command requires a certain type of selection, but the context does not have one.
        }
        final @Nullable CommandContext finalContext = enclosingCommandContextProvider.selectRequired(context, commandAction.requiredEnclosingResourceTypes());
        if(finalContext == null) {
            return; // Command requires a certain type of enclosing context, but context does not have one.
        }

        final LanguageInstance languageInstance = languageComponent.getLanguageInstance();

        try {
            try(final MixedSession session = languageComponent.getPie().newSession()) {
                pieRunner.requireCommand(languageComponent, commandRequest, project, ListView.of(context), session, NullCancelableToken.instance);
            }
        } catch(ExecException ex) {
            throw new RuntimeException("Cannot execute command request '" + commandRequest + "', execution failed unexpectedly", ex);
        } catch(InterruptedException ex) {
            // Ignore
        }
    }

}
