using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using FinalYearProject.Models.Expression;
using FinalYearProject.DataAccessLayer;
using FinalYearProject.Models;
using FinalYearProject.Models.Topic;
using FinalYearProject.AbstractDataType.LinkedQueue;

namespace FinalYearProject.Class.PublishModule
{
    public class matchUsingSubscriptionKeyCommand : Command
    {
        private delegate void PublishProcessDelegate(Subscription pSubscriptionKey, List<Publication> pElement);
        private Subscription subscriptionKey = null;

        public matchUsingSubscriptionKeyCommand(Subscription subscriptionKey)
        {
            this.subscriptionKey = subscriptionKey;
        }

        public void execute()
        {
            if (subscriptionKey != null)
            {
                if (subscriptionKey.expression is StockPriceExpression)
                {
                    IMongoQuery query = null;
                    StockPriceExpression handledKey = (StockPriceExpression)subscriptionKey.expression;
                    switch (handledKey.prefixPrice)
                    {
                        case GenericExpression.PrefixExpressionType.equalOrLessThan:
                            {
                                if (handledKey.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                                {
                                    query = Query.And(
                                        Query.EQ("topic.name",handledKey.stockName),
                                        Query.GT("topic.price", handledKey.priceValue)                                     
                                        ); 
                                    break;
                                }

                                query = Query.And(
                                        Query.EQ("topic.name",handledKey.stockName),
                                        Query.LTE("topic.price", handledKey.priceValue)                                     
                                        ); 
                                break;
                            }
                        case GenericExpression.PrefixExpressionType.equalOrMoreThan:
                            {
                                if (handledKey.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                                {
                                    query = Query.And(
                                        Query.EQ("topic.name",handledKey.stockName),
                                        Query.LT("topic.price", handledKey.priceValue)                                     
                                        ); 
                                    break;
                                }

                                query = Query.And(
                                        Query.EQ("topic.name", handledKey.stockName),
                                        Query.GTE("topic.price", handledKey.priceValue)
                                        ); 
                                break;
                            }
                        case GenericExpression.PrefixExpressionType.lessThan:
                            {
                                if (handledKey.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                                {
                                    query = Query.And(
                                        Query.EQ("topic.name",handledKey.stockName),
                                        Query.GTE("topic.price", handledKey.priceValue)                                     
                                        ); 
                                    break;
                                }

                                query = Query.And(
                                        Query.EQ("topic.name", handledKey.stockName),
                                        Query.LT("topic.price", handledKey.priceValue)
                                        ); 
                                break;
                            }
                        case GenericExpression.PrefixExpressionType.moreThan:
                            {
                                if (handledKey.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                                {
                                    query = Query.And(
                                        Query.EQ("topic.name", handledKey.stockName),
                                        Query.LTE("topic.price", handledKey.priceValue)
                                        );  
                                    break;
                                }

                                query = Query.And(
                                        Query.EQ("topic.name", handledKey.stockName),
                                        Query.GT("topic.price", handledKey.priceValue)
                                        ); 
                                break;
                            }
                    }//end switch

                    if (query != null)
                    {
                        PublicationDAL dal = new PublicationDAL();
                        List<Publication> matchingElement = dal.findPublication(query);

                        if (matchingElement.Any())
                        {
                            PublishProcessDelegate tmpAsynchronousMethod =
                                new PublishProcessDelegate(startPublishProcess);
                            tmpAsynchronousMethod.BeginInvoke(this.subscriptionKey, matchingElement, null, null);
                        }
                    }


                }//end check expressionKey is StockPriceExpression
            }

        }//end execute method


        private void startPublishProcess(Subscription pSubscriptionKey, List<Publication> matchingElement)
        {
            Command publishProcessCommand = new StartPublishProcessCommand(pSubscriptionKey, matchingElement);
            publishProcessCommand.execute();
        }

    }
}