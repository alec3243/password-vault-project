package com.vault.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	static String IV = "AAAAAAAAAAAAAAAA";

	// public static void main(String [] args) {
	// try {
	//
	// System.out.println("==Java==");
	// System.out.println("plain: " + plaintext);
	//
	// byte[] cipher = encrypt(plaintext, encryptionKey);
	//
	// System.out.print("cipher: ");
	// for (int i=0; i<cipher.length; i++)
	// System.out.print(new Integer(cipher[i])+" ");
	// System.out.println("");
	//
	// String decrypted = decrypt(cipher, encryptionKey);
	//
	// System.out.println("decrypt: " + decrypted);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static byte[] encrypt(String plainText, String encryptionKey) throws InvalidKeyException,
			InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException  {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
		return cipher.doFinal(plainText.getBytes("UTF-8"));
	}

	public static String decrypt(byte[] cipherText, String encryptionKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
		return new String(cipher.doFinal(cipherText), "UTF-8");
	}
	
	
}