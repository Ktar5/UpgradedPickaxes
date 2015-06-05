package com.minecave.pickaxes.sql;

import com.tadahtech.pub.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Timothy Andis
 */
public class QueryThread extends Thread {

    public static Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public QueryThread() {
        start();
        setName("PickaxeRevamped-SQL");
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }
            if (queue.peek() != null) {
                queue.poll().run();
            }
        }
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
