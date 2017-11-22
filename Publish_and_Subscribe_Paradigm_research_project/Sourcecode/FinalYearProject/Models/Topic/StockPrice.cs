using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;

namespace FinalYearProject.Models.Topic
{
    public class StockPrice : GenericTopic
    {
        public StockPrice() { }

        [BsonElement("name")]
        public String name { get; set; }

        [BsonElement("price")]
        public String price { get; set; }
    }
}