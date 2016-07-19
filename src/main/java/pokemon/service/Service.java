package pokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import pokemon.dto.InitialLoginResponse;
import pokemon.protobuf.PokemonProtos;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static pokemon.protobuf.PokemonProtos.RequestEnvelop;
import static pokemon.protobuf.PokemonProtos.ResponseEnvelop;

@Component
public class Service {

    private static final String API_URL = "https://pgorelease.nianticlabs.com/plfe/rpc";
    private static final String LOGIN_URL = "https://sso.pokemon.com/sso/login?service=https%3A%2F%2Fsso.pokemon.com%2Fsso%2Foauth2.0%2FcallbackAuthorize";
    private static final String LOGIN_OAUTH = "https://sso.pokemon.com/sso/oauth2.0/accessToken";
    private static final String PTC_CLIENT_SECRET = "w8ScCUXJQc6kXKw8FiOhd8Fixzht18Dq3PEVkUCP5ZPxtgyWsbTvWHFLm2wNY0JR";

    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private String accessToken;
    private String apiEndpoint;

    public Service(){
        objectMapper = new ObjectMapper();
    }

    public void login(HttpServletRequest req){
        httpClient = HttpClients.createDefault();
        try {
            InitialLoginResponse ilr = sendPreflight();
            String ticket = sendLoginRequest(ilr.getLt(), ilr.getExecution());
            accessToken = sendOathRequest(ticket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProfile(){
        RequestEnvelop.Builder envelopBuilder = RequestEnvelop.newBuilder()
                .setRpcId(1469378659230941192L)
                .setUnknown1(2)
                .setLatitude(1L)
                .setLongitude(1L)
                .setAltitude(1L)
                .setUnknown12(989)
                .setAuth(RequestEnvelop.AuthInfo.newBuilder()
                        .setProvider("ptc")
                        .setToken(RequestEnvelop.AuthInfo.JWT.newBuilder()
                                .setContents(accessToken)
                                .setUnknown13(14).build()).build());

        envelopBuilder.addAllRequests(Lists.newArrayList(
                RequestEnvelop.Requests.newBuilder().setType(2).build(),
                RequestEnvelop.Requests.newBuilder().setType(126).build(),
                RequestEnvelop.Requests.newBuilder().setType(4).build(),
                RequestEnvelop.Requests.newBuilder().setType(129).build(),
                RequestEnvelop.Requests.newBuilder().setType(5).build()));

        HttpPost apiPost = new HttpPost(apiEndpoint != null ? apiEndpoint : API_URL);
        try {
            apiPost.setHeader("Content-Type", "binary/octet-stream");
            apiPost.setEntity(new ByteArrayEntity(envelopBuilder.build().toByteArray()));
            HttpResponse response = httpClient.execute(apiPost);
            ResponseEnvelop responseEnvelop = ResponseEnvelop.parseFrom(response.getEntity().getContent());
            if(apiEndpoint == null){
                apiEndpoint = "https://" + responseEnvelop.getApiUrl() + "/rpc";
                getProfile();
            } else {
                ResponseEnvelop.ProfilePayload profilePayload = ResponseEnvelop.ProfilePayload.parseFrom(responseEnvelop.getPayload(0));
                System.out.println(profilePayload.getProfile().getUsername());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InitialLoginResponse sendPreflight() throws IOException {
        HttpGet get = new HttpGet(LOGIN_URL);
        get.addHeader("User-Agent", "Niantic App");
        return objectMapper
                .readValue(EntityUtils.toString(httpClient.execute(get).getEntity()), InitialLoginResponse.class);
    }

    private String sendLoginRequest(String lt, String execution) throws IOException {
        HttpPost post = new HttpPost(LOGIN_URL);
        post.addHeader("User-Agent", "Niantic App");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("lt", lt));
        params.add(new BasicNameValuePair("execution", execution));
        params.add(new BasicNameValuePair("_eventId", "submit"));
        params.add(new BasicNameValuePair("username", "USERNAME"));
        params.add(new BasicNameValuePair("password", "PASSWORD"));
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse loginResponse = httpClient.execute(post);
        String location = loginResponse.getFirstHeader("Location").getValue();
        return location.substring(location.indexOf("ticket=") + 7);
    }

    private String sendOathRequest(String ticket) throws IOException {
        HttpPost oathPost = new HttpPost(LOGIN_OAUTH);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", "mobile-app_pokemon-go"));
        params.add(new BasicNameValuePair("redirect_uri", "https://www.nianticlabs.com/pokemongo/error"));
        params.add(new BasicNameValuePair("client_secret", PTC_CLIENT_SECRET));
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("code", ticket));
        oathPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse oathResponse = httpClient.execute(oathPost);
        String oathResponseString = EntityUtils.toString(oathResponse.getEntity());
        return oathResponseString.subSequence(
                oathResponseString.indexOf('=') + 1, oathResponseString.indexOf('&')).toString();
    }
}
