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
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

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
            	insertDocument(collection);
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
		
		displayFields(documents, availableKeys, fieldsType);
		
		Scanner sc = Singleton.getInstance().getScanner();
		
		boolean chosen = false;
		
		// choose the field
		String chosenField = chooseField(availableKeys, sc, chosen);
                
        // choose the operator
        String operator = chooseOperator(sc);
    	
    	// get data to compare
    	String dataToCompare = chooseDataToCompare(sc);
    	
    	// display search results
    	FindIterable<Document> searchedDocuments = getSearchResults(collection, fieldsType, chosenField, operator, dataToCompare);
    	
    	// get query
    	BsonDocument querySearchResults = buildSearchQuery(fieldsType, chosenField, operator, dataToCompare);
    	
    	// choose action after displaying results
    	System.out.println("Que souhaitez-vous faire ? \n"
    			+ "1 : supprimer les documents récupérés \n"
    			+ "2 : supprimer un des documents récupérés \n"
    			+ "3 : modifier les documents récupérés \n"
    			+ "4 : modifier un document parmis les documents récupérés \n");
    	
    	String chosenOptionAfterSearchDisplayed = sc.nextLine();
    	
    	if(chosenOptionAfterSearchDisplayed.contentEquals("1"))
    	{
    		deleteManyAfterSearch(collection, querySearchResults);
    	}
    	else if(chosenOptionAfterSearchDisplayed.contentEquals("2"))
    	{
    		deleteOneAfterSearch(collection, querySearchResults);
    	}
    	else if(chosenOptionAfterSearchDisplayed.contentEquals("3"))
    	{
    		editDocuments(collection, fieldsType, sc, searchedDocuments);
    	}
    	else if(chosenOptionAfterSearchDisplayed.contentEquals("4"))
    	{
    		editDocument(collection, sc, searchedDocuments);
    	}
    }
	
	
	public void insertDocument(MongoCollection<Document> collection)
	{	
		Document obj = new Document();
		FindIterable<Document> documents = collection.find();
		ArrayList<String> availableKeys = new ArrayList<>();
		Map<String, String> fieldsType = new HashMap<>();
		ArrayList<String> fields = new ArrayList<>();
		
		Scanner sc = Singleton.getInstance().getScanner();
		
		fields = getFields(documents, availableKeys, fieldsType);
		
		for(String field : fields)
		{
			System.out.printf("Insérer une valeur pour le champ %s, rentrer 'null' pour ne pas renseigner le champ.", field);
						
			String valueToInsert = sc.nextLine();
			
			if(valueToInsert != "null")
			{
				obj.put(field, valueToInsert);
			}
		}
		
		collection.insertOne(obj);
    }

	
	private void editDocument(MongoCollection<Document> collection, Scanner sc,
			FindIterable<Document> searchedDocuments)
	{
		Bson filter = null;
		Bson query = null;
		ArrayList<String> availableKeysEditMany = new ArrayList<>();
		Map<String, String> fieldsTypeEditMany = new HashMap<>();
		
		System.out.println("Quel document voulez-vous modifier ? Entrez son ID pour le modifier");
		
		String chosenId = sc.nextLine();
		
		filter = eq("_id", chosenId);
		
		System.out.println("Quel champs souhaitez-vous modifier ?");
		
		// display fields
		displayFields(searchedDocuments, availableKeysEditMany, fieldsTypeEditMany);

		String chosenFieldAfterSearchDisplayed = sc.nextLine();
		
		// get data to edit
		String dataToEdit = chooseDataToEdit(sc);
		
		query = combine(set(chosenFieldAfterSearchDisplayed, dataToEdit));
		
		UpdateResult result = collection.updateOne(filter, query);
		
		System.out.println("Nombre d'enregistrements modifiés : " + result.getModifiedCount());
	}

	
	private void editDocuments(MongoCollection<Document> collection, Map<String, String> fieldsType, Scanner sc,
			FindIterable<Document> searchedDocuments)
	{
		ArrayList<String> availableKeysEditMany = new ArrayList<>();
		Map<String, String> fieldsTypeEditMany = new HashMap<>();
		    		
		System.out.println("Quel champs souhaitez-vous modifier ?");
		
		// display fields
		displayFields(searchedDocuments, availableKeysEditMany, fieldsTypeEditMany);

		String chosenFieldAfterSearchDisplayed = sc.nextLine();
		
		// get data to edit
		String dataToEdit = chooseDataToEdit(sc);
		    		
		searchedDocuments.forEach((doc) -> collection.updateOne(
		    eq("_id", doc.get("_id")), set(chosenFieldAfterSearchDisplayed, getValue(fieldsType, chosenFieldAfterSearchDisplayed, dataToEdit)))
		);
		
		System.out.println("Les champs ont été modifiés !");
	}

	
	private void deleteOneAfterSearch(MongoCollection<Document> collection, BsonDocument querySearchResults)
	{
		collection.deleteOne(querySearchResults);
		System.out.printf("Un document a été supprimé.");
	}

	
	private void displayFields(FindIterable<Document> documents, ArrayList<String> availableKeys,
			Map<String, String> fieldsType)
	{
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
	}
	
	
	private ArrayList<String> getFields(FindIterable<Document> documents, ArrayList<String> availableKeys,
			Map<String, String> fieldsType)
	{
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
			        
				}
			}
		}
		
		return availableKeys;
	}

	private void deleteManyAfterSearch(MongoCollection<Document> collection, BsonDocument querySearchResults)
	{
		DeleteResult deleteResults = collection.deleteMany(querySearchResults);
		System.out.printf("Nombre de documents supprimés : %s", deleteResults.getDeletedCount());
	}

	
	private FindIterable<Document> getSearchResults(MongoCollection<Document> collection, Map<String, String> fieldsType,
			String chosenField, String operator, String dataToCompare)
	{
		BsonDocument query = buildSearchQuery(fieldsType, chosenField, operator, dataToCompare);
    	
    	FindIterable<Document> results = collection.find(query);
    	
    	System.out.println("Recherche en cours...");
    	
    	for(Document result : results)
		{
			System.out.println(result.toJson());
		}
    	
    	return results;
	}
	
	
	public Object getValue(Map<String, String> fieldsType, String field, String value)
	{
        Class<?> type;
        
        try
        {
            type = Class.forName(fieldsType.get(field));
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            type = String.class;
        }
        
        if(type == Integer.class)
        {
            return Integer.parseInt(value);
        }
        else
        {
            return value;
        }
	}

	private BsonDocument buildSearchQuery(Map<String, String> fieldsType, String chosenField, String operator,
			String dataToCompare)
	{
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
    	
		return query;
	}

	private String chooseDataToCompare(Scanner sc)
	{
		System.out.println("Quelle est la valeur avec laquelle vous souhaitez comparer ?");
    	
    	String dataToCompare = "";

    	dataToCompare = sc.nextLine();
		return dataToCompare;
	}
	
	private String chooseDataToEdit(Scanner sc)
	{
		System.out.println("Quelle est la valeur que vous souhaitez insérer ?");
    	
    	String dataToEdit = "";

    	dataToEdit = sc.nextLine();
		return dataToEdit;
	}

	private String chooseOperator(Scanner sc)
	{
		String chosenOperator = "";
		
		String operators [] = {"inf", "inf egal", "sup", "sup egal", "egal", "bool egal", "bool non egal"};
		
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
    		operator = "$eq";
    	}
    	
    	if(chosenOperator.equals("bool non egal"))
    	{
    		operator = "$ne";
    	}
    	
		return operator;
	}

	
	private String chooseField(ArrayList<String> availableKeys, Scanner sc, boolean chosen)
	{
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
        
		return chosenField;
	}
}
