package siggui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import siggui.api.ISigGuiView;
import siggui.utility.SwingFuns;

class SigGuiView {

	SigGuiView(SigGuiController controller, ISigGuiView[] views) {
		this.controller = controller;
		SwingFuns.hideTabs(tabbedPane);
		for (ISigGuiView v : views) {
			tabbedPane.addTab(v.getTitle(), v.toComponent());
		}
		frame.setMinimumSize(new Dimension(400, 200));
		frame.setJMenuBar(makeFileMenuBar(views));
		frame.add(tabbedPane);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFile(File file) {
		frame.setTitle(controller.getAppName() + " - " + file.getName());
		frame.getJMenuBar().getMenu(0).getItem(1).setEnabled(true);
	}

	public void showView(int index) {
		frame.getJMenuBar().getMenu(1).getItem(index).setSelected(true);
		tabbedPane.setSelectedIndex(index);
	}

	private JMenuBar makeFileMenuBar(ISigGuiView[] views) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');
		{
			JMenuItem item = new JMenuItem("Open", KeyEvent.VK_O);
			item.addActionListener(openMenuItemActionListener);
			fileMenu.add(item);
		}
		{
			JMenuItem item = new JMenuItem("Properties", KeyEvent.VK_P);
			item.setEnabled(false);
			item.addActionListener(filePropertiesMenuItemActionListener);
			fileMenu.add(item);
		}
		{
			JMenuItem item = new JMenuItem("Exit", KeyEvent.VK_X);
			item.addActionListener(exitMenuItemActionListener);
			fileMenu.add(item);
		}

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('v');
		{
			ButtonGroup group = new ButtonGroup();
			for (int i = 0; i != views.length; ++i) {
				JMenuItem item = new JRadioButtonMenuItem(views[i].getTitle());
				item.addActionListener(viewMenuItemActionListener);
				group.add(item);
				viewMenu.add(item);
			}
		}

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');
		{
			JMenuItem item = new JMenuItem("About", KeyEvent.VK_A);
			item.addActionListener(aboutMenuItemActionListener);
			helpMenu.add(item);
		}

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}

	private final ActionListener exitMenuItemActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			controller.notifyExitActionPerformed();
		}
	};
	private final ActionListener openMenuItemActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			controller.notifyOpenFileActionPerformed();
		}
	};
	private final ActionListener aboutMenuItemActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			controller.notifyAboutActionPerformed();
		}
	};
	private final ActionListener filePropertiesMenuItemActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			controller.notifyFilePropertiesActionPerformed();
		}
	};
	private final ActionListener viewMenuItemActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			final JMenu viewMenu = frame.getJMenuBar().getMenu(1);
			final int viewCount = viewMenu.getItemCount();
			int i = 0;
			for (; i != viewCount; ++i) {
				JRadioButtonMenuItem item = (JRadioButtonMenuItem) viewMenu.getItem(i);
				if (item.isSelected()) {
					break;
				}
			}
			controller.showView(i);
		}
	};

	private final SigGuiController controller;
	private final JFrame frame = new JFrame();
	private final JTabbedPane tabbedPane = new JTabbedPane();
}
