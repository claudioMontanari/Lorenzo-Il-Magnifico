package it.polimi.ingsw.ps42.model.position;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.ps42.model.effect.CouncilObtain;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.exception.FamiliarInWrongPosition;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;

public class MarketPosition extends Position {
	
	/*Implementation of the Market position, gives to the
	 * player a certain amount of resources or a council privilege 
	*/
	
	private CouncilObtain councilBonus;
	private List<Familiar> bonusFamiliars;
	
	
	public MarketPosition( int level, Obtain bonus, int malus, CouncilObtain councilBonus) {
		super( ActionType.MARKET, level, bonus, malus);
		bonusFamiliars=new ArrayList<>();
		this.councilBonus = councilBonus;
	}
	
	public List<Familiar> getBonusFamiliar() {
		return bonusFamiliars;
	}
	
	@Override
	public void setFamiliar(Familiar familiar) throws FamiliarInWrongPosition {
		super.setFamiliar(familiar);
		applyCouncilBonus(familiar.getPlayer());
	}
	
	public void applyCouncilBonus( Player player){
		if(councilBonus != null)
			councilBonus.enableEffect(player);
	}
	
	public void addBonusFamiliar(Familiar familiar) throws FamiliarInWrongPosition {
		if(super.canStay(familiar)){
			this.bonusFamiliars.add(familiar);
			applyPositionEffect(familiar);
			this.bonusFamiliars.add(familiar);
			applyCouncilBonus(familiar.getPlayer());
		}
		else throw new FamiliarInWrongPosition("The bonus Familiar does not satisfy the position pre-requirements");
	}
	public void removeBonusFamiliars(){
		
		this.bonusFamiliars=new ArrayList<>();
	}

}
