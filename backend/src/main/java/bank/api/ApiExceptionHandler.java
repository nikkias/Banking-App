package bank.api;

import bank.config.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setTitle("Invalid request");
        problem.setProperty("path", request.getRequestURI());
        addCorrelationId(problem, request);
        problem.setProperty("errors", exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleBadRequest(IllegalArgumentException exception, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Business rule rejected the request");
        problem.setProperty("path", request.getRequestURI());
        addCorrelationId(problem, request);
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handleConflict(IllegalStateException exception, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Account state conflict");
        problem.setProperty("path", request.getRequestURI());
        addCorrelationId(problem, request);
        return problem;
    }

    private void addCorrelationId(ProblemDetail problem, HttpServletRequest request) {
        problem.setProperty(CorrelationIdFilter.REQUEST_ATTRIBUTE, request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE));
    }
}