using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.IdGenerators;
using MongoDB.Bson;
using FinalYearProject.Models.Topic;
using MongoDB.Driver;

namespace FinalYearProject.Models
{
    public class Publication
    {

        public Publication()
        {

        }


        [BsonId(IdGenerator = typeof(CombGuidGenerator))]
        public Guid publicationID{get; set;}


        [BsonElement("publisher")]
        public MongoDBRef publisher{get; set;}


        [BsonElement("topic")]
        public GenericTopic topic{get; set;}


        [BsonExtraElements]
        public BsonDocument extraElement { get; set; }

    }
}