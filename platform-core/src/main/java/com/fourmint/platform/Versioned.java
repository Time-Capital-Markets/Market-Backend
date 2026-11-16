package com.fourmint.platform;
public record Versioned<T>(T value, long version) {}
