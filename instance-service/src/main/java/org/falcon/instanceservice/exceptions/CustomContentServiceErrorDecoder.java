package org.falcon.instanceservice.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.falcon.instanceservice.dto.ErrorResponse;

import java.io.IOException;
import java.io.InputStream;

public class CustomContentServiceErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Exception decode(String invoker, Response response) {
        ErrorResponse errorResponse;
        try (InputStream bodyStream = response.body().asInputStream()) {
            errorResponse = objectMapper.readValue(bodyStream, ErrorResponse.class);
        } catch (IOException e) {
            // Fallback to default behavior if the body is unreadable
            return defaultDecoder.decode(invoker, response);
        }
        switch (response.status()) {
            case 400: // if its 400 then Room not found !!!!
                return new InvalidRequestException(response.status(), errorResponse);
            case 404:
                return new ResourceNotFoundException(response.status(), errorResponse);
            // Add other specific 4xx cases here
            default:
                // For any other error, including 5xx errors or unexpected 4xx codes,
                // delegate to the default Feign error decoder. This ensures that
                // we still get a FeignException rather than swallowing the error.
                return defaultDecoder.decode(invoker, response);
        }
    }
}
