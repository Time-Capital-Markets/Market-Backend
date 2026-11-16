package com.fourmint.platform.application;
import java.util.List;
import com.fourmint.platform.*;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.port.out.ResourceRepository;
import static com.fourmint.platform.ResourceFailure.Kind.*;
public final class CrudService<T extends Resource> implements CrudUseCase<T> {
    private final ResourceRepository<T> repository;
    public CrudService(ResourceRepository<T> repository) { this.repository = repository; }
    public Versioned<T> create(T value) {
        Checks.id(value.id());
        if (!repository.insert(value)) throw new ResourceFailure(CONFLICT, "Resource already exists");
        return new Versioned<>(value, 1);
    }
    public Versioned<T> get(String id) {
        Checks.id(id);
        return repository.find(id).orElseThrow(() -> new ResourceFailure(NOT_FOUND, "Resource not found"));
    }
    public Page<T> list(int page, int size) {
        if (page < 0 || size < 1 || size > 200) throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 200");
        List<T> values = repository.findPage((long) page * size, size + 1);
        return new Page<>(values.stream().limit(size).toList(), page, size, values.size() > size);
    }
    public Versioned<T> replace(String id, T value, Long expectedVersion) {
        if (!id.equals(value.id())) throw new IllegalArgumentException("Body id must match the requested id");
        var current = get(id);
        checkVersion(current.version(), expectedVersion);
        if (!repository.replace(value, current.version())) throw concurrentFailure(expectedVersion);
        return new Versioned<>(value, current.version() + 1);
    }
    public void delete(String id, Long expectedVersion) {
        var current = get(id);
        checkVersion(current.version(), expectedVersion);
        if (!repository.delete(id, current.version())) throw concurrentFailure(expectedVersion);
    }
    private void checkVersion(long current, Long expected) {
        if (expected != null && expected != current) throw new ResourceFailure(PRECONDITION_FAILED, "The resource changed; reload it before retrying");
    }
    private ResourceFailure concurrentFailure(Long expected) {
        return new ResourceFailure(expected == null ? CONFLICT : PRECONDITION_FAILED, "Concurrent modification; reload the resource");
    }
}
