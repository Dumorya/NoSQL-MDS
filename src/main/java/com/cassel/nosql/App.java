/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.cassel.nosql;

import java.util.Arrays;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoClient;

public class App
{
    public String getGreeting()
    {
        return "Hello world.";
    }
    
    public static void main(String[] args) throws Exception
    {
        System.out.println(new App().getGreeting());
        
        String database = "";
        String host = "";
        int port = 0;
        
        for(int i = 0 ; i < args.length ; i++)
        {
        	if (args[i].equals("--db"))
        	{
                if (i + 1 < args.length && !args[i + 1].startsWith("--"))
                {
                	database = args[i+1];
                }
                else
                {
                	throw new Exception("--db not defined");
                }
            }
        	
        	if (args[i].equals("--host"))
        	{
                if (i + 1 < args.length && !args[i + 1].startsWith("--"))
                {
                	host = args[i+1];
                }
                else
                {
                	throw new Exception("--host not defined");
                }
            }
        	
        	if (args[i].equals("--port"))
        	{
                if (i + 1 < args.length && !args[i + 1].startsWith("--"))
                {
                	String portString = args[i+1];
                	port = Integer.parseInt(portString);
                }
                else
                {
                	throw new Exception("--port not defined");
                }
            }
        }
        
        // connection
        MongoClient mongoClient = MongoClientConnection.getMongoClient(host, port);
        UserInteractions userInteracts = new UserInteractions(mongoClient, database);
        userInteracts.connection();

    }

}
