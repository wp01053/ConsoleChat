package Client;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * 채팅방 GUI 를 담당
 */
public class ChatRoom extends JPanel implements ActionListener {
	private JList<String> userList;
	private JTextArea textArea;
	private JTextField textField;
	private JTextField textField2;
	private JButton inviteBtn;
	private JButton exitBtn;
	private JButton banBtn;

	String[] word = new String[5];
	int pos = 0;
	Exception ex = new Exception();
	boolean flag = true;

	private ClientHandler ch;
	private String userBorder = "참가자 : ";
	private JPopupMenu popup;
	private JMenuItem whisperMenu;
	private JMenuItem kickOffMenu;

	public ChatRoom() 
	{
		init();
		start();
		ch = ClientHandler.getInstance();
		// getInstance() 사용 이유 =
		// new()는 객체를 계속 만들수 있고,
		// getInstance()는 싱글턴패턴으로 하나의 인스턴스만 가지고 공유해서 사용한다.
		// 싱글턴 패턴이란 .. 생성자를 private로 선언하여
		// 다른 클래스에서 해당 클래스의 인스턴스를 new로 생성하지 못하게 하고,
		// getInstance()함수를 통해서 인스턴스를 갖도록 한다.
		// 매번 새로운 객체를 생성하는 것보다 한번 새로운 객체를 생성하고
		// 그 후에 모든 클래스들이 동일한 객체를 써야하는 경우에 getinstance()메소드를 사용한다.
	}

	private void init() 
	{
		setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 138, 380);
		add(scrollPane);

		userList = new JList<String>();
		scrollPane.setViewportView(userList);
		userList.setBorder(new TitledBorder(userBorder));
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(162, 10, 446, 380);
		add(scrollPane_1);

		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		scrollPane_1.setBounds(160, 10, 446, 330);
		/****************** 위치 잡는 팁 ***********************/
		// (x축 ,정해놓은 위치에서 y축기준 내려감
		// tip. 양옆의 다른 ui랑 맞추면 똑같이 위치 조절가능
		// , 전체적인x의 길이
		// , 전체 x의 높이를 기준으로 밑의 y의 길이를 조절 )

		textArea.setEditable(false);
		// 금지어 등록 필드
		textField2 = new JTextField();
		textField2.setBounds(162, 360, 362, 25);
		add(textField2);
		textField2.setColumns(10);

		textField = new JTextField();
		textField.setBounds(162, 400, 362, 25);
		add(textField);
		textField.setColumns(10);

		inviteBtn = new JButton("초대");
		inviteBtn.setMargin(new Insets(0, 0, 0, 0));
		inviteBtn.setBounds(96, 400, 54, 31);
		add(inviteBtn);
		inviteBtn.setBackground(Color.BLACK); // 색상넣어주기
		inviteBtn.setForeground(Color.WHITE); // 글자색

		banBtn = new JButton("금지어");
		banBtn.setMargin(new Insets(0, 0, 0, 0));
		banBtn.setBounds(536, 360, 72, 31);
		add(banBtn);

		exitBtn = new JButton("나가기");
		exitBtn.setMargin(new Insets(0, 0, 0, 0));
		exitBtn.setBounds(536, 400, 72, 31);
		add(exitBtn);

		popup = new JPopupMenu();
		whisperMenu = new JMenuItem("귓속말");
		popup.add(whisperMenu);
		userList.add(popup);
		kickOffMenu = new JMenuItem("강퇴");
	}

	private void start()
	{
		textField.addActionListener(this);
		inviteBtn.addActionListener(this);
		exitBtn.addActionListener(this);
		whisperMenu.addActionListener(this);
		kickOffMenu.addActionListener(this);
		banBtn.addActionListener(this);
		userList.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getModifiers() == MouseEvent.BUTTON3_MASK && userList.getSelectedIndex() != -1) {
					popup.show(userList, e.getX(), e.getY());
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		String text = textField.getText();

		if (src == exitBtn) 
		{
			ch.exitChatRoom();
		}
		/****************** 방 사람들이 정하는 금칙어 *********************/
		if (src == banBtn) 
		{
			try 
			{
				// 입력창에 뭐든지 들어갔을 경우
				if (!textField2.getText().equals("")) 
				{
					for (int i = 0; i < word.length; i++) 
					{
						if (textField2.getText().equals(word[i])) 
						{

						}

					}
					if (flag) 
					{
						// 값이 배열과 다를 경우
						word[pos] = textField2.getText();
						System.out.println("금지어 등록 : " + word[pos]); // 입력값을 알기위해
						++pos;
						textArea.setText("금지어 등록되었습니다.\n");

					}
					flag = true;
				}
				// 입력창에 아무것도 들어가지 않을 경우.
				else 
				{
					throw ex;
				}
			} 
			catch (Exception e1) 
			{
				JOptionPane.showMessageDialog(null, "금칙어를 입력하세요!");
			}

		}

		else if (src == textField) 
		{
			if (text.isEmpty())
				return;

			if (text.startsWith("/w "))
			// startsWith()메소드는 문자열이 주어진 접두표현을
			// 가지고 시작하는지 확인 시작한다면 true를 반환,
			// 아니라면 false를 반환합니다.
			{
				text = text.substring(3);
				int index = text.indexOf(" ");

				if (index != -1) 
				{
					String nick = text.substring(0, index);
					String msg = text.substring(index + 1);
					if (msg.isEmpty())
						return;
					textField.setText("/w " + nick + " ");
					textField.setCaretPosition(textField.getText().length());
					ch.sendWhisper(nick, msg);

				}
			} 
			else 
			{
				ch.sendRoomChat(ch.getMember().getNick() + "\t- " + text);
				textField.setText("");

				/************************ 대화방금지어 작성중 *********************************/
				if (text.contains("자바채팅")) 
				{
					JOptionPane.showMessageDialog(ch.getFrame(), "관리자 : " + "금지어 입니다.");
					textField.setText("금칙어 사용 금지");
					// 금칙어를 사용하게 되면 대화창에 금칙어 사용금지라는
					// setText를 뿌려준다.
				}
				for (int i = 0; i < pos; i++) 
				{
					if (text.equals(word[i])) 
					{
						JOptionPane.showMessageDialog(ch.getFrame(), "사용자 : " + "금지어 입니다.");
						flag = false;
					}
				}
				/*****************************************************/
			}
		}

		else if (src == inviteBtn) 
		{
			ch.openInvite();
		} else if (src == whisperMenu)
		{
			String nick = userList.getSelectedValue();
			
			if (nick.equals(ch.getMember().getNick())) 
			{
				JOptionPane.showMessageDialog(ch.getFrame(), "자신에게 보낼 수 없습니다");
			} 
			else 
			{
				textField.setText("/w " + userList.getSelectedValue() + " ");
				textField.setCaretPosition(textField.getText().length());
				textField.requestFocus();
			}
		} 
		else if (src == kickOffMenu) 
		{
			String nick = userList.getSelectedValue();
			
			if (nick.equals(ch.getMember().getNick())) 
			{
				JOptionPane.showMessageDialog(ch.getFrame(), "자신을 강퇴할 수 없습니다");
			} 
			else 
			{
				ch.kickOff(nick);
			}
		}
	}

	public void setUserList(String[] nicks) 
	{
		userList.setListData(nicks);
		userList.setBorder(new TitledBorder(userBorder + nicks.length + "명"));
	}

	public void appendMsg(String msg) 
	{
		textArea.append(msg + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void setChatBorder(String msg) 
	{
		textArea.setBorder(new TitledBorder(msg));
	}

	public void setKing()
	{
		popup.add(kickOffMenu);
	}

	public void focusText() 
	{
		textField.requestFocus();
	}
}
