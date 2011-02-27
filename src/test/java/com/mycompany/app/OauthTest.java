package com.mycompany.app;

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
	public void testSendRequest_title_noError() throws IOException {
		URL url = new URL("http://openapi.lovefilm.com/catalog/title");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		Assert.assertEquals(403, conn.getResponseCode());
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

	@Test
	public void testGetRequestToken() throws IOException, OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

		OAuthProvider provider = new DefaultOAuthProvider(NETFLIX_REQUEST_TOKEN_URL, NETFLIX_ACCESS_TOKEN_URL, NETFLIX_AUTHORIZE_URL);
		provider.setRequestHeader("oauth_consumer_key", CONSUMER_KEY);
		
		System.out.println(provider.toString());

		// we do not support callbacks, thus pass OOB
		String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
		authUrl = OAuth.addQueryParameters(authUrl, OAuth.OAUTH_CONSUMER_KEY, CONSUMER_KEY, "application_name",
				APPLICATION_NAME);

		System.out.println("Request token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());

		System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
		System.out.println("Enter the PIN code and hit ENTER when you're done:");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String pin = br.readLine();

		System.out.println("Fetching access token from Twitter...");

		provider.retrieveAccessToken(consumer, pin);

		System.out.println("Access token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());

		URL url = new URL("http://api.netflix.com/catalog/titles");
		HttpURLConnection request = (HttpURLConnection) url.openConnection();

		consumer.sign(request);

		System.out.println("Sending request...");
		request.connect();

		System.out.println("Response: " + request.getResponseCode() + " " + request.getResponseMessage());
	}

	@Ignore
	@Test
	public void testGetRequestTokenGoogle() throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException, IOException {
		OAuthConsumer consumer = new DefaultOAuthConsumer("matthiaskaeppler.de", "etpfOSfQ4e9xnfgOJETy4D56");

		String scope = "http://www.blogger.com/feeds";
		OAuthProvider provider = new DefaultOAuthProvider("https://www.google.com/accounts/OAuthGetRequestToken?scope="
				+ URLEncoder.encode(scope, "utf-8"), "https://www.google.com/accounts/OAuthGetAccessToken",
				"https://www.google.com/accounts/OAuthAuthorizeToken?hd=default");

		System.out.println("Fetching request token...");

		String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

		System.out.println("Request token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());

		System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
		System.out.println("Enter the verification code and hit ENTER when you're done:");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String verificationCode = br.readLine();

		System.out.println("Fetching access token...");

		provider.retrieveAccessToken(consumer, verificationCode.trim());

		System.out.println("Access token: " + consumer.getToken());
		System.out.println("Token secret: " + consumer.getTokenSecret());

		URL url = new URL("http://www.blogger.com/feeds/default/blogs");
		HttpURLConnection request = (HttpURLConnection) url.openConnection();

		consumer.sign(request);

		System.out.println("Sending request...");
		request.connect();

		System.out.println("Response: " + request.getResponseCode() + " " + request.getResponseMessage());
	}
}
