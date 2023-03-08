package pl.ibcgames.smvotifier.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import pl.ibcgames.smvotifier.SMVotifier;
import pl.ibcgames.smvotifier.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class RequestUtils {
    public static JsonObject sendRequest(String url) {
        try {
            URL _url = new URL(url);
            Config config = SMVotifier.getInstance().config;
            Proxy proxy = config.proxy_address.isEmpty()
                    ? null
                    : new Proxy(
                            Proxy.Type.HTTP,
                            new InetSocketAddress(config.proxy_address, config.proxy_port));
            HttpURLConnection con = (HttpURLConnection) ((proxy == null)
                    ? _url.openConnection()
                    : _url.openConnection(proxy));
            con.setRequestMethod("GET");

            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(5 * 1000);

            int status = con.getResponseCode();
            Reader streamReader;

            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }

            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JsonParser parser = new JsonParser();
            return (JsonObject) parser.parse(content.toString());
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }
}
