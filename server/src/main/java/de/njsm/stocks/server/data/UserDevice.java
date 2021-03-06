package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class UserDevice extends Data implements SqlAddable,
                                                SqlRemovable {
    public int id;
    public String name;
    public int userId;

    public UserDevice() {
    }

    public UserDevice(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, name);
        stmt.setInt(2, userId);
    }

    @Override
    @JsonIgnore
    public String getAddStmt() {
        return "INSERT INTO User_device (name, belongs_to) VALUES (?,?)";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    @JsonIgnore
    public String getRemoveStmt() {
        return "DELETE FROM User_device WHERE ID=?";
    }

}
