package com.favoriteTracks.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrackOrganizerService {

    private List<String> tracks;
    private final Map<String, List<String>> categories;

    public TrackOrganizerService(List<String> tracks) {
        this.categories = new HashMap<>();
    }

    public void initializeTracks(List<String> tracks) {
        this.tracks = tracks;
    }

    public void addCategory(String category) {
        categories.putIfAbsent(category, new ArrayList<>());
    }

    public List<Category> getCategories() {
        List<Category> categoryList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : this.categories.entrySet()) {
            categoryList.add(new Category(entry.getKey(), entry.getValue()));
        }

        return categoryList;
    }

    public class Category {
        private final String name;
        private final List<String> tracks;

        public Category(String name, List<String> tracks) {
            this.name = name;
            this.tracks = tracks;
        }

        public String getName() {
            return name;
        }

        public List<String> getTracks() {
            return tracks;
        }
    }

}
