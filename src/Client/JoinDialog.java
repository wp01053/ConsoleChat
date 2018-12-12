package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * - 로그인 화면에서 좌측 상단 메뉴의 가입을 누르면 실행
 * - 아이디는 한글은 입력이 안된다. -
 * - 이름과 닉네임은 10자까지만 입력이 된다.
 * - 맨 처음 이름을 입력시 닉네임이 자동으로 입력이 된다. 
 * - 전화번호는 숫자만 입력되며 각 중간번호와 끝번호는 각각 4자까지만 입력된다. - 아이디와 닉네임을 중복확인을 하지 않으면 가입이 안된다.
 * - 각각 ui의 변수를 가지고 실행한다. 	
 */


public class JoinDialog extends JDialog implements ActionListener {
	private JTextField idField;
	private JPasswordField pwdField1;
	private JPasswordField pwdField2;
	private JTextField nameField;
	private JTextField nickField;
	private JComboBox<String> comboBox;
	private JTextField phoneField2;
	private JTextField phoneField3;
	private JButton checkIdBtn;
	private JLabel pwdCheckLabel1;
	private JLabel pwdCheckLabel2;
	private JButton okBtn;
	private JButton ccBtn;
	private JButton checkNickBtn;
	protected boolean idChecked;
	private boolean nickChecked;
	private ClientHandler ch;
	private int mode;

	public JoinDialog(JFrame frame, String title, boolean isModal, int mode) {
		super(frame, title, isModal);
		ch = ClientHandler.getInstance();
		this.mode = mode;
		//init()메소드 부터 실행 
		init();
		start1();
		if (mode == Message.MODE_MODIFY)
			//수정
			initModify();
		start2();

	}

	private void initModify() {
		idChecked = true;
		nickChecked = true;
		idField.setEnabled(false);
		checkIdBtn.setEnabled(false);
		checkNickBtn.setEnabled(false);

		Member m = ch.getMember();
		idField.setText(m.getId());
		pwdField1.setText(m.getPwd());
		pwdField2.setText(m.getPwd());
		nameField.setText(m.getName());
		nickField.setText(m.getNick());
		String phone[] = m.getPhone().split("-");
		comboBox.setSelectedItem(phone[0]);
		phoneField2.setText(phone[1]);
		phoneField3.setText(phone[2]);
	}
/*************************join gui*************************/
	private void init() {
		setSize(367, 232);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = getSize();
		setLocation(screenSize.width / 2 - size.width / 2, screenSize.height / 2 - size.height / 2);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel idLabel = new JLabel("아이디");
		idLabel.setBounds(12, 10, 53, 15);
		contentPanel.add(idLabel);
		JLabel pwdLabel = new JLabel("암호");
		pwdLabel.setBounds(12, 35, 53, 15);
		contentPanel.add(pwdLabel);
		JLabel nameLabel = new JLabel("이름");
		nameLabel.setBounds(12, 85, 53, 15);
		contentPanel.add(nameLabel);
		JLabel nickLabel = new JLabel("닉네임");
		nickLabel.setBounds(12, 110, 53, 15);
		contentPanel.add(nickLabel);
		JLabel phoneLabel = new JLabel("전화번호");
		phoneLabel.setBounds(12, 135, 53, 15);
		contentPanel.add(phoneLabel);

		idField = new JTextField();
		idField.setBounds(70, 7, 184, 21);
		contentPanel.add(idField);
		idField.setColumns(10);

		pwdField1 = new JPasswordField();
		pwdField1.setBounds(70, 32, 184, 21);
		pwdField1.setColumns(10);
		contentPanel.add(pwdField1);

		pwdField2 = new JPasswordField();
		pwdField2.setBounds(70, 57, 184, 21);
		pwdField2.setColumns(10);
		contentPanel.add(pwdField2);

		nameField = new JTextField();
		nameField.setBounds(70, 82, 184, 21);
		nameField.setColumns(10);
		contentPanel.add(nameField);

		nickField = new JTextField("");
		nickField.setColumns(10);
		nickField.setBounds(70, 107, 184, 21);
		contentPanel.add(nickField);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(70, 132, 54, 21);
		contentPanel.add(comboBox);
		comboBox.addItem("010");
		comboBox.addItem("011");
		comboBox.addItem("016");

		phoneField2 = new JTextField();
		phoneField2.setBounds(136, 132, 53, 21);
		phoneField2.setColumns(10);
		contentPanel.add(phoneField2);

		phoneField3 = new JTextField();
		phoneField3.setColumns(10);
		phoneField3.setBounds(201, 132, 53, 21);
		contentPanel.add(phoneField3);

		checkIdBtn = new JButton("중복 확인");
		checkIdBtn.setMargin(new Insets(0, 0, 0, 0));
		checkIdBtn.setBounds(266, 6, 76, 23);
		contentPanel.add(checkIdBtn);

		checkNickBtn = new JButton("중복 확인");
		checkNickBtn.setMargin(new Insets(0, 0, 0, 0));
		checkNickBtn.setBounds(266, 106, 76, 23);
		contentPanel.add(checkNickBtn);

		JLabel label = new JLabel("암호확인");
		label.setBounds(12, 60, 53, 15);
		contentPanel.add(label);

		pwdCheckLabel1 = new JLabel("암호 : 4~16자");
		pwdCheckLabel1.setBounds(266, 35, 76, 15);
		contentPanel.add(pwdCheckLabel1);

		pwdCheckLabel2 = new JLabel();
		pwdCheckLabel2.setBounds(266, 60, 76, 15);
		contentPanel.add(pwdCheckLabel2);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		okBtn = new JButton("확인");
		buttonPane.add(okBtn);
		getRootPane().setDefaultButton(okBtn);
		ccBtn = new JButton("취소");
		buttonPane.add(ccBtn);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

	private void start1() {
		phoneField2.setDocument(new PhoneDocument());
		phoneField3.setDocument(new PhoneDocument());
		nameField.setDocument(new NameDocument());
	}

	private void start2() {
		okBtn.addActionListener(this);
		ccBtn.addActionListener(this);

		idField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				idChecked = false;
			}
		});

		checkIdBtn.addActionListener(this);
		checkNickBtn.addActionListener(this);
		PwdListener pwdL = new PwdListener();
		pwdField1.addKeyListener(pwdL);
		pwdField2.addKeyListener(pwdL);

		final NameNickListener nnl = new NameNickListener();
		nameField.addKeyListener(nnl);
		nickField.addKeyListener(nnl);
		nameField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				nameField.removeKeyListener(nnl);
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		String pwd1 = String.valueOf(pwdField1.getPassword());
		String pwd2 = String.valueOf(pwdField2.getPassword());
		if (src == okBtn) {
			if (!idChecked) {
				JOptionPane.showMessageDialog(this, "아이디 중복확인을 하세요");
				idField.requestFocus();
			} else if (pwd1.length() < 4 || pwd1.length() > 16) {
				JOptionPane.showMessageDialog(this, "암호는 4자이상 16자 이하");
				pwdField1.requestFocus();
			} else if (!pwd2.equals(pwd1)) {
				JOptionPane.showMessageDialog(this, "암호가 일치하지 않습니다");
				pwdField2.requestFocus();
			} else if (nameField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "이름을 입력하세요");
				nameField.requestFocus();
			} else if (!nickChecked) {
				JOptionPane.showMessageDialog(this, "닉네임 중복확인을 하세요");
				nickField.requestFocus();
			} else if (phoneField2.getText().length() < 3) {
				JOptionPane.showMessageDialog(this, "전화번호를 제대로 입력하세요");
				phoneField2.requestFocus();
			} else if (phoneField3.getText().length() < 4) {
				JOptionPane.showMessageDialog(this, "전화번호를 제대로 입력하세요");
				phoneField3.requestFocus();
			} else {
				String phone = comboBox.getSelectedItem() + "-" + phoneField2.getText() + "-" + phoneField3.getText();
				String msg = idField.getText() + "," + String.valueOf(pwdField1.getPassword()) + ","
						+ nameField.getText() + "," + nickField.getText() + "," + phone;
				switch (mode) {
				case Message.MODE_JOIN:
					ch.requestJoin(msg);
					break;
				case Message.MODE_MODIFY:
					ch.requestModify(msg);
					break;
				}

			}
		} else if (src == ccBtn) {
			dispose();
		} else if (src == checkIdBtn) {
			if (idField.getText().length() < 4) {
				JOptionPane.showMessageDialog(this, "아이디를 4자 이상 입력하세요");
				idField.requestFocus();
			} else {
				ch.requestCheckId(idField.getText());
			}
		} else if (src == checkNickBtn) {
			if (nickField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "닉네임을 입력하세요");
				nickField.requestFocus();
			} else {
				ch.requestCheckNick(nickField.getText(), Message.LOGIN_CHECK);
			}
		}
	}

	public void setIdChecked(boolean idChecked) {
		this.idChecked = idChecked;
	}

	public void setNickChecked(boolean nickChecked) {
		this.nickChecked = nickChecked;
	}

	class NameNickListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) 
		{
			Object src = e.getSource();
			if (src == nameField && mode == Message.MODE_JOIN) { // 가입 모드일 경우
				String name = nameField.getText();
				nickField.setText(name);
			} else if (src == nickField) {
				String nick = nickField.getText();
				if (nick.length() > 10) { // nickField에 10자까지만 입력가능
					e.consume();
				} else {
					if (mode == Message.MODE_MODIFY) { // 수정 모드일 경우
						checkNickBtn.setEnabled(true);
						
					}
				}
			}
		}
	}

	public String getId() {
		return idField.getText();
	}

	class PwdListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			String pwd1 = String.valueOf(pwdField1.getPassword());
			String pwd2 = String.valueOf(pwdField2.getPassword());
			if (!pwd1.equals(pwd2)) {
				pwdCheckLabel2.setText("암호 불일치");
			} else {
				pwdCheckLabel2.setText("");
			}
			if (e.getSource() == pwdField1) {
				if (pwd1.length() < 4 || pwd1.length() > 16) {
					pwdCheckLabel1.setText("암호 : 4~16자");
				} else {
					pwdCheckLabel1.setText("");
				}
			}
		}
	}
/***************************id,phone,name check****************************/
	class NameDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (getLength() + str.length() <= 10) // nameField 에 10자까지만 입력가능
				super.insertString(offs, str, a);
		}
	}

	class PhoneDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			char ch = str.charAt(0);
			if (!Character.isDigit(ch)) // 숫자가 아닐 경우
				return; 
			if (getLength() + str.length() <= 4) // phoneField2, 3 에 4자까지만 입력가능
				super.insertString(offs, str, a);
		}
	}

	class IdDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			char ch = str.charAt(0);
			if (Character.getType(ch) == 5) // 한글일 경우
				return;
			if (getLength() + str.length() <= 16) // idField 에 16자까지만 입력가능
				super.insertString(offs, str, a);
		}
	}
/***************************id,phone,name check****************************/
}
