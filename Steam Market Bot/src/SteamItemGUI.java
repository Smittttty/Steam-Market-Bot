import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import struts.SearchItem;
import struts.SteamItemTable;


public class SteamItemGUI extends JFrame {

	private JPanel contentPane;
	private JTextField txtItemName;
	private JTextField txtSearchTerms;
	private JTextField txtMaxPrice;
	private JCheckBox chkMatch;

	
	public SteamItemGUI(final ArrayList<SearchItem> searchItems, final SteamItemTable model, final SteamBot bot) {
		
		setType(Type.UTILITY);
		setTitle("Add Item");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 264, 163);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblItemName = new JLabel("Item Name:");
		GridBagConstraints gbc_lblItemName = new GridBagConstraints();
		gbc_lblItemName.anchor = GridBagConstraints.EAST;
		gbc_lblItemName.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemName.gridx = 0;
		gbc_lblItemName.gridy = 0;
		contentPane.add(lblItemName, gbc_lblItemName);
		
		txtItemName = new JTextField();
		GridBagConstraints gbc_txtItemName = new GridBagConstraints();
		gbc_txtItemName.insets = new Insets(0, 0, 5, 0);
		gbc_txtItemName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtItemName.gridx = 1;
		gbc_txtItemName.gridy = 0;
		contentPane.add(txtItemName, gbc_txtItemName);
		txtItemName.setColumns(10);
		
		JLabel lblSearchTerms = new JLabel("Search Terms:");
		GridBagConstraints gbc_lblSearchTerms = new GridBagConstraints();
		gbc_lblSearchTerms.anchor = GridBagConstraints.EAST;
		gbc_lblSearchTerms.insets = new Insets(0, 0, 5, 5);
		gbc_lblSearchTerms.gridx = 0;
		gbc_lblSearchTerms.gridy = 1;
		contentPane.add(lblSearchTerms, gbc_lblSearchTerms);
		
		txtSearchTerms = new JTextField();
		GridBagConstraints gbc_txtSearchTerms = new GridBagConstraints();
		gbc_txtSearchTerms.insets = new Insets(0, 0, 5, 0);
		gbc_txtSearchTerms.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearchTerms.gridx = 1;
		gbc_txtSearchTerms.gridy = 1;
		contentPane.add(txtSearchTerms, gbc_txtSearchTerms);
		txtSearchTerms.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Max Price:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		txtMaxPrice = new JTextField();
		GridBagConstraints gbc_txtMaxPrice = new GridBagConstraints();
		gbc_txtMaxPrice.insets = new Insets(0, 0, 5, 0);
		gbc_txtMaxPrice.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMaxPrice.gridx = 1;
		gbc_txtMaxPrice.gridy = 2;
		contentPane.add(txtMaxPrice, gbc_txtMaxPrice);
		txtMaxPrice.setColumns(10);
		
		chkMatch = new JCheckBox("Match Item Name");
		GridBagConstraints gbc_chkMatch = new GridBagConstraints();
		gbc_chkMatch.insets = new Insets(0, 0, 5, 0);
		gbc_chkMatch.anchor = GridBagConstraints.WEST;
		gbc_chkMatch.gridx = 1;
		gbc_chkMatch.gridy = 3;
		contentPane.add(chkMatch, gbc_chkMatch);
		
		JButton btnNewButton = new JButton("Add Item");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtMaxPrice.getText().length() < 1 || txtItemName.getText().isEmpty())
					return;
				
				searchItems.add(new SearchItem(txtItemName.getText(), txtSearchTerms.getText().split(","), Integer.parseInt(txtMaxPrice.getText()), chkMatch.isSelected()));
				model.addItem(new Object[]{txtItemName.getText(), txtSearchTerms.getText(), Integer.parseInt(txtMaxPrice.getText()), Boolean.toString(chkMatch.isSelected())});
				bot.writeItemsFile(bot.account.getUsername());
				dispose();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.EAST;
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 4;
		contentPane.add(btnNewButton, gbc_btnNewButton);
	}

}
