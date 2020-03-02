package com.ityks.main.html;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpDemo {

	static int index=0 ;
	static int length=0;
	OkHttpClient client ;

	static List<String> list = new ArrayList<>();
	
	private Map<String,String> headerMap = null;
	
	private Headers headers = null;
	
	public HttpDemo () {
		client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).cookieJar(new CookieJarWrapper()).build();		
	}
	
	public HttpDemo (boolean flag) {
		
		client = new OkHttpClient().newBuilder().followRedirects(false)  //禁制OkHttp的重定向操作，我们自己处理重定向
                .followSslRedirects(false).connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();
		
		
	}
	
	public HttpDemo (String ip,int port) {
		
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		client = new OkHttpClient().newBuilder().proxy(proxy).cookieJar(new CookieJarWrapper()).build();
		//client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS).cookieJar(new CookieJarWrapper()).build();
		
	}
	
	public HttpDemo (String proxyUrl) {
		
		String ip =  "";
		if (list.size() == 0) {
			getIp(proxyUrl);
		}
		
		if (index<list.size()) {
			ip = list.get(index);
			index++;
		}else {
			ip = list.get(0);
			index = 1;
		}
				
		System.out.println(ip);
		String[] tmpId = ip.split(":");
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(tmpId[0], new Integer(tmpId[1].trim())));
		client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS).proxy(proxy).cookieJar(new CookieJarWrapper()).build();
	}
	
	public HttpDemo (Headers headers) {
		this.headers = headers;
		client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS).cookieJar(new CookieJarWrapper()).build();
	}
	
	public static void getIp (String url) {
		Headers headers = null;
		String goIp = new HttpDemo(headers).doGetString(url);
		
		String[] ips = goIp.split("\r\n");
		for (String ip : ips) {
			list.add(ip.trim());
		}
		
	}
	
	public List<Cookie> getCookie(HttpUrl url) {
		return this.client.cookieJar().loadForRequest(url);
	}
	
	public String doGetString(String url) {
		String body = null;
		
		Request.Builder requestBuilder = new Request.Builder();
	
		Request request = requestBuilder.url(url).build();
		
		try (Response response = client.newCall(request).execute();){
			String l = response.header("location");
			String co = response.header("Set-Cookie");
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		//	return this.doGetString(url);
		}
		
		return body;
	}
	
	public String doGetString(String url, Headers header) {
		String body = null;
		
		Request.Builder requestBuilder = new Request.Builder();
		Set<String> names = header.names();
		for (String n : names) {
			requestBuilder.addHeader(n, header.get(n));
		}
		Request request = requestBuilder.url(url).build();
		
		try (Response response = client.newCall(request).execute();){
			String l = response.header("location");
			String co = response.header("Set-Cookie");
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		//	return this.doGetString(url);
		}
		
		return body;
	}
	
	public Headers doGetHeader(String url) {
		Map<String,String> result = new HashMap<String, String>();
		
		Request.Builder requestBuilder = new Request.Builder();
	
		Request request = requestBuilder.url(url).build();
		
		try (Response response = client.newCall(request).execute();){
		//	String l = response.header("location");
		//	String co = response.header("Set-Cookie");
			Headers headers = response.headers();
			return headers;
		} catch (IOException e) {
			e.printStackTrace();
		//	return this.doGetString(url);
		}
		
		return null;
	}
	
	public String doPostString(String url, Map<String,String> parm) {
		String body = null;
		
		FormBody.Builder formBodyBuilder = new FormBody.Builder();

		Set<Entry<String, String>> entrySet = parm.entrySet();
		
		for (Entry<String, String> ent : entrySet) {
			formBodyBuilder.add(ent.getKey(), ent.getValue());
		}
		
		FormBody formBody = formBodyBuilder.build();
		
		Request.Builder requestBuilder = new Request.Builder();
		Request request = requestBuilder.url(url).post(formBody).build();
		
		try(Response response = client.newCall(request).execute();){
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return body;
	}
	
	public String doPostString(String url, FormBody.Builder formBodyBuilder) {
		String body = null;
		
		FormBody formBody = formBodyBuilder.build();
		
		Request.Builder requestBuilder = new Request.Builder();
		
		Request request = requestBuilder.url(url).post(formBody).build();
		try(Response response = client.newCall(request).execute();){
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return body;
	}
	
	public String doPostString(String url, FormBody.Builder formBodyBuilder, Request.Builder requestBuilder) {
		String body = null;
		
		FormBody formBody = formBodyBuilder.build();
		
		Request request = requestBuilder.url(url).post(formBody).build();
		try(Response response = client.newCall(request).execute();){
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return body;
	}
	
	public ResponseBody upload(String url, String filePath, String fileName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body();
    }
	
	public ResponseBody uploadPart(String url, RequestBody requestBody) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body();
    }
	
	public byte[] doGetByteArray(String url) {
		byte[] body = null;
		
		Request.Builder requestBuilder = new Request.Builder();
		
		Request request = requestBuilder.url(url).build();
		try (Response response = client.newCall(request).execute();){
			body = response.body().bytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return body;
	}
	
	public byte[] doPostByteArray(String url, Map<String,String> parm) {
		byte[] body = null;
		
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		Set<Entry<String, String>> entrySet = parm.entrySet();
		for (Entry<String, String> ent : entrySet) {
			formBodyBuilder.add(ent.getKey(), ent.getValue());
		}
		FormBody formBody = formBodyBuilder.build();
		
		Request.Builder requestBuilder = new Request.Builder();
		Request request = requestBuilder.url(url).post(formBody).build();
		
		try(Response response = client.newCall(request).execute();){
			
			body = response.body().bytes();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return body;
	}
	
}
