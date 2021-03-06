/* Implements the GroupClient Interface */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GroupClient extends Client implements GroupClientInterface {
	int inCounter;
	int outCounter;
	
	public GroupClient(){
		inCounter = 0;
		outCounter = 0;
	}
 
	public void disconnect(){
		super.disconnect(outCounter++);
	}
	
	public void disconnect(byte[] key){
		super.disconnect(key, outCounter++);
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<String, ArrayList<byte[]>> getVersionKeys(byte[] sKey, byte[] hmackey)
	 {
		try
		{
			//UserToken token = null;
			Hashtable<String, ArrayList<byte[]>> versionKeys;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("KEYS");
			//message.addObject(groupName);
			message.setNumber(outCounter++);
			Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
			output.writeObject(m);
			//Get the response from the server
			
			response = (Envelope)input.readObject();
			Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
			if(e.getNumber() != inCounter++){
				 System.out.println("message order incorrect.\nConnection terminated.");
				 System.exit(1);
			 }
			//Successful response
			if(e.getMessage().equals("OK"))
				
			{
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = e.getObjContents();
				
				if(temp.size() == 1)
				{
					versionKeys = (Hashtable<String, ArrayList<byte[]>>)temp.get(0);
					if(versionKeys != null)
					return versionKeys;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		
	 }
	
	@SuppressWarnings("unchecked")
	public ArrayList<byte[]> getVersionKey(byte[] sKey, String groupName, byte[] hmackey)
	 {
		try
		{
			//UserToken token = null;
			ArrayList<byte[]> versionKeys;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("VERSION");
			message.addObject(groupName);
			message.setNumber(outCounter++);
			Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
			output.writeObject(m);
			//Get the response from the server
			
			response = (Envelope)input.readObject();
			Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
			if(e.getNumber() != inCounter++){
				 System.out.println("message order incorrect.\nConnection terminated.");
				 System.exit(1);
			 }
			//Successful response
			if(e.getMessage().equals("OK"))
				
			{
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = e.getObjContents();
				
				if(temp.size() == 1)
				{
					versionKeys= (ArrayList<byte[]>)temp.get(0);
					if(versionKeys != null)
					return versionKeys;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		
	 }
	public PublicKey getPublicKey(){
		try
		{
			PublicKey publicKey = null;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return the public key.
			message = new Envelope("GSPUBLIC");
			message.setNumber(outCounter++);
			output.writeObject(message);
			//Get the response from the server
			response = (Envelope)input.readObject();
			if(response.getNumber() != inCounter++){
				System.out.println("message order incorrect.\nConnection terminated.");
				System.exit(1);
			}
			
			//Successful response
			if(response.getMessage().equals("OK"))
				
			{
				
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = response.getObjContents();
				
				if(temp.size() == 1)
				{
					publicKey = (PublicKey)temp.get(0);
					if(publicKey != null)
					return publicKey;
				}
			}
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	public boolean login(byte[] credential, int keySize, int HmacKeySize){
		try{
			Envelope message = null, response = null;
			
			 message = new Envelope("LOGIN");
			 message.addObject(credential); 
			 message.addObject(keySize);
			 message.addObject(HmacKeySize);
			 message.setNumber(outCounter++);
			 output.writeObject(message);
		
			 response = (Envelope)input.readObject();
			 if(response.getNumber() != inCounter++){
				 System.out.println("message order incorrect.\nConnection terminated.");
				 System.exit(1);
			 }
			 
			 if(response.getMessage().equals("OK"))
			 {
				 return true;
			 }
		}catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return false;
		}
		return false;
	}
	
	 public UserToken getToken(byte[] sKey, byte[] hmackey, String fileServerName)
	 {
		try
		{
			UserToken token = null;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("GET");
			message.addObject(fileServerName);
			message.setNumber(outCounter++);
			Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
			output.writeObject(m);
			//Get the response from the server
			
			response = (Envelope)input.readObject();
			Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
			if(e.getNumber() != inCounter++){
				 System.out.println("message order incorrect.\nConnection terminated.");
				 System.exit(1);
			 }
			//Successful response
			if(e.getMessage().equals("OK"))
				
			{
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = e.getObjContents();
				
				if(temp.size() == 1)
				{
					token = (UserToken)temp.get(0);
					if(token != null)
					return token;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		
	 }
	 
	 public boolean createUser(String username, UserToken token, byte[] sKey, byte[] hmackey)
	 {
		 try
		 {
			 Envelope message = null, response = null;
			 //Tell the server to create a user
			 message = new Envelope("CUSER");
			 message.addObject(username); //Add user name string
			 message.addObject(token); //Add the requester's token
			 message.setNumber(outCounter++);
			 Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
			 output.writeObject(m);
		
			 response = (Envelope)input.readObject();
			 Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
			 if(e.getNumber() != inCounter++){
				 System.out.println("message order incorrect.\nConnection terminated.");
				 System.exit(1);
			 }
			 //If server indicates success, return true
			 if(e.getMessage().equals("OK"))
			 {
				 return true;
			 }
			 return false;
		 }
		 catch(Exception e)
		 {
			 System.err.println("Error: " + e.getMessage());
			 e.printStackTrace(System.err);
			 return false;
		 }
	 }
	 
	 public boolean deleteUser(String username, UserToken token, byte[] sKey, byte[] hmackey)
	 {
		 try
			{
				Envelope message = null, response = null;
			 
				//Tell the server to delete a user
				message = new Envelope("DUSER");
				message.addObject(username); //Add user name
				message.addObject(token);  //Add requester's token
				message.setNumber(outCounter++);
				Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
				output.writeObject(m);
			
				response = (Envelope)input.readObject();
				Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
				if(e.getNumber() != inCounter++){
					 System.out.println("message order incorrect.\nConnection terminated.");
					 System.exit(1);
				 }
				//If server indicates success, return true
				if(e.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean createGroup(String groupname, UserToken token, byte[] sKey, byte[] hmackey)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to create a group
				message = new Envelope("CGROUP");
				message.addObject(groupname); //Add the group name string
				message.addObject(token); //Add the requester's token
				message.setNumber(outCounter++);
				Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
				output.writeObject(m); 
			
				response = (Envelope)input.readObject();
				Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
				if(e.getNumber() != inCounter++){
					 System.out.println("message order incorrect.\nConnection terminated.");
					 System.exit(1);
				 }
				//If server indicates success, return true
				if(e.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteGroup(String groupname, UserToken token, byte[] sKey, byte[]hmackey)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to delete a group
				message = new Envelope("DGROUP");
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				message.setNumber(outCounter++);
				Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
				output.writeObject(m); 
			
				response = (Envelope)input.readObject();
				Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
				if(e.getNumber() != inCounter++){
					 System.out.println("message order incorrect.\nConnection terminated.");
					 System.exit(1);
				 }
				//If server indicates success, return true
				if(e.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 @SuppressWarnings("unchecked")
	public List<String> listMembers(String group, UserToken token, byte[] sKey, byte[] hmackey)
	 {
		 try
		 {
			 Envelope message = null, response = null;
			 //Tell the server to return the member list
			 message = new Envelope("LMEMBERS");
			 message.addObject(group); //Add group name string
			 message.addObject(token); //Add requester's token
			 message.setNumber(outCounter++);
			 Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
			 output.writeObject(m); 
			 
			 response = (Envelope)input.readObject();
			 Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
			 if(e.getNumber() != inCounter++){
				 System.out.println("message order incorrect.\nConnection terminated.");
				 System.exit(1);
			 }
			 //If server indicates success, return the member list
			 if(e.getMessage().equals("OK"))
			 {
				return (List<String>) e.getObjContents().get(0); //This cast creates compiler warnings. Sorry.
			 }
				
			 return null;
			 
		 }
		 catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return null;
			}
	 }
	 
	 public boolean addUserToGroup(String username, String groupname, UserToken token, byte[] sKey, byte[] hmackey)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to add a user to the group
				message = new Envelope("AUSERTOGROUP");
				message.addObject(username); //Add user name string
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				message.setNumber(outCounter++);
				Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
				output.writeObject(m); 
			
				response = (Envelope)input.readObject();
				Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
				if(e.getNumber() != inCounter++){
					 System.out.println("message order incorrect.\nConnection terminated.");
					 System.exit(1);
				 }
				//If server indicates success, return true
				if(e.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteUserFromGroup(String username, String groupname, UserToken token, byte[] sKey, byte[] hmackey)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to remove a user from the group
				message = new Envelope("RUSERFROMGROUP");
				message.addObject(username); //Add user name string
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				message.setNumber(outCounter++);
				Envelope m = Crypt.AESEncrypt(message, sKey, hmackey);
				output.writeObject(m);
			
				response = (Envelope)input.readObject();
				Envelope e = Crypt.AESDecrypt(response, sKey, hmackey);
				if(e.getNumber() != inCounter++){
					 System.out.println("message order incorrect.\nConnection terminated.");
					 System.exit(1);
				 }
				//If server indicates success, return true
				if(e.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public Envelope AESEncrypt(Envelope en, byte[] key){
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
	 
	 public static Envelope AESDecrypt(Envelope envelope, byte[] key){
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
	 
	 private static Envelope getEnvelope(byte[] bytes) throws java.io.IOException, ClassNotFoundException{
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			Envelope e= (Envelope) ois.readObject();
			ois.close();
			bis.close();
			return e;
		}
	 
	 private static byte[] getBytes(Envelope e) throws java.io.IOException{
	      ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	      ObjectOutputStream oos = new ObjectOutputStream(bos); 
	      oos.writeObject(e);
	      oos.flush(); 
	      oos.close(); 
	      bos.close();
	      byte [] data = bos.toByteArray();
	      return data;
	  }
}

