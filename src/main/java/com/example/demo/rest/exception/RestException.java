package com.example.demo.rest.exception;

public class RestException extends Exception {

	private static final long serialVersionUID = 1875508187527201513L;

	public RestException(String message)
    {
       super(message);
    }
}
