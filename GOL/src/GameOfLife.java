import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GameOfLife extends JFrame {

	private GOLField[][] fields;
	private JPanel mainArray;
	Thread GOLThread;
	private boolean mouseclicked;

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
	private JButton clearBtn = new JButton("Clear");
	private ActionListener clearAllCells = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			clearAll();
		}
	};

	private int GOLxDim;
	private int GOLyDim;
	
	private int speed = 100;

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
		
		clearBtn.addActionListener(clearAllCells);
		
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new GridLayout(2,1));
		
		actionPanel.add(pauseBtn);
		actionPanel.add(clearBtn);
		
		this.add(actionPanel, BorderLayout.SOUTH);

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

			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			this.setBackground(Color.WHITE);
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
				this.setBackground(Color.ORANGE);
			} else {
				this.setBackground(Color.WHITE);
			}
		}

		public void writeOldState() {
			oldState = state;
		}
	}

	private class ToggleClicked implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (e.getComponent().getClass() == (new GOLField(0, 0)).getClass()) {
				if	(mouseclicked) {
					if (((GOLField) e.getComponent()).oldState) {
						((GOLField) e.getComponent()).oldState = false;
						((GOLField) e.getComponent()).state = false;
						((GOLField) e.getComponent()).setBackground(Color.LIGHT_GRAY);
					} else {
						((GOLField) e.getComponent()).oldState = true;
						((GOLField) e.getComponent()).state = true;
						((GOLField) e.getComponent()).setBackground(Color.PINK);
					}
				}
			} else {
				JOptionPane.showMessageDialog(mainArray, "Component selected which was not a GOLField");
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseclicked = true;
			if (e.getComponent().getClass() == (new GOLField(0, 0)).getClass()) {
				if (((GOLField) e.getComponent()).oldState) {
					((GOLField) e.getComponent()).oldState = false;
					((GOLField) e.getComponent()).state = false;
					((GOLField) e.getComponent()).setBackground(Color.LIGHT_GRAY);
				} else {
					((GOLField) e.getComponent()).oldState = true;
					((GOLField) e.getComponent()).state = true;
					((GOLField) e.getComponent()).setBackground(Color.PINK);
				}
			} else {
				JOptionPane.showMessageDialog(mainArray, "Component selected which was not a GOLField");
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mouseclicked = false;
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
	
	public void clearAll() {
		for (int j = 0; j < GOLyDim; j++) {
			for (int i = 0; i < GOLxDim; i++) {
				fields[i][j].state = false;
				fields[i][j].oldState = false;
				fields[i][j].setBackground(Color.WHITE);
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
		GameOfLife main = new GameOfLife(100, 100);
	}

}