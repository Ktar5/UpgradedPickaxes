package com.minecave.pickaxes.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Tim [calebbfmv]
 */
public class Hologram {

    private List<ArmorStand> stands = new ArrayList<>();
    private Location lastLocation = null;

    public Hologram(Location location, String... lines) {
        Collections.reverse(Arrays.asList(lines));
        for (String line : lines) {
            createStand(location, line);
        }
    }

    public void destroy() {
        stands.forEach(ArmorStand::remove);
    }

    private void createStand(Location location, String line) {
        Location loc;

        if (lastLocation == null) {
            loc = location.clone();
        } else {
            loc = lastLocation.add(0, 0.30, 0);
        }

        ArmorStand stand = location.getWorld().spawn(loc, ArmorStand.class);
        stand.setVisible(false);
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setCustomName(line);
        stand.setCustomNameVisible(true);

        stands.add(stand);

        lastLocation = loc;
    }

}
