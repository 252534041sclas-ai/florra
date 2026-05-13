package com.example.florra_a.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    // Centralized category list
    public static final List<String> CATEGORIES = Arrays.asList(
        "Floor", 
        "Wall", 
        "Kitchen", 
        "Bathroom", 
        "Living", 
        "Bedroom", 
        "Outdoor",
        "Parking", 
        "Steps", 
        "Roof"
    );

    public static String[] getCategoriesForSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Select");
        list.addAll(CATEGORIES);
        return list.toArray(new String[0]);
    }
}
