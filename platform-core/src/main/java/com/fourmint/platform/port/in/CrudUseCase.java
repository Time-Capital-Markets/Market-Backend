package com.fourmint.platform.port.in;
import com.fourmint.platform.*;
public interface CrudUseCase<T extends Resource> {
    Versioned<T> create(T value);
    Versioned<T> get(String id);
    Page<T> list(int page, int size);
    Versioned<T> replace(String id, T value, Long expectedVersion);
    void delete(String id, Long expectedVersion);
}
