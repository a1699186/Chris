using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using FinalYearProject.Models;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using System.Configuration;


namespace FinalYearProject.DataAccessLayer
{
    public class UserDAL
    {

        public const string collectionName = "UserCollection";
        private MongoDatabase database;
        MongoCollection<User> userCollection;
        
    
        public UserDAL()
        {
            database = DatabaseDAL.getInstance();
            userCollection = database.GetCollection<User>(collectionName);
        }

        
        public void CreateUser(User newUser)
        {
            try
            {
                userCollection.Insert(newUser);
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }


        public void removeUser(User removingUser)
        {
            try
            {
                IMongoQuery query = Query.EQ("userID", removingUser.userID);
                userCollection.Remove(query);
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }


        public void updateUser(User updatedUser)
        {
            try
            {
                IMongoQuery query = Query.EQ("userID", updatedUser.userID);
                User result = userCollection.FindOneAs<User>(query);
                if(result != null)
                {
                    userCollection.Save(updatedUser);
                }
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }

        public List<User> findUser(IMongoQuery demandQuery)
        {
            try
            {
                return userCollection.Find(demandQuery).ToList<User>();
            }
            catch (MongoCommandException)
            {
                return new List<User>();
            }
        }


        //testing purpose, will remove later
        public List<User> GetAllUser()
        {
            try
            {
                return userCollection.FindAll().ToList<User>();
            }
            catch (MongoCommandException)
            {
                return new List<User>();
            }
        }

    }
}