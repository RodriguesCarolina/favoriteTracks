package com.favoriteTracks.demo.service;

import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    private static final String clientId = "41499bb508a7407da7755318e82546e9";
    private static final String clientSecret = "9124cdf9c84b4e56b0df6c4d0b1cc1a1";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/callback");

    private final SpotifyApi spotifyApi;

    public SpotifyService() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    public String getAuthorizationUri() {
        return spotifyApi.authorizationCodeUri()
                .scope("user-top-read")
                .build()
                .execute()
                .toString();
    }

    public void exchangeCodeForAccessToken(String code) throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeCredentials credentials = spotifyApi.authorizationCode(code).build().execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        spotifyApi.setRefreshToken(credentials.getRefreshToken());

        System.out.println("Access Token: " + credentials.getAccessToken());
        System.out.println("Refresh Token: " + credentials.getRefreshToken());
    }

    public List<String> getUserTopTracks() throws IOException, SpotifyWebApiException, ParseException {
        GetUsersTopTracksRequest request = spotifyApi.getUsersTopTracks()
                .limit(10)  // Fetch top 10 tracks
                .build();

        Paging<Track> trackPaging = request.execute();
        Track[] tracks = trackPaging.getItems();

        List<String> topTracks = new ArrayList<>();
        for (Track track : tracks) {
            topTracks.add(track.getName());
        }

        System.out.println("Fetched top tracks: " + topTracks);
        return topTracks;
    }
}
