package com.demo.xmppchat;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class XMPPChatDemoActivity extends Activity {

	public static final String HOST = "192.168.1.102";
	public static final int PORT = 5222;
	public static final String SERVICE = "sunny";
	public static final String USERNAME = "sunny080593@sunny";
	public static final String PASSWORD = "843066303";
    private XMPPConnection connection;
	private ArrayList<String> messages = new ArrayList<String>();
	private Handler mHandler = new Handler();

	 private EditText recipient;
	private EditText textMessage;
	private ListView listview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		recipient = (EditText) this.findViewById(R.id.toET);
		textMessage = (EditText) this.findViewById(R.id.chatET);
		listview = (ListView) this.findViewById(R.id.listMessages);
		setListAdapter();

		// Set a listener to send a chat text message
          		Button send = (Button) this.findViewById(R.id.sendBtn);
		      send.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View view) {
				String to = recipient.getText().toString();
				String text = textMessage.getText().toString();

				Log.i("XMPPChatDemoActivity", "Sending text " + text + " to " + to);
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);				
				if (connection != null) {
					connection.sendPacket(msg);
					messages.add(connection.getUser() + ":");
					messages.add(text);
					setListAdapter();
				}
			}
		});

	
		      connect();
		      
		      
		   /*   ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(connection);
				sdm.addFeature("http://jabber.org/protocol/disco#info");

       sdm.addFeature("jabber:iq:privacy");
       
       FileTransferManager   mFileTransferManager = new FileTransferManager(
               connection);
       String to = connection.getRoster()
               .getPresence("smartgini@smartgini.com")
               .getFrom();
       OutgoingFileTransfer transfer = mFileTransferManager
               .createOutgoingFileTransfer(to);
       System.out.println(Environment.getExternalStorageDirectory().getPath());
       File file = new File(Environment.getExternalStorageDirectory().getPath(),"screen.png");
       try
       {
       transfer.sendFile(file, "test_file");
       }
       catch(XMPPException e)
       {
       e.printStackTrace();
       
        }*/

		      
		      
	}

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	
	
	  public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		
	if (connection != null) {
	// Add a packet listener to get messages sent to us
	PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
	connection.addPacketListener(new PacketListener() {
	@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody()
								+ " from " + fromName );
						messages.add(fromName + ":");
						messages.add(message.getBody());
						// Add the incoming message to the list view
						mHandler.post(new Runnable() {
							public void run() {
								setListAdapter();
	}
	});
	}
	}
	}, filter);
	}
	
/*	final FileTransferManager manager = new FileTransferManager(connection); //Use your xmpp connection
	manager.addFileTransferListener(new FileTransferListener(){
	    @Override
		public void fileTransferRequest(FileTransferRequest request) {
			// TODO Auto-generated method stub
	//Close
			 {
	    public void fileTransferRequest(FileTransferRequest request) {
	            IncomingFileTransfer transfer = request.accept();
	            try {
	                InputStream input = transfer.recieveFile();
	                //This will be a binary stream and you can process it. Create image and display it inline in your chat app.
	            } catch (XMPPException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		//till here	 
	    	IncomingFileTransfer transfer = request.accept();
            try {
                InputStream input = transfer.recieveFile();
             Log.d("File ", "Received");
                //This will be a binary stream and you can process it. Create image and display it inline in your chat app.
            } catch (XMPPException e) {
                e.printStackTrace();
            }
		}
	});*/
	}

	private void setListAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.listitem, messages);
		listview.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (connection != null)
				connection.disconnect();
		} catch (Exception e) {

		}
	}

	public void connect() {

		  configure(ProviderManager.getInstance()); 
		final ProgressDialog dialog = ProgressDialog.show(this,
		"Connecting...", "Please wait...", false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// Create a connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						HOST, PORT, SERVICE);
				XMPPConnection connection = new XMPPConnection(connConfig);
				
				try {
					connection.connect();
					Log.i("XMPPChatDemoActivity",
							"Connected to " + connection.getHost());
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to connect to "
							+ connection.getHost());
					Log.e("XMPPChatDemoActivity", ex.toString());
					setConnection(null);
				}
				try {
					// SASLAuthentication.supportSASLMechanism("PLAIN", 0);
					connection.login(USERNAME, PASSWORD);
					Log.i("XMPPChatDemoActivity",
							"Logged in as " + connection.getUser());

					// Set the status to available
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
					setConnection(connection);

					Roster roster = connection.getRoster();
					Log.d("XMPPChatDemoActivity", "Roster is "+roster.getGroups());
					Collection<RosterGroup> groups =roster.getGroups();
					for(RosterGroup group : groups)
					{
					System.out.println("Roster group "+group.getName());
					}
					String group[]={"Friends"};
				try
				{
				roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
				
				roster.createEntry("sudhir@sunny", "Sudhir", null);
				
				
				enableFileTransferListener(connection);
				ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(connection);
				sdm.addFeature("http://jabber.org/protocol/disco#info");

         sdm.addFeature("jabber:iq:privacy");
         
         FileTransferManager   mFileTransferManager = new FileTransferManager(
                 connection);
         String to = connection.getRoster()
                 .getPresence("sudhir@sunny")
                 .getFrom();
         OutgoingFileTransfer transfer = mFileTransferManager
                 .createOutgoingFileTransfer(to);
         System.out.println(Environment.getExternalStorageDirectory().getPath());
         File file = new File(Environment.getExternalStorageDirectory().getPath(),"screen.png");
         try
         {
         transfer.sendFile(file, "test_file");
        
         }
         catch(XMPPException e)
         {
         e.printStackTrace();
          }
         catch (Exception e) {
			// TODO: handle exception
        	 e.printStackTrace();
             
         }
      }
				catch(XMPPException ex)
				{
					ex.printStackTrace();
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
					System.out.println("Roster Added");
					Collection<RosterEntry> entries = roster.getEntries();
					for (RosterEntry entry : entries) {
						Log.d("XMPPChatDemoActivity",
								"--------------------------------------");
						Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
						Log.d("XMPPChatDemoActivity",
								"User: " + entry.getUser());
						Log.d("XMPPChatDemoActivity",
								"Name: " + entry.getName());
						Log.d("XMPPChatDemoActivity",
								"Status: " + entry.getStatus());
						Log.d("XMPPChatDemoActivity",
								"Type: " + entry.getType());
						Presence entryPresence = roster.getPresence(entry
								.getUser());

						Log.d("XMPPChatDemoActivity", "Presence Status: "
								+ entryPresence.getStatus());
						Log.d("XMPPChatDemoActivity", "Presence Type: "
								+ entryPresence.getType());
						Presence.Type type = entryPresence.getType();
						if (type == Presence.Type.available)
							Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
						Log.d("XMPPChatDemoActivity", "Presence : "
								+ entryPresence);

					}
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to log in as "
							+ USERNAME);
					Log.e("XMPPChatDemoActivity", ex.toString());
					setConnection(null);
				}

				dialog.dismiss();
			     }
	        	});
		t.start();
		dialog.show();
	}
	
	/*public void enableFileTransferListener(XMPPConnection _connection) {
		final String TAG="XMPP File Receiver";
		
		ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        if (sdm == null){
            sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        }
        sdm.addFeature("http://jabber.org/protocol/disco#info");
        sdm.addFeature("jabber:iq:privacy");
        sdm.addFeature("http://jabber.org/protocol/disco#items");
		if (_connection != null) {
			 configure(ProviderManager.getInstance()); 
			 
		           
		            FileTransferNegotiator.setServiceEnabled(_connection, true);
		            FileTransferNegotiator.IBB_ONLY = true;                            
                      
		            FileTransferManager manager = new FileTransferManager(_connection);
		            manager.addFileTransferListener(new FileTransferListener() {
		                public void fileTransferRequest(
		                        final FileTransferRequest request) {
		                    Log.i(TAG, "in fileTransferRequest");
		                    new Thread() {
		                        @Override
		                        public void run() {
		                            IncomingFileTransfer transfer = request.accept();
		                            configure(ProviderManager.getInstance());
		                            File mf = Environment.getExternalStorageDirectory();
		                            String DROP_LOC = mf.getAbsoluteFile()+ "/DCIM/" + transfer.getFileName();
		                            File file = new File(DROP_LOC);                         
		                            try {
		                                transfer.recieveFile(file);
		                                
		                                while (!transfer.isDone()) {
		                                    Log.i(TAG, "in While");
		                                    InputStream input = transfer.recieveFile();
		                                   Log.d(TAG, input.toString());
		                                   try {
		                                        Thread.sleep(1000L);
		                                    } catch (Exception e) {
		                                        Log.e("", e.getMessage());
		                                    }
		                                    if (transfer.getStatus().equals(
		                                            Status.error)) {
		                                    	Log.d("Error is", Status.error.toString());
		                                    	
		                                        Log.e("ERROR!!! ", transfer.getError()
		                                                + "");
		                                    }
		                                    if (transfer.getException() != null) {
		                                        transfer.getException()
		                                                .printStackTrace();
		                                    }     
		                                    
		                                    } 

		                                if ( !transfer.getStatus().equals("Refused")
		                                        || !transfer.getStatus().equals("Error")
		                                        || !transfer.getStatus().equals("Cancelled")) {

		                                       System.out.println("Inside if "+transfer.getStatus());                                                                                                                                                
		                                }
		                            } catch (Exception e) {
		                                Log.e(TAG, e.getMessage());
		                            }

		                        };
		                    }.start();


		                }
		            });
		        }
		    }
*/
	/*public void enableFileTransferListener(XMPPConnection _connection) {
		final String TAG="XMPP File Receiver";
		
		ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        if (sdm == null){
            sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        }
        sdm.addFeature("http://jabber.org/protocol/disco#info");
        sdm.addFeature("jabber:iq:privacy");
        sdm.addFeature("http://jabber.org/protocol/disco#items");
		if (_connection != null) {
			 configure(ProviderManager.getInstance()); 
			 
		           
		            FileTransferNegotiator.setServiceEnabled(_connection, true);
		            FileTransferNegotiator.IBB_ONLY = true;                            
                      
		            FileTransferManager manager = new FileTransferManager(_connection);
		            manager.addFileTransferListener(new FileTransferListener() {
		                public void fileTransferRequest(
		                        final FileTransferRequest request) {
		                    Log.i(TAG, "in fileTransferRequest");
		                    new Thread() {
		                        @Override
		                        public void run() {
		                            IncomingFileTransfer transfer = request.accept();
		                            configure(ProviderManager.getInstance());
		                            File mf = Environment.getExternalStorageDirectory();
		                            String DROP_LOC = mf.getAbsoluteFile()+ "/DCIM/" + transfer.getFileName();
		                         
		                         
		                            File file = new File(DROP_LOC);                         
		                            
		                            try {
		                                transfer.recieveFile(file);
		                                InputStream input =transfer.recieveFile();
		                                
		                                
		                                while (!transfer.isDone()) {
		                                    Log.i(TAG, "in While");
		                                    try {
		                                        Thread.sleep(1000L);
		                                    } catch (Exception e) {
		                                        Log.e("", e.getMessage());
		                                    }
		                                    if (transfer.getStatus().equals(
		                                            Status.error)) {
		                                    	Log.d("Error is", Status.error.toString());
		                                    	
		                                        Log.e("ERROR!!! ", transfer.getError()
		                                                + "");
		                                    }
		                                    if (transfer.getException() != null) {
		                                        transfer.getException()
		                                                .printStackTrace();
		                                    }     
		                                    
		                                    } 

		                                if ( !transfer.getStatus().equals("Refused")
		                                        || !transfer.getStatus().equals("Error")
		                                        || !transfer.getStatus().equals("Cancelled")) {

		                                       System.out.println("Inside if "+transfer.getStatus());                                                                                                                                                
		                                }
		                            } catch (Exception e) {
		                                Log.e(TAG, e.getMessage());
		                            }

		                        };
		                    }.start();


		                }
		            });
		        }
		    }*/
	public void enableFileTransferListener(XMPPConnection _connection) {
		final String TAG="XMPP File Receiver";
		
		ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        if (sdm == null){
            sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        }
        sdm.addFeature("http://jabber.org/protocol/disco#info");
        sdm.addFeature("jabber:iq:privacy");
        sdm.addFeature("http://jabber.org/protocol/disco#items");
		if (_connection != null) {
			 configure(ProviderManager.getInstance()); 
			 
		           
		            FileTransferNegotiator.setServiceEnabled(_connection, true);
		            //FileTransferNegotiator.IBB_ONLY = true;    
		            
		            FileTransferNegotiator.getInstanceFor(_connection);
		           
                    FileTransferNegotiator.getSupportedProtocols();
		            FileTransferManager manager = new FileTransferManager(_connection);
		            manager.addFileTransferListener(new FileTransferListener() {
		                public void fileTransferRequest(
		                        final FileTransferRequest request) {
		                    Log.i(TAG, "in fileTransferRequest");
		                    new Thread() {
		                        @Override
		                        public void run() {
		                            IncomingFileTransfer transfer = request.accept();
		                            configure(ProviderManager.getInstance());
		                            File mf = Environment.getExternalStorageDirectory();
		                            String DROP_LOC = mf.getAbsoluteFile()+ "/DCIM/" + request.getFileName();
		                         
		                         
		                            File file = new File(DROP_LOC);                         
		                            
		                            try {
		                            	
		                                transfer.recieveFile(file);
		                                
		                              
		                                while (!transfer.isDone()
		                                        || (transfer.getProgress() < 1)) {
		                                    Log.i(TAG, "in While");
		                                    
		                                    try {
		                                    	   Log.i("Recieve File alert dialog",
		                                                   "still receiving : "
		                                                           + (transfer
		                                                                   .getProgress())
		                                                           + " status "
		                                                           + transfer.getStatus());
		                                    	
		                                        Thread.sleep(1000L);
		                                    } catch (Exception e) {
		                                        Log.e("", e.getMessage());
		                                    }
		                                    if (transfer.getStatus().equals(
		                                            Status.error)) {
		                                    	Log.d("Error is", Status.error.toString());
		                                    	
		                                        Log.e("ERROR!!! ", transfer.getError()
		                                                + "");
		                                        
		                                        Log.i("Recieve File alert dialog",
		                                                "cancelling still receiving : "
		                                                        + (transfer.getProgress())
		                                                        + " status "
		                                                        + transfer.getStatus());
		                                        transfer.cancel();

		                                        break;
		                                    }
		                                    if (transfer.getException() != null) {
		                                        transfer.getException()
		                                                .printStackTrace();
		                                    }     
		                                    
		                                    } 
		                                Log.d("Data Size 2"," "+String.valueOf(transfer.getAmountWritten()));
	                                    
		                                Log.d("File Size"," "+String.valueOf(transfer.getFileSize()));
		                                InputStream input=transfer.recieveFile();
		                                    
		                                Log.d("File from inputstream"," "+String.valueOf(input));
		                                
		                                if ( !transfer.getStatus().equals("Refused")
		                                        || !transfer.getStatus().equals("Error")
		                                        || !transfer.getStatus().equals("Cancelled")) {

		                                       System.out.println("Inside if "+transfer.getStatus());                                                                                                                                                
		                                }
		                            } catch (Exception e) {
		                                Log.e(TAG, e.getMessage());
		                            }

		                        };
		                    }.start();
                    }
		     });
		}
	}
	
	
	

	public void configure(ProviderManager pm) {

	    // Private Data Storage
	    pm.addIQProvider("query", "jabber:iq:private",
	            new PrivateDataManager.PrivateDataIQProvider());

	    // Time
	    try {
	        pm.addIQProvider("query", "jabber:iq:time",
	                    Class.forName("org.jivesoftware.smackx.packet.Time"));
	    } catch (ClassNotFoundException e) {
	        Log.w("TestClient",
	                "Can't load class for org.jivesoftware.smackx.packet.Time");
	    }

	    // Roster Exchange
	    pm.addExtensionProvider("x", "jabber:x:roster",
	            new RosterExchangeProvider());

	    // Message Events
	    pm.addExtensionProvider("x", "jabber:x:event",
	            new MessageEventProvider());

	    // Chat State
	    pm.addExtensionProvider("active",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("composing",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("paused",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("inactive",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("gone",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());

	    // XHTML
	    pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
	            new XHTMLExtensionProvider());

	    // Group Chat Invitations
	    pm.addExtensionProvider("x", "jabber:x:conference",
	            new GroupChatInvitation.Provider());

	    // Service Discovery # Items
	    pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
	            new DiscoverItemsProvider());

	    // Service Discovery # Info
	    pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
	            new DiscoverInfoProvider());

	    // Data Forms
	    pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

	    // MUC User
	    pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
	            new MUCUserProvider());

	    // MUC Admin
	    pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
	            new MUCAdminProvider());

	    // MUC Owner
	    pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
	            new MUCOwnerProvider());

	    // Delayed Delivery
	    pm.addExtensionProvider("x", "jabber:x:delay",
	            new DelayInformationProvider());

	    // Version
	    try {
	        pm.addIQProvider("query", "jabber:iq:version",
	                Class.forName("org.jivesoftware.smackx.packet.Version"));
	    } catch (ClassNotFoundException e) {
	        // Not sure what's happening here.
	    }

	    // VCard
	    pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

	    // Offline Message Requests
	    pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
	            new OfflineMessageRequest.Provider());

	    // Offline Message Indicator
	    pm.addExtensionProvider("offline",
	            "http://jabber.org/protocol/offline",
	            new OfflineMessageInfo.Provider());

	    // Last Activity
	    pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

	    // User Search
	    pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

	    // SharedGroupsInfo
	    pm.addIQProvider("sharedgroup",
	            "http://www.jivesoftware.org/protocol/sharedgroup",
	            new SharedGroupsInfo.Provider());

	    // JEP-33: Extended Stanza Addressing
	    pm.addExtensionProvider("addresses",
	            "http://jabber.org/protocol/address",
	            new MultipleAddressesProvider());

	    // FileTransfer
	    pm.addIQProvider("si", "http://jabber.org/protocol/si",
	            new StreamInitiationProvider());

	    pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
	            new BytestreamsProvider());
	    pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
	            new OpenIQProvider());
	    pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
	            new CloseIQProvider());
	    pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
	            new DataPacketProvider());
	    // Privacy
	    pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
	    pm.addIQProvider("command", "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider());
	    pm.addExtensionProvider("malformed-action",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.MalformedActionError());
	    pm.addExtensionProvider("bad-locale",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.BadLocaleError());
	    pm.addExtensionProvider("bad-payload",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.BadPayloadError());
	    pm.addExtensionProvider("bad-sessionid",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.BadSessionIDError());
	    pm.addExtensionProvider("session-expired",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.SessionExpiredError());
	}
	}