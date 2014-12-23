package cn.fh.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpSession;

import cn.fh.security.credential.Credential;

public class CredentialUtils {
	
	/**
	 * Get credential from session
	 * @param session
	 * @return null if nothing has found
	 */
	public static Credential getCredential(HttpSession session) {
		Credential credential = (Credential) session.getAttribute(Credential.CREDENTIAL_CONTEXT_ATTRIBUTE);
		
		return credential;
	}
	
	/**
	 * Put credential into session
	 * 
	 * @param session
	 * @param credential
	 * @return
	 */
	public static void createCredential(HttpSession session, Credential credential) {
/*		if (null != session.getAttribute(Credential.CREDENTIAL_CONTEXT_ATTRIBUTE)) {
			throw new RuntimeException("用户已登陆，不得重复设置Credential!");
		}*/

		session.setAttribute(Credential.CREDENTIAL_CONTEXT_ATTRIBUTE, credential);
	}
	
	/**
	 * Get cryptograph of the password using SHA-1 algorithm.
	 * 
	 * @param psd
	 * @return
	 */
	public static String sha(String psd) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(psd.getBytes());
			byte[] bytes = md.digest();
			
			StringBuilder sb = new StringBuilder();
            for(int i = 0 ; i < bytes.length ; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            
            return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(sha("111111"));
	}
}
