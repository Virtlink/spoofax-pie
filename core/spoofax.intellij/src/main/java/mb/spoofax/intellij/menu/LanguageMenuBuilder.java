package mb.spoofax.intellij.menu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import mb.common.util.ListView;
import mb.spoofax.core.language.command.CommandRequest;
import mb.spoofax.core.language.menu.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Optional;


/**
 * Builds a menu for a language.
 */
public final class LanguageMenuBuilder {

    private final Provider<LanguageActionGroup> languageActionGroupProvider;
    private final EditorContextLanguageAction.Factory editorContextLanguageActionFactory;

    /**
     * Initializes a new instance of the {@link LanguageMenuBuilder} class.
     */
    @Inject
    public LanguageMenuBuilder(
        Provider<LanguageActionGroup> languageActionGroupProvider,
        EditorContextLanguageAction.Factory editorContextLanguageActionFactory) {
        this.languageActionGroupProvider = languageActionGroupProvider;
        this.editorContextLanguageActionFactory = editorContextLanguageActionFactory;
    }

    /**
     * Builds an IntelliJ action group from the specified list of menu items.
     *
     * @param menuItems The list of menu items.
     * @return The built action group.
     */
    public DefaultActionGroup build(List<MenuItem> menuItems) {
        DefaultActionGroup group = this.languageActionGroupProvider.get();
        Visitor visitor = new Visitor(group);
        for (MenuItem menuItem : menuItems) {
            visitor.visit(menuItem);
        }
        return group;
    }

    /**
     * A visitor that adds items to a group.
     */
    private final class Visitor {

        private final DefaultActionGroup group;

        /**
         * Initializes a new instance of the {@link Visitor} class.
         *
         * @param group The group to which the visitor adds items.
         */
        public Visitor(DefaultActionGroup group) {
            this.group = group;
        }

        /**
         * Visits the menu item
         *
         * @param menuItem the menu item to visit
         */
        public void visit(MenuItem menuItem) {
            menuItem.caseOf()
                .commandAction(commandAction -> {
                    this.group.add(createCommand(commandAction));
                    return Optional.empty();
                })
                .menu((displayName, description, items) -> {
                    this.group.add(createMenu(displayName, description, items));
                    return Optional.empty();
                })
                .separator(displayName -> {
                    this.group.add(createSeparator(displayName));
                    return Optional.empty();
                });
        }

        /**
         * Creates a menu menu item.
         *
         * @param displayName the display name of the menu item
         * @param description the description of the menu item
         * @param items the items in the menu item
         * @return the created IntelliJ menu
         */
        private DefaultActionGroup createMenu(String displayName, String description, ListView<MenuItem> items) {
            DefaultActionGroup subGroup = new DefaultActionGroup(displayName, true);
            Visitor subVisitor = new Visitor(subGroup);
            for(MenuItem item : items) {
                subVisitor.visit(item);
            }
            return subGroup;
        }

        /**
         * Creates a menu command item.
         *
         * @param commandAction The menu command item template.
         * @return the created IntelliJ action
         */
        private AnAction createCommand(CommandAction commandAction) {
            CommandRequest<?> commandRequest = commandAction.commandRequest();
            // TODO: Ensure ID is unique!
            // Multiple commands may use the same command request.
            String id = commandRequest.def().getId();
            return editorContextLanguageActionFactory.create(
                id, commandRequest, commandAction,
                commandAction.displayName(), commandAction.description(), null);
        }

        /**
         * Creates a menu separator item.
         *
         * @param displayName the display name of the separator
         * @return the created IntelliJ separator
         */
        private com.intellij.openapi.actionSystem.Separator createSeparator(@Nullable String displayName) {
            return com.intellij.openapi.actionSystem.Separator.create(displayName);
        }
    }

}
