package Client;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

/**
 * 
 * 대기실
 */
public class WaitRoom extends JPanel implements ActionListener {

	private JTextField textField;
	private JList<String> roomList;
	private JList<String> userList;
	private JButton makeBtn;
	private JButton enterBtn;
	private JTextArea textArea;
	private ClientHandler ch;
	private JButton exitBtn;


	public WaitRoom() {
		init();
		start();
		ch = ClientHandler.getInstance();
	}

	private void init() {
		setLayout(null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 565, 148);
		add(scrollPane);

		roomList = new JList<String>();
		scrollPane.setViewportView(roomList);
		roomList.setBorder(new TitledBorder("방 목록 : "));
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 168, 162, 210);
		add(scrollPane_1);

		userList = new JList<String>();
		scrollPane_1.setViewportView(userList);
		userList.setBorder(new TitledBorder("대기자 : "));
		userList.setEnabled(false);

		makeBtn = new JButton("방만들기");
		makeBtn.setBounds(12, 388, 75, 23);
		makeBtn.setMargin(new Insets(0, 0, 0, 0));
		add(makeBtn);

		enterBtn = new JButton("방 입장");
		enterBtn.setMargin(new Insets(0, 0, 0, 0));
		enterBtn.setBounds(99, 388, 75, 23);
		add(enterBtn);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(186, 168, 391, 210);
		add(scrollPane_2);

		textArea = new JTextArea();
		scrollPane_2.setViewportView(textArea);
		textArea.setBorder(new TitledBorder("전체 채팅방 : "));
		textArea.setEditable(false);

		textField = new JTextField();
		textField.setBounds(186, 389, 317, 21);
		add(textField);
		textField.setColumns(10);

		exitBtn = new JButton("나가기");
		exitBtn.setMargin(new Insets(0, 0, 0, 0));
		exitBtn.setBounds(515, 388, 59, 23);
		add(exitBtn);
	}

	private void start() {
		makeBtn.addActionListener(this);
		enterBtn.addActionListener(this);
		textField.addActionListener(this);
		roomList.addMouseListener(new MouseAdapter() {
			
			/*****************방 리스트************************/
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { //마우스 더블클릭 시에
					String roomName = roomList.getSelectedValue();
					if (roomName == null || roomName.isEmpty())
						return;
					roomName = roomName.substring(0, roomName.indexOf(":") - 1);
					ch.enterRoom(roomName);
				}
			}
		});
		exitBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object src = e.getSource();
		if (src == textField) {	
			String txt = textField.getText();
			if (txt.length() == 0)
				return;
			//대기방에서 채팅 보냈을 때 클라이언트 핸들러를 통해 아래 매세지 전송 
			ch.sendWaitChat(ch.getMember().getNick() + "\t : " + txt);
			textField.setText("");
		
			/****************대기방 금지어 작성중************************/
			if (txt.equals("자바채팅"))//대기방 금지어
			{
				JOptionPane.showMessageDialog(ch.getFrame(),"관리자 : "+ "금지어 입니다.");
				
			}
		} 
		/*****************방만들기 버튼 클릭 시*******************/
		else if (src == makeBtn) {
			
			String roomName = JOptionPane.showInputDialog("방 이름을 입력하세요");
			ch.makeRoom(roomName);
			/********방 입장 버튼 클릭 시************************/
		} else if (src == enterBtn) {
			String roomName = roomList.getSelectedValue();
			if (roomName == null) {
				JOptionPane.showMessageDialog(ch.getFrame(), "들어갈 방을 선택하세요");
			} else {
				roomName = roomName.substring(0, roomName.indexOf(":") - 1);
				ch.enterRoom(roomName);
			}
			
		} 
		/********************나가기 버튼 클릭시*************************/
		else if (src == exitBtn) {

			ch.exitWaitRoom();
		}
	}

	public void setUserList(String msg) {
		String users[] = msg.split(",");
		userList.setListData(users);
		userList.setBorder(new TitledBorder("대기자 : " + users.length + "명"));
	}

	public void setRoomList(String msg) {
		String rooms[] = msg.split(",");
		roomList.setListData(rooms);
		roomList.setBorder(new TitledBorder("방 list : " + (rooms[0].equals("") ? 0 : rooms.length) + "개"));
	}

	public void focusText() {
		textField.requestFocus();
	}

	public void appendMsg(String msg) {
		textArea.append(msg + "\n");
		
		textArea.setCaretPosition(textArea.getText().length());
		if (msg.equals("자바채팅")) {
			JOptionPane.showMessageDialog(ch.getFrame(), "금지어!!");
		}
	}

	public void setChatBorder(String msg) {
		textArea.setBorder(new TitledBorder(msg));
	}
}
