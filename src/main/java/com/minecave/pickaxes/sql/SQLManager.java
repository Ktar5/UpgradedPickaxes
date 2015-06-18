package com.minecave.pickaxes.sql;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Timothy Andis
 */
public class SQLManager {

    private Connection con;
    private int query_count = 0;
    private String host, db, user, pass;
    private int port;
    private String url;

    public SQLManager(String host, String db, String user, String pass, int port) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, pass);
            PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + db);
            statement.execute();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        this.host = host;
        this.db = db;
        this.user = user;
        this.pass = pass;
        this.port = port;
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db;
        getConnection();
        QueryThread.addQuery("CREATE TABLE IF NOT EXISTS `player_info`" +
                "(" +
                "`player` varchar(64) PRIMARY KEY NOT NULL, " +
                "`picks` BLOB," +
                "`swords` BLOB" +
                ")");
        new QueryThread();
//        new QueryThread();
    }

    public Connection getConnection() {
        try {
            if (query_count >= 1000) {
                if (con != null) {
                    con.close();
                }
                con = DriverManager.getConnection(url, user, pass);
                query_count = 0;
            }
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url, user, pass);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        query_count++;
        return con;
    }

    public ResultSet getResultSet(String query) {
        PreparedStatement pst;
        try {
            pst = getConnection().prepareStatement(query);
            return pst.executeQuery();
        } catch (Exception err) {
            if (err instanceof NullPointerException) {
                return null;
            }
            err.printStackTrace();
            return null;
        }
    }

    public void init(Player player) {
        new PlayerInfo(player);
        player.getInventory().forEach(i -> this.tryGetPItem(player, i));
        new BukkitRunnable() {
            @Override
            public void run() {
                String query = "SELECT * FROM `player_info` WHERE `player`='" + player.getUniqueId().toString() + "'";
                ResultSet res = getResultSet(query);
                try {
                    if (res == null || !res.isBeforeFirst()) {
                        return;
                    }
                    if (res.next()) {
                        Inventory swords = Utils.deserializeInventory(res.getBytes("swords"));
                        Inventory picks = Utils.deserializeInventory(res.getBytes("picks"));
                        List<Sword> swordList = new ArrayList<>();
                        List<Pickaxe> pickList = new ArrayList<>();
                        int i = 0;
                        for (ItemStack stack : swords.getContents()) {
                            if (stack == null) {
                                continue;
                            }
                            Sword sword = Utils.deserializeSword(stack);
                            swordList.set(i++, sword);
                        }
                        i = 0;
                        for (ItemStack stack : picks.getContents()) {
                            if (stack == null) {
                                continue;
                            }
                            Pickaxe pick = Utils.deserializePick(stack);
                            pickList.set(i++, pick);
                        }
                        PlayerInfo info = new PlayerInfo(player);
                        pickList.forEach(info::addPickaxe);
                        swordList.forEach(info::addSword);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(PickaxesRevamped.getInstance());
    }

    public void tryGetPItem(Player player, ItemStack i) {
        Pickaxe p = Pickaxe.tryFromItem(i);
        if (p == null) {
            Sword s;
            if ((s = Sword.tryFromItem(i)) != null) {
                s.update(player);
            }
        } else {
            p.update(player);
        }
    }

    public void logoff(PlayerInfo info) {
        List<Sword> swords = info.getSwords();
        List<Pickaxe> pickaxes = info.getPickaxes();
        UUID uuid = info.getPlayer().getUniqueId();
        byte[] swordData = Utils.serialSwords(swords);
        byte[] pickData = Utils.serialPicks(pickaxes);
        String query = "INSERT INTO `player_info` VALUES ('" + uuid.toString() + "', '" + swordData + "', '" + pickData + "') " +
                "ON DUPLICATE KEY UPDATE `swords` = ?, `picks` = ?";
        try {
            PreparedStatement pst = PickaxesRevamped.getInstance().getSqlManager().getConnection().prepareStatement(query);
            if (swordData != null || swords.isEmpty()) {
                pst.setBytes(1, swordData);
            } else {
                pst.setNull(1, Types.BLOB);
            }
            if (pickData != null || pickaxes.isEmpty()) {
                pst.setBytes(2, pickData);
            } else {
                pst.setNull(2, Types.BLOB);
            }
            QueryThread.addQuery(pst);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerInfo.getInfoMap().remove(info.getPlayer().getUniqueId());
    }

}
