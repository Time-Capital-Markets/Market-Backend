package com.fourmint.platform.port.out;
import java.util.*;
import com.fourmint.platform.*;
public interface ResourceRepository<T extends Resource> {
    Optional<Versioned<T>> find(String id);
    List<T> findPage(long offset, int limit);
    boolean insert(T value);
    boolean replace(T value, long currentVersion);
    boolean delete(String id, long currentVersion);
}
