package mypackage;

public class XOREncryption {

	public static String encryptDecrypt(String input, String pass) {
		char[] key = pass.toCharArray();
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			output.append((char) (input.charAt(i) ^ key[i % key.length]));
		}

		return output.toString();
	}
}