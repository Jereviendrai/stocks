package de.njsm.stocks.server.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlAddable {

    void fillAddStmt(PreparedStatement stmt) throws SQLException;

    String getAddStmt();
}
