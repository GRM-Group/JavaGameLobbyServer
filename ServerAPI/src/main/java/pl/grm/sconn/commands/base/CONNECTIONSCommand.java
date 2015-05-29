/**
 * 
 */
package pl.grm.sconn.commands.base;

import pl.grm.sconn.ServerMain;
import pl.grm.sconn.commands.CommandType;
import pl.grm.sconn.commands.Commands;
import pl.grm.sconn.commands.IBaseCommand;
import pl.grm.sconn.connection.Connection;

/**
 * @author Levvy055
 *
 */
public class CONNECTIONSCommand implements IBaseCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.grm.sconn.commands.ICommand#execute(pl.grm.sconn.commands.Commands,
	 * java.lang.String, pl.grm.sconn.commands.CommandType,
	 * pl.grm.sconn.connection.Connection)
	 */
	@Override
	public boolean execute(Commands command, String args, CommandType cType, Connection connection) {
		System.out.println(ServerMain.instance.getConnectionsAmount());
		return true;
	}

}