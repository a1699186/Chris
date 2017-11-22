using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using FinalYearProject.Models;
using FinalYearProject.DataAccessLayer;

namespace FinalYearProject.DataAccessLayer
{
    public class PublicationDAL
    {
        private String collectionName = "Publication";
        private MongoDatabase database;
        private MongoCollection<Publication> publicationCollection;

        public PublicationDAL()
        {
            database = DatabaseDAL.getInstance();
            publicationCollection = database.GetCollection<Publication>(collectionName);
        }


        public void createPublication(Publication newPublication)
        {
            try
            {
                publicationCollection.Insert(newPublication);
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }

        public void removePublication(Publication removingPublication)
        {
            try
            {
                IMongoQuery query = Query.EQ("publicationID",removingPublication.publicationID);
                publicationCollection.Remove(query);
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }

        public void updatePublication(Publication updatedPublication)
        {
            try
            {
                IMongoQuery query = Query.EQ("publicationID", updatedPublication.publicationID);
                Publication result = publicationCollection.FindOneAs<Publication>(query);
                if (result != null)
                {
                    publicationCollection.Save(updatedPublication);
                }
            }
            catch (MongoCommandException ex)
            {
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
        }

        public List<Publication> findPublication(IMongoQuery demandQuery)
        {
            try
            {
                return publicationCollection.Find(demandQuery).ToList<Publication>();
            }
            catch
            {
                return new List<Publication>();
            }
        }

    }
}