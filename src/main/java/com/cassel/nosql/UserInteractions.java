package com.cassel.nosql;

import java.util.ArrayList;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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
		System.out.println("Veuillez taper le nom d'un des champs qui s'affichent ci-dessous :");
		
		for(Document doc : documents)
		{
			for(String key : doc.keySet())
			{
				if(!availableKeys.contains(key))
				{
					availableKeys.add(key);
					System.out.println(key);
				}
			}
		}
		
		Scanner sc = Singleton.getInstance().getScanner();
		
		boolean chosen = false;
		String chosenField = "";

		// choose the field
        while(!chosen)
        {
			chosenField = sc.nextLine();

			if(availableKeys.contains(chosenField))
			{
				System.out.printf("Vous avez choisi %s.", chosenField);
				chosen = true;
			}
			else
			{
				System.out.println("Le champ choisi n'existe pas. Veuillez réessayer.");
				chosen = false;
			}
        }
        
        //TODO: get datatype, in order to check automatically if the operator is correct
        
        // choose the operator
        
        chosen = false;
		String chosenOperator = "";
		
		String operators [] = {"inf", "inf egal", "sup", "sup egal", "egal", "true", "false"};
		
		System.out.println("Veuillez choisir un opérateur parmis ceux ci-dessous :");
		
		for(int i = 0 ; i < operators.length ; i++)
		{
			System.out.printf("%s \n", operators[i]);
		}
		
    	chosenOperator = sc.nextLine();
    	
    	String operator = "";
    	
		//TODO: check operator exists
    	
    	if(chosenOperator.equals("inf"))
    	{
    		operator = "$lt";
    	}
    	
    	if(chosenOperator.equals("inf egal"))
    	{
    		operator = "$lte";
    	}
    	
    	if(chosenOperator.equals("sup"))
    	{
    		operator = "$gt";
    	}
    	
    	if(chosenOperator.equals("sup egal"))
    	{
    		operator = "$gte";
    	}
    	
    	
    	// get data to compare
    	System.out.println("Quelle est la valeur avec laquelle vous souhaitez comparer ?");
    	
    	String dataToCompare = "";

    	dataToCompare = sc.nextLine();
    	
    	BasicDBObject query = null;
    	    	
    	if(!operator.equals("egal") || !operator.equals("true") || !operator.equals("false"))
    	{
    		query = new BasicDBObject(chosenField,
                    new BasicDBObject(operator, dataToCompare));
    	}
    	else
    	{
    		if(operator.equals("egal"))
    		{
    			
    		}
    		else if(operator.equals("true"))
    		{
    			
    		}
    		else if(operator.equals("false"))
    		{
    			
    		}
    	}
    	
    	FindIterable<Document> results = collection.find(query);
    	
    	System.out.println("Recherche en cours...");
    	
    	for(Document result : results)
		{
			System.out.println(result);
			//affiche rien
		}
    }
	
	public void insertDocument()
	{
    	
    }
}
