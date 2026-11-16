package com.fourmint.platform.spring;
import com.fourmint.platform.*;
import com.fourmint.platform.port.in.CrudUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
public abstract class CrudController<T extends Resource> {
    private final CrudUseCase<T> useCase;
    protected CrudController(CrudUseCase<T> useCase) { this.useCase = useCase; }
    @GetMapping(params = "!id")
    public Page<T> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) { return useCase.list(page, size); }
    @GetMapping("/{id}")
    public ResponseEntity<T> get(@PathVariable String id) { return response(useCase.get(id)); }
    // Query id also supports FourMint IDs containing slashes, e.g. "FXNDF USD/BRL".
    @GetMapping(params = "id")
    public ResponseEntity<T> getByQuery(@RequestParam String id) { return get(id); }
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T value) {
        var saved = useCase.create(value);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().replaceQuery(null).queryParam("id", "{id}").buildAndExpand(saved.value().id()).encode().toUri())
            .eTag(tag(saved.version())).body(saved.value());
    }
    @PutMapping("/{id}")
    public ResponseEntity<T> replace(@PathVariable String id, @RequestBody T value, @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        return response(useCase.replace(id, value, version(ifMatch)));
    }
    @PutMapping
    public ResponseEntity<T> replaceByQuery(@RequestParam String id, @RequestBody T value, @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        return replace(id, value, ifMatch);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        useCase.delete(id, version(ifMatch)); return ResponseEntity.noContent().build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteByQuery(@RequestParam String id, @RequestHeader(value = "If-Match", required = false) String ifMatch) { return delete(id, ifMatch); }
    private ResponseEntity<T> response(Versioned<T> value) { return ResponseEntity.ok().eTag(tag(value.version())).body(value.value()); }
    private String tag(long version) { return "\"" + version + "\""; }
    private Long version(String value) {
        if (value == null) return null;
        if (!value.matches("\"[1-9][0-9]*\"")) throw new IllegalArgumentException("If-Match must contain one quoted positive version");
        try { return Long.parseLong(value.substring(1, value.length() - 1)); }
        catch (NumberFormatException error) { throw new IllegalArgumentException("If-Match version is too large"); }
    }
}
