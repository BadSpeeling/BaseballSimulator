package ui;

import java.awt.Container;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import datatype.Coordinate3D;
import game.Game;
import messages.DebugMessage;

public class BasicBoard {

	private BufferedImage data;
	private JLabel display;
	private int foulDistance;
	
	public BasicBoard (int width, int height, int foulDistance) {

		data = new BufferedImage (width, height, BufferedImage.TYPE_3BYTE_BGR);		
		display = new JLabel(new ImageIcon (data));
		display.setSize(width, height);
		display.setVisible(true);
		this.foulDistance = foulDistance;
		
	}

	/*
	public void addComponents (Container main) {
		main.add(display);
		display.setVisible(true);
	}
	*/
	
	public JLabel getDisplay () {
		return display;
	}

	public void drawObject (Coordinate3D hitBall, int color, int size) {

		int i = (int)hitBall.x+foulDistance;
		int j = (int)hitBall.y+foulDistance;
				
		if (i-size-1 < 0 || i+size+1 >= data.getWidth() || j-size-1 < 0 || j+size+1 >= data.getHeight()) {
			return;
		}

		for (int x = i-size; x <= i+size; x++) {

			for (int y = j-size; y <= j+size; y++) {
				data.setRGB(x, data.getHeight()-(y), color);
			}

		}

	}
	
	//should be called when theimage is ready to be updated
	public void redraw () {
		display.repaint();
	}
	
	public BufferedImage getData () {
		return data;
	}
	
	public void clearObject (Coordinate3D loc, int size) {
		
		int i = (int)loc.x+foulDistance;
		int j = (int)loc.y+foulDistance;

		if (i-size-1 < 0 || i+size+1 >= data.getWidth() || j-size-1 < 0 || j+size+1 >= data.getHeight()) {
			return;
		}

		for (int x = i-size; x <= i+size; x++) {

			for (int y = j-size; y <= j+size; y++) {
				data.setRGB(x, data.getHeight()-(y), 0x000000);
			}

		}
		
	}

}
