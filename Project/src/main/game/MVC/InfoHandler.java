package main.game.MVC;
import main.game.stuff.Kind;
import main.game.stuff.Plane;

import java.io.*;
import java.sql.*;
import java.util.*;

public class InfoHandler {
    private static InfoHandler infoHandler;
    private String currentUser;
    private Map<String, Map<String,?>> users=new HashMap<>();
    private Connection conn;
    private Statement st;
    private static final String CREATE_ROW =" insert into users (user_name, add_power, time_played, wave, grade, boss_time, after_apex, number_power_ups, lvl, bombs, plane, kind)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_TABLE_IF_NOT_EXISTS =" CREATE TABLE IF NOT EXISTS users(" +
            " user_name VARCHAR(100) NOT NULL ," +
            " add_power DOUBLE ," +
            " time_played INT ," +
            " wave INT ," +
            " grade INT ," +
            " boss_time BOOL ," +
            " after_apex BOOL ," +
            " number_power_ups INT ," +
            " lvl INT ," +
            " bombs INT ," +
            " plane VARBINARY(8000) ," +
            " kind VARBINARY(8000) ," +
            " PRIMARY KEY (user_name)" +
            " )";
    private static final String UPDATE =" update users" +
            " set add_power=? ," +
            " time_played=? ," +
            " wave=? ," +
            " grade=? ," +
            " boss_time=? ," +
            " after_apex=? ," +
            " number_power_ups=? ," +
            " lvl=? ," +
            " bombs=? ," +
            " plane=? ," +
            " kind=? " +
            " where user_name=?;";
    private InfoHandler(){
        loadMap();
    }
    public static InfoHandler getInfoHandler(){
        if(infoHandler ==null)
            infoHandler =new InfoHandler();
        return infoHandler;
    }
    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root", "root", "m13y25s19q17l12");
        }catch(SQLException e){
            View.getView().informDatabaseConnectionError();
        }
        try {
            st = conn.createStatement();
            st.execute("USE game_data");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void loadMap(){
        try {
            connect();
            st.executeUpdate(CREATE_TABLE_IF_NOT_EXISTS);
            ResultSet rs=st.executeQuery("SELECT * FROM users");
               setUsersInfo(rs);
            closeConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void closeConnection() throws SQLException {
        st.close();
        conn.close();
    }
    private void setUsersInfo(ResultSet rs) throws SQLException {
        Map<String,Object> user=new HashMap<>();
        while(rs.next()){
            String name=rs.getString("user_name");
            user.put("addPower",rs.getDouble("add_power"));
            user.put("secondsPlayed",rs.getLong("time_played"));
            user.put("wave",rs.getInt("wave"));
            user.put("grade",rs.getInt("grade"));
            user.put("bossTime",rs.getBoolean("boss_time"));
            user.put("afterApex",rs.getBoolean("after_apex"));
            user.put("numberPowerUps",rs.getInt("number_power_ups"));
            user.put("level",rs.getInt("lvl"));
            user.put("bombs",rs.getInt("bombs"));
            byte[] planeInByteArray=rs.getBytes("plane");
            if(planeInByteArray.length!=0) {
                user.put("Plane", toObject(planeInByteArray));
                user.put("Kind", toObject(rs.getBytes("kind")));
            }
            else{
                user.put("Plane",new Plane());
                user.put("Kind", Kind.NORMAL);
            }
            users.put(name,user);
        }
        rs.close();
    }
    Set<String> userNames(){
        return users.keySet();
    }
    void addUserInfo(Map<String,?> userInfo){
            users.put(currentUser,userInfo);
    }
    void setLogger(String currentUser){
        this.currentUser=currentUser;
    }
    Map<String,?> userInfo(){
        return users.get(currentUser);
    }
    void save() {
        try {
            connect();
            ResultSet rs=st.executeQuery("SELECT * FROM users");
            List<String> presentUsers=handlePreviouslyExistingRows(rs);
            handleNewRows(presentUsers);
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    returns users who existed before this round
    private List<String> handlePreviouslyExistingRows(ResultSet rs) throws SQLException {
        List<String> presentUsers=new ArrayList<>();
        PreparedStatement ps=conn.prepareStatement(UPDATE);
        while (rs.next()){
            String name=rs.getString("user_name");
            presentUsers.add(name);
            if(users.containsKey(name))
                updateRow(ps,name,users.get(name));
            else
                deleteRow(name);
        }
        rs.close();
        ps.close();
        return presentUsers;
    }
    private void handleNewRows(List<String> presentUsers) throws SQLException {
        PreparedStatement ps=conn.prepareStatement(CREATE_ROW);
        for(Map.Entry<String,Map<String,?>> e: users.entrySet())
            if(!presentUsers.contains(e.getKey()))
            updateRow(ps,e.getKey(),e.getValue());
        ps.close();
    }
    private void updateRow(PreparedStatement ps,String name,Map<String,?> user) throws SQLException {
        ps.setString(1,name);
        if(user!=null) {
            ps.setDouble(2, (Double) user.get("addPower"));
            ps.setLong(3, (Long) user.get("secondsPlayed"));
            ps.setInt(4, (Integer) user.get("wave"));
            ps.setInt(5, (Integer) user.get("grade"));
            ps.setBoolean(6, (Boolean) user.get("bossTime"));
            ps.setBoolean(7, (Boolean) user.get("afterApex"));
            ps.setInt(8, (Integer) user.get("numberPowerUps"));
            ps.setInt(9, (Integer) user.get("level"));
            ps.setInt(10, (Integer) user.get("bombs"));
            ps.setBytes(11, toByteArray(user.get("Plane")));
            ps.setBytes(12, toByteArray(user.get("Kind")));
        }
        else{
            ps.setDouble(2, 0);
            ps.setLong(3, 0);
            ps.setInt(4, 1);
            ps.setInt(5, 0);
            ps.setBoolean(6, false);
            ps.setBoolean(7, false);
            ps.setInt(8, 0);
            ps.setInt(9, 1);
            ps.setInt(10, 3);
            ps.setBytes(11, new byte[0]);
            ps.setBytes(12, new byte[0]);
        }
        ps.executeUpdate();
    }
    private void deleteRow(String name) throws SQLException {
        String deleteUser="DELETE FROM users WHERE user_name=?;";
        PreparedStatement st=conn.prepareStatement(deleteUser);
        st.setString(1,name);
        st.executeUpdate();
    }
    private byte[] toByteArray(Object o){
        try {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream outputStream=new ObjectOutputStream(baos);
            outputStream.writeObject(o);
            byte[] answer=baos.toByteArray();
            outputStream.close();
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Object toObject(byte[] bytes){
        try {
            ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
            ObjectInputStream inputStream=new ObjectInputStream(bais);
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    void addUserInfo(String userName){
        users.put(userName,null);
    }
    void removeUserInfo(String userName){
        users.remove(userName);
    }
    Map<String,Map<String,?>> getUsers() {
        return users;
    }

}
