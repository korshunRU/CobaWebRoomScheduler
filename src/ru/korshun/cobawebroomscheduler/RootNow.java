package ru.korshun.cobawebroomscheduler;


import java.sql.*;
import java.text.ParseException;

public class RootNow  {


    public static void main(String[] args) {

        System.out.println("NOW!");

        RootNow.updateDB();

    }




    /**
     *      Получаем данные из БД access и обновляем БД mysql
     */
    protected static void updateDB() {
//        System.out.println("updateDB");
        Connection connAccess = null, connMysql = null;

        try {

            // Соединяемся с Access
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connAccess = DriverManager.getConnection("jdbc:ucanaccess://" + Config.PATH + Config.A_DATABASE_NAME);





            // ========================================================================================================================
            // Выбираем клиентов
            String queryClient = "SELECT ID, Cl_Type, Cl_Name, Cl_Address, Cl_Phone FROM cabclient";
            PreparedStatement stmtAccess_Client = connAccess.prepareStatement(queryClient);
            ResultSet rsAccessClient = stmtAccess_Client.executeQuery();

            while (rsAccessClient.next()) {

                // Соединяемся с MySQL
                Sql.getInstance().connectionToSql();
                connMysql = Sql.getInstance().getConnection();
                connMysql.setAutoCommit(false);

                PreparedStatement stmtMysql;

                // Обновляем информацию по клиентам
                String updateClient =   "UPDATE " + Config.DB_TABLE_PREFIX + "users " +
                        "SET name = ?," +
                        "address = ?," +
                        "id = LAST_INSERT_ID(id)," +
                        "type = ?," +
                        "phone = ? " +
                        "WHERE id_coba = ?";

                stmtMysql = Sql.getInstance().getConnection().prepareStatement(updateClient);

                stmtMysql.setString(1,  Functions.encodeStr(rsAccessClient.getString("Cl_Name")));
                Sql.getInstance().checkNullValue(2, rsAccessClient.getString("Cl_Address"), stmtMysql, true);
                stmtMysql.setInt(3,     rsAccessClient.getInt("Cl_Type"));
                Sql.getInstance().checkNullValue(4, rsAccessClient.getString("Cl_Phone"), stmtMysql, true);
                stmtMysql.setInt(5,     rsAccessClient.getInt("ID"));

                stmtMysql.executeUpdate();

                System.out.println(Sql.getInstance().getLastId(stmtMysql));




                // ========================================================================================================================
                // Выбираем объекты клиента ТОЛЬКО если делали с ним какие-то изменения
                // сделано для отсеивания клиентов, которых нет в ЛК
                if(Sql.getInstance().getLastId(stmtMysql) > 0) {

                    String queryObjects = "SELECT Ob_ID, Ob_Num, Ob_Type, Ob_Address, Ob_Phone, Ob_State, Ob_DateCont, " +
                            "Ob_Pay, Ob_TimeF, Ob_TimeT, Ob_SMS " +
                            "FROM cabobject " +
                            "WHERE Ob_IDCl = ? AND Ob_Agency = ?";
                    PreparedStatement stmtAccessObject = connAccess.prepareStatement(queryObjects);

                    stmtAccessObject.setInt(1, rsAccessClient.getInt("ID"));
                    stmtAccessObject.setString(2, "Висоник");

                    ResultSet rsAccessObject = stmtAccessObject.executeQuery();

                    while (rsAccessObject.next()) {


                        // Разбиваем АП
                        String ob_pay = rsAccessObject.getString("Ob_Pay") == null ? "0" : rsAccessObject.getString("Ob_Pay");

                        String ap[] = ob_pay.split("~");
                        String apStr = ap.length > 1 ?
                                "Охранная сигнализация - " + ap[0] + ", пожарная сигнализация - " + ap[1] :
                                "Охранная сигнализация - " + ap[0];

                        // Разбиваем смски
                        String smsStr = "";
                        if (rsAccessObject.getString("Ob_SMS") != null) {
                            String sms[] = rsAccessObject.getString("Ob_SMS").split(",");
                            for (String item : sms) {
                                String itemL = item.substring(0, item.indexOf("@"));
                                smsStr += Functions.isNumber(itemL) ? itemL + "," : item + ",";
                            }
                        }

                        // Формируем режимность
                        String sch = rsAccessObject.getString("Ob_TimeF") != null ?
                                "C " + rsAccessObject.getString("Ob_TimeF") + " до " + rsAccessObject.getString("Ob_TimeT") :
                                "";

                        // Обновляем информацию по объектам: если объекта с таким номером нет - вставляем
                        String updateObject = "INSERT INTO " + Config.DB_TABLE_PREFIX + "objects " +
                                "SET id_client = (SELECT id FROM " + Config.DB_TABLE_PREFIX + "users WHERE id_coba = ?)," +
                                "number = ?," +
                                "date_start = ?," +
                                "name = ?," +
                                "address = ?," +
                                "status = ?," +
                                "balance = ?," +
                                "phone = ?," +
                                "sms_alarm = ?," +
                                "schedule = ?," +
                                "chk = 1 " +
                                "ON DUPLICATE KEY UPDATE " +
                                "date_start = ?," +
                                "name = ?," +
                                "address = ?," +
                                "status = ?," +
                                "balance = ?," +
                                "phone = ?," +
                                "sms_alarm = ?," +
                                "schedule = ?," +
                                "chk = 1," +
                                "id=LAST_INSERT_ID(id)";

                        stmtMysql = Sql.getInstance().getConnection().prepareStatement(updateObject);

                        stmtMysql.setInt(1, rsAccessClient.getInt("ID"));
                        stmtMysql.setInt(2, rsAccessObject.getInt("Ob_Num"));
                        stmtMysql.setString(3, Functions.parseStringToSqlDateFormat(rsAccessObject.getString("Ob_DateCont"), "yyyy-MM-dd"));
                        stmtMysql.setString(4, Functions.encodeStr(rsAccessObject.getString("Ob_Type")));
                        stmtMysql.setString(5, Functions.encodeStr(rsAccessObject.getString("Ob_Address")));
                        stmtMysql.setInt(6, rsAccessObject.getString("Ob_State").equals("Охрана") ? 1 : 2);
                        stmtMysql.setString(7, Functions.encodeStr(apStr));
                        Sql.getInstance().checkNullValue(8, rsAccessObject.getString("Ob_Phone").trim(), stmtMysql, true);
                        Sql.getInstance().checkNullValue(9, smsStr, stmtMysql, true);
                        Sql.getInstance().checkNullValue(10, sch, stmtMysql, true);

                        stmtMysql.setString(11, Functions.parseStringToSqlDateFormat(rsAccessObject.getString("Ob_DateCont"), "yyyy-MM-dd"));
                        stmtMysql.setString(12, Functions.encodeStr(rsAccessObject.getString("Ob_Type")));
                        stmtMysql.setString(13, Functions.encodeStr(rsAccessObject.getString("Ob_Address")));
                        stmtMysql.setInt(14, rsAccessObject.getString("Ob_State").equals("Охрана") ? 1 : 2);
                        stmtMysql.setString(15, Functions.encodeStr(apStr));
                        Sql.getInstance().checkNullValue(16, rsAccessObject.getString("Ob_Phone").trim(), stmtMysql, true);
                        Sql.getInstance().checkNullValue(17, smsStr, stmtMysql, true);
                        Sql.getInstance().checkNullValue(18, sch, stmtMysql, true);

                        stmtMysql.executeUpdate();


                        // Последний заюзанный ID
                        int lastObjectId = Sql.getInstance().getLastId(stmtMysql);


//                    System.out.println(Sql.getInstance().getLastId(stmtMysql) + " - " + rsAccessObject.getInt("Ob_Num"));


                        // ========================================================================================================================
                        // Выбираем ответственных на объект
                        String queryAgents = "SELECT ID, idOb, P_Num, P_Name, P_Phone " +
                                "FROM cabphone " +
                                "WHERE P_Num = ? AND idOb = ?";
                        PreparedStatement stmtAccessAgents = connAccess.prepareStatement(queryAgents);

                        stmtAccessAgents.setInt(1, rsAccessObject.getInt("Ob_Num"));
                        stmtAccessAgents.setInt(2, rsAccessObject.getInt("Ob_ID"));

                        ResultSet rsAccessAgents = stmtAccessAgents.executeQuery();

                        while (rsAccessAgents.next()) {

                            // Обновляем ответственных
                            String updateAgent = "INSERT INTO " + Config.DB_TABLE_PREFIX + "agents " +
                                    "SET id_coba = ?," +
                                    "id_object = ?," +
                                    "name = ?," +
                                    "phone = ?," +
                                    "chk = 1 " +
                                    "ON DUPLICATE KEY UPDATE " +
                                    "name = ?," +
                                    "phone = ?," +
                                    "chk = 1 ";

                            stmtMysql = Sql.getInstance().getConnection().prepareStatement(updateAgent);

                            stmtMysql.setInt(1, rsAccessAgents.getInt("ID"));
                            stmtMysql.setInt(2, lastObjectId);
                            stmtMysql.setString(3, Functions.encodeStr(rsAccessAgents.getString("P_Name")));
                            stmtMysql.setString(4, Functions.encodeStr(rsAccessAgents.getString("P_Phone")));

                            stmtMysql.setString(5, Functions.encodeStr(rsAccessAgents.getString("P_Name")));
                            stmtMysql.setString(6, Functions.encodeStr(rsAccessAgents.getString("P_Phone")));

                            stmtMysql.executeUpdate();


                        }


                        // ========================================================================================================================
                        // Выбираем сигналы объекта
                        String queryEvents = "SELECT  format([S_Date], \"yyyy-mm-dd\") as S_Date, " +
                                "format([S_Time], \"HH:nn:ss\") as S_Time, " +
                                "S_Descr " +
                                "FROM cabsig " +
                                "WHERE S_Num = ? AND S_Date=(Date()-1)";
                        PreparedStatement stmtAccessEvents = connAccess.prepareStatement(queryEvents);

                        int number = rsAccessObject.getInt("Ob_Num");

                        stmtAccessEvents.setInt(1, number);

                        ResultSet rsAccessEvents = stmtAccessEvents.executeQuery();

                        while (rsAccessEvents.next()) {

                            String event = Functions.parseStringToSqlDateFormat(
                                    rsAccessEvents.getString("S_Date") + " " +
                                            rsAccessEvents.getString("S_Time"), "yyyy-MM-dd HH:mm:ss");

                            System.out.println(number + ": " + event);
                            // Добавляем сигналы
                            String updateEvents = "INSERT INTO " + Config.DB_TABLE_PREFIX + "events " +
                                    "SET id_object = ?," +
                                    "time = ?," +
                                    "event = ?";

                            stmtMysql = Sql.getInstance().getConnection().prepareStatement(updateEvents);

                            stmtMysql.setInt(1, lastObjectId);
                            stmtMysql.setString(2, event);
                            stmtMysql.setString(3, rsAccessEvents.getString("S_Descr"));

                            stmtMysql.executeUpdate();

                        }


                    }


                }








//                System.out.println("==============");

                connMysql.commit();
                Sql.getInstance().disconnectionFromSql(connMysql);
            }






            // Соединяемся с MySQL для удаления обьектов и ответственных
            Sql.getInstance().connectionToSql();
            connMysql = Sql.getInstance().getConnection();
            connMysql.setAutoCommit(false);

            PreparedStatement stmtMysql;




            // ===============================================================================================================
            // Удаляем те объекты, которые остались с нулем в поле chk - таких объектов быть не должно
            String deleteObjects =  "DELETE FROM " + Config.DB_TABLE_PREFIX + "objects " +
                    "WHERE chk = 0";
            stmtMysql = Sql.getInstance().getConnection().prepareStatement(deleteObjects);
            stmtMysql.executeUpdate();

            // У оставшихся сбрасываем chk на 0
            String resetObjects =   "UPDATE " + Config.DB_TABLE_PREFIX + "objects " +
                    "SET chk = 0 " +
                    "WHERE chk = 1";
            stmtMysql = Sql.getInstance().getConnection().prepareStatement(resetObjects);
            stmtMysql.executeUpdate();





            // ===============================================================================================================
            // Удаляем те объекты, которые остались с нулем в поле chk - таких объектов быть не должно
            String deleteAgents =   "DELETE FROM " + Config.DB_TABLE_PREFIX + "agents " +
                    "WHERE chk = 0";
            stmtMysql = Sql.getInstance().getConnection().prepareStatement(deleteAgents);
            stmtMysql.executeUpdate();

            // У оставшихся сбрасываем chk на 0
            String resetAgents =    "UPDATE " + Config.DB_TABLE_PREFIX + "agents " +
                    "SET chk = 0 " +
                    "WHERE chk = 1";
            stmtMysql = Sql.getInstance().getConnection().prepareStatement(resetAgents);
            stmtMysql.executeUpdate();





            // ===============================================================================================================
            // Удаляем сигналы, которые "старше" 10ти дней
            String deleteEvents =   "DELETE FROM " + Config.DB_TABLE_PREFIX + "events " +
                    "WHERE  DATEDIFF(NOW(), time) > 10";
            stmtMysql = Sql.getInstance().getConnection().prepareStatement(deleteEvents);
            stmtMysql.executeUpdate();






            connMysql.commit();
            Sql.getInstance().disconnectionFromSql(connMysql);

//            System.out.println("Complite!");

            Root.sendMail(true);

        } catch (ClassNotFoundException | SQLException | ParseException e) {
            try {
                if(connMysql != null) {
                    connMysql.rollback();
                    connMysql.setAutoCommit(true);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if(connAccess != null) {
                try {
                    connAccess.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}