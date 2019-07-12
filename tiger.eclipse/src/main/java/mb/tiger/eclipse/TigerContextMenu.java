package mb.tiger.eclipse;

import mb.spoofax.eclipse.menu.ContextMenu;

public class TigerContextMenu extends ContextMenu {
    public TigerContextMenu() {
        super(
            TigerPlugin.getComponent(),
            TigerProjectNature.id,
            "tiger.eclipse.nature.add",
            "tiger.eclipse.nature.remove",
            "tiger.eclipse.observe",
            "tiger.eclipse.unobserve"
        );
    }
}