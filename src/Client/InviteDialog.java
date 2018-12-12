package Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

//룸 채팅방에서 초대버튼 클릭시에 실행 

public class InviteDialog extends JDialog implements ActionListener 
{
	private ClientHandler ch;
	private JList<String> list;
	private JButton okBtn;
	private JButton ccBtn;

	private void init() 
	{
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		list = new JList<String>();
		scrollPane.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okBtn = new JButton("보내기");
		buttonPane.add(okBtn);
		getRootPane().setDefaultButton(okBtn);
		ccBtn = new JButton("취소");
		buttonPane.add(ccBtn);
	}

	private void start()

	{
		okBtn.addActionListener(this);
		ccBtn.addActionListener(this);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {
					String nick = (String) list.getSelectedValue();
					if (nick == null)
						return;
					ch.invite(nick);

				}

			}
		});
	}

	public InviteDialog(MainFrame frame, String title, boolean modal) {
		super(frame, title, modal);
		init();
		start();
		ch = ClientHandler.getInstance();
		Point location = ch.getFrame().getLocationOnScreen();
		setBounds(location.x, location.y, 234, 370);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		//초대버튼 누르고 확인 클릭시 발생 
		if (src == okBtn) {
			String nick = (String) list.getSelectedValue();
			if (nick == null) {
				JOptionPane.showMessageDialog(this, "보낼 사람을 선택하세요");
				return;
			}
			ch.invite(nick);
			dispose();
		} else if (src == ccBtn) {
			dispose();
		}
	}

	public void setListData(String msg) {
		String tmp[] = msg.split(",");
		list.setListData(tmp);
	}
}
