package org.wispcrm.mikrotik;

import java.util.List;
import me.legrange.mikrotik.MikrotikApiException;

public class Funciones extends Conectar {

    public Funciones() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addlistsuspendidos(String ip,String comentario) throws Exception {
	    	connect();
	         con.execute("/ip/firewall/address-list/add address="+ip+" list=Morosos comment="+comentario);//,80,32");
 	        disconnect();
	    }
	        }