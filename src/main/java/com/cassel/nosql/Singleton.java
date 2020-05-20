package com.cassel.nosql;

import java.util.Scanner;

public class Singleton
{	
    /** Constructeur privé */
    private Singleton()
    {
    }
     
    /** Instance unique non préinitialisée */
    private static Singleton INSTANCE = null;
     
    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized Singleton getInstance()
    {           
        if (INSTANCE == null)
        {  
        	INSTANCE = new Singleton(); 
        }
        return INSTANCE;
    }
    
    public Scanner getScanner()
    {
    	Scanner sc = new Scanner(System.in);
    	
    	return sc;
    }
}