using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.IdGenerators;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Options;
using MongoDB.Driver;
using FinalYearProject.Models.Expression;


namespace FinalYearProject.Models
{
    public class Subscription
    {

        public Subscription() 
        {

        }

        [BsonId(IdGenerator = typeof(CombGuidGenerator))]
        public Guid subscriptionID { get; set; }


        [BsonElement("subscriber")]
        public MongoDBRef subscriber { get; set; }


        private DateTime ExpiryDate;
        [BsonElement("expiryDate")]
        public DateTime expiryDate
        {
            get { return ExpiryDate.ToLocalTime(); }
            set { ExpiryDate = value; }
        }


        [BsonElement("expression")]
        public GenericExpression expression { get; set; }


        
        [BsonElement("notificationChannel")]
        public NotificationChannel notificationChannel { get; set; }


        [BsonExtraElements]
        public BsonDocument extraElement { get; set; }

    }
}