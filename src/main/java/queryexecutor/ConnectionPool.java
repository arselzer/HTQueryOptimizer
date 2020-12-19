package queryexecutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {
    private List<Connection> connections = new LinkedList<>();
    private LinkedBlockingQueue<Connection> connectionQueue = new LinkedBlockingQueue<>();

    public ConnectionPool(String dbURL, Properties connectionProperties, int size) throws SQLException {
        for (int i = 0; i < size; i++) {
            Connection conn = DriverManager.getConnection(dbURL, connectionProperties);
            connections.add(conn);
            connectionQueue.add(conn);
        }
    }

    public ConnectionPool(String dbURL, Properties connectionProperties) throws SQLException {
        this(dbURL, connectionProperties, Runtime.getRuntime().availableProcessors());
    }

    public Connection getConnection() throws InterruptedException {
        return connectionQueue.take();
    }

    public void returnConnection(Connection conn) {
        if (connections.contains(conn)) {
            connectionQueue.offer(conn);
        }
    }

    public void close() throws SQLException {
        for (Connection conn : connections) {
            conn.close();
        }
    }
}
