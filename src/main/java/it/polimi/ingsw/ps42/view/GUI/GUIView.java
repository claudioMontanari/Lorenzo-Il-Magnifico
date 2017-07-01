package it.polimi.ingsw.ps42.view.GUI;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import it.polimi.ingsw.ps42.message.CardRequest;
import it.polimi.ingsw.ps42.message.PlayerMove;
import it.polimi.ingsw.ps42.message.PlayerToken;
import it.polimi.ingsw.ps42.message.leaderRequest.LeaderFamiliarRequest;
import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.model.action.ActionPrototype;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.exception.ElementNotFoundException;
import it.polimi.ingsw.ps42.model.leaderCard.LeaderCard;
import it.polimi.ingsw.ps42.model.player.BonusBar;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.parser.ImageLoader;
import it.polimi.ingsw.ps42.view.TableInterface;
import it.polimi.ingsw.ps42.view.View;
import it.polimi.ingsw.ps42.view.GUI.dialog.BonusBarRequestDialog;
import it.polimi.ingsw.ps42.view.GUI.dialog.CardRequestDialog;
import it.polimi.ingsw.ps42.view.GUI.dialog.LeaderCardChooseDialog;

public class GUIView extends View implements TableInterface{

	//The GUI main frame, where all the others Components are placed
	private JFrame mainFrame;
	private Dimension tableImageDimension;
	
	//The Label where the zoom of the Card is displayed
	private CardZoom cardZoom;
	
	//The Container for all the cards taken
	private CardContainer cardContainer;
	
	//The Image of the familiar currently moving
	private DraggableComponent movingFamiliar;
	
	//The List of CardLabels separated by Color
	private LinkedList<CardLabel> greenTower;
	private LinkedList<CardLabel> yellowTower;
	private LinkedList<CardLabel> blueTower;
	private LinkedList<CardLabel> violetTower;
	
	//The List of positions where the Familiars can be placed
	//Starting Positions 
	private DraggableComponent neutralFamiliar;
	private DraggableComponent blackFamiliar;
	private DraggableComponent orangeFamiliar;
	private DraggableComponent whiteFamiliar;
	//Tower positions
	private LinkedList<JLabel> greenTowerForFamiliar;
	private LinkedList<JLabel> yellowTowerForFamiliar;
	private LinkedList<JLabel> blueTowerForFamiliar;
	private LinkedList<JLabel> violetTowerForFamiliar;
	//Yield and Product Positions
	private List<JLabel> yield;
	private List<JLabel> produce;
	//Council Positions
	private List<JLabel> council;
	private List<JLabel> market;
	
	//Window for Resource update
	private ResourceWindow resourceWindow;
	
	//Labels for the Dice Values
	private JLabel blackDice;
	private JLabel whiteDice;
	private JLabel orangeDice;
	
	//New Order 
	private List<JLabel> newOrder;
	//The List of Bans
	private List<CardLabel> bans;
	//The player next move
	private PlayerMove nextMove;
	//The Loader for all the images of the game
	private ImageLoader imageLoader;
	//GUI Logger
	private Logger logger = Logger.getLogger(GUIView.class);
	
	public GUIView() throws IOException {
		
		super();
		PropertyConfigurator.configure("Logger//Properties//client_log.properties");
		imageLoader = new ImageLoader("Resource//Configuration//imagePaths.json");
		initialize();
		mainFrame.setVisible(true);
	}
	
	private void initialize() throws IOException{
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		//Create a full Screen main frame
		mainFrame = new JFrame(); 
		mainFrame.setBounds(0, 0,screen.width,(int)(screen.height * 0.90));
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setTitle("Lorenzo il Magnifico");
		//Add a JLayeredPane
		JLayeredPane mainLayeredPane = new JLayeredPane();
		mainLayeredPane.setBounds(5, 5, mainFrame.getWidth(), mainFrame.getHeight());
		mainFrame.getContentPane().add(mainLayeredPane);
		
		Dimension leftPaneDimension = new Dimension((int)(mainLayeredPane.getWidth()*0.42), mainLayeredPane.getHeight());
		Dimension rightPanelDimension = new Dimension((int)(mainLayeredPane.getWidth()*0.58), mainLayeredPane.getHeight());
		
		//Set the Table main Image
		JLabel tableLabel = new JLabel();
		tableLabel.setSize(new Dimension((int)leftPaneDimension.getWidth(), (int)leftPaneDimension.getHeight()));
		tableLabel.setLocation(0, 0);
//		tableLabel.setBorder(new LineBorder(Color.BLACK, 10));
		ImageIcon tableIcon = resizeImage(ImageIO.read(GUIView.class.getResource("/Images/TableUpperPart.png")), tableLabel.getSize());
		tableLabel.setIcon(tableIcon);
		mainLayeredPane.add(tableLabel, -1);
		//Set the Card and Table Dimension from the TableIcon and build all the CardPositions
		tableImageDimension = new Dimension(tableIcon.getIconWidth(), tableIcon.getIconHeight());
		Dimension cardDimension = new Dimension((int)(tableIcon.getIconWidth()*0.13), (int)(tableIcon.getIconHeight()*0.17));

		//Set the CardZoom Panel
		BufferedImage cardBack = ImageIO.read(GUIView.class.getResource("/Images/LeaderCards/back.jpg"));
		cardZoom = new CardZoom(cardBack, new Dimension((int)(cardDimension.getWidth()*3), (int)(cardDimension.getHeight()*3)));
		cardZoom.setLocation((int)leftPaneDimension.getWidth(),0);
		mainLayeredPane.add(cardZoom);
		
		//Set the lowerPart of the Table
		JLabel lowTableLabel = new JLabel();
		Dimension lowTableDimension = new Dimension((int)(rightPanelDimension.getWidth()-cardZoom.getWidth()), (int)(cardZoom.getHeight()*0.70));
		lowTableLabel.setSize(lowTableDimension);
		lowTableLabel.setLocation((int)(leftPaneDimension.getWidth()+cardZoom.getWidth()), 0);
		lowTableLabel.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/TableDownPart.png")), lowTableLabel.getSize()));
		mainLayeredPane.add(lowTableLabel, -1);
		
		//Set the Cards taken container
		Dimension containerDimension = new Dimension((int)(rightPanelDimension.getWidth()),(int) (rightPanelDimension.getHeight()-cardZoom.getHeight()));
		Point containerLocation = new Point((int)leftPaneDimension.getWidth(), (int)cardZoom.getHeight());
		cardContainer = new CardContainer(containerDimension, containerLocation, cardZoom, ImageIO.read(GUIView.class.getResource("/Images/Others/cardContainer.jpg")), cardDimension);
		mainLayeredPane.add(cardContainer, -1);
		
		//Build the main Familiar positions
		buildCardPosition(cardDimension, mainLayeredPane);
		
		buildFamiliarStartingPositions(mainLayeredPane);
		
		buildFamiliarMovePositions(mainLayeredPane);
		
		//Add the Resource Update Window
		Dimension resourceWindowDimension = new Dimension((int)(rightPanelDimension.getWidth()-cardZoom.getWidth()), (int)(rightPanelDimension.getHeight() - lowTableLabel.getHeight() - containerDimension.getHeight()));
		Point resourceWindowLocation = new Point((int)(leftPaneDimension.getWidth()+cardZoom.getWidth()), (int)lowTableLabel.getHeight());
		resourceWindow = new ResourceWindow(resourceWindowDimension, resourceWindowLocation );
		mainFrame.add(resourceWindow);
		
		//Add the label for the Dice
		buildDicePositions(mainLayeredPane, lowTableDimension);
		
		LoginWindow login = new LoginWindow(this, "");
		login.run();

	}
	/**
	 * Initialize the Card Position Label
	 * @param cardDimension Dimension of the card that will be placed
	 * @param mainPane Pane where the Card will be placed
	 * @throws IOException 
	 */
	private void buildCardPosition(Dimension cardDimension, JLayeredPane mainPane) throws IOException{
		
		int deltaX = (int)(tableImageDimension.getWidth()*0.022);
		int deltaY = (int)(tableImageDimension.getHeight()*0.03);

		greenTower = new LinkedList<>();
		blueTower = new LinkedList<>();
		violetTower = new LinkedList<>();
		yellowTower = new LinkedList<>();
		
		for(int i=0; i<4; i++){
			CardLabel card = new CardLabel(deltaX, deltaY, cardDimension, cardZoom); 
			greenTower.addFirst(card);
			mainPane.add(card, 0);
			deltaY += (int)(tableImageDimension.getHeight()*0.015) + cardDimension.getHeight();
		}
		deltaY = (int)(tableImageDimension.getHeight()*0.03);
		deltaX += (int) (tableImageDimension.getWidth()*0.241);
		
		for(int i=0; i<4; i++){
			CardLabel card = new CardLabel(deltaX, deltaY, cardDimension, cardZoom); 
			blueTower.addFirst(card);
			mainPane.add(card, 0);
			deltaY += (int)(tableImageDimension.getHeight()*0.015) + cardDimension.getHeight();
		}
		deltaY = (int)(tableImageDimension.getHeight()*0.03);
		deltaX += (int) (tableImageDimension.getWidth()*0.241);
		
		for(int i=0; i<4; i++){
			CardLabel card = new CardLabel(deltaX, deltaY, cardDimension, cardZoom); 
			yellowTower.addFirst(card);
			mainPane.add(card, 0);
			deltaY += (int)(tableImageDimension.getHeight()*0.015) + cardDimension.getHeight();
		}
		deltaY = (int)(tableImageDimension.getHeight()*0.03);
		deltaX += (int) (tableImageDimension.getWidth()*0.241);
		
		for(int i=0; i<4; i++){
			CardLabel card = new CardLabel(deltaX, deltaY, cardDimension, cardZoom); 
			violetTower.addFirst(card);
			mainPane.add(card, 0);
			deltaY += (int)(tableImageDimension.getHeight()*0.015) + cardDimension.getHeight();
		}

	}
	

	/**
	 * 
	 * @param mainPane
	 * @throws IOException
	 */
	private void buildFamiliarStartingPositions(JLayeredPane mainPane) throws IOException{
		
		int deltaX = (int)(tableImageDimension.getWidth()*0.56 );
		int deltaY = (int)(tableImageDimension.getHeight()*0.93);
		
		blackFamiliar = new DraggableComponent(deltaX, deltaY, tableImageDimension.getSize(), ImageIO.read(GUIView.class.getResource("/Images/Others/RedBlackFamiliar.png")), FamiliarColor.BLACK);
		blackFamiliar.enableListener();
		blackFamiliar.setTable(this);
		mainPane.add(blackFamiliar, 0);
		
		int border = (int)(blackFamiliar.getWidth()*1.1); 
		deltaX += border;
		whiteFamiliar = new DraggableComponent(deltaX, deltaY, tableImageDimension.getSize(), ImageIO.read(GUIView.class.getResource("/Images/Others/RedWhiteFamiliar.png")), FamiliarColor.WHITE);
		whiteFamiliar.enableListener();
		whiteFamiliar.setTable(this);
		mainPane.add(whiteFamiliar, 0);
		
		deltaX += border;
		orangeFamiliar = new DraggableComponent(deltaX, deltaY, tableImageDimension.getSize(), ImageIO.read(GUIView.class.getResource("/Images/Others/RedOrangeFamiliar.png")), FamiliarColor.ORANGE);
		orangeFamiliar.enableListener();
		orangeFamiliar.setTable(this);
		mainPane.add(orangeFamiliar, 0);
		
		deltaX += border;
		neutralFamiliar = new DraggableComponent(deltaX, deltaY, tableImageDimension.getSize(), ImageIO.read(GUIView.class.getResource("/Images/Others/RedNeutralFamiliar.png")), FamiliarColor.NEUTRAL);
		neutralFamiliar.enableListener();
		neutralFamiliar.setTable(this);
		mainPane.add(neutralFamiliar, 0);
		
	}
	
	/**
	 * 
	 * @param mainPane
	 * @throws IOException 
	 */
	private void buildFamiliarMovePositions(JLayeredPane mainPane) throws IOException{
		
		//Build the Tower related familiarMove positions
		greenTowerForFamiliar = new LinkedList<>();
		yellowTowerForFamiliar = new LinkedList<>();
		blueTowerForFamiliar = new LinkedList<>();
		violetTowerForFamiliar = new LinkedList<>();

		int deltaX = (int)(tableImageDimension.getWidth()*0.172);
		Dimension positionDimension = new Dimension((int)(tableImageDimension.getWidth()*0.06), (int)(tableImageDimension.getHeight()*0.05));
		placeFamiliarPosition(mainPane, positionDimension, greenTowerForFamiliar, deltaX);
		deltaX += (int)(tableImageDimension.getWidth()*0.242);
		placeFamiliarPosition(mainPane, positionDimension, blueTowerForFamiliar, deltaX);
		deltaX += (int)(tableImageDimension.getWidth()*0.242);
		placeFamiliarPosition(mainPane, positionDimension, yellowTowerForFamiliar, deltaX);
		deltaX += (int)(tableImageDimension.getWidth()*0.241);
		placeFamiliarPosition(mainPane, positionDimension, violetTowerForFamiliar, deltaX);		
		
		//Build the Council related familiarMove positions
		council = new ArrayList<>();
		JLabel firstCouncil = new JLabel();
		firstCouncil.setSize(positionDimension);
		firstCouncil.setLocation((int)(tableImageDimension.getWidth()*0.7), (int)(tableImageDimension.getHeight()*0.8));
		//firstCouncil.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/Others/BluFamiliareNero.png")), positionDimension));
		council.add(firstCouncil);
		mainPane.add(firstCouncil, 0);
		
		//Build the Market, Yield and Product familiarMove positions
		yield = new ArrayList<>();
		produce = new ArrayList<>();
		market = new ArrayList<>();

		int deltaY = (int)(tableImageDimension.getHeight()*0.11);
		placeYieldAndProductPosition(mainPane, positionDimension, produce, deltaY);
		deltaY += (int)(tableImageDimension.getHeight()*0.145);
		placeYieldAndProductPosition(mainPane, positionDimension, yield, deltaY);
		
		deltaX = (int)(tableImageDimension.getWidth()*1.615 + cardZoom.getWidth() );
		deltaY = (int)(tableImageDimension.getHeight()*0.08);
		placeMarket(positionDimension, deltaX, deltaY, mainPane);
		deltaX += (int)(positionDimension.getWidth()*1.85);
		placeMarket(positionDimension, deltaX, deltaY, mainPane);
		deltaX += (int)(positionDimension.getWidth()*1.75);
		deltaY = (int)(tableImageDimension.getHeight()*0.11);
		placeMarket(positionDimension, deltaX, deltaY, mainPane);
		deltaX += (int)(positionDimension.getWidth()*1.42);
		deltaY += (int)(tableImageDimension.getHeight()*0.085);
		placeMarket(positionDimension, deltaX, deltaY, mainPane);
	}
	
	private void placeMarket(Dimension positionDimension, int x, int y, JLayeredPane mainPane) throws IOException{

		JLabel position = new JLabel();
		position.setSize(positionDimension);
		position.setLocation(x, y);
		//position.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/Others/BluFamiliareNero.png")), positionDimension));
		market.add(position);
		mainPane.add(position, 0);
	}
	
	private void placeYieldAndProductPosition(JLayeredPane mainPane, Dimension positionDimension, List<JLabel> yieldAndProduct, int deltaY) throws IOException{
		
		int deltaX = (int)(tableImageDimension.getWidth()*1.03 + cardZoom.getWidth() );
		JLabel position = new JLabel();
		position.setSize(positionDimension);
		position.setLocation(deltaX, deltaY);
		//position.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/Others/BluFamiliareNero.png")), positionDimension));
		yieldAndProduct.add(position);
		mainPane.add(position, 0);
		deltaX += positionDimension.getWidth();
		for(int i=0; i<2; i++){
			deltaX += positionDimension.getWidth()*1.4;
			JLabel tempPosition = new JLabel();
			tempPosition.setSize(positionDimension);
			tempPosition.setLocation(deltaX, deltaY);
			//tempPosition.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/Others/BluFamiliareNero.png")), positionDimension));
			yieldAndProduct.add(tempPosition);
			mainPane.add(tempPosition, 0);
		}
	}
	
	private void placeFamiliarPosition(JLayeredPane mainPane, Dimension positionDimension, LinkedList<JLabel> tower, int rightShift) throws IOException{
		
		int deltaY = (int)(tableImageDimension.getHeight()*0.081);
		for (int i=0; i<4; i++){
			JLabel familiarPosition = new JLabel();
			familiarPosition.setSize(positionDimension);
			familiarPosition.setLocation(rightShift, deltaY);
			//familiarPosition.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/Others/BluFamiliareNero.png")), positionDimension));
			tower.addFirst(familiarPosition);
			mainPane.add(familiarPosition, 0);
			deltaY += (int)(tableImageDimension.getHeight()*0.1858);
		}
	}
	
	private void buildDicePositions(JLayeredPane mainPane, Dimension lowerTableDimension ) throws IOException{
		
		Dimension diceDimension = new Dimension((int)(lowerTableDimension.getWidth()*0.09),(int)(lowerTableDimension.getHeight()*0.21));
		buildSingleDice(mainPane, diceDimension, lowerTableDimension, 0, blackDice);
		int rightShift = (int)(diceDimension.getWidth()*1.29);
		buildSingleDice(mainPane, diceDimension, lowerTableDimension, rightShift, whiteDice);
		rightShift += (int)(diceDimension.getWidth()*1.3);
		buildSingleDice(mainPane, diceDimension, lowerTableDimension, rightShift, orangeDice);
	}
	
	private void buildSingleDice(JLayeredPane mainPane, Dimension diceDimension, Dimension lowerTableDimension, int rightShift, JLabel diceLable) throws IOException{
		
		Point location = new Point((int) (tableImageDimension.getWidth() + cardZoom.getWidth() + lowerTableDimension.getWidth()*0.58 + rightShift),(int) (lowerTableDimension.getHeight()*0.72));
		diceLable = new JLabel();
		diceLable.setSize(diceDimension);
		diceLable.setLocation(location);
		mainPane.add(diceLable, 0);
	}
	
	//Method used to resize the image for a JLabel ImageIcon
	private ImageIcon resizeImage(BufferedImage imageToResize, Dimension newDimension){
		Image imageResized = null;
		if(imageToResize != null){
			int width = (int)newDimension.getWidth();
			int height = (int)newDimension.getHeight();
			int scaledHeight = height;
			int scaledWidth= (int)(width*scaledHeight/(height));
			imageResized = imageToResize.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
		}
		
		return new ImageIcon(imageResized);
	}
	
	@Override
	public boolean handleEvent(int x, int y, DraggableComponent familiarMoving, FamiliarColor color) {
	
		//For each position check if contains the point (x,y), if so change the imageIcon
		int actionValue = 0;
		for (JLabel position : greenTowerForFamiliar) {
			if( containsPoint(position, x, y) ){
				actionValue = 1 + greenTowerForFamiliar.indexOf(position)*2;
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				createNewMove(ActionType.TAKE_GREEN, color, greenTowerForFamiliar.indexOf(position), actionValue, familiarMoving);
				return true;
			}
		}
		for (JLabel position : yellowTowerForFamiliar) {
			if( containsPoint(position, x, y) ){
				actionValue = 1 + yellowTowerForFamiliar.indexOf(position)*2;
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				createNewMove(ActionType.TAKE_YELLOW, color, yellowTowerForFamiliar.indexOf(position),actionValue, familiarMoving);
				return true;
			}
		}
		for (JLabel position : blueTowerForFamiliar) {
			if( containsPoint(position, x, y) ){
				actionValue = 1 + blueTowerForFamiliar.indexOf(position)*2;
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				createNewMove( ActionType.TAKE_BLUE, color, blueTowerForFamiliar.indexOf(position),actionValue, familiarMoving);
				return true;
			}
		}
		for (JLabel position : violetTowerForFamiliar) {
			if( containsPoint(position, x, y) ){
				actionValue = 1 + violetTowerForFamiliar.indexOf(position)*2;
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				createNewMove(ActionType.TAKE_VIOLET, color, violetTowerForFamiliar.indexOf(position),actionValue, familiarMoving);
				return true;
			}
		}
		for (JLabel position : council) {
			if( containsPoint(position, x, y) ){
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				actionValue = 1;
				createNewMove( ActionType.COUNCIL, color, council.indexOf(position), actionValue, familiarMoving);
				return true;
			}
		}
		for (JLabel position : yield) {
			if( containsPoint(position, x, y) ){
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				if(yield.indexOf(position) == 0)
					actionValue = 1;
				else
					actionValue = -3;
				createNewMove( ActionType.YIELD, color, yield.indexOf(position), actionValue, familiarMoving);
				return true;
			}
		}
		for (JLabel position : produce) {
			if( containsPoint(position, x, y) ){
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				if(yield.indexOf(position) == 0)
					actionValue = 1;
				else
					actionValue = -3;
				createNewMove( ActionType.PRODUCE, color, produce.indexOf(position),actionValue , familiarMoving);
				return true;
			}
		}
		for (JLabel position : market) {
			if( containsPoint(position, x, y) ){
				position.setIcon(resizeImage(familiarMoving.getImage(), position.getSize()));
				actionValue = 1;
				createNewMove( ActionType.MARKET, color, market.indexOf(position),actionValue , familiarMoving);
				return true;
			}
		}
		return false;
	}
	
	private boolean containsPoint(JLabel label, int x, int y){
		if(label.getIcon() == null){
			//If the position is empty, discover if has been pointed
			Point p = label.getLocationOnScreen();
			if(p.getX() < x && x < p.getX()+label.getWidth()
					&& p.getY() < y && y < p.getY() + label.getHeight())
				return true;
		}
		return false;
	}
	
	@Override
	public void addPlayer(String playerID) {
		super.addPlayer(playerID);
		resourceWindow.setPlayer(this.player);
		resourceWindow.update();
	}
	
	@Override
	public void setResources(HashMap<Resource, Integer> resources, String playerID) {
		super.setResources(resources, playerID);
		if(hasToAnswer(playerID))
			resourceWindow.update();
	}
	
	@Override
	public void setBlackDie(int value) {
		super.setBlackDie(value);
		//Update the image value for the blackDice
		try {
			blackDice.setIcon(resizeImage(imageLoader.loadBlackDieImage(new Integer(value)), blackDice.getSize()));
		} catch (IOException e) {
			logger.error("Black Dice Image not found!");
			logger.info(e);
		}
	}
	
	@Override
	public void setOrangeDie(int value) {
		super.setOrangeDie(value);
		//Update the image value for the orangeDice
		try {
			orangeDice.setIcon(resizeImage(imageLoader.loadOrangeDieImage(new Integer(value)), orangeDice.getSize()));
		} catch (IOException e) {
			logger.error("Orange Dice Image not found!");
			logger.info(e);
		}

	}
	
	@Override
	public void setWhiteDie(int value) {
		super.setWhiteDie(value);
		//Update the image value for the whiteDice
		try {
			whiteDice.setIcon(resizeImage(imageLoader.loadWhiteDieImage(new Integer(value)), whiteDice.getSize()));
		} catch (IOException e) {
			logger.error("White Dice Image not found!");
			logger.info(e);
		}

	}
	
	private void createNewMove( ActionType type, FamiliarColor familiarColor, int position, int actionValue, DraggableComponent familiarMoving){
		//Ask the Player if he wants to increment the actual move and set the increment
		this.movingFamiliar = familiarMoving;
		new IncrementWindow(this, type, familiarColor, position, actionValue, player.getResource(Resource.SLAVE));
		
	}
	
	public void cancelMove(ActionType type, int position){
		//Restore the position of the moved Familiar
		movingFamiliar.resetFamiliar();
		
		switch (type) {
		case TAKE_GREEN:
			greenTowerForFamiliar.get(position).setIcon(null);
			break;
		case TAKE_YELLOW:
			yellowTowerForFamiliar.get(position).setIcon(null);
			break;
		case TAKE_BLUE:
			blueTowerForFamiliar.get(position).setIcon(null);
			break;
		case TAKE_VIOLET:
			violetTowerForFamiliar.get(position).setIcon(null);
			break;
		case MARKET:
			market.get(position).setIcon(null);
			break;
		case YIELD:
			yield.get(position).setIcon(null);
			break;
		case PRODUCE:
			produce.get(position).setIcon(null);
			break;
		case COUNCIL:
			council.get(position).setIcon(null);
			break;
			
		default:
			break;
		}
	}
	
	private void enableMove(){
		enableFamiliar(blackFamiliar, player.getFamiliar(FamiliarColor.BLACK), true);
		enableFamiliar(whiteFamiliar, player.getFamiliar(FamiliarColor.WHITE), true);
		enableFamiliar(orangeFamiliar, player.getFamiliar(FamiliarColor.ORANGE), true);
		enableFamiliar(neutralFamiliar, player.getFamiliar(FamiliarColor.NEUTRAL), true);
		
	}
	
	public void disableMove(){

		enableFamiliar(blackFamiliar, player.getFamiliar(FamiliarColor.BLACK), false);
		enableFamiliar(whiteFamiliar, player.getFamiliar(FamiliarColor.WHITE), false);
		enableFamiliar(orangeFamiliar, player.getFamiliar(FamiliarColor.ORANGE), false);
		enableFamiliar(neutralFamiliar, player.getFamiliar(FamiliarColor.NEUTRAL), false);
	}
	
	private void enableFamiliar(DraggableComponent familiarIcon, Familiar familiar, boolean status){
		if(!familiar.isPositioned())
			familiarIcon.setCanMove(status);
	}
	
	private void restoreFamiliar(){
		blackFamiliar.resetFamiliar();
		whiteFamiliar.resetFamiliar();
		orangeFamiliar.resetFamiliar();
		neutralFamiliar.resetFamiliar();
	}
	
	@Override
	public void resetTable() {
		super.resetTable();
		restoreFamiliar();
	}
	
	@Override
	public void setFamiliarInBlueTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInBlueTower(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, blueTowerForFamiliar.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInGreenTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInGreenTower(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, greenTowerForFamiliar.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInYellowTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInYellowTower(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, yellowTowerForFamiliar.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInVioletTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInVioletTower(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, violetTowerForFamiliar.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInMarket(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInMarket(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, market.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInYield(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInYield(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, yield.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInProduce(String playerID, FamiliarColor color, int position) throws ElementNotFoundException {
		super.setFamiliarInProduce(playerID, color, position);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, produce.get(position));
		}		
	}
	
	@Override
	public void setFamiliarInCouncil(String playerID, FamiliarColor color) throws ElementNotFoundException {
		super.setFamiliarInCouncil(playerID, color);
		if( !hasToAnswer(playerID)){
			setOccupied(playerID, color, council.get(0));
		}		
	}
	
	private void setOccupied(String playerID, FamiliarColor color, JLabel position){
		
		try {
			position.setIcon(resizeImage(ImageIO.read(GUIView.class.getResource("/Images/Others/BluFamiliareNero.png")), position.getSize()));
		} catch (IOException e) {
			logger.error("Image for "+color.toString()+" of the player: "+playerID+" not found!");
			logger.info(e);
		}
	}
	
	@Override
	public void setGreenCards(StaticList<Card> cards) {
		super.setGreenCards(cards);
		placeCards(greenTower, cards);
	}
	
	@Override
	public void setYellowCards(StaticList<Card> cards) {
		super.setYellowCards(cards);
		placeCards(yellowTower, cards);
	}
	@Override
	public void setBlueCards(StaticList<Card> cards) {
		super.setBlueCards(cards);
		placeCards(blueTower, cards);
	}
	@Override
	public void setVioletCards(StaticList<Card> cards) {
		super.setVioletCards(cards);
		placeCards(violetTower, cards);
	}
	private void placeCards(List<CardLabel> tower, StaticList<Card> cards) {
		for(int i=0; i<4; i++){
			BufferedImage cardImage;
			try {
				cardImage = imageLoader.loadCardImage(cards.get(i).getName());
				tower.get(i).placeCard(cardImage);
			} catch (IOException e) {
				logger.error("Image not Found! Probably a wrong name is given or the loader has been misconfigured");
				logger.info(e);
			}
		}
	}
	
	@Override
	public void setGreenCard(String playerID, int position) throws ElementNotFoundException {

		Player player = searchPlayer(playerID);
		Card card = this.table.getGreenCard(position);
		player.addCard(card);
		if(hasToAnswer(playerID))
			try {
				cardContainer.addGreenCard(imageLoader.loadCardImage(card.getName()));
			} catch (IOException e) {
				logger.error("Image not Found! Probably a wrong name is given or the loader has been misconfigured");
				logger.info(e);
			}
		greenTower.get(position).removeCard();
	}
	
	@Override
	public void setYellowCard(String playerID, int position) throws ElementNotFoundException {

		Player player = searchPlayer(playerID);
		Card card = this.table.getYellowCard(position);
		player.addCard(card);
		if(hasToAnswer(playerID))
			try {
				cardContainer.addYellowCard(imageLoader.loadCardImage(card.getName()));
			} catch (IOException e) {
				logger.error("Image not Found! Probably a wrong name is given or the loader has been misconfigured");
				logger.info(e);
			}
		yellowTower.get(position).removeCard();
	}
	
	@Override
	public void setBlueCard(String playerID, int position) throws ElementNotFoundException {
		
		Player player = searchPlayer(playerID);
		Card card = this.table.getBlueCard(position);
		player.addCard(card);
		if(hasToAnswer(playerID)){
			try {
				cardContainer.addBlueCard(imageLoader.loadCardImage(card.getName()));
			} catch (IOException e) {
				logger.error("Image not Found! Probably a wrong name is given or the loader has been misconfigured");
				logger.info(e);
			}
		}
		blueTower.get(position).removeCard();
	}
	
	@Override
	public void setVioletCard(String playerID, int position) throws ElementNotFoundException {

		Player player = searchPlayer(playerID);
		Card card = this.table.getVioletCard(position);
		player.addCard(card);
		if(hasToAnswer(playerID))
			try {
				cardContainer.addVioletCard(imageLoader.loadCardImage(card.getName()));
			} catch (IOException e) {
				logger.error("Image not Found! Probably a wrong name is given or the loader has been misconfigured");
				logger.info(e);
			}
		violetTower.get(position).removeCard();
	}

	@Override
	protected void chooseBonusBar(List<BonusBar> bonusBarList) {
		//Ask the Player to choose a BonusBar from the given List
		BonusBarRequestDialog dialog = new BonusBarRequestDialog(this, bonusBarList);
		dialog.run();
	}

	@Override
	protected void chooseLeaderCard(List<LeaderCard> leaderCardList) {
		// Ask the Player to choose a LeaderCard from the given List
		try {
			LeaderCardChooseDialog dialog = new LeaderCardChooseDialog(this, leaderCardList);
		} catch (IOException e) {
			logger.error("Error in open the image conversion file");
			logger.info(e);
		}
	}

	@Override
	protected void chooseCouncilConversion(List<Obtain> possibleConversions, int quantity) {
		// Ask the Player to choose a council conversion quantity

	}

	@Override
	protected void choosePlayerMove(ActionPrototype prototype, boolean isRetrasmission) {
		//Enable the Player to perform a new Move, if is a retrasmission then cancel the precedent
		if(isRetrasmission && nextMove != null)
			cancelMove(nextMove.getActionType(), nextMove.getPosition());
		if(prototype != null){
			//Show ActionPrototype
			
		}
		enableMove();
	}

	@Override
	public void setNewMove(PlayerMove move) {
		super.setNewMove(move);
		this.nextMove = move;
		disableMove();
	}
	
	@Override
	protected void chooseIfPayBan(int banPeriod) {
		// Ask to the Player if he wants to pay for the current Period Ban
		
		
	}

	@Override
	protected void answerCardRequest(CardRequest message) {
		// Ask to the player to choose from the given card request
		CardRequestDialog dialog = new CardRequestDialog(this, message);
		dialog.run();
	}

	@Override
	protected void chooseFamiliarColor(LeaderFamiliarRequest message) {
		// Ask to the Player what color to choose for the leaderCard Request
		
	}

	@Override
	protected void notifyLeaderCardActivation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void notifyLeaderCardDiscard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void askNewPlayerID() {
		//Show a window asking the Player username
		LoginWindow login = new LoginWindow(this, "Used ID yet chosen by another player");
		login.run();
	}

	@Override
	protected void askIfWantToPlay(PlayerToken moveToken) {
		//Ask to the Player if he wants to perform a new Action 
		
		//If so ask a new PlayerMove
		this.choosePlayerMove(moveToken.getActionPrototype(), moveToken.isRetrasmission());
		
	}

	@Override
	protected void showResult(List<String> finalChart) {
		// TODO Auto-generated method stub
		
	}
	
	public JFrame getMainFrame() {
		return mainFrame;
	}
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(GUIView.class);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIView window = new GUIView();
				} catch (Exception e) {
					logger.error("Problems in GUI View creation");
					logger.info(e);
				}
			}
		});
	}


}
