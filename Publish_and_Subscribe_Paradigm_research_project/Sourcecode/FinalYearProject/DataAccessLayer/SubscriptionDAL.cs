using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Configuration;
using FinalYearProject.Models;
using FinalYearProject.DataAccessLayer;
using MongoDB.Driver;
using MongoDB.Driver.Builders;

namespace FinalYearProject.DataAccessLayer
{
    public class SubscriptionDAL
    {
        private String collectionName = "Subscription";
        private MongoDatabase database;
        private MongoCollection<Subscription> subscriptionCollection;

        public SubscriptionDAL()
        {
            database = DatabaseDAL.getInstance();
            subscriptionCollection = database.GetCollection<Subscription>(collectionName);
        }


        public void createSubscription(Subscription newSubscription)
        {
            try
            {
                subscriptionCollection.Insert(newSubscription);
            }
            catch(MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }


        public void removeSubscription(Subscription removingSubscription)
        {
            try
            {
                IMongoQuery query = Query.EQ("subscriptionID",removingSubscription.subscriptionID);
                subscriptionCollection.Remove(query);
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }


        public void updateSubscription(Subscription updatedSubscription)
        {
            try
            {
                IMongoQuery query = Query.EQ("subscriptionID", updatedSubscription.subscriptionID);
                Subscription result = subscriptionCollection.FindOneAs<Subscription>(query);
                if (result != null)
                {
                    subscriptionCollection.Save(updatedSubscription);
                }
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }


        public List<Subscription> findSubscription(IMongoQuery demandQuery)
        {
           try
           {
               return subscriptionCollection.Find(demandQuery).ToList<Subscription>();
           }
           catch(MongoCommandException)
           {
               return new List<Subscription>();
           }
        }
        

    }
}