package com.fourmint.platform.spring;
import com.fourmint.platform.ResourceFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.context.request.WebRequest;
@RestControllerAdvice
public class ApiErrors extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiErrors.class);
    @ExceptionHandler(ResourceFailure.class)
    ResponseEntity<ProblemDetail> resource(ResourceFailure error) {
        var status = switch (error.kind()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case PRECONDITION_FAILED -> HttpStatus.PRECONDITION_FAILED;
        };
        return problem(status, error.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> invalid(IllegalArgumentException error) { return problem(HttpStatus.BAD_REQUEST, error.getMessage()); }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException error, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // Do not disclose JSON input, account details or implementation exceptions.
        return handleExceptionInternal(error, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid JSON, missing required field, unknown field or invalid value"), headers, HttpStatus.BAD_REQUEST, request);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> unexpected(Exception error) {
        LOG.error("Unhandled API failure", error);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred");
    }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String message) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(ProblemDetail.forStatusAndDetail(status, message));
    }
}
