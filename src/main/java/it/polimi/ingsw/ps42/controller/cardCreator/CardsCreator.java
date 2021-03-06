package it.polimi.ingsw.ps42.controller.cardCreator;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.parser.CardLoader;

/**
 * Abstract class used to implement the State Pattern with this cards loader
 * 
 * @author Luca Napoletano, Claudio Montanari
 *
 */
public abstract class CardsCreator {
	//Max number of card in the StaticLists
	private final static int CARDS_NUMBER = 8;
	
	protected CardLoader loader;
	
	protected StaticList<Card> green;
	protected StaticList<Card> yellow;
	protected StaticList<Card> blue;
	protected StaticList<Card> violet;
	
	protected int lowIndex;
	protected int highIndex;
	
	protected String folderPath;
	
	/**
	 * The constructor of the cards creator
	 * 
	 * @param folderPath		The path to the folder which contains the correct cards to load
	 * @throws IOException		Exception thrown if there is a Input/Output problem. Probably the path isn't correct
	 */
	public CardsCreator(String folderPath) throws IOException {
		this.folderPath = folderPath;
		
		//Initialize cards vectors
		green = loadCard("green.json");
		yellow = loadCard("yellow.json");
		blue = loadCard("blue.json");
		violet = loadCard("violet.json");
		
		//Initialize variables
		lowIndex = 0;
		highIndex = 4;
	}
	
	/**
	 * Private method to shuffle the cards
	 * 
	 * @param cards		StaticList of cards to shuffle
	 * @return			The StaticList shuffled
	 */
	private StaticList<Card> shuffle(StaticList<Card> cards) {
		//TODO testing
		//Shuffle the cards list
		StaticList<Card> temporary = new StaticList<>(CARDS_NUMBER);
		
		while(!cards.isEmpty()) {
			//Select a random card
			int randomNumber = new Random().nextInt(cards.size());
			Card temporaryCard = cards.get(randomNumber);
			
			//Add the chosen card in the temporary arrayList and
			//remove it from the cards array
			temporary.add(temporaryCard);
			cards.remove(temporaryCard);
		}
		return temporary;
	}
	
	/**
	 * Load the next 4 green cards
	 * @return	StaticList with 4 green cards
	 */
	public StaticList<Card> getNextGreenCards() {
		return getNextCards(green, lowIndex, highIndex);
	}

	/**
	 * Load the next 4 yellow cards
	 * @return	StaticList with 4 yellow cards
	 */
	public StaticList<Card> getNextYellowCards() {
		return getNextCards(yellow, lowIndex, highIndex);
	}

	/**
	 * Load the next 4 blue cards
	 * @return	StaticList with 4 blue cards
	 */
	public StaticList<Card> getNextBlueCards() {
		return getNextCards(blue, lowIndex, highIndex);
	}

	/**
	 * Load the next 4 violet cards
	 * @return	StaticList with 4 violet cards
	 */
	public StaticList<Card> getNextVioletCards() {
		return getNextCards(violet, lowIndex, highIndex);
	}
	
	/**
	 * Private method used to get the next (highIndex - lowIndex) cards to give it to the Table
	 * 
	 * @param cards			The correct StaticList to copy
	 * @param lowIndex		The index of the first card to copy
	 * @param highIndex		The index of the last card to copy (not included)
	 * @return				StaticList of 4 chosen cards
	 */
	
	private StaticList<Card> getNextCards(StaticList<Card> cards, int lowIndex, int highIndex) {
		
		StaticList<Card> temporary = new StaticList<>(highIndex - lowIndex);
		
		for(int i = lowIndex; i < highIndex; i++)
			temporary.add(cards.get(i));
		
		return temporary;
	}
	
	/**
	 * 	Private method used to load the card
	 *	Card file must be at most with 8 card
	 *
	 * @param fileName			The name of the cards file in the directory specified in construction
	 * @return					The StaticList of 8 cards
	 * @throws IOException		Thrown if there is a problem with the file. Maybe the name isn't correct or there are less card than 8 in the file
	 */

	private StaticList<Card> loadCard(String fileName) throws IOException {
		
		//Construct the correct path to file
		String temporaryPath = folderPath + fileName;
		
		loader = new CardLoader(temporaryPath);
		
		StaticList<Card> temporary = new StaticList<>(8);
		
		//Set the correct name to the loader
		List<Card> readCards = loader.getCards();
		
		if(readCards.size() < CARDS_NUMBER)
			throw new IOException("Cannot load card because in file there are less card than " + CARDS_NUMBER);
		
		for(int i = 0; i < CARDS_NUMBER; i++)
			temporary.add(readCards.get(i));
		
		//Shuffle the cards
		temporary = shuffle(temporary);
		
		return temporary;
	}
	
	/**
	 * Method to implement in the inherited classes to go to the new state
	 * 
	 * @return					Return the new state
	 * @throws IOException		Thrown if there is a problem in the new state creation
	 */
	public abstract CardsCreator nextState() throws IOException;
}
