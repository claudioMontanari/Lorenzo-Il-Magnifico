package it.polimi.ingsw.ps42.parser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.reflect.TypeToken;

public class ImageLoader extends Loader{
	
	private HashMap<String, String> cardsPath;
	private HashMap<String, String> leaderCardsPath;
	private HashMap<String, String> bansPath;

	public ImageLoader(String fileName) throws IOException {
		super(fileName);
		
		//Initialize the maps
		cardsPath = loader();
		if(cardsPath == null)
			throw new IOException();
		
		leaderCardsPath = loader();
		if(cardsPath == null)
			throw new IOException();	
		
		bansPath = loader();
		if(bansPath == null)
			throw new IOException();
	}

	@Override
	protected void initGson() {
		gson = new Gson();
		parser = new JsonStreamParser(buffer);
	}
	
	private HashMap<String, String> loader() {
		if(parser.hasNext()) {
			JsonElement element = parser.next();
			
			if(element.isJsonObject()) {
				Type type = new TypeToken< HashMap<String, String> >(){}.getType();
				return gson.fromJson(element, type);
			}
		}
		return null;
	}
	
	private BufferedImage loadGenericImage(HashMap<String, String> map, String cardName) throws IOException {
		return ImageIO.read(new File(map.get(cardName)));
	}
	
	public BufferedImage loadCardImage(String cardName) throws IOException {
		return loadGenericImage(cardsPath, cardName);
	}
	
	public BufferedImage loadLeaderCardImage(String leaderCardName) throws IOException {
		return loadGenericImage(leaderCardsPath, leaderCardName);
	}
	
	public BufferedImage loadBanImage(Integer period, Integer index) throws IOException {
		final int OFFSET = 7;
		
		Integer exactIndex = index + period * OFFSET;
		return loadGenericImage(bansPath, exactIndex.toString());
	}
	
	public int cardsMapSize() {
		return cardsPath.size();
	}
	
	public int leaderCardsMapSize() {
		return leaderCardsPath.size();
	}
	
	public int bansMapSize() {
		return bansPath.size();
	}
	
	public static void main(String[] args) throws IOException {
		ImageLoader loader = new ImageLoader("Resource//Configuration//imagePaths.json");
		System.out.println(loader.cardsMapSize());
		System.out.println(loader.leaderCardsMapSize());
		System.out.println(loader.bansMapSize());
	}
}