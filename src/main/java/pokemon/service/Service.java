package pokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.catalina.connector.Request;
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
import java.util.Date;
import java.util.List;

import static pokemon.protobuf.PokemonProtos.UnknownAuth;
import static pokemon.protobuf.PokemonProtos.RequestEnvelop;
import static pokemon.protobuf.PokemonProtos.RequestEnvelop.Requests;
import static pokemon.protobuf.PokemonProtos.RequestEnvelop.AuthInfo;
import static pokemon.protobuf.PokemonProtos.RequestEnvelop.MessageSingleInt;
import static pokemon.protobuf.PokemonProtos.RequestEnvelop.MessageSingleString;
import static pokemon.protobuf.PokemonProtos.RequestEnvelop.MessageQuad;
import static pokemon.protobuf.PokemonProtos.ResponseEnvelop;
import static pokemon.protobuf.PokemonProtos.ResponseEnvelop.Profile;
import static pokemon.protobuf.PokemonProtos.ResponseEnvelop.ProfilePayload;
import static pokemon.protobuf.PokemonProtos.ResponseEnvelop.HeartbeatPayload;


@Component
public class Service {

    private static final String API_URL = "https://pgorelease.nianticlabs.com/plfe/rpc";
    private static final String LOGIN_URL = "https://sso.pokemon.com/sso/login?service=https%3A%2F%2Fsso.pokemon.com%2Fsso%2Foauth2.0%2FcallbackAuthorize";
    private static final String LOGIN_OAUTH = "https://sso.pokemon.com/sso/oauth2.0/accessToken";
    private static final String PTC_CLIENT_SECRET = "w8ScCUXJQc6kXKw8FiOhd8Fixzht18Dq3PEVkUCP5ZPxtgyWsbTvWHFLm2wNY0JR";
    private static final String SERVER_DOWN_MSG = "Servers potentially down, please try again later.";

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
            System.out.print("Sending Preflight Request: ");
            InitialLoginResponse ilr = sendPreflight();
            System.out.print("SUCCESS\n");
            System.out.print("Sending Login Request: ");
            String ticket = sendLoginRequest(ilr.getLt(), ilr.getExecution());
            System.out.print("SUCCESS\n");
            System.out.print("Sending Oath Request: ");
            accessToken = sendOathRequest(ticket);
            System.out.print("SUCCESS\n");
        } catch (IOException e) {
            System.out.println(SERVER_DOWN_MSG);
        }
    }

    public Profile getProfile(){
        System.out.println("Requesting Profile Information");
        ResponseEnvelop profileResponse = getProfileResponse(null, null);
        ProfilePayload profilePayload = null;
        Profile profile = null;
        try {
            profile= ProfilePayload.parseFrom(profileResponse.getPayload(0)).getProfile();
            System.out.println("Profile Information Retrieved");
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public HeartbeatPayload heartbeat(){
        httpClient = HttpClients.createDefault();
        return heartbeat(getProfileResponse(null, null));
    }

    private ResponseEnvelop getProfileResponse(UnknownAuth auth, List<Requests.Builder> requests) {
        RequestEnvelop.Builder envelopBuilder = RequestEnvelop.newBuilder();

        Requests.Builder req1 = Requests.newBuilder().setType(2);
        if(requests != null && requests.get(0) != null)
            mergeRequests(requests.get(0), req1);

        Requests.Builder req2 = Requests.newBuilder().setType(126);
        if(requests != null && requests.get(1) != null)
            mergeRequests(requests.get(1), req2);

        Requests.Builder req3 = Requests.newBuilder().setType(4);
        if(requests != null && requests.get(2) != null)
            mergeRequests(requests.get(2), req3);

        Requests.Builder req4 = Requests.newBuilder().setType(129);
        if(requests != null && requests.get(3) != null)
            mergeRequests(requests.get(3), req4);

        Requests.Builder req5 = Requests.newBuilder().setType(5);
        if(requests != null && requests.get(4) != null)
            mergeRequests(requests.get(4), req5);

        envelopBuilder.addAllRequests(Lists.newArrayList(
                req1.build(),
                req2.build(),
                req3.build(),
                req4.build(),
                req5.build()));

        ResponseEnvelop responseEnvelop;

        responseEnvelop = apiRequest(envelopBuilder, auth);
        if(responseEnvelop != null && apiEndpoint != null){
            responseEnvelop = apiRequest(envelopBuilder, auth);
            if(responseEnvelop != null){
                return responseEnvelop;
            }
        }

        return responseEnvelop;
    }

    private HeartbeatPayload heartbeat(ResponseEnvelop profileResponse){
        Requests.Builder m4 = Requests.newBuilder()
                .setMessage(ByteString.copyFromUtf8(MessageSingleInt.newBuilder()
                        .setF1((new Date()).getTime() * 1000).build().toString()));
        Requests.Builder m5 = Requests.newBuilder()
                .setMessage(ByteString.copyFromUtf8(MessageSingleString.newBuilder()
                        .setBytes(ByteString.copyFromUtf8("05daf51635c82611d1aac95c0b051d3ec088a930")).toString()));
        Requests.Builder m1 = Requests.newBuilder()
                .setType(106)
                .setMessage(
                        ByteString.copyFrom(MessageQuad.newBuilder()
                                .setLat(1L) //Need to put the correct value here
                                .setLong(1L) //Need to put the correct value here
                                .setF1(ByteString.EMPTY) //Need to put the correct value here
                                .setF2(ByteString.copyFromUtf8("\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"))
                            .build().toString().getBytes()));

        ResponseEnvelop response = getProfileResponse(profileResponse.getUnknown7(),
                Lists.newArrayList(m1,
                Requests.newBuilder(),
                m4,
                Requests.newBuilder(),
                m5));

        HeartbeatPayload heartbeatPayload = null;
        try {
            heartbeatPayload = HeartbeatPayload.parseFrom(response.getPayload(0));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return heartbeatPayload;
    }

    private ResponseEnvelop apiRequest(RequestEnvelop.Builder reqBuilder, UnknownAuth auth){
        RequestEnvelop.Builder envelopBuilder = reqBuilder != null ? reqBuilder : RequestEnvelop.newBuilder();
        reqBuilder.setRpcId(1469378659230941192L)
                .setUnknown1(2)
                .setLatitude(1L) //Need to put the correct value here
                .setLongitude(1L) //Need to put the correct value here
                .setAltitude(1L) //Need to put the correct value here
                .setUnknown12(989);
        if(auth == null){
           reqBuilder.setAuth(AuthInfo.newBuilder()
                    .setProvider("ptc")
                    .setToken(AuthInfo.JWT.newBuilder()
                            .setContents(accessToken)
                            .setUnknown13(14).build()).build());
        } else {
            reqBuilder.setUnknown11(auth);
        }


        HttpPost apiPost = new HttpPost(apiEndpoint != null ? apiEndpoint : API_URL);
        apiPost.setHeader("Content-Type", "binary/octet-stream");
        apiPost.setEntity(new ByteArrayEntity(envelopBuilder.build().toByteArray()));
        try {
            HttpResponse response = httpClient.execute(apiPost);
            ResponseEnvelop responseEnvelop = ResponseEnvelop.parseFrom(response.getEntity().getContent());
            if(apiEndpoint == null){
                System.out.println("API Endpoint Retrieved");
                apiEndpoint = "https://" + responseEnvelop.getApiUrl() + "/rpc";
            }
            return responseEnvelop;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            System.out.println(SERVER_DOWN_MSG);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(SERVER_DOWN_MSG);
        }

        return null;
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
        List<NameValuePair> params = new ArrayList<>();
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
        List<NameValuePair> params = new ArrayList<>();
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

    private Requests.Builder mergeRequests(Requests.Builder src, Requests.Builder dest){
        if(src.getType() == 0){
            src.setType(dest.getType());
        }
        dest.mergeFrom(src.build());
        return dest;
    }
}
