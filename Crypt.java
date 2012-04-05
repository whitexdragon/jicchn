import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypt {
	//Generate AES Key
	public static byte[] generateAESKey(int keySize){
		Security.addProvider(new BouncyCastleProvider());
		byte[] skey = null;
		try{
			KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
			kgen.init(keySize);
			SecretKey key = kgen.generateKey();
			skey = key.getEncoded();
		}catch(Exception e){
			System.out.println(e);
		}
		return skey;
	}
	
	//Get public key fingerprint
	public static String getFingerprint(PublicKey key){
		String fingerprint = null;
		Security.addProvider(new BouncyCastleProvider());
		try{
			byte[] pKey = key.getEncoded();
			MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
			byte[] fingerPrint = md.digest(pKey);
			StringBuffer strbuf = new StringBuffer(fingerPrint.length * 2);
		    int i;
		    for (i = 0; i < fingerPrint.length; i++) {
		    	if (((int) fingerPrint[i] & 0xff) < 0x10)
		    		strbuf.append("0");
		    	strbuf.append(Long.toString((int) fingerPrint[i] & 0xff, 16));
		    }
		    fingerprint = strbuf.toString();
		} catch(Exception e){
			System.out.println(e);
		}
		return fingerprint;
	}
	
	//Encrypt using RSA private key
	public static byte[] RSAEncrypt(byte[] bytes, PrivateKey key){
		Security.addProvider(new BouncyCastleProvider());
		byte[] encrypted = null;
		try{
			Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypted = cipher.doFinal(bytes);
		} catch(Exception e){
			System.out.println(e);
		}
		return encrypted;
	}

	//Encrypt using RSA public key
	public static byte[] RSAEncrypt(byte[] bytes, PublicKey key){
		Security.addProvider(new BouncyCastleProvider());
		byte[] encrypted = null;
		try{
			Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypted = cipher.doFinal(bytes);
		} catch(Exception e){
			System.out.println(e);
		}
		return encrypted;
	}

	//Decrypt using RSA private key
	public static byte[] RSADecrypt(byte[] bytes, PrivateKey key){
		Security.addProvider(new BouncyCastleProvider());
		byte[] decrypt = null; 
		try{
			Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key);
			decrypt = cipher.doFinal(bytes);
		} catch(Exception e){
			System.out.println(e);
		}
		return decrypt;
	}

	//Decrypt using RSA public key
	public static byte[] RSADecrypt(byte[] bytes, PublicKey key){
		Security.addProvider(new BouncyCastleProvider());
		byte[] decrypt = null; 
		try{
			Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key);
			decrypt = cipher.doFinal(bytes);
		} catch(Exception e){
			System.out.println(e);
		}
		return decrypt;
	}

	//Encrypt the envelope and put the encrypted data into envelope along with the IV
	public static Envelope AESEncrypt(Envelope en, byte[] key){
		Security.addProvider(new BouncyCastleProvider());
		Envelope envelope = new Envelope("IV, Encryption");
		SecretKeySpec skeyspec = new SecretKeySpec(key, "AES");
		try{
			byte[] bytes = getBytes(en);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			envelope.addObject(cipher.getIV());
			envelope.addObject(cipher.doFinal(bytes));
		} catch(Exception e){
			System.out.println(e);
		}
		return envelope;
	}

	//Decrypt the content in the envelope with IV
	public static Envelope AESDecrypt(Envelope envelope, byte[] key){
		Security.addProvider(new BouncyCastleProvider());
		Envelope en = null;
		byte[] decrypt = null; 
		byte[] IV = (byte[]) envelope.getObjContents().get(0);
		byte[] encrypted = (byte[]) envelope.getObjContents().get(1);
		SecretKeySpec skeyspec = new SecretKeySpec(key, "AES");
		try{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec, new IvParameterSpec(IV));
			decrypt = cipher.doFinal(encrypted);
			en = getEnvelope(decrypt);
		} catch(Exception e){
			System.out.println(e);
		}
		return en;
	}

	//Convert Envelope Object into byte arrays
	private static byte[] getBytes(Envelope e) throws java.io.IOException{
		Security.addProvider(new BouncyCastleProvider());
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(e);
		oos.flush(); 
		oos.close(); 
		bos.close();
		byte [] data = bos.toByteArray();
		return data;
	}

	//Convert bytes array into Envelope Object
	private static Envelope getEnvelope(byte[] bytes) throws java.io.IOException, ClassNotFoundException{
		Security.addProvider(new BouncyCastleProvider());
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Envelope e= (Envelope) ois.readObject();
		ois.close();
		bis.close();
		return e;
	}
	
	public static byte[] getHash(String s){
		Security.addProvider(new BouncyCastleProvider());
		byte[] hashed = null;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
			hashed = md.digest(s.getBytes());
		} catch(Exception e){
			System.out.println(e);
		}
		return hashed;
	}
}