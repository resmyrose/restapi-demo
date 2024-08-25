package com.example.demo.rest.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import com.example.demo.rest.resource.Dietician;
import com.example.demo.rest.resource.Patient;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;

public class AuthUtil {

	public static final String ROLES = "roles";
	public static final String PRINCIPAL = "principal";
	private static final int accessExpirationMs=600000; // 10 mins
    
	public static boolean hasDieticianAccess(HttpServletRequest request, Dietician dietician) {
		List<String> authorizedRoles = (List<String>)request.getAttribute(ROLES);
		if (authorizedRoles != null) {
			if (authorizedRoles.contains("admin")) {
				return true;
		    } else if (authorizedRoles.contains("dietician")) {
		    	String principal = (String) request.getAttribute(PRINCIPAL);
		    	return dietician.getEmail().equals(principal);
		    }
		}
		return false;
	}
	
	public static boolean hasPatientAccess(HttpServletRequest request, Patient patient) {
		List<String> authorizedRoles = (List<String>)request.getAttribute(ROLES);
		if (authorizedRoles != null) {
			if (authorizedRoles.contains("admin")) {
				return true;
		    }
			String principal = (String) request.getAttribute(PRINCIPAL);
			if (authorizedRoles.contains("dietician")) {
		    	return patient.getDieticianEmail().equals(principal);
		    } else if (authorizedRoles.contains("patient")) {
		    	return patient.getEmail().equals(principal);
		    }
		}
		return false;
	}
	
	public static boolean hasAdminAccess(HttpServletRequest request) {
		List<String> authorizedRoles = (List<String>)request.getAttribute(ROLES);
		return (authorizedRoles != null && authorizedRoles.contains("admin"));
	}
	
	public static String generateAccessToken(String userName,  List<String> roleArray, String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
        		.setSubject(userName)
                .claim("roles", roleArray)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + accessExpirationMs))
                .signWith(SignatureAlgorithm.RS256, generateJwtKeyEncryption(jwtPrivateKey))
                .compact();
    }

    public static Jws<Claims> validateJwt(String authToken,String jwtPublicKey) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(generateJwtKeyDecryption(jwtPublicKey)).parseClaimsJws(authToken);
            return claims;
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: {}"+ e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: {}"+ e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: {}"+ e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: {}"+ e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: {}"+ e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("no such algorithm exception");
        } catch (InvalidKeySpecException e) {
            System.out.println("invalid key exception");
        }

        return null;
    }

    private static PublicKey generateJwtKeyDecryption(String jwtPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.getDecoder().decode(jwtPublicKey);
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyBytes);

        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private static PrivateKey generateJwtKeyEncryption(String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.getDecoder().decode(jwtPrivateKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(keyBytes);
        
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

}
