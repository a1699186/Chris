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
    public class matchUsingPublicationKeyCommand : Command
    {

        private delegate void MathcingProcessDelegate(string pHandleKeyStockName, float pHandlePriceValue, Subscription pElement);
        private Publication publicationKey = null;

        public matchUsingPublicationKeyCommand(Publication publicationKey)
        {
            this.publicationKey = publicationKey;
        }


        public void execute()
        {
            if (publicationKey != null)
            {
                if (publicationKey.topic is StockPrice)
                {
                    StockPrice handleKey = (StockPrice)publicationKey.topic;
                    float handleKeyPriceValue;
                    string handleKeyStockName;

                    float.TryParse(handleKey.price, out handleKeyPriceValue);
                    handleKeyStockName = handleKey.name;

                    IMongoQuery query = Query.EQ("expression._t", "StockPriceExpression");
                    SubscriptionDAL tmpDal = new SubscriptionDAL();
                    foreach (Subscription element in tmpDal.findSubscription(query))
                    {
                        MathcingProcessDelegate tmpAsynchronousMethod =
                            new MathcingProcessDelegate(matchSubscriptionUsingPublicationKeyForStockPrice);

                        IAsyncResult tag = tmpAsynchronousMethod.BeginInvoke(handleKeyStockName,handleKeyPriceValue, element, null, null);

                    }// end foreach loop

                }
            }
        }// end execute


        private void matchSubscriptionUsingPublicationKeyForStockPrice(string pHandleKeyStockName,float pHandlePriceValue, Subscription pElement)
        {
            StockPriceExpression elementExpression = (StockPriceExpression)pElement.expression;

            if (!pHandleKeyStockName.Equals(elementExpression.stockName))
            {
                return;
            }

            float elementExpressionValue;
            float.TryParse(elementExpression.priceValue, out elementExpressionValue);

            List<Publication> matchingElement = new List<Publication>();
            matchingElement.Add(this.publicationKey);    
              
            switch (elementExpression.prefixPrice)
            {
                case GenericExpression.PrefixExpressionType.equalOrLessThan:
                    {
                        if (pHandlePriceValue <= elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.follow)
                        {
                            this.startPublishProcess(pElement,matchingElement);
                            break;
                        }

                        if (pHandlePriceValue > elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }
                        break;
                    }
                case GenericExpression.PrefixExpressionType.equalOrMoreThan:
                    {
                        if (pHandlePriceValue >= elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.follow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }

                        if (pHandlePriceValue < elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }
                        break;
                    }
                case GenericExpression.PrefixExpressionType.lessThan:
                    {
                        if (pHandlePriceValue < elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.follow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }

                        if (pHandlePriceValue >= elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }
                        break;
                    }
                case GenericExpression.PrefixExpressionType.moreThan:
                    {
                        if (pHandlePriceValue > elementExpressionValue
                             && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.follow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }

                        if (pHandlePriceValue <= elementExpressionValue
                            && elementExpression.firstFix == GenericExpression.FirstFixExpressionType.notFollow)
                        {
                            this.startPublishProcess(pElement, matchingElement);
                            break;
                        }
                        break;
                    }
            }//end switch 
            
        }// end childMethodOfMatchSubscriptionUsingPublicationKey


        private void startPublishProcess(Subscription pSubscriptionKey, List<Publication> matchingElement)
        {
            Command publishProcessCommand = new StartPublishProcessCommand(pSubscriptionKey, matchingElement);
            publishProcessCommand.execute();
        }

    }//end class
}