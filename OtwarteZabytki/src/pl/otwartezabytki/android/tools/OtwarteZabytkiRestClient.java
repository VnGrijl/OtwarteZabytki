package pl.otwartezabytki.android.tools;

import com.loopj.android.http.*;

public class OtwarteZabytkiRestClient {
	
	private static final String BASE_API_URL = "http://otwartezabytki.pl/api/v1/";
	private static final int TIMEOUT = 30000; //miliseconds

	  private static AsyncHttpClient client = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  client.setTimeout(TIMEOUT);
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_API_URL + relativeUrl;
	  }
}
