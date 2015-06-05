package com.minecave.pickaxes.sql;

import com.tadahtech.pub.PickaxesRevamped;
import com.tadahtech.pub.pitem.Pickaxe;
import com.tadahtech.pub.pitem.Sword;
import com.tadahtech.pub.utils.DataItem;
import com.tadahtech.pub.utils.Utils;
import org.bukkit.entity.Player;
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
            PreparedStatement statement = connection.prepareStatement("CREATE DATAVASE IF NOT EXISTS " + db);
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
        new QueryThread();
        QueryThread.addQuery("CREATE TABLE IF NOT EXISTS `player_info`" +
          "(" +
          "`player` varchar(64) PRIMARY KEY NOT NULL, " +
          "`picks` longtext," +
          "`swords` longtext" +
          ")");
        new QueryThread();
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
            pst.execute();
            return pst.getResultSet();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public void init(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String query = "SELECT * FROM `player_info` WHERE `player` ='" + player.getUniqueId().toString();
                ResultSet res = getResultSet(query);
                try {
                    if(res.next()) {
                        DataItem[] swords = Utils.deserializeInventory(res.getString("swords"));
                        if(swords == null) {
                            swords = new DataItem[45];
                        }
                        DataItem[] picks = Utils.deserializeInventory(res.getString("picks"));
                        if(picks == null) {
                            picks = new DataItem[45];
                        }
                        List<Sword> swordList = new ArrayList<>();
                        List<Pickaxe> pickaxes = new ArrayList<>();
                        for(DataItem dataItem : swords) {
                            if(dataItem == null) {
                                continue;
                            }
                            Sword sword = (Sword) dataItem.getItemStack();
                            swordList.set(dataItem.getSlot(), sword);
                        }
                        for(DataItem dataItem : picks) {
                            if(dataItem == null) {
                                continue;
                            }
                            Pickaxe pickaxe = (Pickaxe) dataItem.getItemStack();
                            pickaxes.set(dataItem.getSlot(), pickaxe);
                        }
                        new PlayerInfo(pickaxes, swordList, player);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(PickaxesRevamped.getInstance());
    }

    public void logoff(PlayerInfo info) {
        List<Sword> swords = info.getSwords();
        List<Pickaxe> pickaxes = info.getPickaxes();
        UUID uuid = info.getPlayer().getUniqueId();
        String swordData = Utils.serialSwords(swords);
        String pickData = Utils.serialPicks(pickaxes);
        String query = "INSERT INTO `player_info` VALUES ('" + uuid.toString() + "', '" + swordData + "', '" + pickData + "') " +
          "ON DUPLICATE KEY UPDATE `swords` ='" + swordData + "', `picks` ='" + pickData + "'";
        QueryThread.addQuery(query);
    }

}
