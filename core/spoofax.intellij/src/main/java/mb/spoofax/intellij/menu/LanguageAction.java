package mb.spoofax.intellij.menu;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;

/**
 * A language action.
 */
public abstract class LanguageAction extends AnActionWithId {

    /**
     * Initializes a new instance of the {@link AnActionWithId} class.
     *
     * @param id          The ID of the action.
     * @param text        The text of the action; or {@code null}.
     * @param description The description of the action; or {@code null}.
     * @param icon        The icon of the action; or {@code null}.
     */
    protected LanguageAction(String id, @Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(id, text, description, icon);
    }

}
