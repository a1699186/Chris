using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;

namespace FinalYearProject.Models.Expression
{
    public class WaterPresenceExpression : GenericExpression
    {

        [BsonElement("Fix1")]
        public FirstFixExpressionType firstFix { get; set; }

        [BsonElement("Fix2")]
        public PrefixExpressionType prefixWater { get; set; }

        [BsonElement("Fix3")]
        public PrefixExpressionType prefixOxygen { get; set; }

        [BsonElement("Conjuction")]
        public ConjunctionExpressionType conjuction{ get; set; }

        [BsonElement("Water")]
        public String waterValue { get; set; }

        [BsonElement("Oxygen")]
        public String oxygenValue { get; set; }

    }
}