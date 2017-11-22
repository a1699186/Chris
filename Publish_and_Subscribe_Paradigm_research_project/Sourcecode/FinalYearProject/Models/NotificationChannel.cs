using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace FinalYearProject.Models
{
    public class NotificationChannel
    {
        public NotificationChannel() { }

        [BsonElement("PhoneNo")]
        public string phoneNumber { get; set; }


        [BsonElement("Email")]
        public string email { get; set; }


        [BsonExtraElements]
        public BsonDocument extraElement { get; set; }

    }
}