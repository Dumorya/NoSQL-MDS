package com.cassel.nosql;

import java.util.ArrayList;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class UserInteractions
{
	
	private MongoDatabase db;

	public UserInteractions(MongoClient client, String database)
	{
		this.db = client.getDatabase(database);
	}

	public void connection() 
	{
        
        // get collections name 
        MongoIterable <String> collections = db.listCollectionNames();
        
        for (String collectionName: collections)
        {
            System.out.println(collectionName);
        }
        
        boolean chosen = false;
        String chosenCollection = "";
        
    	Scanner sc = Singleton.getInstance().getScanner();

        while(!chosen)
        {
            System.out.println("Veuillez saisir une collection, ou 'fin' pour finir :");
            chosenCollection = sc.nextLine();
            
            for (String collectionName: collections)
            {
                if(chosenCollection.equals(collectionName))
                {
                    System.out.println("Vous avez saisi : " + chosenCollection);
                    chosen = true;
                }
                else if(chosenCollection.equals("fin"))
                {
                	System.out.println("FIN");
                	chosen = true;
                }
                else
                {
                	System.out.println("Cette collection n'existe pas.");
                	chosen = false;
                }
            }
        }
        
        MongoCollection<Document> collection = db.getCollection(chosenCollection);
        
        chosen = false;
        
        while(!chosen)
        {
            System.out.println("Taper 're' pour rechercher un document, 'in' pour insérer un document, 'fin' pour arrêter.");
            String choice = sc.nextLine();
            
        	if(choice.equals("re"))
            {
                System.out.println("Vous avez choisi de rechercher un document");
                searchDocument(collection);
                chosen = true;
            }
            else if(choice.equals("in"))
            {
            	System.out.println("Vous avez choisi d'insérer un document");
            	chosen = true;
            }
            else if(choice.equals("fin"))
            {
            	System.out.println("FIN");
            	chosen = true;
            }
            else
            {
            	System.out.println("Ce choix n'existe pas. Recommencer");
            	chosen = false;
            }
        }

    }
	
	
	public void searchDocument(MongoCollection<Document> collection)
	{
		FindIterable<Document> documents = collection.find();
		ArrayList<String> availableKeys = new ArrayList<>();;
		
		for(Document doc : documents)
		{
			for(String key : doc.keySet())
			{
				if(availableKeys.contains(key))
				{
					availableKeys.add(key);
					System.out.println(key);
				}
			}
		}
    }
	
	public void insertDocument()
	{
    	
    }
}
