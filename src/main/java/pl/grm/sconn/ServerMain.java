package pl.grm.sconn;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

import pl.grm.sconn.commands.*;
import pl.grm.sconn.connection.*;
import pl.grm.sconn.gui.*;

public class ServerMain {
	public static int				EST_PORT				= 4342;
	public static int				START_PORT				= 4343;
	public static int				MAX_PORT				= 4350;
	public static int				CONNECTIONS_MAX_POOL	= 5;
	private ArrayList<Connection>	connectionThreadsList;
	private boolean					stopRequsted			= false;
	private ExecutorService			executor;
	private Thread					serverConsoleThread;
	private Thread					connectorThread;
	private CommandManager			commandManager;
	private static ServerMain		instance;
	private boolean					guiActive;
	private Connector				connector;
	
	public ServerMain() {
		commandManager = new CommandManager(this);
	}
	
	public static void main(String[] args) {
		ServerMain.instance = new ServerMain();
		if (args.length != 0 && args[0].equals("gui")) {
			instance.setGuiActive(true);
		}
		instance.startServer();
	}
	
	private void startServer() {
		Thread.currentThread().setName("Main");
		CLogger.initLogger();
		setupBaseServerThreads();
		serverConsoleThread.start();
		connectorThread.start();
		if (isGuiActive()) {
			startGUI();
		}
	}
	
	public void setupBaseServerThreads() {
		CLogger.info("Starting server");
		executor = Executors.newFixedThreadPool(CONNECTIONS_MAX_POOL);
		connectionThreadsList = new ArrayList<Connection>();
		serverConsoleThread = new Thread(new ServerConsole(this));
		connector = new Connector(this);
		connectorThread = new Thread(connector);
	}
	
	private void startGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ServerGUI sGUI = new ServerGUI(ServerMain.this);
					connector.addObserver(sGUI);
					sGUI.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void stopServer() {
		CLogger.info("Stopping server ...\nConnection amount on stop "
				+ connectionThreadsList.size());
		executor.shutdownNow();
		setStopRequsted(true);
		for (Connection connection : connectionThreadsList) {
			connection.closeConnection();
		}
		System.exit(0);
	}
	
	public void addNewConnectionThread(Connection connection) {
		connectionThreadsList.add(connection);
	}
	
	public Connection getConnection(int id) {
		if (id < connectionThreadsList.size()) {
			Connection connection = connectionThreadsList.get(id);
			return connection;
		}
		return null;
	}
	
	public void executeCommand(String command) {
		commandManager.executeCommand(command);
	}
	
	public boolean isStopRequsted() {
		return stopRequsted;
	}
	
	public void setStopRequsted(boolean stopRequsted) {
		this.stopRequsted = stopRequsted;
	}
	
	public int getConnectionsAmount() {
		return connectionThreadsList.size();
	}
	
	public boolean isGuiActive() {
		return guiActive;
	}
	
	public void setGuiActive(boolean guiActive) {
		this.guiActive = guiActive;
	}
}