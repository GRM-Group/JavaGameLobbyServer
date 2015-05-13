package pl.grm.sconn.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import pl.grm.sconn.CLogger;
import pl.grm.sconn.ServerMain;
import pl.grm.sconn.commands.CommandType;
import pl.grm.sconn.commands.Commands;
import pl.grm.sconn.data.User;
import pl.grm.sconn.gui.ConnectionTab;

public class Connection extends Thread {

	private int ID;
	private int port;
	private InputStream is;
	private OutputStream os;
	private boolean connected;
	private boolean initialized;
	private Socket socket;
	private User user;
	private ConnectionTab tab;

	public Connection(int id, Socket socket) {
		this.ID = id;
		this.socket = socket;
		this.port = socket.getPort();
		this.setName("ID: " + id);
		this.setTab(new ConnectionTab(this));
		ServerMain.instance.notifyObservers();
	}

	@Override
	public void run() {
		if (!isConnected() && !isInitialized()) {
			try {
				configureConnection();
				setInitialized(true);
				String received = "";
				user = PacketParser.receiveUserData(socket);
				CLogger.info("Welcome " + user.getName() + "!");
				tab.fillUP();
				while (!received.contains("!close")) {
					if (received != null & received != "") {
						CLogger.info("Server received message: " + received);
						PacketParser.sendMessage(received, socket);// TODO: stop
																	// requested
					}
					received = PacketParser.receiveMessage(socket);
					Commands cmm;
					if (received.length() > 0
							&& (cmm = ServerMain.instance.getCM().executeCommand(
									received, CommandType.CLIENT)) != Commands.NONE) {
						if (cmm == Commands.ERROR) {
							CLogger.info("Command not executed");
						} else {
							CLogger.info("Command executed on connection " + ID);
						}
					}
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			finally {
				closeConnection();
				setConnected(false);
				CLogger.info("Connection " + ID + " | Disconnected");
				ServerMain.instance.destroyConnection(ID);
			}
		}
	}

	public void configureConnection() throws IOException {
		try {
			setConnected(true);
			CLogger.info("Connected on port " + port);
			is = socket.getInputStream();
			os = socket.getOutputStream();

		}
		catch (IOException e) {
			if (!e.getMessage().contains("socket closed")) {
				e.printStackTrace();
			}
			throw new IOException(e.getMessage());
		}
	}

	public void closeConnection() {
		try {
			if (socket != null) {
				socket.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getID() {
		return ID;
	}

	public int getPort() {
		return port;
	}

	public InputStream getIs() {
		return is;
	}

	public OutputStream getOs() {
		return os;
	}

	public Socket getSocket() {
		return socket;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ConnectionTab getTab() {
		return tab;
	}

	public void setTab(ConnectionTab tab) {
		this.tab = tab;
	}
}
