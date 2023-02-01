package com.example.accountsystemimpl.type;


import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;

    public static ResponseEntity<ErrorResponse> toResponseEntity(
            ErrorCode errorCode, String errorMessage) {
        return new ResponseEntity<>(new ErrorResponse(errorCode, errorMessage), HttpStatus.BAD_REQUEST);
    }
}
