/*
 * Licensed Materials - Property of IBM
 *
 * 5747-SM3
 *
 * (C) Copyright IBM Corp. 1999, 2012 All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 *
 */
package com.drikvy.summon;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.content.Context;
import android.util.Log;

import com.drikvy.summon.ActionListener.Action;
import com.drikvy.summon.Connection.ConnectionStatus;
import com.drikvy.summon.services.MqttClientAndroidService;

/**
 * Deals with actions performed in the {@link ClientConnections} activity
 * and the {@link ConnectionDetails} activity and associated fragments
 *
 */
public class Listener {

  /** The handle to a {@link Connection} object which contains the {@link MqttClientAndroidService} associated with this object **/
  public String clientHandle = "";

  /** {@link ConnectionDetails} reference used to perform some actions**/
  //private ConnectionDetails connectionDetails = null;
  
  /** {@link ClientConnections} reference used to perform some actions**/
  //private ClientConnections clientConnections = null;
  /** {@link Context} used to load and format strings **/
  public MainActivity main;

  static boolean logging = false;

  /**
   * Constructs a listener object for use with {@link ConnectionDetails} activity and
   * associated fragments.
   * @param connectionDetails The instance of {@link ConnectionDetails}
   * @param clientHandle The handle to the client that the actions are to be performed on
   */
  /*
  public Listener(ConnectionDetails connectionDetails, String clientHandle)
  {
    this.connectionDetails = connectionDetails;
    this.clientHandle = clientHandle;
    context = connectionDetails;

  }
*/
  public Listener(MainActivity mainActivity)
  {
    this.main = mainActivity;
  }
  /**
   * Constructs a listener object for use with {@link ClientConnections} activity.
   * @param clientConnections The instance of {@link ClientConnections}
   */
  /*
  public Listener(ClientConnections clientConnections) {
    this.clientConnections = clientConnections;
    context = clientConnections;
  }
*/
  /**
   * Perform the needed action required based on the button that
   * the user has clicked.
   * 
   * @param item The menu item that was clicked
   * @return If there is anymore processing to be done
   * 
   */
/*
  @Override
  public boolean onMenuItemClick(MenuItem item) {

    int id = item.getItemId();

    switch (id)
    {
    
      case R.id.publish :
        publish();
        break;
      case R.id.subscribe :
        subscribe();
        break;
      case R.id.newConnection :
        createAndConnect();
        break;
      case R.id.disconnect :
        disconnect();
        break;
      case R.id.connectMenuOption :
        reconnect();
        break;
      case R.id.startLogging :
        enablePahoLogging();
        break;
      case R.id.endLogging :
        disablePahoLogging();
        break;
      case R.id.dumpLog :
        writeLog();
        break;
       
    }

    return false;
  }
*/
  /**
   * Reconnect the selected client
   */
  private void reconnect() {

    Connections.getInstance().getConnection(clientHandle).changeConnectionStatus(ConnectionStatus.CONNECTING);

    Connection c = Connections.getInstance().getConnection(clientHandle);
    try {
      c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(main, Action.CONNECT, clientHandle, null));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }

  }

  /**
   * Disconnect the client
   */
  public void disconnect() {

    Connection c = Connections.getInstance().getConnection(clientHandle);
    try {
      c.getClient().disconnect(null, new ActionListener(main, Action.DISCONNECT, clientHandle, null));
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to disconnect");
    }

  }

  /**
   * Subscribe to a topic that the user has specified
   */
  public void subscribe()
  {
    String topic = "hello/summon";

    try {
      String[] topics = new String[1];
      topics[0] = topic;
      
      Connections.getInstance().getConnection(clientHandle).getClient()
          .subscribe(topic, 0, null, new ActionListener(main, Action.SUBSCRIBE, clientHandle, topics));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
  }

  /**
   * Publish the message the user has specified
   */
  public void publish(String jsonMessage)
  {// TODO
    String topic = "hello/summon";

    String message = jsonMessage;

    int qos = ActivityConstants.defaultQos;

    boolean retained = false;

    String[] args = new String[2];
    args[0] = message;
    args[1] = topic;

    try {
      Connections.getInstance().getConnection(clientHandle).getClient()
          .publish(topic, message.getBytes(), qos, retained, null, new ActionListener(main, Action.PUBLISH, clientHandle, args));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }

  }

  /**
   * Create a new client and connect
   */
  MqttClientAndroidService client;
  public void createAndConnect()
  {
	  SummonApplication summ = (SummonApplication) main.getApplication();
	  
	  	// TODO The basic client information
	    String server = "118.96.201.59";//"192.168.10.14";////"192.168.10.43";
	    String clientId = summ.getUserFacebookName();
	    int port = 1883;
	    boolean cleanSession = false;

	    boolean ssl = false;
	    String uri = null;
	    if (ssl) {
	      uri = "ssl://";
	    }
	    else {
	      uri = "tcp://";
	    }

	    uri = uri + server + ":" + port;

	    // MqttClientAndroidService client;
	    client = Connections.getInstance().createClient(main, uri, clientId);
	    // create a client handle
	    String clientHandle = uri + clientId;
	    this.clientHandle = clientHandle;
	    Log.v("Listener", "clientHandle: "+this.clientHandle);
	    // TODO last will message
	    String message = ActivityConstants.empty;
	    String topic = ActivityConstants.empty;
	    Integer qos = ActivityConstants.defaultQos;
	    Boolean retained = ActivityConstants.defaultRetained;

	    // connection options
	    String username = ActivityConstants.empty;

	    String password = ActivityConstants.empty;

	    int timeout = ActivityConstants.defaultTimeOut;
	    int keepalive = ActivityConstants.defaultKeepAlive;

	    Connection connection = new Connection(clientHandle, clientId, server, port,
	    		main, client);
	    // arrayAdapter.add(connection);

	    // connection.registerChangeListener(changeListener);
	    Connections.getInstance().addConnection(connection);
	    Log.d("Listener", "Connect Success!!!");
	    // connect client

	    String[] actionArgs = new String[1];
	    actionArgs[0] = clientId;
	    connection.changeConnectionStatus(ConnectionStatus.CONNECTING);

	    MqttConnectOptions conOpt = new MqttConnectOptions();
	    conOpt.setCleanSession(cleanSession);
	    conOpt.setConnectionTimeout(timeout);
	    conOpt.setKeepAliveInterval(keepalive);
	    if (!username.equals(ActivityConstants.empty)) {
	      conOpt.setUserName(username);
	    }
	    if (!password.equals(ActivityConstants.empty)) {
	      conOpt.setPassword(password.toCharArray());
	    }

	    final ActionListener callback = new ActionListener(main,
	        ActionListener.Action.CONNECT, clientHandle, actionArgs);

	    boolean doConnect = true;

	    if ((!message.equals(ActivityConstants.empty))
	        || (!topic.equals(ActivityConstants.empty))) {
	      // need to make a message since last will is set
	      try {
	        conOpt.setWill(topic, message.getBytes(), qos.intValue(),
	            retained.booleanValue());
	      }
	      catch (Exception e) {
	        doConnect = false;
	        callback.onFailure(null, e);
	      }
	    }
	    client.setCallback(new MqttCallbackHandler(main, clientHandle));
	    connection.addConnectionOptions(conOpt);

	    if (doConnect) {
	      try {
	        client.connect(conOpt, null, callback);
	      }
	      catch (MqttException e) {
	        Log.e(this.getClass().getCanonicalName(),
	            "MqttException Occured", e);
	      }
	    }
	    //new WaitForSubscribe().execute();
	    // subscribe();
  }
  /*
  public class WaitForSubscribe extends AsyncTask<String, Integer, Void> {

	@Override
	protected Void doInBackground(String... params) {
		boolean isLoop = true;
		while(isLoop) {
			try {
				Thread.sleep(1000);
				if(client.mqttService != null) {
					isLoop = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		subscribe();
	}
  }
  */
  /**
   * Enables logging in the Paho MQTT client
   */
  /*
  private void enablePahoLogging() {

    try {
      InputStream logPropStream = context.getResources().openRawResource(R.raw.jsr47android);
      LogManager.getLogManager().readConfiguration(logPropStream);
      logging = true;
      clientConnections.invalidateOptionsMenu();
    }
    catch (IOException e) {
      Log.e("MqttClientAndroidService",
          "Error reading logging parameters", e);
    }

  }
*/
  /**
   * Disables logging in the Paho MQTT client
   */
  /*
  private void disablePahoLogging() {
    LogManager.getLogManager().reset();
    logging = false;
  }
*/
  /**
   * Writes the log to disk in the directory returned by 
   * {@link System#getProperty(String)} with the parameter
   * "java.io.tmpdir"
   * 
   */
  /*
  private void writeLog() {

    Map<String, Connection> connections = Connections.getInstance().getConnections();
    for (Map.Entry<String, Connection> entry : connections.entrySet())
    {
      entry.getValue().getClient().getDebug().dumpBaseDebug();
    }

    clientConnections.invalidateOptionsMenu();
  }
*/
}
