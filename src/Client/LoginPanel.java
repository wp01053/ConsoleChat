package Client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *  맨 처음 실행되는 로그인 창. Frame 은 ClientHandler 의 
 *  멤버변수인 MainFrmae을 사용하며 MainFrame 의 
 *  setPanel 을 이용해서 JPanel 을 상속받은 ChatRoom, WiatRoom
 *  LoginPanel 들이 번갈아가며 frame에 얹힌다.
 */
public class LoginPanel extends JPanel implements ActionListener {
	private JTextField idField;
	private JPasswordField pwdField;
	private JButton btnLogin;
	private JButton btnClose;
	private ClientHandler ch;

	private void init() {
		setLayout(null);

		JLabel lblId = new JLabel("아이디");
		lblId.setBounds(12, 103, 40, 15);
		add(lblId);

		JLabel lblPwd = new JLabel("암호");
		lblPwd.setBounds(12, 153, 40, 15);
		add(lblPwd);

		idField = new JTextField();
		idField.setBounds(64, 100, 116, 21);
		add(idField);
		idField.setColumns(10);

		pwdField = new JPasswordField();
		pwdField.setBounds(64, 150, 116, 21);
		add(pwdField);
		pwdField.setColumns(10);

		btnLogin = new JButton("로그인");
		btnLogin.setBounds(12, 250, 80, 37);
		add(btnLogin);

		btnClose = new JButton("취소");
		btnClose.setBounds(100, 250, 80, 37);
		add(btnClose);

	}

	private void start() {
		pwdField.addActionListener(this);
		btnLogin.addActionListener(this);
		btnClose.addActionListener(this);
	}

	public LoginPanel() {
		init();
		start();
		ch = ClientHandler.getInstance();
	}

	/***********************
	 * 버튼에 ActionListener 객체를 등록합니다.
	 * 그리고 actionPerformed() 함수가 실행될 때 텍스트 값들을 가져오게
	 * 되는데 getText() 를 이용해 입력된 값을 추출합니다.
	 ***********************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		/*****************아이디 .getText()로 저장된데이터 불러와서 저장**************/
		String id = idField.getText();
		if (src == pwdField || src == btnLogin) {
			if (idField.getText().length() == 0) {
				JOptionPane.showMessageDialog(ch.getFrame(), "아이디를 입력하세요");
			}

			else if (pwdField.getPassword().length == 0) {

				JOptionPane.showMessageDialog(ch.getFrame(), "암호를 입력하세요");
			} else {
				ch.requestLogin(idField.getText() + "," + String.valueOf(pwdField.getPassword()));
			}
		} else if (src == btnClose) {
			System.exit(0);
		}
		/******************************블랙리스트************************************/
		if (id.equals("Black")) {
			JOptionPane.showMessageDialog(ch.getFrame(), "블랙리스트 사용자입니다.!!");
			System.out.println("블랙리스트 사용자입니다 : " + id);
			System.exit(0);
		}

	}

	public void setId(String id) {
		idField.setText(id);

	}

	public void focusIdField() {
		idField.requestFocus();
		idField.selectAll();

	}

	public void focusPwdField() {
		pwdField.requestFocus();
	}

	public void setPwdField(String string) {
		pwdField.setText(string);
	}

}
