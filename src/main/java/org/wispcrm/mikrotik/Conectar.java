package org.wispcrm.mikrotik;

import javax.net.SocketFactory;
import me.legrange.mikrotik.ApiConnection;

/**
 *
 */
 abstract class Conectar {
     
    protected void connect() throws Exception {
        con = ApiConnection.connect(SocketFactory.getDefault(), Config.HOST, ApiConnection.DEFAULT_PORT, 2000);
        con.login(Config.USERNAME, Config.PASSWORD);
    }

    protected void disconnect() throws Exception {
        con.close();
    }
    
    protected ApiConnection con;
    
}