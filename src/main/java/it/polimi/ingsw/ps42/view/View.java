package it.polimi.ingsw.ps42.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.polimi.ingsw.ps42.message.BanRequest;
import it.polimi.ingsw.ps42.message.BonusBarMessage;
import it.polimi.ingsw.ps42.message.CardRequest;
import it.polimi.ingsw.ps42.message.CouncilRequest;
import it.polimi.ingsw.ps42.message.LeaderCardMessage;
import it.polimi.ingsw.ps42.message.Message;
import it.polimi.ingsw.ps42.message.PlayerMove;
import it.polimi.ingsw.ps42.message.PlayerToken;
import it.polimi.ingsw.ps42.message.visitorPattern.ViewVisitor;
import it.polimi.ingsw.ps42.message.visitorPattern.Visitor;
import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.model.action.ActionPrototype;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.exception.ElementNotFoundException;
import it.polimi.ingsw.ps42.model.exception.WrongChoiceException;
import it.polimi.ingsw.ps42.model.leaderCard.LeaderCard;
import it.polimi.ingsw.ps42.model.player.BonusBar;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;

public abstract class View extends Observable implements Observer {

	
	private TableView table; 
	private Player player;
	private List<Player> otherPlayers; 
	private Visitor viewVisitor;
	
	public View() {
		
		this.viewVisitor = new ViewVisitor(this);
		this.otherPlayers = new ArrayList<>();
	}
	
	public void addPlayer(String playerID){
		
		this.player = new Player(playerID);
	}
	
	public void createTable(List<String> playersID){
		
		//Method to be called from the ViewVisitor when receive the players of the game
		if(playersID.contains(player.getPlayerID())){
			
			playersID.remove(player.getPlayerID());
			//Create the others players
			for (String playerID : playersID) {
				otherPlayers.add(new Player(playerID));
			}
			//Add the players to the Table
			if(playersID.size() == 1)
				this.table = new TableView( player, otherPlayers.get(0));
			else if(playersID.size() == 2)
				this.table = new TableView( player, otherPlayers.get(0), otherPlayers.get(1));
			else if(playersID.size() == 3)
				this.table = new TableView( player,  otherPlayers.get(0), otherPlayers.get(1), otherPlayers.get(2));
			
		}
	}
	
	//Methods to Ask Player Input
	private boolean hasToAnswer(String playerID){
		
		return player.getPlayerID().equals(playerID);
	}
	
	public void askBonusBar(BonusBarMessage message) throws WrongChoiceException{
		
		if( hasToAnswer(message.getPlayerID())){
			//Ask to choose a BonusBar
			message.setChoice(chooseBonusBar(message.getAvailableBonusBar()));
			
			//Notify the choice to the Game Logic
			setChanged();
			notifyObservers(message);
		}

	}
	
	public void askLeaderCard(LeaderCardMessage message ) throws WrongChoiceException{
		
		if( hasToAnswer(message.getPlayerID())){
			//Ask to choose a Leader Card
			message.setChoice(chooseLeaderCard(message.getAvailableLeaderCards()));
			
			//Notify the choice to the Game Logic
			setChanged();
			notifyObservers(message);
		}
	}
		
	public void askCouncilRequest( CouncilRequest message) throws WrongChoiceException{
		
		if( hasToAnswer(message.getPlayerID())){
			//Ask to choose a Conversion for the Council Privilege
			message.addChoice(chooseCouncilConversion(message.getPossibleChoice()));
			
			//Notify the choice to the Game Logic
			setChanged();
			notifyObservers(message);
		}
		
	}
	
	public void askPlayerMove( PlayerToken message){
		
		if(hasToAnswer(message.getPlayerID())){
			//Ask to choose a Move to the Player
			PlayerMove move = choosePlayerMove(message.getActionPrototype());
			//Notify the Move to Game Logic
			setChanged();
			notifyObservers(move);
		}
	}
	
	public void askPayBan(BanRequest message){
	
		if(hasToAnswer(message.getPlayerID())){
			//Ask to the player if he wants to pay the faithPoint instead of getting the ban
			if( chooseIfPayBan( message.getBanNumber()))
				message.wantPayForBan();
			
			//Notify the choice to the Game Logic
			setChanged();
			notifyObservers(message);
		}
	}
	
	public void askCardRequest( CardRequest message){
		
		if(hasToAnswer(message.getPlayerID())){
			//Ask to the player to answer
			message.setChoice(answerCardRequest(message));
			
		}
	}
	
	//Methods For Input/Output Implemented by Concrete Classes
	protected abstract int chooseBonusBar(List<BonusBar> bonusBarList);

	protected abstract int chooseLeaderCard(List<LeaderCard> leaderCardList);
	
	protected abstract int chooseCouncilConversion(List<Obtain> possibleConversions);
	
	protected abstract PlayerMove choosePlayerMove(ActionPrototype prototype);
	
	protected abstract boolean chooseIfPayBan(int banPeriod);
	
	protected abstract int answerCardRequest( CardRequest message);
	
	
	//SETTER FOR TABLE BANS
	public void setFirstBan(Effect firstBan){
		if(firstBan != null)
			this.table.addFirstBan(firstBan);
	}
	
	public void setSecondBan(Effect secondBan){
		if(secondBan != null)
			this.table.addSecondBan(secondBan);
	}
	
	public void setThirdBan(Effect thirdBan){
		if(thirdBan != null)
			this.table.addThirdBan(thirdBan);
	}
	
	public void setBanToPlayer(String playerID, int banPeriod) throws ElementNotFoundException{
			
		if(searchPlayer(playerID) != null){
			if( banPeriod == 0)
				this.table.setPlayerFirstBan(playerID);
			else if( banPeriod == 1)
				this.table.setPlayerSecondBan(playerID);
			else if( banPeriod == 2)
				this.table.setPlayerThirdBan(playerID);
		}
	}
	
	//SETTER FOR DICE VALUES
	public void setOrangeDie(int value){
		this.table.setOrangeDie(value);
	}
	
	public void setBlackDie(int value){
		this.table.setBlackDie(value);
	}
	
	public void setWhiteDie(int value){
		this.table.setWhiteDie(value);
	}
	
	private Player searchPlayer(String playerID) throws ElementNotFoundException{
		
		if( playerID != null  && playerID.equals(player.getPlayerID()))
			return player;
		else{
			for (Player tempPlayer : otherPlayers) {
				if( playerID.equals(tempPlayer.getPlayerID()))
						return tempPlayer;
			}
		}
		throw new ElementNotFoundException("Player not Found");
		
	}
	public void setResources( HashMap<Resource, Integer> resources, String playerID){
		
		try {
			
			Player player = searchPlayer(playerID);
			player.setCurrentResources(resources);
		} catch (ElementNotFoundException e) {
			// TODO Discuss on how to handle this
			e.printStackTrace();
		}
	}
	
	//SETTER FOR THE CARDS
	public void setGreenCards(StaticList<Card> cards){
		
		if( cards != null)
			this.table.placeGreenTower(cards);
	}
	
	public void setYellowCards(StaticList<Card> cards){
		
		if( cards != null)
			this.table.placeYellowTower(cards);	
	}
	
	public void setBlueCards(StaticList<Card> cards){
	
		if( cards != null)
			this.table.placeBlueTower(cards);
	}

	public void setVioletCards(StaticList<Card> cards){

		if( cards != null)
			this.table.placeVioletTower(cards);
	}
	
	//SETTERS FOR THE FAMILIAR
	public void setFamiliarInGreenTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInGreenTower(familiar, position);
	}
	
	public void setFamiliarInBlueTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInBlueTower(familiar, position);
	}	
	
	public void setFamiliarInYellowTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInYellowTower(familiar, position);
	}
	
	public void setFamiliarInVioletTower(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInVioletTower(familiar, position);
	}
	
	public void setFamiliarInYield(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInYield(familiar, position);
	}
	
	public void setFamiliarInProduce(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInProduce(familiar, position);
	}
	
	public void setFamiliarInCouncil(String playerID, FamiliarColor color) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInCouncil(familiar);
	}
	
	public void setFamiliarInMarket(String playerID, FamiliarColor color, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Familiar familiar = player.getFamiliar(color);
		this.table.placeInMarket(familiar, position);
	}
	
	public void setGreenCard(String playerID, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Card card = this.table.getGreenCard(position);
		player.addCard(card);
		
	}
	
	public void setBlueCard(String playerID, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Card card = this.table.getBlueCard(position);
		player.addCard(card);
		
	}	
	
	public void setVioletCard(String playerID, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Card card = this.table.getVioletCard(position);
		player.addCard(card);
		
	}
	
	public void setYellowCard(String playerID, int position) throws ElementNotFoundException{
		
		Player player = searchPlayer(playerID);
		Card card = this.table.getYellowCard(position);
		player.addCard(card);
		
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		
		if (arg1 instanceof Message) {
			Message message = (Message) arg1;
			message.accept(viewVisitor);
		}
	}

}
