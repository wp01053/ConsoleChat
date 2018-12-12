package Client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * 모든 GUI(채팅방, 대기실, 로그인창) 들을 화면에 보여주기위해 필요한
 *  JFrame setPanel 메소드를 통해 채팅방Panel,
 * 대기실Panel, 로그인Panel 을 번갈아가며 얹어서 화면에 출력한다.
 */
public class MainFrame extends JFrame implements ActionListener {
	private JMenuItem joinItem;
	private JMenuItem modItem;
	private JMenuItem logoutItem;
	private JMenuItem infoItem;
	private ClientHandler ch;

	public MainFrame() {
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu("Menu");
		JMenu menu2 = new JMenu("Game");
		menubar.add(menu1);
		menubar.add(menu2);
		joinItem = new JMenuItem("가입");
		modItem = new JMenuItem("수정");
		logoutItem = new JMenuItem("로그아웃");
		infoItem = new JMenuItem("숫자야구");
		menu1.add(joinItem);
		menu1.add(modItem);
		menu1.add(new JSeparator());
		menu1.add(logoutItem);
		menu2.add(infoItem);
		modItem.setEnabled(false);
		logoutItem.setEnabled(false);

		setJMenuBar(menubar);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		joinItem.addActionListener(this);
		modItem.addActionListener(this);
		logoutItem.addActionListener(this);
		infoItem.addActionListener(this);
	}

	public void setHandler(ClientHandler ch) {
		this.ch = ch;
	}

	public void setPanel(JPanel panel) {

		if (panel instanceof LoginPanel) {
			setSize(202, 376);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension size = getSize();
			setLocation(screenSize.width / 2 - size.width / 2, screenSize.height / 2 - size.height);
		} else if (panel instanceof WaitRoom) {
			setSize(595, 475);
		} else if (panel instanceof ChatRoom) {
			setSize(627, 493);
		}
		setContentPane(panel);
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == joinItem) {
			ch.openJoin();
		} else if (src == infoItem) {
			JOptionPane.showMessageDialog(this, "숫자야구 게임 시작");
		} else if (src == logoutItem) {
			ch.logout();
		} else if (src == modItem) {
			ch.openPwdCheck();
		}
	}

	public void setMenuItemEnabled(boolean flag) {
		joinItem.setEnabled(!flag);
		modItem.setEnabled(flag);
		logoutItem.setEnabled(flag);
	}
}
