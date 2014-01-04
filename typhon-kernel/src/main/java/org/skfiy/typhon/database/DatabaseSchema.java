/*
 * Copyright 2013 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DatabaseSchema {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSchema.class);
    
    /**
     * Executes a SQL script.
     *
     * @param conn database connection.
     * @param resource an input stream for the script to execute.
     * @throws IOException if an IOException occurs.
     * @throws SQLException if an SQLException occurs.
     */
    public void executeSQLScript(Connection conn, InputStream resource) throws IOException,
            SQLException {
        
        BufferedReader in = null;
        try {
            
            in = new BufferedReader(new InputStreamReader(resource));
            boolean done = false;
            while (!done) {
                StringBuilder command = new StringBuilder();
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        done = true;
                        break;
                    }
                    
                    line = line.trim();
                    // Ignore comments and blank lines.
                    if (isSQLCommandPart(line)) {
                        command.append(" ").append(line);
                    }
                    
                    if (line.endsWith(";")) {
                        break;
                    }
                }
                
                // Send command to database.
                if (!done && !command.toString().isEmpty()) {
                    PreparedStatement pstmt = null;
                    try {
                        
                        String cmdStr = command.toString();
                        pstmt = conn.prepareStatement(cmdStr);
                        pstmt.execute();
                        
                        LOG.trace(cmdStr);
                    } catch (SQLException e) {
                        // Lets show what failed
                        throw e;
                    } finally {
                        if (pstmt != null) {
                            pstmt.close();
                        }
                    }
                }
                
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Returns true if a line from a SQL schema is a valid command part.
     *
     * @param line the line of the schema.
     * @return true if a valid command part.
     */
    private boolean isSQLCommandPart(String line) {
        if (line.equals("")) {
            return false;
        }
        
        // Check to see if the line is a comment. Valid comment types:
        //   "//" is HSQLDB
        //   "--" is DB2 and Postgres
        //   "#" is MySQL
        //   "REM" is Oracle
        //   "/*" is SQLServer
        return !(line.startsWith("//") || line.startsWith("--") || line.startsWith("#")
                || line.startsWith("REM") || line.startsWith("/*") || line.startsWith("*"));
    }
}
