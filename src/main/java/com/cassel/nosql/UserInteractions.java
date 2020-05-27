package com.cassel.nosql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
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
		ArrayList<String> availableKeys = new ArrayList<>();
		Map<String, String> fieldsType = new HashMap<>();
		
		System.out.println("Veuillez taper le nom d'un des champs qui s'affichent ci-dessous :");
		
		for(Document doc : documents)
		{
			for(String key : doc.keySet())
			{
				if(!availableKeys.contains(key))
				{
					availableKeys.add(key);
					
					String typeString = doc.get(key) == null ? "null" : doc.get(key).getClass().getName();
			        if (!typeString.equals(ArrayList.class.getName()))
			        {
			          fieldsType.put(key, typeString);
			        }
			        else
			        {
			          // System.out.println("Array");
			        }
			        
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
		
		String operators [] = {"inf", "inf egal", "sup", "sup egal", "string egal", "bool egal", "bool non egal"};
		
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
    	
    	if(chosenOperator.equals("egal"))
    	{
    		operator = "$eq";
    	}
    	
    	if(chosenOperator.equals("bool egal"))
    	{
    		operator = "=";
    	}
    	
    	if(chosenOperator.equals("bool non egal"))
    	{
    		operator = "!=";
    	}
    	
    	
    	// get data to compare
    	System.out.println("Quelle est la valeur avec laquelle vous souhaitez comparer ?");
    	
    	String dataToCompare = "";

    	dataToCompare = sc.nextLine();
    	
    	Class<?> cls = null;
        try
        {
        	cls = Class.forName(fieldsType.get(chosenField));
        }
        catch (ClassNotFoundException e)
        {
        	e.printStackTrace();
        	cls = String.class;
        }
        
    	BsonDocument query = null;
    	    	
    	if(cls == String.class)
    	{
    		query = new BsonDocument(chosenField, new BsonDocument(operator, new BsonString(dataToCompare)));
    	}
    	else if(cls == Integer.class)
    	{
    		int dataInt = Integer.parseInt(dataToCompare);
    		query = new BsonDocument(chosenField, new BsonDocument(operator, new BsonInt32(dataInt)));
    	}
    	else if(cls == Boolean.class)
    	{
    		boolean dataBool = Boolean.parseBoolean(dataToCompare);
    		query = new BsonDocument(chosenField, new BsonDocument(operator, new BsonBoolean(dataBool)));
    	}
    	
    	FindIterable<Document> results = collection.find(query);
    	
    	System.out.println("Recherche en cours...");
    	
    	for(Document result : results)
		{
			System.out.println(result);
		}
    }
	
	public void insertDocument()
	{
    	
    }
}
