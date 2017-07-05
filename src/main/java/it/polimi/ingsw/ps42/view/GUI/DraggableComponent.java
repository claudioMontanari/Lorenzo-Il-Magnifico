package it.polimi.ingsw.ps42.view.GUI;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.view.TableInterface;

public class DraggableComponent extends FunctionalLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3784924483053777524L;

	private FamiliarColor familiarColor;
	private volatile int screenX;
	private volatile int screenY;
	private volatile int myX;
	private volatile int myY;
	private BufferedImage image;
	private boolean canMove;
	
	private int initialX;
	private int initialY;
	
	private TableInterface table;
	
	private class FamiliarPressedListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			//Nothing to do
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			//Nothing to do
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			//Nothing to do
			
		}

		@Override
		public void mousePressed(MouseEvent event) {
			screenX = event.getXOnScreen();
			screenY = event.getYOnScreen();
			myX = getX();
			myY = getY();
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			if(event.getSource() instanceof DraggableComponent){
				DraggableComponent source = (DraggableComponent) event.getSource();
				if( !canMove ){
					setLocation(initialX, initialY);
				}
				else{
					if(table.handleEvent(event.getXOnScreen(), event.getYOnScreen(), source)){
						setIcon(null);
					}
					else 
						setLocation(initialX, initialY);
				}
			}
		}

	
		
	}
	
	private class FamiliarDraggedListener implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent event) {
			int deltaX = event.getXOnScreen() - screenX;
			int deltaY = event.getYOnScreen() - screenY;
			
			setLocation(myX + deltaX, myY + deltaY);
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			//Nothing to do
			
		}
		
	}
	
	public DraggableComponent(int x, int y, Dimension dimension, BufferedImage image, FamiliarColor color) {
		super();
		this.familiarColor= color;
		dimension = new Dimension((int)(dimension.getWidth()*0.06), (int)(dimension.getHeight()*0.05)); 
		setLocation(x, y);
		setSize(dimension);
		myX = x;
		myY = y;
		screenX = x;
		screenY = y;
		initialX = x;
		initialY = y;
		canMove = false;
		this.image = image;
		this.setIcon(resizeImage(image, this.getSize()));
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void enableListener(){
		this.addMouseListener(new FamiliarPressedListener());
		this.addMouseMotionListener(new FamiliarDraggedListener());
	}
	
	public void setCanMove(boolean state){
		this.canMove = state;
	}
	
	public void setTable(TableInterface table) {
		this.table = table;
	}
	
	public void resetFamiliar(){
		setLocation(initialX, initialY);
		setIcon(resizeImage(image, this.getSize()));
	}
	
	public FamiliarColor getFamiliarColor() {
		return familiarColor;
	}
	
}
