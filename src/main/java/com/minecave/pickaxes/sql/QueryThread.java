package com.minecave.pickaxes.sql;

import com.minecave.pickaxes.PickaxesRevamped;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Timothy Andis
 */
public class QueryThread {

    public static Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    public static BukkitTask t;

    public QueryThread() {
//        start();
//        setName("PickaxeRevamped-SQL");
        t = PickaxesRevamped.getInstance().getServer().getScheduler()
                .runTaskTimerAsynchronously(PickaxesRevamped.getInstance(), () -> {
                    if (queue.peek() != null) {
                        queue.poll().run();
                    }
                }, 0L, 20L);
    }

//    @SuppressWarnings("InfiniteLoopStatement")
//    @Override
//    public void run() {
//        while (true) {
//            try {
//                Thread.sleep(250);
//            } catch (InterruptedException ignored) {
//            }
//            if (queue.peek() != null) {
//                queue.poll().run();
//            }
//        }
//    }


    public static void addQuery(final PreparedStatement pst) {
        queue.add(() -> {
            Connection con = null;
            try {
                con = PickaxesRevamped.getInstance().getSqlManager().getConnection();
                pst.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void addQuery(final String query) {
        queue.add(() -> {
            Connection con = null;
            PreparedStatement pst = null;
            try {
                con = PickaxesRevamped.getInstance().getSqlManager().getConnection();
                pst = PickaxesRevamped.getInstance().getSqlManager().getConnection().prepareStatement(query);
                pst.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
