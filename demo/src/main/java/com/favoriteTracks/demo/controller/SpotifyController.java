package com.favoriteTracks.demo.controller;

import com.favoriteTracks.demo.service.SpotifyService;
import com.favoriteTracks.demo.service.TrackOrganizerService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@Controller
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final TrackOrganizerService trackOrganizerService;

    @Autowired
    public SpotifyController(SpotifyService spotifyService, TrackOrganizerService trackOrganizerService) {
        this.spotifyService = spotifyService;
        this.trackOrganizerService = trackOrganizerService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:" + spotifyService.getAuthorizationUri();
    }

    @GetMapping("/callback")
    public String handleSpotifyCallback(@RequestParam String code, Model model) throws IOException, ParseException, SpotifyWebApiException {
        System.out.println("code: " + code);

        try {
            // Use the SpotifyService to handle the authorization code exchange
            spotifyService.exchangeCodeForAccessToken(code);

            // Fetch the top tracks from the user's Spotify account
            List<String> topTracks = spotifyService.getUserTopTracks();

            // Initialize the track organizer with the user's top tracks
            trackOrganizerService.initializeTracks(topTracks);

            // Add the categories (with tracks) to the model to be displayed in the view
            model.addAttribute("categories", trackOrganizerService.getCategories());

            // Return the view name to be rendered (favorite-tracks.html)
            return "favorite-tracks";
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            model.addAttribute("error", "Network error: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/addCategory")
    public String addCategory(@RequestParam String categoryName, Model model) {
        // Add a new category
        trackOrganizerService.addCategory(categoryName);

        // Update the model with the latest categories and tracks
        model.addAttribute("categories", trackOrganizerService.getCategories());

        // Redirect back to the favorite-tracks page
        return "favorite-tracks";
    }
}
