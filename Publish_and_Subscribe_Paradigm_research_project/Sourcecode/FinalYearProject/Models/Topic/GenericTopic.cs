using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace FinalYearProject.Models.Topic
{
    [BsonKnownTypes(typeof(StockPrice),typeof(WaterPresence))]
    public class GenericTopic
    {
        [BsonExtraElements]
        public BsonDocument extraElement { get; set; }
    }
}