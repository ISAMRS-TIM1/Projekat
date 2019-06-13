package isamrs.tim1.common;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

public class RequestSender {
	private static final GenericUrl url = new GenericUrl("http://nominatim.openstreetmap.org/reverse?format=json");
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static String getCountry(double latitude, double longitude) throws IOException {
		HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {

			@Override
			public void initialize(HttpRequest request) throws IOException {
				request.setParser(new JsonObjectParser(JSON_FACTORY));
			}
		});
		url.set("format", "json");
		url.set("lat", latitude);
		url.set("lon", longitude);
		url.set("zoom", "18");
		url.set("addressdetails", "1");
		url.set("accept-language", "en-gb");
		HttpRequest request = requestFactory.buildGetRequest(url);
		try {
			GeoResponse response = request.execute().parseAs(GeoResponse.class);
			return response.getAddress().getCountry();
		} catch (Exception e) {
			return "UndefinedCountry";
		}

	}
}
