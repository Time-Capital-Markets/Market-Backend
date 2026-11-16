package com.fourmint.platform;
public final class ResourceFailure extends RuntimeException {
    public enum Kind { NOT_FOUND, CONFLICT, PRECONDITION_FAILED }
    private final Kind kind;
    public ResourceFailure(Kind kind, String message) { super(message); this.kind = kind; }
    public Kind kind() { return kind; }
}
