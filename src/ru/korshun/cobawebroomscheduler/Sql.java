package ru.korshun.cobawebroomscheduler;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class Sql {

    private Connection connection = null;
    private static volatile Sql instance;
    private boolean isConnected = false;
    private Throwable exception;

    private Sql() {
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                NoSuchMethodException | InvocationTargetException e) {
            setConnected(false);
        }
    }

    public static Sql getInstance() {
        Sql localInstance = instance;
        if (localInstance == null) {
            synchronized (Sql.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Sql();
                }
            }
        }
        return localInstance;
    }


    /*
    *   Функция соединения с БД
    */
    public void connectionToSql() {
        try {
            Connection c = DriverManager.getConnection(
                    Config.HOST_SUFFIX + Config.HOST + Config.DATABASE_NAME,
                    Config.USERNAME,
                    Config.PASSWORD
            );
            setConnected(true);
            setConnection(c);
        } catch (SQLException e) {
            setConnected(false);
            setException(e);
        }
    }

    /*
    *   Функция отсоединения с БД
    */
    public void disconnectionFromSql(Connection c) {
        if (c != null) {
            try {
                c.close();
                setConnected(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
    *   Получаем последний добавленный ID
    */
    public int getLastId(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }


    /*
    *   Функция подготовки переменных для записи в БД
    */
    public void checkNullValue(int num, Object str, PreparedStatement ps, boolean crypt) throws SQLException {
        if(str == null || str.equals("-1") || str.toString().isEmpty()) {
            ps.setString(num, null);
        }
        else {
            ps.setString(num, crypt ? Functions.encodeStr(str.toString()) : str.toString());
        }
    }




    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean isConnect) {
        this.isConnected = isConnect;
    }

    public Throwable getConnectException() {
        return exception;
    }

    private void setException(Throwable exception) {
        this.exception = exception;
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }


}
