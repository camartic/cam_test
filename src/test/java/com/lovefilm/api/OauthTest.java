package com.lovefilm.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import junit.framework.Assert;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.junit.Ignore;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

public class OauthTest {

	public static final String CONSUMER_KEY = "h3tvwysvzg5kaufsqwaadg7f";
	public static final String CONSUMER_SECRET = "kVXnCNTD2d";
	public static final String APPLICATION_NAME = "Easy Online Movie Browser";

	public static final String NETFLIX_REQUEST_TOKEN_URL = "http://openapi.lovefilm.com/oauth/request_token";
	public static final String NETFLIX_ACCESS_TOKEN_URL = "http://openapi.lovefilm.com/oauth/access_token";
	public static final String NETFLIX_AUTHORIZE_URL = "http://openapi.lovefilm.com/oauth/oauth/login";


	@Test
	public void testGetRequest_normalCondition_expect403() throws IOException {
		URL url = new URL("http://openapi.lovefilm.com/oauth/request_token");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		Assert.assertEquals(403, conn.getResponseCode());
	}

	@Test
	public void testConnection_whenRequestIsSigned_return200() throws IOException, OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		//URL url = new URL("http://openapi.lovefilm.com/catalog/title");
		URL url = new URL("http://openapi.lovefilm.com/oauth/request_token");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		consumer.sign(conn);
		conn.connect();
		Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
	}

	private String readResponse(HttpURLConnection conn) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String decodedString = null;
		StringBuilder response = new StringBuilder();
	
		while ((decodedString = in.readLine()) != null) {
			response.append(decodedString);
		}
		//in.close();
		return response.toString();
	}
	
	@Test
	public void testStep1PostRequest_whenRequestIsSigned_returnTokenAndSecret() throws IOException, OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		//URL url = new URL("http://openapi.lovefilm.com/catalog/title");
		URL url = new URL("http://openapi.lovefilm.com/oauth/request_token");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		consumer.sign(conn);
		conn.connect();
		
		String response = readResponse(conn);
		Assert.assertTrue(response.indexOf("oauth_token")!= -1);
		Assert.assertTrue(response.indexOf("oauth_token_secret")!= -1);
		Assert.assertTrue(response.indexOf("login_url")!= -1);
		System.out.println(response);
		int startIndex = response.indexOf("oauth_token");
		startIndex = response.indexOf("=", startIndex)+1;
		int endIndex = response.indexOf("&", startIndex);
		String value = response.substring(startIndex, endIndex);
		System.out.println("To allow us to access your Lovefilm account on your behalf, please visit https://www.lovefilm.com/activate, and enter the activation code: "+value);
		System.out.println(readResponse(conn));
	}
	
	@Test
	public void testParseToken(){
		String response = "oauth_token=ZGMNM&oauth_token_secret=TyaVX3KVyVJEqpvZYA6bRhtv&login_url=https%3A%2F%2Fwww.lovefilm.com%2Factivate:";
		int startIndex = response.indexOf("oauth_token");
		startIndex = response.indexOf("=", startIndex)+1;
		int endIndex = response.indexOf("&", startIndex);
		String value = response.substring(startIndex, endIndex);
		Assert.assertEquals("ZGMNM", value);
	}
	
	@Ignore
	@Test
	public void testPostRequest() throws IOException {
		URL url = new URL("http://openapi.lovefilm.com/catalog/title");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write("string=test");
		out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String decodedString;

		while ((decodedString = in.readLine()) != null) {
			System.out.println(decodedString);
		}
		in.close();
	}

}
