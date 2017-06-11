package it.polimi.ingsw.ps42.parser;

import java.io.IOException;

import com.google.gson.GsonBuilder;

import it.polimi.ingsw.ps42.model.effect.CardBan;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.ForEachObtain;
import it.polimi.ingsw.ps42.model.effect.IncreaseAction;
import it.polimi.ingsw.ps42.model.effect.IncreaseFamiliarsPoint;
import it.polimi.ingsw.ps42.model.effect.NoBonusInTower;
import it.polimi.ingsw.ps42.model.effect.NoFirstActionBan;
import it.polimi.ingsw.ps42.model.effect.NoMarketBan;
import it.polimi.ingsw.ps42.model.effect.ObtainBan;
import it.polimi.ingsw.ps42.model.effect.SlaveBan;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class BanBuilder extends Builder{

	public BanBuilder(String fileName) throws IOException{
		super(fileName);
	
	}
	
	@Override
	protected void initGson() {
		
		GsonBuilder builder=new GsonBuilder().serializeNulls().setPrettyPrinting();
		this.gson = builder.registerTypeAdapter(Effect.class, new Serializer()).create();
			
	}
	
	public void addBan() throws IOException{
		System.out.println("Inizio Procedura aggiunta di una nuove scomuniche");
		
		Effect ban = askBan();
		
		String parse = gson.toJson(ban);
		buffer.append(parse);
		
	}
	
	private Effect askBan(){
		
		Effect effect = null;
		System.out.println("Tipo ? ");
		System.out.println(EffectType.OBTAIN.toString()+" "+EffectType.FOR_EACH_OBTAIN.toString()+" "
				+EffectType.INCREASE_ACTION.toString()+" "+EffectType.DO_ACTION+" "+ EffectType.COUNCIL_OBTAIN.toString()+
				" "+EffectType.INCREASE_FAMILIARS.toString()+" "+EffectType.INCREASE_SINGLE_FAMILIAR.toString()+" "+
				EffectType.NO_TOWER_BONUS.toString());
		String effectType=scanner.nextLine();
		
		switch (effectType.toUpperCase()) {
		case "OBTAIN_BAN":
			effect = askObtainBan();
			break;
		case "FOR_EACH_OBTAIN":
			effect = askForEachObtain();
			break;
		case "INCREASE_ACTION":
			effect = askIncreaseAction();
			break;
		case "INCREASE_FAMILIARS":
			effect = askIncreaseFamiliars();
			break;
		case "NO_TOWER_BONUS":
			effect = new NoBonusInTower();
			break;
		case "NO_MARKET_BAN":
			effect = new NoMarketBan();
			break;
		case "SLAVE_BAN":
			effect = askSlaveBan();
			break;
		case "NO_FIRST_ACTION_BAN":
			effect = new NoFirstActionBan();
			break;
		case "CARD_BAN":
			effect = askCardBan();
			break;
		default:
			System.out.println("tipo non valido");
			break;
		}
		return effect;
		
	}
	
	private Effect askObtainBan(){
		
		String response;
		int quantity;
		Resource resource;
		System.out.println("Risorsa su cui applicare la scomunica?");
		response = scanner.nextLine();
		resource = Resource.parseInput(response);
		System.out.println("Quantità penalizzazione?");
		response = scanner.nextLine();
		quantity = Integer.parseInt(response);
		Unit unit = new Unit(resource, quantity);
		
		return new ObtainBan(unit);
	}
	
	private Effect askForEachObtain(){
		
		Packet requirements = new Packet();
		Packet gains = new Packet();
		String response;
		
		System.out.println("Aggiungere requisiti della scomunica? (si/no)");
		response = scanner.nextLine();
		if(response.toUpperCase().equals("SI"))
			requirements=askPacket();
		System.out.println("Aggiungere penalizzazioni della scomunica");
		gains=askPacket();
		
		return new ForEachObtain(requirements, gains);
	}
	
	private Effect askIncreaseAction(){
		
		String response;
		ActionType type;
		int value;
		
		System.out.println("Tipo azione?");
		response = scanner.nextLine();
		type = ActionType.parseInput(response);
		System.out.println("Valore decremento?");
		response = scanner.nextLine();
		value = Integer.parseInt(response);
		
		return new IncreaseAction(type, value, null);
	}
	
	private Effect askIncreaseFamiliars(){
		
		String response;
		int quantity;
		System.out.println("Quantità penalizzazione dei familiari?");
		response = scanner.nextLine();
		quantity = Integer.parseInt(response);
		
		return new IncreaseFamiliarsPoint(quantity);
	}
	
	private Effect askSlaveBan(){

		String response;
		int divisory;
		System.out.println("Quantità penalizzazione degli incrementi sui familiari?");
		response = scanner.nextLine();
		divisory = Integer.parseInt(response);
		
		return new SlaveBan(divisory);
	}
	
	private Effect askCardBan(){

		String response;
		CardColor color;
		System.out.println("Tipo colore carta da penalizzare?");
		response = scanner.nextLine();
		color = CardColor.parseInput(response);
		
		return new CardBan(color);
	}
	
	
	public static void main(String[] args){
		
		try {
			BanBuilder builder = new BanBuilder("prova.json");
			
			builder.addBan();
			
			builder.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
