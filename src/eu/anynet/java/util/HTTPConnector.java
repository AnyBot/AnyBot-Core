package eu.anynet.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

// TODO: http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html

/**
 *
 * @author sim4000
 */
public class HTTPConnector {

   private DefaultHttpClient client;
   private BasicResponseHandler responseHandler;

   public HTTPConnector() {
      this.client = new DefaultHttpClient();
      this.responseHandler = new BasicResponseHandler();
      this.client.getParams().setParameter("http.useragent", "Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/534.13 (KHTML, like Gecko) Chrome/9.0.597.98 Safari/534.13");

      //SchemeRegistry schemeRegistry = client.getConnectionManager().getSchemeRegistry();
      //schemeRegistry.register(new Scheme("https", new TlsSniSocketFactory(), 443));
   }

   public void close()
   {
      this.client.close();
      this.client = null;
   }

   public HttpResponse doGet(String url, String referer, HashMap<String,String> headers) throws IOException {
      HttpGet httpget = new HttpGet(url);
      httpget.getParams().setBooleanParameter("http.protocol.handle-redirects", true);

      for(Entry<String,String> item : headers.entrySet())
      {
         httpget.addHeader(item.getKey(), item.getValue());
      }

      if(referer!=null && !referer.isEmpty()) {
          httpget.getParams().setParameter("Referer", referer);
      }

      HttpResponse response = this.client.execute(httpget);
      return response;
   }

   public HttpResponse doGet(String url, String referer) throws IOException {
      return this.doGet(url, referer, new HashMap<String,String>());
   }

   public HttpResponse doGet(String url) throws IOException {
      return this.doGet(url, null);
   }

   public String responseToString(HttpResponse response) throws HttpResponseException, IOException {
       String text = this.responseHandler.handleResponse(response);
       response.getEntity().consumeContent();
       return text;
   }

   public JSONObject responseToJSONObject(HttpResponse response) throws HttpResponseException, IOException
   {
      String text = this.responseToString(response);
      JSONObject json = (JSONObject)JSONValue.parse(text);
      return json;
   }

   public JSONArray responseToJSONArray(HttpResponse response) throws HttpResponseException, IOException
   {
      String text = this.responseToString(response);
      JSONArray json = (JSONArray)JSONValue.parse(text);
      return json;
   }

   public String getTextFromUrl(String url) throws IOException, HttpResponseException {
       HttpResponse response = this.doGet(url);
       return this.responseToString(response);
   }





	public String getHeaderValue(HttpResponse response, String header) {
		Header headervalue = response.getFirstHeader(header);
		if(headervalue!=null) {
			return headervalue.getValue();
		} else {
			throw new IllegalArgumentException("Der Header wurde nicht gefunden!");
		}
	}



	public int getStatusCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}



	public boolean isRedirect(HttpResponse response) {
		boolean redirect = response.getParams().isParameterTrue("http.protocol.handle-redirects");
		int responsecode = ((int)(this.getStatusCode(response)/10));
		if(responsecode==30 && redirect==true) {
			return true;
		} else {
			return false;
		}
	}




	public HttpResponse followRedirect(String url, HttpResponse response) throws IOException {

		//--> Wenn ein redirect festgestellt wurde, diesem folgen
		try {
			while(this.isRedirect(response)) {

				String headerurl = this.getHeaderValue(response, "Location");
				try {
					URI uri = new URI(headerurl);
					String host = uri.getHost();
					String path = uri.getRawPath();
					boolean absolute = (path.length()>0 ? path.substring(0, 1).equals("/") : false);
					String querystring = uri.getRawQuery();

					// Host und Pfad von original URL nutzen
					if(host==null && absolute==false) {
						url = url.substring(0, url.lastIndexOf("/")+1)+path;
						url = (querystring==null ? url : url+"?"+querystring);

					// Host von originalurl, Pfad von Header
					} else if(host==null && absolute==true) {
						url = url.substring(0, url.indexOf("/", 8))+path;
						url = (querystring==null ? url : url+"?"+querystring);

					// Redirect URL so verwenden
					} else if(host!=null && absolute==true) {
						url = headerurl;

					}

				} catch(URISyntaxException uriex) {
					throw new IllegalArgumentException("keine uri");
				}

				response.getEntity().consumeContent();
				response = this.doGet(url);

			}
		} catch(IllegalArgumentException e) {
			throw new IOException("In einem HTTP Response mit Status Code 30x wurde kein Location Header gefunden!", e);
		}
		return response;
	}





	public HttpResponse doPost(String url, HashMap<String,String> parameters, HashMap<String,String> headers) throws ClientProtocolException, IOException {

		HttpPost http = new HttpPost(url);
        http.getParams().setBooleanParameter("http.protocol.handle-redirects", true);

      for(Entry<String,String> item : headers.entrySet())
      {
         http.addHeader(item.getKey(), item.getValue());
      }

		Iterator<String> iparameters = parameters.keySet().iterator();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		while (iparameters.hasNext()) {
			String key = iparameters.next();
			formparams.add(new BasicNameValuePair(key, parameters.get(key)));
		}

		String encoding="UTF-8";

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, encoding);
		http.setEntity(entity);


		HttpResponse response = this.client.execute(http);
		//Logger.log(Integer.toString(this.getStatusCode(response)));

		return this.followRedirect(url, response);
	}


   public HttpResponse doPost(String url, HashMap<String,String> parameters) throws ClientProtocolException, IOException {
      return this.doPost(url, parameters, new HashMap<String,String>());
   }


	public HttpResponse doPut(String url, InputStream str, int length, HashMap<String,String> headers) throws ClientProtocolException, IOException
	{
		HttpPut httpPut = new HttpPut(url);

		for(String headerkey : headers.keySet())
		{
			httpPut.setHeader(headerkey, headers.get(headerkey));
		}

		InputStreamEntity entity = new InputStreamEntity(str, length);
		httpPut.setEntity(entity);

		HttpResponse response = this.client.execute(httpPut);
		return this.followRedirect(url, response);
	}



}