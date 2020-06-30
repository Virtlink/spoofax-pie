package mb.spoofax.intellij.resource;

import mb.common.util.ListView;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A resource path.
 *
 * This class avoids some ambiguities in the way paths are interpreted
 * by normalizing them according to these rules:
 * <ul>
 * <li>When the path is absolute, the first segment is named {@code ""}.
 * <li>When the path points to a directory, the last segment is named {@code "."}.
 * <li>When a path segment includes the forward slash character {@code '/'}, it is escaped as {@code "%2f"}
 * <li>Special path segments {@code "."} and {@code ".."} are removed or collapsed according to RFC-3986.</li>
 * </ul>
 *
 * Note that other characters in path segments are not escaped by this class, but might require escaping
 * when the path is used in other context such as an URL. Also note that {@code ""}, {@code "."}, and {@code ".."} have
 * special meanings in this path.
 */
@SuppressWarnings("unused")
public final class ResourcePath2 {

    private static final Character PATH_SEPARATOR = '/';
    private static final Character EXTENSION_SEPARATOR = '.';

    private static final String PARENT_PATH = "..";
    private static final String CURRENT_PATH = ".";
    private static final String ROOT_PATH = "";

    /** Normalized path. */
    private final String normalizedPath;

    /**
     * Initializes a new instance of the {@link ResourcePath2} class
     * with the specified normalized path.
     *
     * @param normalizedPath the normalized path
     */
    public ResourcePath2(String normalizedPath) {
        if (normalizedPath.isEmpty() || normalizedPath.endsWith("/")) {
            throw new IllegalArgumentException("The path is not properly normalized. It appears to point to a directory, " +
                "but normalized paths to a directory must have a dot \".\" as the final path segment.");
        }

        this.normalizedPath = normalizedPath;
    }

    /**
     * Gets the segments of the path.
     *
     * Each segment will be unescaped.
     *
     * If the path is an absolute path, it starts with an empty segment {@code ""}.
     * If the path points to a directory, it ends with a dot segment {@code "."}.
     *
     * @return the path segments
     */
    public ListView<String> getSegments() {
        // FIXME: This could probably be more efficient, since we know ListView is immutable
        // For example, apply unescape on-demand to each element as it is required,
        // instead of mapping the whole list in advance
        return getRawSegments().stream().map(ResourcePath2::unescape).collect(ListView.collectToListView());
    }

    /**
     * Gets the raw segments of the path.
     *
     * Each segment will be escaped.
     *
     * If the path is an absolute path, it starts with an empty segment {@code ""}.
     * If the path points to a directory, it ends with a dot segment {@code "."}.
     *
     * @return the path segments
     */
    private ListView<String> getRawSegments() {
        // FIXME: This could probably be more efficient, we don't need the power of regexes to split on slashes.
        return ListView.of(this.normalizedPath.split(PATH_SEPARATOR.toString()));
    }

    /**
     * Gets the number of segments in the path.
     *
     * This returns the same number as the number of segments
     * returned by {@link #getSegments()}.
     *
     * If the path is an absolute path, this counts an extra initial empty segment {@code ""}.
     * If the path points to a directory, this counts an extra terminating dot segment {@code "."}.
     *
     * @return the number of segments in the path
     */
    public int getSegmentCount() {
        // FIXME: This could probably be more efficient, such as counting the number of slashes + 1
        return getRawSegments().size();
    }

    /**
     * Gets the name of the resource.
     *
     * When the name is not in the path, such as in {@code ""} or in {@code "./"},
     * this returns {@code null}. The name of the root is an empty string.
     *
     * @return the name; or {@code null} when the name is not in the path
     */
    public @Nullable String getName() {
        // The name is the last segment, except when this is a directory,
        // in which case it is the penultimate segment (the last is ".")
        ListView<String> segments = getSegments();
        if (segments.isEmpty()) return null;
        if (segments.get(segments.size()).equals(CURRENT_PATH)) {
            if (segments.size() < 2 || segments.get(segments.size() - 1).equals(PARENT_PATH)) {
                // Assuming the path is properly normalized, the penultimate segment can never be "."
                // so we don't need to test for it.
                return null;
            }
            return segments.get(segments.size() - 1);
        } else {
            return segments.get(segments.size());
        }
    }

    /**
     * Gets the name of the resource
     * without its extension.
     *
     * When the name is not in the path, such as in {@code ""} or in {@code "./"},
     * this returns {@code null}.
     *
     * @return the name without its extension; or {@code null} when the name is not in the path
     */
    public @Nullable String getNameWithoutExtension() {
        @Nullable String name = getName();
        if (name == null) return null;
        @Nullable String ext = getExtension();
        assert ext != null;
        return name.substring(0, name.length() - ext.length());
    }

    /**
     * Gets the extension of the resource's name, including the leading dot.
     *
     * When the name is not in the path, such as in {@code ""} or in {@code "./"},
     * this returns {@code null}.
     *
     * As an invariant, concatenating {@link #getNameWithoutExtension()} and {@link #getExtension()}
     * results in the same name as {@link #getName()}.
     *
     * @return the name's extension including the leading dot;
     * or an empty string when the name has no extension,
     * or {@code null} when the name is not in the path
     */
    public @Nullable String getExtension() {
        @Nullable String name = getName();
        if (name == null) return null;
        // We start at index 1 as a special case for files that start with a dot, such as ".gitignore"
        int extIndex = name.indexOf(EXTENSION_SEPARATOR, 1);
        if (extIndex < 0) return "";
        return name.substring(extIndex);
    }

    /**
     * Gets whether the name is an absolute path.
     *
     * @return {@code true} when the name is an absolute path;
     * otherwise, {@code false}
     */
    public boolean isAbsolute() {
        return this.normalizedPath.startsWith(ROOT_PATH + PATH_SEPARATOR);
    }

    /**
     * Gets whether the name is a relative path.
     *
     * @return {@code true} when the name is a relative path;
     * otherwise, {@code false}
     */
    public boolean isRelative() {
        return !isAbsolute();
    }

    /**
     * Gets whether the name is a directory path.
     *
     * This does not check whether the path points to a valid directory.
     *
     * @return {@code true} when the name is a directory path;
     * otherwise, {@code false}
     */
    public boolean isDirectory() {
        return this.normalizedPath.endsWith(PATH_SEPARATOR + CURRENT_PATH) || this.normalizedPath.equals(CURRENT_PATH);
    }

    /**
     * Gets whether the name is a file path.
     *
     * This does not check whether the path points to a valid file.
     *
     * @return {@code true} when the name is a file path;
     * otherwise, {@code false}
     */
    public boolean isFile() {
        return !isDirectory();
    }

    /**
     * Gets whether the name is a root path.
     *
     * This does not check whether the path points to a valid root.
     *
     * @return {@code true} when the name is a root path;
     * otherwise, {@code false}
     */
    public boolean isRoot() {
        return this.normalizedPath.equals(ROOT_PATH + PATH_SEPARATOR + CURRENT_PATH);
    }

    /**
     * Gets the parent path of this path.
     *
     * This does not check whether the parent exists.
     *
     * The parent of a file is its directory.
     * The parent of a directory is its parent directory.
     * The root path does not have a parent path.
     *
     * @return the parent path; or {@code null} when the path does not have a parent path
     */
    public @Nullable ResourcePath2 getParent() {
        if (isRoot())
            return null;
        else if (isDirectory()) {
            return resolve(new ResourcePath2(PARENT_PATH + PATH_SEPARATOR));
        } else {
            return resolve(new ResourcePath2(CURRENT_PATH + PATH_SEPARATOR));
        }
    }

    /**
     * Gets the root path of this path.
     *
     * This does not check whether the root exists.
     * Relative paths do not have a root path.
     *
     * @return the root path; or {@code null} when the path does not have a root path
     */
    public @Nullable ResourcePath2 getRoot() {
        if (isRelative()) return null;
        return resolve(new ResourcePath2(ROOT_PATH + PATH_SEPARATOR));
    }

    /**
     * Resolves the given path against this path
     * and returns the resulting path.
     *
     * There is no guarantee that the resulting path is valid or exists.
     *
     * Special paths such as {@code /}, {@code ..}, and {@code .}, are interpreted
     * as you would expect. It is valid for a path element to have a name that matches
     * the special name, in which case it must be escaped.
     *
     * If the other path has a trailing slash, it is <em>not</em> removed.
     * Therefore, {@code "/abc/"} and {@code "/abc"} represent different paths.
     * It is up to the interpretation of the path to determine if they point to the same resource,
     * but is recommended that the trailing slash is used to distinguish between
     * a file resource {@code "/abc"} and a directory resource {@code "/abc/"} when both could have
     * the same name.
     *
     * @param other the other path to resolve against this path.
     * It may be an absolute or relative path.
     * @return the resulting path
     */
    public ResourcePath2 resolve(ResourcePath2 other) {
        ListView<String> thisPathSegments = this.getRawSegments();
        ListView<String> otherPathSegments = other.getRawSegments();

        if (otherPathSegments.isEmpty()) return this;
        // The other path is an absolute path, so we return that instead.
        if (otherPathSegments.get(0).equals(ROOT_PATH)) return other;

        // Concatenate into a new path
        List<String> newPathSegments = new ArrayList<>();
        thisPathSegments.addAllTo(newPathSegments);
        otherPathSegments.addAllTo(newPathSegments);

        // Normalize the path
        List<String> normalizedPath = normalize(newPathSegments);

        return new ResourcePath2(String.join(PATH_SEPARATOR.toString(), normalizedPath));
    }

    /**
     * Determines the path of this resource path relative to the specified resource path.
     *
     * When both paths are relative, they are assumed to be relative to the same directory.
     *
     * @param other the other resource path
     * @return the relative path; or {@code null} when it could not be determined
     * (e.g., the paths are not both absolute or both relative)
     */
    public @Nullable ResourcePath2 relativeTo(ResourcePath2 other) {
        if (this.isAbsolute() != other.isAbsolute()) return null;

        // Determine the common prefix
        ListView<String> thisSegments = this.getRawSegments();
        ListView<String> otherSegments = other.getRawSegments();
        int commonPrefixLength = getCommonPrefix(thisSegments, otherSegments).size();

        // /a/b/c  relative to /a/b/c/   = ../c         (prefix: /a/b/c)
        // /a/b/c  relative to /a/b/c    = ./c          (prefix: /a/b/c)
        // /a/b/c/ relative to /a/b/c/   = ./           (prefix: /a/b/c/)

        // /a/b/c  relative to /a/b/d    = ./c          (prefix: /a/b/)
        // /a/b/c/ relative to /a/b/d/   = ../c/        (prefix: /a/b/)
        // /a/b/c  relative to /a/b/d/e  = ../c         (prefix: /a/b/)
        // /a/b/c/ relative to /a/b/d/e/ = ../../c/     (prefix: /a/b/)

        // ["", "a", "b", "c"]      relative to ["", "a", "b", "c", ""]         = ["..", "c"]           (prefix: ["", "a", "b", "c"])
        // ["", "a", "b", "c"]      relative to ["", "a", "b", "c"]             = [".", "c"]            (prefix: ["", "a", "b", "c"])
        // ["", "a", "b", "c", ""]  relative to ["", "a", "b", "c", ""]         = [".", ""]             (prefix: ["", "a", "b", "c", ""])

        // ["", "a", "b", "c"]      relative to ["", "a", "b", "d"]             = [".", "c"]            (prefix: ["", "a", "b"])
        // ["", "a", "b", "c", ""]  relative to ["", "a", "b", "d", ""]         = ["..", "c", ""]       (prefix: ["", "a", "b"])
        // ["", "a", "b", "c"]      relative to ["", "a", "b", "d", "e"]        = ["..", "c"]           (prefix: ["", "a", "b"])
        // ["", "a", "b", "c", ""]  relative to ["", "a", "b", "d", "e", ""]    = ["..", "..", "c", ""] (prefix: ["", "a", "b"])

        // []           relative to [""]            = ["..", "c"]
        // []           relative to []              = [".", "c"]
        // []           relative to []              = [".", ""]

        // ["c"]      relative to ["d"]             = [".", "c"]
        // ["c", ""]  relative to ["d", ""]         = ["..", "c", ""]
        // ["c"]      relative to ["d", "e"]        = ["..", "c"]
        // ["c", ""]  relative to ["d", "e", ""]    = ["..", "..", "c", ""]


        Stream<String> thisRemainder = thisSegments.stream().skip(commonPrefixLength);
        Stream<String> otherRemainder = otherSegments.stream().skip(commonPrefixLength);

        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Determines the relation between this resource path and the specified resource path.
     *
     * When both paths are relative, they are assumed to be relative to the same directory.
     *
     * @param other the other resource path
     * @return the relation between this resource path and the specified resource path;
     * or {@code null} when there is no relation
     */
    public @Nullable ResourceRelation relationTo(ResourcePath2 other) {
        if (this.isAbsolute() != other.isAbsolute()) {
            // An absolute and a relative path are never related.
            return null;
        }

        // Find the depth of the closest common ancestor,
        // relative to an imaginary current folder
        // (or the root in case of absolute paths).
        ListView<String> thisSegments = this.getRawSegments();
        ListView<String> otherSegments = other.getRawSegments();
        int commonPrefixLength = getCommonPrefix(thisSegments, otherSegments).size();

        // Calculate the steps up, then down,
        // from the other node to this node,
        // relative to an imaginary current folder
        // (or the root in case of absolute paths).
        int up = otherSegments.size() - commonPrefixLength;
        int down = thisSegments.size() - commonPrefixLength;

        return new ResourceRelation(up, down);
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ResourcePath2 that = (ResourcePath2)o;
        return normalizedPath.equals(that.normalizedPath);
    }

    @Override
    public int hashCode() {
        return normalizedPath.hashCode();
    }

    @Override public String toString() {
        return this.normalizedPath;
    }

    /**
     * Escapes a name for use as a path segment.
     *
     * Escapes reserved characters (the forward slash {@code '/'}).
     *
     * Escaping uses RFC-3986 escape sequences, such as {@code %2f}.
     *
     * @param name the original name
     * @return the escaped name
     */
    public static String escape(String name) {
        // We do minimal escaping here. We need to escape slashes to be able to distinguish
        // slashes in names from path separators.
        return name.replace(PATH_SEPARATOR.toString(), "%2f");
    }

    /**
     * Unescapes a name from its use as a path segment.
     *
     * Unescaping uses RFC-3986 escape sequences, such as {@code %2f}.
     *
     * @param escapedName the escaped name
     * @return the original name
     */
    public static String unescape(String escapedName) {
        try {
            // We unescape anything we can, not just the characters that we would escape ourselves.
            return URLDecoder.decode(escapedName, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the common prefix of the two lists of segments.
     *
     * @param segments1 a list of raw segments
     * @param segments2 another list of raw segments
     * @return the common prefix of raw segments
     */
    private static ListView<String> getCommonPrefix(ListView<String> segments1, ListView<String> segments2) {
       for (int i = 0; i < Math.min(segments1.size(), segments2.size()); i++) {
           if (!segments1.get(i).equals(segments2.get(i))) {
               // FIXME: This can be more efficient by returning a sublist view of the list
               return ListView.of(segments1.stream().limit(i).collect(Collectors.toList()));
           }
       }
       // Both are equal
       return segments1;
    }

    /**
     * Normalize a path according to RFC-3986.
     *
     * Normalization attempts to collapse {@code "../"} and {@code "./"}.
     * This function can normalize both relative and absolute paths.
     *
     * @param segments a sequence of path segments, each segment escaped
     * @return a list of normalized path segments, each segment escaped
     */
    private static List<String> normalize(Iterable<String> segments) {
        Deque<String> normalizedPath = new ArrayDeque<>();
        for (String segment : segments) {
            if (segment.equals(PARENT_PATH)) {
                if (!normalizedPath.isEmpty()) {
                    String top = normalizedPath.getLast();
                    if((normalizedPath.size() > 1 || !top.equals(ROOT_PATH)) && !top.equals(CURRENT_PATH) && !top.equals(PARENT_PATH)) {
                        // We pop only when the top element is not special.
                        normalizedPath.pop();
                    }
                } else {
                    normalizedPath.push(segment);
                }
            } else if (segment.equals(CURRENT_PATH)) {
                if (normalizedPath.isEmpty()) {
                    normalizedPath.push(segment);
                }
            } else {
                normalizedPath.push(segment);
            }
        }
        return new ArrayList<>(normalizedPath);
    }

}
