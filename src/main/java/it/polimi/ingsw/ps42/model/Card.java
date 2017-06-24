package it.polimi.ingsw.ps42.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.ps42.message.CardRequest;
import it.polimi.ingsw.ps42.message.FinalRequest;
import it.polimi.ingsw.ps42.message.ImmediateRequest;
import it.polimi.ingsw.ps42.message.PayRequest;
import it.polimi.ingsw.ps42.message.PermanentRequest;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;


public class Card implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3184629522167274982L;
	/*Class for Card, when created is placed in a position and do not has a owner, 
	 * the owner is setted only when a player takes the card. In every ArrayList 
	 * (Packet or Effect) the elements have to be considered in OR while IN the single 
	 * Packet/Effect they are in AND
	 */
	private final String name;
	private final String description;
	private final CardColor color;
	private final int period;
	private final int level;
	private Player owner;
	private final List<Packet> costs;
	private final List<Packet> requirements;
	private final List<Effect> immediateEffects;
	private final List<Effect> permanentEffects;
	private final List<Effect> finalEffects;
	
	//ArrayList used for check if player can pay to obtain the card or enable the effect
	private List<Printable> possibleChoice;
	private List<Integer> possibleChoiceIndex;
	
	//ArrayList of cost used for the cost player can pay effectively
	private List<Packet> effectivelyCosts;
	
	public Card(String name, String description, CardColor color, int period, 
			int level, List<Packet> costs, List<Effect> immediateEffects, List<Packet> requirements,
			List<Effect> permanentEffect, List<Effect> finalEffects){
		//Construct the card
		
		this.name = name;
		this.description = description;
		this.color = color;
		this.period = period;
		this.level = level;
		this.costs = costs;
		this.requirements = requirements;
		this.immediateEffects = immediateEffects;
		this.permanentEffects = permanentEffect;
		this.finalEffects = finalEffects;
		
		//Construct the arraylist
		this.possibleChoice = new ArrayList<>();
		this.possibleChoiceIndex = new ArrayList<>();
		this.effectivelyCosts = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public CardColor getColor() {
		return color;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public int getLevel() {
		return level;
	}
	
	public List<Packet> getCosts() {
		//Return a copy of costs array
		return copyPacketList(costs);
	}
	
	/*public List<Packet> getRequirements() {
		//Return a copy of requirements array
		return copyPacketList(requirements);
	}*/
	
	public void setPlayer(Player owner) {
		this.owner = owner;
	}
	
	
	/*	PAY A CARD METHODS	*/
	public void payCard(Player player, int choice, Packet discount) throws NotEnoughResourcesException {
		Packet chosenCost = costs.get(choice);
		
		//Discount the cost
		if(discount != null) {
			for(Unit unit : discount) {
				chosenCost.subtractUnit(unit);
			}
		}
		
		player.decreaseResource(chosenCost);
	}
	
	public void payCard(Player player, Packet discount) throws NotEnoughResourcesException {
		
		//First of all, control if card costs are null
		if(costs != null && !costs.isEmpty()) {
			
			//Then check the requirements, maybe player can pay the card but he hasn't the requirements, such as enough military point
			checkRequirements(player);
			
			if(effectivelyCosts.size() == 0)
				throw new NotEnoughResourcesException("Player has not enough resources");
			
			//Then check if there is a discount to apply
			increaseDiscount(player, discount);
			
			//At this point, card can have only one cost or higher
			//If there is only one cost, control if player can pay it
			//In this case, the player can pay immediately, without a request
			if(effectivelyCosts.size() == 1) {
				if(checkPlayerCanPay(effectivelyCosts.get(0), player, discount)) {
					decreaseDiscount(player, discount);
					payCard(player, 0, discount);
				}
				else {
					decreaseDiscount(player, discount);
					throw new NotEnoughResourcesException("Player hasn't enough resource");
				}
			}
			
			//This branch will enable only if there is more costs in card
			if(effectivelyCosts.size() > 1) {
				
				//Then control the cost player can pay
				for(Packet cost : effectivelyCosts) {
					if(checkPlayerCanPay(cost, player, discount)) {
						possibleChoice.add(cost.clone());
						possibleChoiceIndex.add(costs.indexOf(cost));
					}
				}
				
				//The costs player can afford will be send to the client. In this way player can choose which cost
				//he want to pay
				if(possibleChoice.isEmpty() || possibleChoiceIndex.isEmpty())  {
					decreaseDiscount(player, discount);
					throw new NotEnoughResourcesException("The possibleChoice array is empty, cannot pay this");
				}
				decreaseDiscount(player, discount);
				CardRequest request = new PayRequest(player.getPlayerID(), this, possibleChoiceIndex, possibleChoice, discount);
				player.addRequest(request);
				resetPossibleChoice();
			}
		}
	}
	
	//Methods for the discount
	private void increaseDiscount(Player player, Packet discount) {
		if(discount != null) {
			player.increaseResource(discount);
			player.synchResource();
		}
	}
	
	private void decreaseDiscount(Player player, Packet discount) throws NotEnoughResourcesException {
		if(discount != null) {
			player.decreaseResource(discount);
			player.synchResource();
		}
	}
	
	/*	IMMEDIATE EFFECT */
	public void enableImmediateEffect() throws NotEnoughResourcesException {
		if(immediateEffects != null && !( immediateEffects.isEmpty() ) ) {
			//If the effectList exist
			if(canEnableNowEffect(immediateEffects))
				enableEffect(0, immediateEffects, owner);
			else{
				if(controlPossibleChoice(immediateEffects))
					resetPossibleChoice();
				else {
					CardRequest request = new ImmediateRequest(owner.getPlayerID(), this, possibleChoiceIndex, possibleChoice);
					owner.addRequest(request);
					resetPossibleChoice();
				}
			}
		}
	}
	
	public void enableImmediateEffect(int choice, Player player) {
		enableEffect(choice, immediateEffects, player);
	}
	/* END IMMEDIATE EFFECT */
	
	/*	PERMANENT EFFECT */
	public void enablePermanentEffect() throws NotEnoughResourcesException {
		if(permanentEffects != null && !( permanentEffects.isEmpty() ) ) {
			//If the effectList exist
			if(canEnableNowEffect(permanentEffects))
				enableEffect(0, permanentEffects, owner);
			else {
				if(controlPossibleChoice(permanentEffects))
					resetPossibleChoice();
				else {
					CardRequest request = new PermanentRequest(owner.getPlayerID(), this, possibleChoiceIndex, possibleChoice);
					owner.addRequest(request);
					resetPossibleChoice();
				}
			}
		}
	}
	
	public void enablePermanentEffect(int choice, Player player) {
		enableEffect(choice, permanentEffects, player);
	}
	/*	END PERMANENT EFFECT */
	
	/* FINAL EFFECT */
	public void enableFinalEffect() throws NotEnoughResourcesException {
		if(finalEffects != null && !( finalEffects.isEmpty() ) ) {
			//If the effectList exist
			if(canEnableNowEffect(finalEffects))
				enableEffect(0, finalEffects, owner);
			else {
				if(controlPossibleChoice(finalEffects))
					resetPossibleChoice();
				else {
					CardRequest request = new FinalRequest(owner.getPlayerID(), this, possibleChoiceIndex, possibleChoice);
					owner.addRequest(request);
					resetPossibleChoice();
				}
			}
		}
	}
	
	public void enableFinalEffect(int choice, Player player) {
		enableEffect(choice, finalEffects, player);
	}
	/* END FINAL EFFECT */
	
	/*public Packet checkCosts(Player player) {
		for(Packet cost : costs)
			if(checkPlayerCanPay(cost, player, null))
				return cost;
		return null;
	}*/
	
	//PRIVATE METHODS FOR CARD CLASS
	private void resetPossibleChoice() {
		this.possibleChoice = new ArrayList<>();
		this.possibleChoiceIndex = new ArrayList<>();
	}
	
	private boolean controlPossibleChoice(List<Effect> effectList) throws NotEnoughResourcesException {
		//ONLY PRIVATE request
		//Used only to verify if the arrays of choices isn't empty
		if(possibleChoice.isEmpty() || possibleChoiceIndex.isEmpty()) 
			throw new NotEnoughResourcesException("The possibleChoice array is empty, cannot pay this");
		
		if(possibleChoice.size() == 1) {
			Effect effect = effectList.get(possibleChoiceIndex.get(0));
			if(effect.getTypeOfEffect() != EffectType.OBTAIN) {
				effect.enableEffect(owner);
				return true;	
			}

		}
		return false;
	}
	
	private void enableEffect(int choice, List<Effect> effectList, Player player) {
			effectList.get(choice).enableEffect(player);
	}
	
	private boolean canEnableNowEffect(List<Effect> effectList) {
		//Control if the effect can be enabled immediately
		
		if(effectList.size() == 1) {
			//If card has only one effect, control it and return if it is activated
			return checkActivable(effectList.get(0), 0);	
		}
		for(Effect effect : effectList) {
			//Else if card has more than one effect, control all the effect, and add it to the possibleChoice array
			//But return false because the card need to know the user's choice
			if(checkActivable(effect, effectList.indexOf(effect))) {
				possibleChoiceIndex.add(effectList.indexOf(effect));
				possibleChoice.add(effect.clone());
			}
		}
		return false;
	}
	
	private boolean checkActivable(Effect effect, int index) {
		//Return true if the effect can be enabled
		boolean checker = true;
		
		//If the effect is obtain, cast the effect to get the obtain costs
		if(effect.getTypeOfEffect() == EffectType.OBTAIN) {
			Obtain obtainEffect = (Obtain)effect;
			Packet obtainCosts = obtainEffect.getCosts();
			
			//If there is at least one cost
			if(obtainCosts != null) {
				//Check if player can pay
				//If player can pay this cost, then checker = false because the effect cannot be immediately consumed
				//Else the effect cannot be payed nor added to the possible choice array
				checker = checkOwnerCanPay(obtainCosts, null);
				if(checker == true) {
					possibleChoice.add(effect.clone());
					possibleChoiceIndex.add(index);
					checker = false;
				}
			}
		}
		return checker;
	}
	
	private boolean checkOwnerCanPay(Packet costs, Packet discount) {
		//Check if the owner can pay an effect
		return checkPlayerCanPay(costs, owner, discount);
	}
	
	private boolean checkPlayerCanPay(Packet costs, Player player, Packet discount) {
		//Check if a generic player can pay a packet of costs
		for (Unit unit : costs) {
			if(unit.getQuantity() > player.getResource(unit.getResource())) {
				return false;
			}
		}
		return true;
	}
	
	private void checkRequirements(Player player) {
		//Used to fill the effectivelyCosts array to pay the card
		//Costs with requirement before than normal costs
		int i = 0;
		if(requirements != null) {
			for(Packet requirement : requirements) {
				boolean checker = true;
				for(Unit unit : requirement) {
					if(unit.getQuantity() > player.getResource(unit.getResource()))
						checker = false;
				}
				if(checker == true)
					effectivelyCosts.add(costs.get(requirements.indexOf(requirement)));
				i++;
			}
		}
		
		//Add the other costs to the effectivelyCosts
		for(int j = i; j < costs.size(); j++){
			effectivelyCosts.add(costs.get(j));
		}
	}
	
	private List<Packet> copyPacketList(List<Packet> start) {
		List<Packet> temp = new ArrayList<>();
		if(start != null) {
			for(Packet packet : start) {
				temp.add(packet.clone());
			}
		}
		return temp;
	}
	
	public String showCard(){
		
		StringBuilder cardBuilder = new StringBuilder();
		cardBuilder.append("NAME: "+this.name+"\n");
		cardBuilder.append("DESCRIPTION: "+this.description+"\n");
		cardBuilder.append("LEVEL: "+this.level+"\n");
		
		cardBuilder.append("COSTS: \n");
		for (Packet cost : this.costs) {
			cardBuilder.append(cost.print()+"\n");
		}
		
		cardBuilder.append("REQUIREMENTS: \n");
		for (Packet requirement: this.requirements){
			cardBuilder.append(requirement.print()+"\n");
		}
		
		cardBuilder.append("IMMEDIATE EFFECTS: \n");
		for (Effect immediate : this.immediateEffects){
			cardBuilder.append(immediate.print()+"\n");
		}
		
		cardBuilder.append("PERMANENT EFFECTS: \n");
		for (Effect permanent : this.permanentEffects){
			cardBuilder.append(permanent.print()+"\n");
		}
		
		cardBuilder.append("FINAL EFFECTS: \n");
		for (Effect finalEff : this.finalEffects){
			cardBuilder.append(finalEff.print()+"\n");
		}
		
		
		return cardBuilder.toString();
	}
	
	@Override
	public Card clone() {
		//Create temporary arrays of card
		ArrayList<Packet> tempRequirements = new ArrayList<>();
		ArrayList<Packet> tempCosts = new ArrayList<>();
		ArrayList<Effect> tempImmediateEffects = new ArrayList<>();
		ArrayList<Effect> tempPermanentEffects = new ArrayList<>();
		ArrayList<Effect> tempFinalEffects = new ArrayList<>();
		
		//Copy the requirements
		if(requirements != null)
			for(Packet requirement : requirements)
				tempRequirements.add(requirement.clone());
		
		//Copy the costs
		if(costs != null)
			for(Packet cost : costs)
				tempCosts.add(cost.clone());
		
		//Copy the immediate effects
		if(immediateEffects != null)
			for(Effect immediateEffect : immediateEffects)
				tempImmediateEffects.add(immediateEffect.clone());
		
		//Copy the permanent effects
		if(permanentEffects != null)
			for(Effect permanentEffect : permanentEffects)
				tempPermanentEffects.add(permanentEffect.clone());
		
		//Copy the final effects
		if(finalEffects != null)
			for(Effect finalEffect : finalEffects)
				tempFinalEffects.add(finalEffect.clone());
		
		return new Card(getName(), getDescription(), getColor(), getPeriod(), getLevel(), tempCosts, tempImmediateEffects, tempRequirements, tempPermanentEffects, tempFinalEffects);
	}
}
