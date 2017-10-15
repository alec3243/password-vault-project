package com.vault.exception;

public class CryptoException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3760192618302081063L;

	public CryptoException() {
	}

	public CryptoException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
