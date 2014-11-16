import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import struts.SearchItem;
import struts.SteamAccount;
import struts.SteamItemTable;
import struts.SteamListing;
import api.Steam;



public class SteamBot extends JFrame {

	private JPanel contentPane;
	SteamAccount account;
	private JTable table;
	SteamItemTable tableModel = new SteamItemTable();
	ArrayList<SearchItem> searchItems = new ArrayList<SearchItem>();
	File itemsPath = new File("./searchItems/");
	double walletBalance = 0.00;
	JTextPane txtLog;
	Deque<SteamListing> foundListings = new LinkedList<SteamListing>();
	
	boolean isRunning = false;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					} catch (Exception e) {
						try {
							UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					SteamBot frame = new SteamBot(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SteamBot(final SteamAccount account) {
		
		setTitle("Steam Market Bot");
		
		try {
			if(account != null){
				Steam.steamEligibilityCheck(account);
				getItems(account.getUsername());
			} else
				getItems("smitteh1103");
		} catch (Exception e) {
		}
		
		
		this.account = account;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane);
	//	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
		table.getColumnModel().getColumn(3).setMaxWidth(100);
		table.getColumnModel().getColumn(3).setMinWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMinWidth(100);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.WEST);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{57, 0};
		gbl_panel_1.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		final JButton btnStop = new JButton("Stop");
		final JButton btnNewButton = new JButton("Start");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isRunning = false;
				
				btnNewButton.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
		btnStop.setEnabled(false);

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log("Started.");
				isRunning = true;
				for(int i = 0; i < 10; i++){
					RunnerThread thread = new RunnerThread();
					thread.start();
					System.out.println("Thread " + (i+1) + " started.");
				}
				
				BuyThread buyThread = new BuyThread();
				buyThread.start();
				
				btnNewButton.setEnabled(false);
				btnStop.setEnabled(true);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		panel_1.add(btnNewButton, gbc_btnNewButton);
		
		
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.anchor = GridBagConstraints.WEST;
		gbc_btnStop.insets = new Insets(0, 0, 5, 0);
		gbc_btnStop.gridx = 0;
		gbc_btnStop.gridy = 1;
		panel_1.add(btnStop, gbc_btnStop);
		
		final SteamBot bot = this;
		
		JButton btnAddItem = new JButton("Add Item");
		btnAddItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new SteamItemGUI(searchItems, tableModel, bot).show();
			}
		});
		GridBagConstraints gbc_btnAddItem = new GridBagConstraints();
		gbc_btnAddItem.anchor = GridBagConstraints.WEST;
		gbc_btnAddItem.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddItem.gridx = 0;
		gbc_btnAddItem.gridy = 2;
		panel_1.add(btnAddItem, gbc_btnAddItem);
		
		JButton btnRemoveItem = new JButton("Remove Item");
		btnRemoveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(int i = 0; i < 20; i++)
					log("" + i);
				int row = table.getSelectedRow();
				
				if(row >= 0){
					tableModel.removeItem(row);
					searchItems.remove(row);
					writeItemsFile(account.getUsername());
				}
				
				
			}
		});
		GridBagConstraints gbc_btnRemoveItem = new GridBagConstraints();
		gbc_btnRemoveItem.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveItem.anchor = GridBagConstraints.WEST;
		gbc_btnRemoveItem.gridx = 0;
		gbc_btnRemoveItem.gridy = 3;
		panel_1.add(btnRemoveItem, gbc_btnRemoveItem);
		
		final JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_2.add(scrollPane_1);
		
		txtLog = new JTextPane();
		//txtLog.setText("asfasf\r\naf\r\nas\r\nf\r\nasf\r\nasf");
		txtLog.setPreferredSize(new Dimension(panel_2.getWidth(), 120));
		txtLog.setMinimumSize(new Dimension(panel_2.getWidth(), 120));
		txtLog.setMaximumSize(new Dimension(panel_2.getWidth(), 120));
		txtLog.setSize(panel_2.getWidth(), 120);
		//txtLog.set
		scrollPane_1.setViewportView(txtLog);
		this.addComponentListener(new ComponentListener(){

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				txtLog.setSize(panel_2.getWidth(), 120);
				txtLog.setPreferredSize(new Dimension(panel_2.getWidth(), 120));
				txtLog.setMinimumSize(new Dimension(panel_2.getWidth(), 120));
				txtLog.setMaximumSize(new Dimension(panel_2.getWidth(), 120));
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void getItems(String username){
		if(!itemsPath.exists()){
			itemsPath.mkdir();
			searchItems = new ArrayList<SearchItem>();
		}
		
		File file = new File(itemsPath.getPath() + "/" + username.toLowerCase() + ".items");
		
		if(!file.exists())
			searchItems = new ArrayList<SearchItem>();
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			searchItems = (ArrayList<SearchItem>) ois.readObject();
			ois.close();

		} catch (Exception e) {
			searchItems = new ArrayList<SearchItem>();
		} 
		
		if(searchItems == null)
			searchItems = new ArrayList<SearchItem>();
		
		for(SearchItem item : searchItems){
			String terms = "";
			if(item.getSearchTerms() != null){
				for(int i = 0; i < item.getSearchTerms().length; i++){
					terms += item.getSearchTerms()[i];
					if(i < item.getSearchTerms().length-1)
						terms += ",";
				}
			}
			tableModel.addItem(new Object[]{item.getItemName(), terms, item.getMaxBuyout(), Boolean.toString(item.isExactMatch())});
		}
	}
	public void writeItemsFile(String username){
		if(!itemsPath.exists())
			itemsPath.mkdir();
		
		File file = new File(itemsPath.getPath() + "/" + username.toLowerCase() + ".items");
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(searchItems);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void log(String message){
		try {
			Date date = new Date();

			FileWriter fw = new FileWriter("./LOG.txt", true);
			fw.write(message + "\r\n");
			fw.close();
			txtLog.setText(txtLog.getText() + "[" + date.toLocaleString() + "] " +  message + "\r\n");
			txtLog.setSelectionStart(txtLog.getText().length());
			txtLog.setSelectionEnd(txtLog.getText().length());
			//txtLog.setCaretPosition();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	class BuyThread extends Thread{
		public void run(){
			while(isRunning){
				if(!foundListings.isEmpty()){
					SteamListing listing = foundListings.pop();
					
					//log("Buying " + listing.getItemName() + " for $" + ((float)listing.getPrice() / 100f));
					
					JSONObject response = null;
					try {
						response = Steam.purchaseListing(account, listing);
					} catch (Exception e) {
						log("Failed! " + listing.getItemName() + " $" + ((float)listing.getPrice()/100.f) + " " + e.getMessage());
					}
					
					if(response != null){
						if(response.get("wallet_info") != null)
							log(listing.itemName + " bought for $" + ((float)listing.getPrice()/100.f) + "!");
						else
							log("Failed! " + listing.getItemName() + " $" + ((float)listing.getPrice()/100.f) + " " + response.get("message"));
					}
				}
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class RunnerThread extends Thread{
		

		public void run(){
			while(isRunning){
				try{
					ArrayList<SteamListing> listings; 
					try{
						listings = Steam.getRecentListings(account);
					}catch(Exception e){
						continue;
					}
					for(SteamListing listing : listings){
						for(SearchItem item : searchItems){
							if(item.isExactMatch()){
								if(listing.getAppId() == 730 && listing.getItemName().equals(item.getItemName())){
									if(listing.getPrice() <= item.getMaxBuyout() && !foundListings.contains(listing)){
										foundListings.add(listing);
									}
								}
							} else {
								for(String term : item.getSearchTerms()){
									if(listing.getAppId() == 730 && listing.getItemName().toLowerCase().contains(term.toLowerCase())){
										if(listing.getPrice() <= item.getMaxBuyout() && !foundListings.contains(listing)){
											foundListings.add(listing);
										}
										break;
									}
								}
							}
						}
					}
				} catch(Exception e){
					//log(e.getMessage());
				}
				
			}
			
			log("Stopped.");
		}
	}
}
