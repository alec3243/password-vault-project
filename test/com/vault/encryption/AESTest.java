package com.vault.encryption;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class AESTest {
	
	//test 2 passwords are encrypted to different hashes
	@Test
	public void encryptTest() throws InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String password1 = "testing123";
		String password2 = "testing123";
		String key = "mastermastermast";
		boolean same = (AES.encrypt(password1, key) == AES.encrypt(password2, key));
		assertFalse(same);
	}
	
	@Test
	public void decryptTest() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String password = "testing123";
		String key = "mastermastermast";
		byte[] encryptedPassword = AES.encrypt(password, key);
		assertEquals(AES.decrypt(encryptedPassword, "mastermastermast"), password);
	}

}
