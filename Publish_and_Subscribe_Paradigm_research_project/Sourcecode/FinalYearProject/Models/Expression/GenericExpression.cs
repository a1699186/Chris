using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace FinalYearProject.Models.Expression
{
    [BsonKnownTypes(typeof (StockPriceExpression),typeof(WaterPresenceExpression))]
    public class GenericExpression
    {

        
        public  enum FirstFixExpressionType { notFollow, follow }

        
        public enum PrefixExpressionType { moreThan, equalOrLessThan, equalOrMoreThan, lessThan }

        
        public  enum ConjunctionExpressionType { and, or, exclusiveOr}


        public GenericExpression(){}


        [BsonExtraElements]
        public BsonDocument extraElement { get; set; }    
    }
}