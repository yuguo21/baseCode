package com.ityks.main.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarWrapper implements CookieJar{

	private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
	
	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		List<Cookie> cookieTmp = cookieStore.get(url.host());
		List<Cookie> coptTmp = null;
		if (cookieTmp != null) {
			coptTmp = new ArrayList<>(cookieTmp);
		}
		List<Cookie> adds = new ArrayList<>();
		List<Cookie> upds = new ArrayList<>();
		if (cookieTmp == null || cookieTmp.size()==0) {
			cookieStore.put(url.host(), cookies);
		}else {
			for (Cookie cSrc : cookies) {
				String name = cSrc.name();
				boolean flag = true;
				for (int i=0; i<cookieTmp.size(); i++) {
					Cookie tSrc = cookieTmp.get(i);
					String name2 = tSrc.name();
					if (name.equals(name2)) {
						coptTmp.remove(i);
						coptTmp.add(cSrc);
						flag = false;
						continue;
					}
				}
				if (flag) {
					coptTmp.add(cSrc);
				}
			}
			cookieStore.put(url.host(), coptTmp);
		}
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		List<Cookie> cookies = cookieStore.get(url.host());
		return cookies != null ? cookies : new ArrayList<Cookie>();
	}

}
