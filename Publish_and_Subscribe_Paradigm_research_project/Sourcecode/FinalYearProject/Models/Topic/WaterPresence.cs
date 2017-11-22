using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;

namespace FinalYearProject.Models.Topic
{
    public class WaterPresence : GenericTopic
    {
        public WaterPresence(){}

        [BsonElement("water")]
        public String water { get; set; }

        [BsonElement("oxygen")]
        public String oxygen { get; set; }
    }
}