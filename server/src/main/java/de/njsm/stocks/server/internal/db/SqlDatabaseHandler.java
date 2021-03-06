package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.AuthAdmin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlDatabaseHandler implements DatabaseHandler{

    private static final Logger LOG = LogManager.getLogger(SqlDatabaseHandler.class);

    private final String url;
    private final Config c;
    private final String username;
    private final String password;

    public SqlDatabaseHandler(Config c) {

        this.c = c;

        String address = c.getDbAddress();
        String port = c.getDbPort();
        String name = c.getDbName();
        username = c.getDbUsername();
        password = c.getDbPassword();
        url = String.format("jdbc:mariadb://%s:%s/%s",
                address,
                port,
                name);

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("DB driver not present", e);
        }
    }

    private Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void add(SqlAddable d) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getAddStmt());
            d.fillAddStmt(stmt);
            stmt.execute();
        } catch (SQLException e) {
            LOG.error("Failed to add " + d.toString(), e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public void rename(SqlRenamable d, String newName) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRenameStmt());
            d.fillRenameStmt(stmt, newName);
            stmt.execute();
        } catch (SQLException e) {
            LOG.error("Failed to rename " + d.toString(), e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public void remove(SqlRemovable d) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRemoveStmt());
            d.fillRemoveStmt(stmt);
            stmt.execute();
        } catch (SQLException e) {
            LOG.error("Failed to remove " + d.toString(), e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public void removeUser(User u){

        String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
        String deleteDevicesCommand = "DELETE FROM User_device WHERE belongs_to=?";
        AuthAdmin ca = c.getCertAdmin();
        List<Integer> certificateList = new ArrayList<>();
        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlQuery = con.prepareStatement(getDevicesQuery);
            PreparedStatement sqlStmt=con.prepareStatement(u.getRemoveStmt());
            PreparedStatement sqlDeleteDevices = con.prepareStatement(deleteDevicesCommand);

            con.setAutoCommit(false);
            // revoke all devices
            sqlQuery.setInt(1, u.id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()){
                certificateList.add(res.getInt("ID"));
            }

            sqlDeleteDevices.setInt(1, u.id);
            sqlDeleteDevices.execute();
            u.fillRemoveStmt(sqlStmt);
            sqlStmt.execute();
            con.commit();

            certificateList.forEach(ca::revokeCertificate);
        } catch (SQLException e){
            LOG.error("Error deleting devices", e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public Ticket addDevice(UserDevice d) {

        String addTicket = "INSERT INTO Ticket (ticket, belongs_device, created_on) VALUES (?,LAST_INSERT_ID(), NOW())";
        Ticket result = new Ticket();
        result.ticket = Ticket.generateTicket();
        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlAddDevice = con.prepareStatement(d.getAddStmt(), Statement.RETURN_GENERATED_KEYS);
            PreparedStatement sqlAddTicket = con.prepareStatement(addTicket);

            con.setAutoCommit(false);
            d.fillAddStmt(sqlAddDevice);
            sqlAddDevice.execute();
            ResultSet keys = sqlAddDevice.getGeneratedKeys();
            keys.next();
            result.deviceId = keys.getInt(1);

            sqlAddTicket.setString(1, result.ticket);
            sqlAddTicket.execute();


            con.commit();

        } catch (SQLException e) {
            LOG.error("Error adding device", e);
            rollback(con);
            result.ticket = null;
        } finally {
            close(con);
        }
        return result;
    }

    @Override
    public void removeDevice(UserDevice u){

        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlStmt=con.prepareStatement(u.getRemoveStmt());
            con.setAutoCommit(false);
            u.fillRemoveStmt(sqlStmt);
            sqlStmt.execute();
            con.commit();

            c.getCertAdmin().revokeCertificate(u.id);

        } catch (SQLException e){
            LOG.error("Error deleting devices", e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public Data[] get(DataFactory df) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(df.getQuery());
            ResultSet rs = stmt.executeQuery();
            List<Data> result = df.createDataList(rs);
            return result.toArray(new Data[result.size()]);
        } catch (SQLException e) {
            LOG.error("Error getting data", e);
            rollback(con);
        } finally {
            close(con);
        }
        return new Data[0];
    }

    @Override
    public void moveItem(FoodItem item, int loc) {
        Connection con = null;
        String sqlString = "UPDATE Food_item SET stored_in=? WHERE ID=?";

        try {

            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sqlString);
            stmt.setInt(1, loc);
            stmt.setInt(2, item.id);
            stmt.executeQuery();

        } catch (SQLException e) {
            LOG.error("Error moving item", e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    protected void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    protected void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                LOG.error("Error while rollback", e1);
            }
        }
    }

}
