package it.polimi.ingsw.ps42.parser;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class BuilderTester {
	
	@Test
	public void builderTest() throws Exception {
		
		CardBuilder b2=new CardBuilder("prova2.json");
		b2.addCard();
		b2.close();
		
		
	}

}
