using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;


namespace FinalYearProject.Models.Expression
{
    public class StockPriceExpression : GenericExpression
    {
        public StockPriceExpression() { }

        

        [BsonElement("fistFix")]
        public FirstFixExpressionType firstFix { get; set; }

        [BsonElement("secondFix")]
        public PrefixExpressionType prefixPrice { get; set; }

        [BsonElement("stockName")]
        public string stockName { get; set; }

        [BsonElement("price")]
        public string priceValue { get; set; }
        
    }
}