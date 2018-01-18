import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GameOfLife extends JFrame {

	private GOLField[][] fields;
	private JPanel mainArray;
	Thread GOLThread;

	private ToggleClicked setSelected = new ToggleClicked();;

	private boolean pause = true;
	private JButton pauseBtn = new JButton("Play");
	private ActionListener pauseAction = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			pause = !pause;
			if(pause) {
				((JButton)e.getSource()).setText("Play");
			}else {
				((JButton)e.getSource()).setText("Pause");
			}
		}
	};

	private int GOLxDim;
	private int GOLyDim;
	
	private int speed = 10;

	public GameOfLife(int xDim, int yDim) {
		super("GameOfLife");
		
		GOLxDim = xDim;
		GOLyDim = yDim;

		JPanel mainArray = new JPanel(new GridLayout(GOLyDim, GOLxDim));

		fields = new GOLField[GOLxDim][];
		for (int i = 0; i < GOLxDim; i++) {
			fields[i] = new GOLField[GOLyDim];
			for (int j = 0; j < GOLyDim; j++) {
				fields[i][j] = new GOLField(i, j);
				fields[i][j].addMouseListener(setSelected);
			}
		}
		for (int j = 0; j < GOLyDim; j++) {
			for (int i = 0; i < GOLxDim; i++) {
				mainArray.add(fields[i][j]);
			}
		}

		updateFields();

		this.add(mainArray, BorderLayout.CENTER);
		
		pauseBtn.addActionListener(pauseAction);
		
		this.add(pauseBtn, BorderLayout.SOUTH);

		this.setVisible(true);
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		GOLThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!pause) {
						updateFields();
					}
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		start();
	}

	private class GOLField extends JPanel {
		public int GOLx;
		public int GOLy;

		public boolean state = false;
		public boolean oldState = false;

		public GOLField(int x, int y) {
			super();

			this.GOLx = x;
			this.GOLy = y;

			this.setBorder(BorderFactory.createLineBorder(Color.black));
			this.setBackground(Color.getHSBColor(0, 1, 1));
		}

		public void updateState() {
			int neighbours = getNeighbours(GOLx, GOLy);
			if (state) {
				if (neighbours < 2) {
					state = false;
				} else if (neighbours > 3) {
					state = false;
				}
			} else {
				if (neighbours == 3) {
					state = true;
				}
			}
			if (state) {
				this.setBackground(Color.getHSBColor((float) 0.5, 1, 1));
			} else {
				this.setBackground(Color.getHSBColor(0, 1, 1));
			}
		}

		public void writeOldState() {
			oldState = state;
		}
	}

	private class ToggleClicked implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getComponent().getClass() == (new GOLField(0, 0)).getClass()) {
				if (((GOLField) e.getComponent()).oldState) {
					((GOLField) e.getComponent()).oldState = false;
					((GOLField) e.getComponent()).state = false;
					((GOLField) e.getComponent()).setBackground(Color.getHSBColor((float) 0.1, 1, 1));
				} else {
					((GOLField) e.getComponent()).oldState = true;
					((GOLField) e.getComponent()).state = true;
					((GOLField) e.getComponent()).setBackground(Color.getHSBColor((float) 0.6, 1, 1));
				}
			} else {
				JOptionPane.showMessageDialog(mainArray, "Component selected which was not a GOLField");
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public void updateFields() {
		for (int j = 0; j < GOLyDim; j++) {
			for (int i = 0; i < GOLxDim; i++) {
				fields[i][j].updateState();
			}
		}
		this.writeOld();
	}

	public void writeOld() {
		for (int j = 0; j < GOLyDim; j++) {
			for (int i = 0; i < GOLxDim; i++) {
				fields[i][j].writeOldState();
			}
		}
	}

	private int getNeighbours(int x, int y) {
		int sum = 0;
		int testX;
		int testY;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0)) {
					testX = x + i;
					testX = (testX + GOLxDim) % GOLxDim;
					testY = y + j;
					testY = (testY + GOLyDim) % GOLyDim;
					if (fields[testX][testY].oldState) {
						sum++;
					}
				}
			}
		}
		return sum;
	}

	public void start() {
		GOLThread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		GOLThread.stop();
		dispose();
	}
	
	public static void main(String[] args) {
		GameOfLife main = new GameOfLife(30,30);
	}

}