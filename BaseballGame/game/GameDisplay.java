package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import datatype.Coordinate3D;
import stadium.Stadium;
import stadium.Wall;

public class GameDisplay extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Stadium curStadium;
	public BufferedImage board;
	public JLabel frame = new JLabel (); 
	public int offset; //the amount of foul territory
	
	public GameDisplay (int x, int y, int offset, Stadium curStadium) {
		super("Display Test");
		setSize(600, 600);
		board = new BufferedImage (x,y,BufferedImage.TYPE_3BYTE_BGR);
		frame = new JLabel (new ImageIcon(board));
		this.add(frame);
		frame.setVisible(true);
		setVisible(true);
		this.offset = offset;
		this.curStadium = curStadium;
	}
	
	//draws the outfield walls and foul lines
	public void drawFieldOutline () {
		
		int ySubtract = board.getHeight(); //this is need to flip the image over the x-axis.  otherwise the image is drawn in the 4th quadrant, which is confusing
		int basePathDistance = 90;
		
		int offset = curStadium.dim.get("f");
		
		List <Wall> walls = curStadium.getWalls();
		Wall w1 = walls.get(0);
		Wall w2 = walls.get(1);
		Wall w3 = walls.get(2);
		Wall w4 = walls.get(3);
		
		Coordinate3D leftCorner = w1.getP1();
		Coordinate3D leftCenterCorner = w2.getP1();
		Coordinate3D centerCorner = w3.getP1();
		Coordinate3D rightCenterCorner = w4.getP1();
		Coordinate3D rightCorner = w4.getP2();
				
		Graphics2D leftFieldLine = board.createGraphics();
		leftFieldLine.setColor(Color.WHITE);
		leftFieldLine.draw(new Line2D.Double(offset, ySubtract-(offset), offset, ySubtract-(leftCorner.y+offset)));
		leftFieldLine.dispose();
		
		Graphics2D rightFieldLine = board.createGraphics();
		rightFieldLine.setColor(Color.WHITE);
		rightFieldLine.draw(new Line2D.Double(offset, ySubtract-(offset), offset+rightCorner.x, ySubtract-(offset)));
		rightFieldLine.dispose();
		
		Graphics2D secondBaseLine = board.createGraphics();
		secondBaseLine.setColor(Color.WHITE);
		secondBaseLine.draw(new Line2D.Double(offset+basePathDistance, ySubtract-(offset), offset+basePathDistance, ySubtract-(offset+basePathDistance)));
		secondBaseLine.dispose();
		
		Graphics2D thirdBaseLine = board.createGraphics();
		thirdBaseLine.setColor(Color.WHITE);
		thirdBaseLine.draw(new Line2D.Double(offset+basePathDistance, ySubtract-(offset+basePathDistance), offset, ySubtract-(offset+basePathDistance)));
		thirdBaseLine.dispose();
		
		Graphics2D leftToLeftCenter = board.createGraphics();
		leftToLeftCenter.setColor(Color.WHITE);
		leftToLeftCenter.draw(new Line2D.Double(leftCorner.x+offset,ySubtract-(leftCorner.y+offset), leftCenterCorner.x+offset, ySubtract-(leftCenterCorner.y+offset)));
		leftToLeftCenter.dispose();
		
		Graphics2D leftCenterToCenter = board.createGraphics();
		leftCenterToCenter.setColor(Color.WHITE);
		leftCenterToCenter.draw(new Line2D.Double(leftCenterCorner.x+offset, ySubtract-(leftCenterCorner.y+offset), centerCorner.x+offset, ySubtract-(centerCorner.y+offset)));
		leftCenterToCenter.dispose();
		
		Graphics2D centerToRightCenter = board.createGraphics();
		centerToRightCenter.setColor(Color.WHITE);
		centerToRightCenter.draw(new Line2D.Double(centerCorner.x+offset, ySubtract-(centerCorner.y+offset), rightCenterCorner.x+offset, ySubtract-(rightCenterCorner.y+offset)));
		centerToRightCenter.dispose();
		
		Graphics2D rightCenterToRight = board.createGraphics();
		rightCenterToRight.setColor(Color.WHITE);
		rightCenterToRight.draw(new Line2D.Double(rightCenterCorner.x+offset, ySubtract-(rightCenterCorner.y+offset), rightCorner.x+offset, ySubtract-(rightCorner.y+offset)));
		rightCenterToRight.dispose();
		
	}
	
	//test
	public void draw (int x, int y, int color) {
	
		
		for (int i = x-1; i <= x+1; i++) {

			for (int j = y-1; j <= y+1; j++) {
				board.setRGB(i,j, color);
			}

		}
	}
	/*
	public void drawField (int foul, Stadium stad) {
		
		for (int i = -1*stad.dim.get("f") + 1; i < stad.field.field.length - stad.dim.get("f"); i++) {
			
			for (int j = -1*stad.dim.get("f") + 1; j < stad.field.field[0].length - stad.dim.get("f"); j++) {
				
				if (stad.field.get(i, j) == SectorT.HR) {
					setCoor(i, j, 16777215);
				}
				
				else if (stad.field.get(i, j) == SectorT.FOUL) {
					setCoor(i, j, 11673118);
				}
				
			}
			
		}
		
		
	}
	*/
	public void setCoor (int x, int y, int color) {
		
		//System.out.println(board.getHeight()-(y+offset));
		board.setRGB(x+offset, board.getHeight()-(y+offset), color);
	
	}
	
	public void removeSpot (Coordinate3D hitBall, int size) {
		
		int i = (int)hitBall.x+offset;
		int j = (int)hitBall.y+offset;
		
		if (i-size < 0 || i+size > board.getHeight() || j-size < 0 || j+size > board.getWidth()) {
			return;
		}
		
		for (int x = i-size; x <= i+size; x++) {

			for (int y = j-size; y <= j+size; y++) {
				//System.out.println(board.getHeight()-(y+offset));
				board.setRGB(x, board.getHeight()-(y), 0x000000);
			}

		}
		
	}
	
	public void drawBall (Coordinate3D hitBall, int color, int size) {
		
		int i = (int)hitBall.x+offset;
		int j = (int)hitBall.y+offset;
		
		if (i-size < 0 || i+size > 450 || j-size < 0 || j+size > 450) {
			return;
		}
		
		for (int x = i-size; x <= i+size; x++) {

			for (int y = j-size; y <= j+size; y++) {
				//System.out.println(board.getHeight()-(y+offset));
				board.setRGB(x, board.getHeight()-(y), color);
			}

		}
		
	}
	
}
