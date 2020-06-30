package mb.spoofax.intellij.resource;

import java.util.Objects;

/**
 * Specifies how two resources are related.
 */
@SuppressWarnings("unused")
public final class ResourceRelation {

    private final int up;
    private final int down;

    /**
     * Initializes a new instance of the {@link ResourceRelation} class.
     *
     * @param up the minimum number of steps up the hierarchy
     * @param down the minimum number of steps down the hierarchy, after up
     */
    public ResourceRelation(int up, int down) {
        if (up < 0) throw new IllegalArgumentException("up must be positive or zero");
        if (down < 0) throw new IllegalArgumentException("down must be positive or zero");

        this.up = up;
        this.down = down;
    }

    /** Gets whether the resource is the same as the other resource. */
    public boolean isSame() { return up == 0 && down == 0; }
    /** Gets whether the resource is a child of the other resource. */
    public boolean isChild() { return up == 0 && down == 1; }
    /** Gets whether the resource is the parent of the other resource. */
    public boolean isParent() { return up == 1 && down == 0; }
    /** Gets whether the resource is a sibling of the other resource. */
    public boolean isSibling() { return up == 1 && down == 1; }
    /** Gets whether the resource is a descendant of the other resource. */
    public boolean isDescendant() { return up == 0 && down != 0; }
    /** Gets whether the resource is an ancestor of the other resource. */
    public boolean isAncestor() { return up != 0 && down == 0; }
    /** Gets whether the resource is a cousin of the other resource. */
    public boolean isCousin() { return up != 0 && down != 0; }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ResourceRelation that = (ResourceRelation)o;
        // @formatter:off
        return up == that.up
            && down == that.down;
        // @formatter:on
    }

    @Override
    public int hashCode() {
        return Objects.hash(up, down);
    }

    @Override public String toString() {
        if (isSame()) return "same";
        if (isChild()) return "child";
        if (isParent()) return "parent";
        if (isSibling()) return "sibling";
        if (isDescendant()) return "descendant";
        if (isAncestor()) return "ancestor";
        return "cousin";
    }
}
