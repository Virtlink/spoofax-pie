package mb.common.message;

import mb.common.region.Region;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String text;
    public final MessageSeverity severity;
    public final @Nullable Region region;
    public final @Nullable Throwable exception;


    public Message(String text, MessageSeverity severity, @Nullable Region region, @Nullable Throwable exception) {
        this.text = text;
        this.severity = severity;
        this.region = region;
        this.exception = exception;
    }

    public Message(String text, MessageSeverity severity, @Nullable Region region) {
        this(text, severity, region, null);
    }

    public Message(String text, MessageSeverity severity) {
        this(text, severity, null, null);
    }


    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final Message message = (Message) o;
        return text.equals(message.text) &&
            severity == message.severity &&
            Objects.equals(region, message.region) &&
            Objects.equals(exception, message.exception);
    }

    @Override public int hashCode() {
        return Objects.hash(text, severity, region, exception);
    }

    @Override public String toString() {
        return text;
    }
}
