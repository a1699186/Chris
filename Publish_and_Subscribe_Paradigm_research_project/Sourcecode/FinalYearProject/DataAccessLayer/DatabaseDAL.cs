using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Driver;
using System.Configuration;

namespace FinalYearProject.DataAccessLayer
{
    public class DatabaseDAL
    {

        private static MongoDatabase instance;  

        public DatabaseDAL()
        {

        }

        public static MongoDatabase getInstance(){
            if(instance == null){
                
                //string connectionString = System.Environment.GetEnvironmentVariable("CUSTOMCONNSTR_MONGOLAB_URI");
                string connectionString = "mongodb://localhost";
                string dbName = "marshesMongoLab";

                try
                {
                    MongoUrl url = new MongoUrl(connectionString);
                    MongoClient client = new MongoClient(url);
                    MongoServer mongoServer = client.GetServer();
                    instance = mongoServer.GetDatabase(dbName);
                }
                catch (MongoConnectionException ex) 
                {
                    System.Diagnostics.Debug.WriteLine(ex.Message);
                }
            }

            return instance;
        }

        
    }
}